package com.sparc.frjvcapp;

import com.sparc.frjvcapp.pojo.M_Dgps_Sync_Data;
import com.sparc.frjvcapp.pojo.Response1;
//import com.squareup.okhttp.ResponseBody;
import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @Multipart
    @POST("/images/upload")
    Call<Response1> uploadImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("/dgpsimages/upload")
    Call<Response1> uploadDGPSImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("/api/values/savezipfiles")
    Call<Object> sendDataWithFile(@Query("fid") Integer fid,@Part MultipartBody.Part file);

    @Multipart
    @POST("/api/values/savezipfilesrtx")
    Call<Object> sendRTXDataWithFile(@Query("fid") Integer fid,@Part MultipartBody.Part file);
}
