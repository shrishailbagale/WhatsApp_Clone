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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailValue = findViewById(R.id.email_value);
        verifiedStatus = findViewById(R.id.verified_status);
        editIcon = findViewById(R.id.edit_icon);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            emailValue.setText(currentUser.getEmail());
            if (currentUser.isEmailVerified()) {
                verifiedStatus.setText("Verified");
            } else {
                verifiedStatus.setText("Not Verified");
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
        // Here you would send the OTP using your email service provider
        // For demonstration purposes, we will skip the actual sending part
        // and directly show the OTP verification dialog
        Toast.makeText(this, "OTP sent to " + newEmail, Toast.LENGTH_SHORT).show();

        // Show the dialog to verify OTP
        showOtpVerificationDialog(newEmail);
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
        // Here you would verify the OTP
        // For demonstration purposes, we assume OTP is always correct
        updateEmail(newEmail);
    }

    private void updateEmail(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                    emailValue.setText(newEmail);
                    verifiedStatus.setText("Not Verified");
                    sendVerificationEmail();
                } else {
                    Toast.makeText(EmailActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmailActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}