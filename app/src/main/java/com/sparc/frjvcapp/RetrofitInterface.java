package com.sparc.frjvcapp;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterface {
    @Multipart
    @POST("/images/upload")
    Call<Response1> uploadImage(@Part MultipartBody.Part image);
}