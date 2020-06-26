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
import com.sparc.frjvcapp.config.AllApi;
import com.sparc.frjvcapp.pojo.Response1;

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
    SQLiteDatabase db, db1, db2, db3;
    String divid, userid;

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
                Log.d("util123", "pass");
        Util.scheduleJob(getApplication());
        //Log.d("util", "pass");
        new imgaeSync().execute("");
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }

    public void Images() {
        try {
            SharedPreferences shared = getSharedPreferences(userlogin, MODE_PRIVATE);
            divid = shared.getString("udivid", "0");
            userid = shared.getString("uemail", "0");
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            db1 = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            db2 = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            //Cursor c1 = db.rawQuery("update m_pillar_reg set img_status='0' where uid='" + userid + "' and d_id='" + divid + "'", null);

            Cursor c = db.rawQuery("select * from m_pillar_reg where d_id='" + divid + "' and uid='" + userid + "' and img_status='0' and p_pic is not null", null);
            int count = c.getCount();
            if (c.getCount() >= 1) {
                if (c.moveToFirst()) {
                    try {
                        uploadImage(Utility.getByeArr(Utility.setPic(c.getString(c.getColumnIndex("p_pic")))), c.getString(c.getColumnIndex("p_pic")), "1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                c.close();
                db.close();
            }

            Cursor c1 = db1.rawQuery("select * from m_shifting_pillar_reg where uid='" + userid + "' and simg_status='0' and s_pic is not null", null);
            int count1 = c.getCount();
            if (c1.getCount() >= 1) {
                if (c1.moveToFirst()) {
                    try {
                        uploadImage(Utility.getByeArr(Utility.setPic(c1.getString(c1.getColumnIndex("s_pic")))), c1.getString(c1.getColumnIndex("s_pic")), "2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                c1.close();
                db1.close();
            }

            Cursor c2 = db1.rawQuery("select * from m_fb_dgps_survey_pill_pic where u_id='" + userid + "' and pic_status='0' and pic_name is not null", null);
            int count2 = c.getCount();
            if (c2.getCount() >= 1) {
                if (c2.moveToFirst()) {
                    try {
                        uploadDGPSImage(Utility.getByeArr(Utility.setPic(c2.getString(c2.getColumnIndex("pic_name")))), c2.getString(c2.getColumnIndex("pic_name")), "1", c2.getString(c2.getColumnIndex("pic_view")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                c2.close();
                db2.close();
            }
        }
        catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void uploadImage(byte[] imageBytes, final String imgname, String value) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.F_PILL_PIC_NODE_SERVICE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = null;
            body = MultipartBody.Part.createFormData("image", imgname, requestFile);
            Call<Response1> call = retrofitInterface.uploadImage(body);
            call.enqueue(new Callback<Response1>() {
                @Override
                public void onResponse(Call<Response1> call, retrofit2.Response<Response1> response) {
                    if (response.isSuccessful()) {
                        try {
                            if (value.equals("1")) {
                                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                Cursor c = db.rawQuery("update m_pillar_reg set img_status='1' where uid='" + userid + "' and d_id='" + divid + "' and p_pic='" + imgname + "'", null);
                                if (c.getCount() >= 0) {
                                    Response1 responseBody = response.body();
                                    Toast.makeText(ImageService.this, responseBody.getPath(), Toast.LENGTH_SHORT).show();
                                }
                                c.close();
                                db.close();
                            } else {
                                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                Cursor c = db.rawQuery("update m_shifting_pillar_reg set simg_status='1' where uid='" + userid + "' and s_pic='" + imgname + "'", null);
                                if (c.getCount() >= 0) {
                                    Response1 responseBody = response.body();
                                    Toast.makeText(ImageService.this, responseBody.getPath(), Toast.LENGTH_SHORT).show();
                                }
                                c.close();
                                db.close();
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
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
                }

            });
        } else {
            Toast.makeText(ImageService.this, "Internet Connection is Not Available", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadDGPSImage(byte[] imageBytes, final String imgname, String value, String pic_view) {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.DGPS_PILL_PIC_NODE_SERVICE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = null;
            body = MultipartBody.Part.createFormData("image", imgname, requestFile);
            Call<Response1> call = retrofitInterface.uploadDGPSImage(body);
            // mProgressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<Response1>() {
                @Override
                public void onResponse(Call<Response1> call, retrofit2.Response<Response1> response) {
                    if (response.isSuccessful()) {
                        if (value.equals("1")) {
                            try {
                                db3 = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                Cursor c4 = db3.rawQuery("update m_fb_dgps_survey_pill_pic set pic_status='1' where u_id='" + userid + "' and pic_name='" + imgname + "'", null);
                                if (c4.getCount() >= 0) {
                                    SQLiteDatabase dbtemp = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                    Cursor ctemp = null;
                                    try {
                                        if (pic_view.equals("F")) {
                                            ctemp = dbtemp.rawQuery("update m_fb_dgps_survey_pill_data set f_pic_status='1' where u_id='" + userid + "' and f_pic_name='" + imgname + "'", null);
                                        } else if (pic_view.equals("B")) {
                                            ctemp = dbtemp.rawQuery("update m_fb_dgps_survey_pill_data set b_pic_status='1' where u_id='" + userid + "' and b_pic_name='" + imgname + "'", null);
                                        } else if (pic_view.equals("I")) {
                                            ctemp = dbtemp.rawQuery("update m_fb_dgps_survey_pill_data set i_pic_status='1' where u_id='" + userid + "' and i_pic_name='" + imgname + "'", null);
                                        } else if (pic_view.equals("O")) {
                                            ctemp = dbtemp.rawQuery("update m_fb_dgps_survey_pill_data set o_pic_status='1' where u_id='" + userid + "' and o_pic_name='" + imgname + "'", null);
                                        } else if (pic_view.equals("T")) {
                                            ctemp = dbtemp.rawQuery("update m_fb_dgps_survey_pill_data set div_pic_status='1' where u_id='" + userid + "' and div_pic_name='" + imgname + "'", null);
                                        } else {

                                        }
                                        if (ctemp.getCount() >= 0) {
                                            Toast.makeText(ImageService.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                        }
                                        ctemp.close();
                                        dbtemp.close();
                                    } catch (Exception ee) {
                                    ee.printStackTrace();
                                    } finally {
                                        Response1 responseBody = response.body();
                                        Toast.makeText(ImageService.this, responseBody.getPath(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                c4.close();
                                db3.close();
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        } else {
                        }
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
