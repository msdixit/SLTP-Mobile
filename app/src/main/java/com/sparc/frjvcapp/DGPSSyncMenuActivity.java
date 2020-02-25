package com.sparc.frjvcapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sparc.frjvcapp.config.AllApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DGPSSyncMenuActivity extends AppCompatActivity {
    public static final String data = "data";
    public static final String userlogin = "userlogin";
    private SQLiteDatabase db;
    String sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid, jobid, div_name, range_name, fb_name, _token, frjvc_lat, frjvc_long;
    SharedPreferences shared, _shareToken;
    Button sync, syncfile, rtxfile;
    DbHelper dbHelper;
    private ProgressDialog progressDialog;
    JSONArray jsonArray;
    JSONObject fp_data;
    ProgressDialog progressDialog1, progressDialog2;
    TextView totpoint, syncpoint, totpic, syncpic, totsign, syncattendance, totfolder, syncfolder, dgpsfbName;
    String sfinalpath, dfinalpath;
    private RetrofitInterface jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpssync_menu);

        sync = findViewById(R.id.sync);
        syncfile = findViewById(R.id.syncfile);
        rtxfile = findViewById(R.id.rtxfile);

        totpoint = findViewById(R.id.totpoint);
        syncpoint = findViewById(R.id.syncpoint);
        totpic = findViewById(R.id.totpic);
        syncpic = findViewById(R.id.syncpic);
        totsign = findViewById(R.id.totsign);
        syncattendance = findViewById(R.id.syncattendance);
        totfolder = findViewById(R.id.totfolder);
        syncfolder = findViewById(R.id.syncfolder);
        dgpsfbName = findViewById(R.id.dgpsfbName);


        shared = getApplicationContext().getSharedPreferences(data, MODE_PRIVATE);
        sharediv = shared.getString("fbdivcode", "0");
        sharerange = shared.getString("fbrangecode", "0");
        sharefb = shared.getString("fbcode", "0");
        sharefbtype = shared.getString("fbtype", "0");
        sharefbname = shared.getString("fbname", "0");
        jobid = shared.getString("jobid", "0");
        userid = shared.getString("userid", "0");
        div_name = shared.getString("div_name", "0");
        range_name = shared.getString("range_name", "0");
        fb_name = shared.getString("fb_name", "0");

        _shareToken = getApplicationContext().getSharedPreferences(userlogin, MODE_PRIVATE);
        _token = _shareToken.getString("token", "0");

        dgpsfbName.setText(fb_name);

        getDataforDisplay(userid);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor c = db.rawQuery("update m_fb_dgps_survey_pill_data set pillar_sfile_status='1',pillar_rfile_status='1',sync_status='0'", null);
        if (c.getCount() >= 0) {

        }
        c.close();
        db.close();
/*        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Cache-Control", "no-store");

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });*/
       /* httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+_token).build();
                return chain.proceed(request);
            }
        });*/
       /* OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer "+_token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();*/


        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer " + _token).build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AllApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        jsonPlaceHolderApi = retrofit.create(RetrofitInterface.class);


        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDataForSynchronization()) {
                    if (CheckDataTagging()) {
                        BindDGPSData(sharediv, userid);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your pillar data is not tagged with the static data.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //progressDialog1.dismiss();
                    Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
                }
            }
        });
        syncfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                    if (checkFileStatus(userid, sharediv)) {
                        String sfile = Environment.getExternalStorageDirectory().toString();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String spath = "/StaticData";
                        String zipPath = "/SyncFile" + "_" + timeStamp + ".zip";
                        sfinalpath = sfile + spath;
                        dfinalpath = sfile + zipPath;
                        File f = new File(dfinalpath);
                        zipFileAtPath(sfinalpath, dfinalpath);
                        Toast.makeText(getApplicationContext(), "Zip Completed", Toast.LENGTH_LONG).show();
                        File file = new File(dfinalpath);
                        ZipFolder(file);
                    } else {
                        Toast.makeText(getApplicationContext(), "All files are synced", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        rtxfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                    if (checkRTXFileStatus(userid, sharediv)) {
                        String sfile = Environment.getExternalStorageDirectory().toString();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String spath = "/RTXData";
                        String zipPath = "/RtxData" + "_" + timeStamp + ".zip";
                        sfinalpath = sfile + spath;
                        dfinalpath = sfile + zipPath;
                        zipFileAtPath(sfinalpath, dfinalpath);
                        Toast.makeText(getApplicationContext(), "Zip Completed", Toast.LENGTH_LONG).show();
                        File file = new File(dfinalpath);
                        ZipRTXFolder(file);
                    } else {
                        Toast.makeText(getApplicationContext(), "All files are synced", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    private boolean checkFileStatus(String userid, String sharefb) {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        boolean b = false;
        Cursor cursor = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and pillar_sfile_path is not null and pillar_sfile_status='1' order by pill_no", null);
        try {
            if (cursor.getCount() > 0) {
                b = true;
            } else {
                b = false;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return b;
    }

    private boolean checkRTXFileStatus(String userid, String sharefb) {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        boolean b = false;
        Cursor cursor = db.rawQuery("select distinct fb_name from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and pillar_rfile_path is not null and pillar_rfile_status='1'", null);
        try {
            if (cursor.getCount() > 0) {
                b = true;
            } else {
                b = false;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return b;
    }

    private void ZipFolder(File file) {
        /*if(file.isDirectory())
        {
            file.delete();
        }*/
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<Object> responseBodyCall = jsonPlaceHolderApi.sendDataWithFile(Integer.parseInt(sharefb), multipartBody);
        Toast.makeText(this, "ddd", Toast.LENGTH_LONG).show();
        responseBodyCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    if (response.isSuccessful()) {
                        if (new Gson().toJson(response.body()) == null) {
                            if (response.code() == 409) {
                                Toast.makeText(getApplicationContext(), "File already exist", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Internal server error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            try {
                                String j_array = new Gson().toJson(response.body());
                                JsonObject jobj = (JsonObject) new JsonParser().parse(j_array);
                                JsonArray arr = (JsonArray) jobj.get("fileStatus");
                                for (int i = 0; i < arr.size(); i++) {
                                    JsonObject obj = (JsonObject) arr.get(i);
                                    try {
                                        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                        String path = "%" + obj.get("chrv_statusName").getAsString();
                                        Cursor c = db.rawQuery("update m_fb_dgps_survey_pill_data set pillar_sfile_status='2' where pillar_sfile_path like '" + path + "'", null);
                                        if (c.getCount() >= 0) {
                                            if (file.delete()) {
                                                Toast.makeText(DGPSSyncMenuActivity.this, "Data Synchronization successfully completed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        c.close();
                                        db.close();
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    } finally {

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    } else {
                        String s = response.errorBody().toString();
                        Toast.makeText(getApplicationContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                } finally {

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "bsvshcvsc", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ZipRTXFolder(File file) {
       /* if(file.isDirectory())
        {
            file.delete();
        }
*/
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<Object> responseBodyCall = jsonPlaceHolderApi.sendRTXDataWithFile(Integer.parseInt(sharefb), multipartBody);
        responseBodyCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                if (response.isSuccessful()) {
                    if (new Gson().toJson(response.body()) == null) {
                        if (response.code() == 409) {
                            Toast.makeText(getApplicationContext(), "File already exist", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Internal server error", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            String j_array = new Gson().toJson(response.body());
                            JsonObject jobj = (JsonObject) new JsonParser().parse(j_array);
                            JsonArray arr = (JsonArray) jobj.get("fileStatus");
                            for (int i = 0; i < arr.size(); i++) {
                                JsonObject obj = (JsonObject) arr.get(i);
                                try {
                                    db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                    String path = "%" + obj.get("chrv_statusName").getAsString();
                                    Cursor c = db.rawQuery("update m_fb_dgps_survey_pill_data set pillar_rfile_status='2' where pillar_rfile_path like '" + path + "'", null);
                                    if (c.getCount() >= 0) {
                                        if (file.delete()) {
                                            Toast.makeText(DGPSSyncMenuActivity.this, "Data Synchronization successfully completed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    c.close();
                                    db.close();
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                } finally {

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
            }
        });
    }

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;
        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length() + 1);
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    private boolean CheckDataTagging() {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        boolean b = false;
        Cursor cursor = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and pillar_sfile_status='1' order by pill_no", null);
        try {
            if (cursor.getCount() > 0) {
                b = true;
            } else {
                b = false;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return b;
    }

    private void getDataforDisplay(String userid) {
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select count(id) as totpoint from m_fb_dgps_survey_pill_data where u_id='" + userid + "'", null);
            Cursor cursor1 = db.rawQuery("select count(id) as totsyncpoint from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and sync_status='1' ", null);

            Cursor cursor2 = db.rawQuery("select count(id) as totpic from m_fb_dgps_survey_pill_pic where u_id='" + userid + "'", null);
            Cursor cursor3 = db.rawQuery("select count(id) as totsyncpic from m_fb_dgps_survey_pill_pic where u_id='" + userid + "' and pic_status='1'", null);

            Cursor cursor4 = db.rawQuery("select count(id) as totfile from m_fb_dgps_survey_pill_data where u_id='" + userid + "'", null);
            Cursor cursor5 = db.rawQuery("select count(id) as totsyncfile from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and pillar_sfile_status='2'", null);

            Cursor cursor6 = db.rawQuery("select count(distinct fb_name) as totrfile from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "'", null);
            Cursor cursor7 = db.rawQuery("select count(distinct fb_name) as totrsyncfile from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and pillar_rfile_path is not null and pillar_rfile_status='2'", null);


            cursor.moveToFirst();
            cursor1.moveToFirst();
            cursor2.moveToFirst();
            cursor3.moveToFirst();
            cursor4.moveToFirst();
            cursor5.moveToFirst();
            cursor6.moveToFirst();
            cursor7.moveToFirst();

            if (cursor.moveToFirst()) {
                do {
                    totpoint.setText(cursor.getString(cursor.getColumnIndex("totpoint")));
                } while (cursor.moveToNext());
            }
            if (cursor1.moveToFirst()) {
                do {
                    syncpoint.setText(cursor1.getString(cursor1.getColumnIndex("totsyncpoint")));
                } while (cursor1.moveToNext());
            }
            if (cursor2.moveToFirst()) {
                do {
                    totpic.setText(cursor2.getString(cursor2.getColumnIndex("totpic")));
                } while (cursor2.moveToNext());
            }
            if (cursor3.moveToFirst()) {
                do {
                    syncpic.setText(cursor3.getString(cursor3.getColumnIndex("totsyncpic")));
                } while (cursor3.moveToNext());
            }
            if (cursor4.moveToFirst()) {
                do {
                    totfolder.setText(cursor4.getString(cursor4.getColumnIndex("totfile")));
                } while (cursor4.moveToNext());
            }
            if (cursor5.moveToFirst()) {
                do {
                    syncfolder.setText(cursor5.getString(cursor5.getColumnIndex("totsyncfile")));
                } while (cursor5.moveToNext());
            }
            if (cursor6.moveToFirst()) {
                do {
                    totsign.setText(cursor6.getString(cursor6.getColumnIndex("totrfile")));
                } while (cursor6.moveToNext());
            }
            if (cursor7.moveToFirst()) {
                do {
                    syncattendance.setText(cursor7.getString(cursor7.getColumnIndex("totrsyncfile")));
                } while (cursor7.moveToNext());
            }
            cursor.close();
            cursor1.close();
            cursor2.close();
            cursor3.close();
            cursor4.close();
            cursor5.close();
            cursor6.close();
            cursor7.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {

        }
    }

    private void BindDGPSData(String divid, String userid) {
        try {
            jsonArray = new JSONArray();
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + divid + "' and sync_status ='" + 0 + "' order by pill_no", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("id", cursor.getString(cursor.getColumnIndex("id")));
                            json.put("d_id", cursor.getString(cursor.getColumnIndex("d_id")));
                            json.put("r_id", cursor.getString(cursor.getColumnIndex("r_id")));
                            json.put("fb_id", cursor.getString(cursor.getColumnIndex("fb_id")));
                            json.put("pill_no", cursor.getString(cursor.getColumnIndex("pill_no")));
                            json.put("job_id", cursor.getString(cursor.getColumnIndex("job_id")));
                            json.put("u_id", cursor.getString(cursor.getColumnIndex("u_id")));
                            json.put("survey_durn", cursor.getString(cursor.getColumnIndex("survey_durn")));
                            json.put("f_pic_status", cursor.getString(cursor.getColumnIndex("f_pic_status")));
                            json.put("b_pic_status", cursor.getString(cursor.getColumnIndex("b_pic_status")));
                            json.put("i_pic_status", cursor.getString(cursor.getColumnIndex("i_pic_status")));
                            json.put("o_pic_status", cursor.getString(cursor.getColumnIndex("o_pic_status")));
                            json.put("div_pic_status", cursor.getString(cursor.getColumnIndex("div_pic_status")));
                            json.put("patch_no", cursor.getString(cursor.getColumnIndex("patch_no")));
                            json.put("ring_no", cursor.getString(cursor.getColumnIndex("ring_no")));
                            json.put("forest_person", cursor.getString(cursor.getColumnIndex("forest_person")));
                            json.put("surveyor_name", cursor.getString(cursor.getColumnIndex("surveyor_name")));
                            json.put("survey_time", cursor.getString(cursor.getColumnIndex("survey_time")));
                            json.put("div_name", cursor.getString(cursor.getColumnIndex("div_name")));
                            json.put("range_name", cursor.getString(cursor.getColumnIndex("range_name")));
                            json.put("fb_name", cursor.getString(cursor.getColumnIndex("fb_name")));
                            json.put("sync_status", cursor.getString(cursor.getColumnIndex("sync_status")));
                            json.put("ack_status", cursor.getString(cursor.getColumnIndex("ack_status")));
                            json.put("delete_status", cursor.getString(cursor.getColumnIndex("delete_status")));
                            json.put("survey_segment", cursor.getString(cursor.getColumnIndex("survey_segment")));
                            json.put("completion_sts", cursor.getString(cursor.getColumnIndex("completion_sts")));
                            json.put("f_pic_name", cursor.getString(cursor.getColumnIndex("f_pic_name")));
                            json.put("b_pic_name", cursor.getString(cursor.getColumnIndex("b_pic_name")));
                            json.put("i_pic_name", cursor.getString(cursor.getColumnIndex("i_pic_name")));
                            json.put("o_pic_name", cursor.getString(cursor.getColumnIndex("o_pic_name")));
                            json.put("div_pic_name", cursor.getString(cursor.getColumnIndex("div_pic_name")));
                            json.put("device_imei_no", cursor.getString(cursor.getColumnIndex("device_imei_no")));
                            json.put("pillar_sfile_path", cursor.getString(cursor.getColumnIndex("pillar_sfile_path")));
                            json.put("pillar_sfile_status", cursor.getString(cursor.getColumnIndex("pillar_sfile_status")));
                            json.put("frjvc_lat", cursor.getString(cursor.getColumnIndex("frjvc_lat")));
                            json.put("frjvc_long", cursor.getString(cursor.getColumnIndex("frjvc_long")));
                            json.put("d_pill_no", cursor.getString(cursor.getColumnIndex("d_pill_no")));
                            json.put("d_old_id", cursor.getString(cursor.getColumnIndex("d_old_id")));
                            json.put("pillar_rfile_path", cursor.getString(cursor.getColumnIndex("pillar_rfile_path")));
                            json.put("pillar_rfile_status", cursor.getString(cursor.getColumnIndex("pillar_rfile_status")));
                            json.put("completion_status", cursor.getString(cursor.getColumnIndex("completion_status")));
                            json.put("rtx_survey_min", cursor.getString(cursor.getColumnIndex("rtx_survey_min")));
                            json.put("rtx_survey_second", cursor.getString(cursor.getColumnIndex("rtx_survey_second")));
                            json.put("survey_status", cursor.getString(cursor.getColumnIndex("survey_status")));
                            json.put("reason", cursor.getString(cursor.getColumnIndex("reason")));
                            json.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
                            jsonArray.put(json);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }

                    } while (cursor.moveToNext());
                }
                sendDatatoServer(jsonArray);
            } else {
                Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
            }
            cursor.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void sendDatatoServer(JSONArray jsonArray) {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                try {
                    progressDialog1 = new ProgressDialog(this, R.style.MyAlertDialogStyle);
                    progressDialog1.setMessage("Uploading DGPS pillar data to server.....");
                    progressDialog1.show();
                    fp_data = new JSONObject();
                    fp_data.put("fpdata", jsonArray);
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String URL = AllApi.DGPS_D_FB_PILL_DATA_API;
                    requestQueue.getCache().remove(URL);
                    final String requestBody = fp_data.toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // SendImageFile();
                            try {
                                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                Cursor c = db.rawQuery("update m_fb_dgps_survey_pill_data set sync_status='1' where u_id='" + userid + "' and d_id='" + sharediv + "'", null);
                                if (c.getCount() >= 0) {
                                    progressDialog1.dismiss();
                                    Toast.makeText(DGPSSyncMenuActivity.this, "Data Synchronization successfully completed", Toast.LENGTH_SHORT).show();
                                }
                                c.close();
                                db.close();
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            } finally {
                                progressDialog1.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog1.dismiss();

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json; charset=UTF-8");
                            params.put("Authorization", "Bearer " + _token);
                            return params;
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                                // can get more details such as response.headers
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            30000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(DGPSSyncMenuActivity.this, "You do not have Internet Connection", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private boolean checkDataForSynchronization() {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        boolean b = false;
        Cursor cursor = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and sync_status ='" + 0 + "'  and pillar_sfile_path is not null order by pill_no", null);
        try {
            if (cursor.getCount() > 0) {
                b = true;
            } else {
                b = false;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return b;
    }
}
