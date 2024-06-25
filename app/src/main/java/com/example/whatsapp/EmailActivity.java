package com.example.whatsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailActivity extends AppCompatActivity {

    private TextView emailValue, verifiedStatus;
    private ImageView editIcon;
    private FirebaseAuth mAuth;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailValue = findViewById(R.id.email_value);
        verifiedStatus = findViewById(R.id.verified_status);
        editIcon = findViewById(R.id.edit_icon);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getClient().create(ApiService.class);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            emailValue.setText(currentUser.getEmail());
            verifiedStatus.setText(currentUser.isEmailVerified() ? "Verified" : "Not Verified");

            if (!currentUser.isEmailVerified()) {
                currentUser.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Email sent
                    }
                });
            }
        }

        editIcon.setOnClickListener(v -> showEmailUpdateDialog());
    }

    private void showEmailUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_email, null);
        builder.setView(dialogView);

        EditText newEmail = dialogView.findViewById(R.id.new_email);

        builder.setTitle("Update Email")
                .setPositiveButton("Send OTP", (dialog, which) -> sendOtpToNewEmail(newEmail.getText().toString().trim()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void sendOtpToNewEmail(String newEmail) {
        HashMap<String, String> emailMap = new HashMap<>();
        emailMap.put("email", newEmail);
        Call<ResponseBody> call = apiService.sendOtp(emailMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EmailActivity.this, "OTP sent to " + newEmail, Toast.LENGTH_SHORT).show();
                    showOtpVerificationDialog(newEmail);
                } else {
                    Toast.makeText(EmailActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EmailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOtpVerificationDialog(String newEmail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_verify_otp, null);
        builder.setView(dialogView);

        EditText otpCode = dialogView.findViewById(R.id.otp_code);

        builder.setTitle("Verify OTP")
                .setPositiveButton("Verify", (dialog, which) -> verifyOtpAndUpdateEmail(newEmail, otpCode.getText().toString().trim()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void verifyOtpAndUpdateEmail(String newEmail, String otpCode) {
        HashMap<String, String> otpMap = new HashMap<>();
        otpMap.put("email", newEmail);
        otpMap.put("otp", otpCode);
        Call<ResponseBody> call = apiService.verifyOtp(otpMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    updateEmail(newEmail);
                } else {
                    Toast.makeText(EmailActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EmailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmail(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                    emailValue.setText(newEmail);
                    verifiedStatus.setText("Not Verified");
                } else {
                    Toast.makeText(EmailActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}