package com.example.whatsapp;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiService {
    @POST("/send-otp")
    Call<ResponseBody> sendOtp(@Body HashMap<String, String> email);

    @POST("/verify-otp")
    Call<ResponseBody> verifyOtp(@Body HashMap<String, String> otpData);
}
