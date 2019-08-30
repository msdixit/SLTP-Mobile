package com.sparc.frjvcapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.Response;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ImageService extends JobService {
    public static final String userlogin = "userlogin";
    SQLiteDatabase db;
    String divid, userid;

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.d("util123", "pass");
        Util.scheduleJob(getApplication());
        Log.d("util", "pass");
        new imgaeSync().execute("");
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }

    public void Images() {
        SharedPreferences shared = getSharedPreferences(userlogin, MODE_PRIVATE);
        divid = shared.getString("udivid", "0");
        userid = shared.getString("uemail", "0");
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        //Cursor c1 = db.rawQuery("update m_pillar_reg set img_status='0' where uid='" + userid + "' and d_id='" + divid + "'", null);

        Cursor c = db.rawQuery("select * from m_pillar_reg where d_id='" + divid + "' and uid='" + userid + "' and img_status='0'", null);
        int count = c.getCount();
        if (c.getCount() >= 1) {
            if (c.moveToFirst()) {
                try {
                    uploadImage(Utility.getByeArr(Utility.setPic(c.getString(c.getColumnIndex("p_pic")))), c.getString(c.getColumnIndex("p_pic")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
            //  c1.close();
            db.close();
        }
    }

    private void uploadImage(byte[] imageBytes, final String imgname) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://203.129.207.130:5067/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = null;
            body = MultipartBody.Part.createFormData("image", imgname, requestFile);
            Call<Response1> call = retrofitInterface.uploadImage(body);
            // mProgressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<Response1>() {
                @Override
                public void onResponse(Call<Response1> call, retrofit2.Response<Response1> response) {
                    if (response.isSuccessful()) {
                        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                        Cursor c = db.rawQuery("update m_pillar_reg set img_status='1' where uid='" + userid + "' and d_id='" + divid + "' and p_pic='" + imgname + "'", null);
                        if (c.getCount() >= 0) {
                            Response1 responseBody = response.body();
                            Toast.makeText(ImageService.this, responseBody.getPath(), Toast.LENGTH_SHORT).show();
                        }
                        c.close();
                        db.close();
                        //Toast.makeText(ImageService.this, "done", Toast.LENGTH_SHORT).show();
                    } else {
                        ResponseBody errorBody = response.errorBody();
                        Gson gson = new Gson();
                        try {
                            Response errorResponse = gson.fromJson(errorBody.string(), Response.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Response1> call, Throwable t) {
//                    db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
//                    Cursor c = db.rawQuery("update m_pillar_reg set img_status='0' where uid='" + userid + "' and d_id='" + divid + "' and p_pic='" + imgname + "'", null);
//                    c.close();
//                    db.close();
                }

            });
        } else {
            Toast.makeText(ImageService.this, "Internet Connection is Not Available", Toast.LENGTH_LONG).show();
        }
    }

    private class imgaeSync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //inside image sync
            Images();
            return "Executed";
        }
    }


}
