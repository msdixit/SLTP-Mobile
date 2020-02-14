package com.sparc.frjvcapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.sparc.frjvcapp.config.AllApi;
import com.sparc.frjvcapp.pojo.Response1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    public static final String userlogin = "coltlogin";
    public static final String token = "userlogin";
    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    TextView name, design, email, circle, division, totdiv, totrange, totfb, totfp;
    DbHelper dbHelper;
    String divid, userid, path,_token;
    SQLiteDatabase db;
    JSONArray jsonArray;
    JSONObject fp_data;
    ProgressDialog progressDialog1,ProgreesDialog2;
    boolean doubleBackToExitPressedOnce = false;
    ImageView logout;
    private Button record, sync;
    private DbHelper.DatabaseHelper mDbHelper;
    SharedPreferences shared,_shareToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DbHelper(this);
        design = findViewById(R.id.design);
        name = findViewById(R.id.user_profile_name);
        record = findViewById(R.id.record);
        email = findViewById(R.id.email);
        circle = findViewById(R.id.circle);
        division = findViewById(R.id.div);
        //view = findViewById(R.id.view);
        sync = findViewById(R.id.sync);
        totdiv = findViewById(R.id.totdiv);
        totrange = findViewById(R.id.totrange);
        totfb = findViewById(R.id.totfb);
        totfp = findViewById(R.id.totfp);


        SharedPreferences shared = getSharedPreferences(userlogin, MODE_PRIVATE);
        name.setText(shared.getString("uname", "0"));
        design.setText(shared.getString("upos", "0"));
        email.setText(shared.getString("uid", "0"));
        circle.setText(shared.getString("ucir", "0"));
        division.setText(shared.getString("udivname", "0"));
        divid = shared.getString("udivid", "0");
        userid = shared.getString("uemail", "0");

        _shareToken = getSharedPreferences(token, MODE_PRIVATE);
        _token=_shareToken.getString("token","0");

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchMapFile.class);
                startActivity(intent);
                finish();
            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            Cursor cursor,cursor1,cursor2;
            @Override
            public void onClick(View v) {
                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                try {
                    cursor = db.rawQuery("select * from m_pillar_reg where uid='" + userid + "' and d_id='" + divid + "' and p_sts ='" + 0 + "' order by point_no", null);
                    cursor1 = db.rawQuery("select * from m_survey_pillar_reg where uid='" + userid + "' and d_id='" + divid + "' and p_sts ='" + 0 + "' order by point_no", null);
                    cursor2 = db.rawQuery("select * from m_shifting_pillar_reg where uid='" + userid + "' and sync_status ='" + 0 + "'", null);
                    if (cursor.getCount() > 0) {
                        getDataforSync(divid, userid);
                    } else {
                        Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
                    }
                    if (cursor1.getCount() > 0) {

                        getResurveyedPillarData(divid, userid);
                    } else {
                        Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
                    }
                    if (cursor2.getCount() > 0) {
                        getShiftinhDataforSync(userid);
                    } else {
                        Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }finally {
                    cursor.close();
                    cursor1.close();
                    cursor2.close();
                    db.close();

                }
            }
        });
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select count(id) as totufb  from m_pillar_reg where uid='" + shared.getString("uemail", "0") + "' and p_sts='1' and img_status='1'", null);
            // Cursor cursor4 = db.rawQuery("select * from m_pillar_reg where uid='" + shared.getString("uemail", "0") + "'", null);
            Cursor cursor1 = db.rawQuery("select count(distinct r_id) as totrange from m_pillar_reg where uid='" + shared.getString("uemail", "0") + "'", null);
            Cursor cursor2 = db.rawQuery("select count(distinct fb_id) as totfb  from m_pillar_reg where uid='" + shared.getString("uemail", "0") + "'", null);
            Cursor cursor3 = db.rawQuery("select count(id) as totfp from m_pillar_reg where uid='" + shared.getString("uemail", "0") + "' and delete_status='0'", null);
            cursor.moveToFirst();
            cursor1.moveToFirst();
            cursor2.moveToFirst();
            cursor3.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    totdiv.setText(cursor.getString(cursor.getColumnIndex("totufb")));
                } while (cursor.moveToNext());
            }
            if (cursor1.moveToFirst()) {
                do {
                    totrange.setText(cursor1.getString(cursor1.getColumnIndex("totrange")));
                } while (cursor1.moveToNext());
            }
            if (cursor2.moveToFirst()) {
                do {
                    totfb.setText(cursor2.getString(cursor2.getColumnIndex("totfb")));
                } while (cursor2.moveToNext());
            }
            if (cursor3.moveToFirst()) {
                do {
                    totfp.setText(cursor3.getString(cursor3.getColumnIndex("totfp")));
                } while (cursor3.moveToNext());
            }
            cursor.close();
            cursor1.close();
            cursor2.close();
            cursor3.close();
            db.close();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    //Toast.makeText(addAlarm.this, "Permission denied to access your location.", Toast.LENGTH_SHORT).show();
                }
            }
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        //url = editTextUrl.getText().toString();
        //new DownloadFile().execute(url);
        getCMVMMVData();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Log.d(TAG, "Permission has been denied");
    }

    public void getCMVMMVData() {
        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVMMVFiles(divid);
            for (int i = 0; i < mfb.size(); i++) {

                new DownloadFile().execute(AllApi.F_KML_API + mfb.get(i));

            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void getDataforSync(String divid, String userid) {
    try {
        jsonArray = new JSONArray();
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from m_pillar_reg where uid='" + userid + "' and d_id='" + divid + "' and p_sts ='" + 0 + "' order by point_no", null);
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            if (cursor.moveToFirst()) {

                //progressDialog1 = ProgressDialog.show(UserProfileActivity.this, "", "Uploading files to server.....", false);
                do {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("id", cursor.getString(cursor.getColumnIndex("point_no")));
                        json.put("d_id", cursor.getString(cursor.getColumnIndex("d_id")));
                        json.put("r_id", cursor.getString(cursor.getColumnIndex("r_id")));
                        json.put("fb_id", cursor.getString(cursor.getColumnIndex("fb_id")));
                        json.put("p_sl_no", cursor.getString(cursor.getColumnIndex("p_sl_no")));
                        json.put("p_lat", cursor.getString(cursor.getColumnIndex("p_lat")));
                        json.put("p_long", cursor.getString(cursor.getColumnIndex("p_long")));
                        json.put("p_type", cursor.getString(cursor.getColumnIndex("p_type")));
                        json.put("p_cond", cursor.getString(cursor.getColumnIndex("p_cond")));
                        json.put("p_rmk", cursor.getString(cursor.getColumnIndex("p_rmk")));
                        json.put("p_pic", cursor.getString(cursor.getColumnIndex("p_pic")));
                        json.put("patch_no", cursor.getString(cursor.getColumnIndex("patch_no")));
                        json.put("ring_no", cursor.getString(cursor.getColumnIndex("ring_no")));
                        json.put("p_loc_type", cursor.getString(cursor.getColumnIndex("p_loc_type")));
                        json.put("p_no", cursor.getString(cursor.getColumnIndex("p_no")));
                        json.put("p_paint_status", cursor.getString(cursor.getColumnIndex("p_paint_status")));
                        json.put("fb_name", cursor.getString(cursor.getColumnIndex("fb_name")));
                        json.put("uid", cursor.getString(cursor.getColumnIndex("uid")));
                        json.put("img_status", cursor.getString(cursor.getColumnIndex("img_status")));
                        json.put("delete_status", cursor.getString(cursor.getColumnIndex("delete_status")));
                        json.put("shifting_status", cursor.getString(cursor.getColumnIndex("shifting_status")));
                        json.put("survey_dir", cursor.getString(cursor.getColumnIndex("surv_direction")));
                        json.put("accuracy", cursor.getString(cursor.getColumnIndex("p_accuracy")));
                        json.put("survey_dt", cursor.getString(cursor.getColumnIndex("survey_dt")));
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
    }catch (Exception ee)
    {
        ee.printStackTrace();
    }

    }
    private void getResurveyedPillarData(String divid, String userid) {
    try {
        jsonArray = new JSONArray();
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from m_survey_pillar_reg where uid='" + userid + "' and d_id='" + divid + "' and p_sts ='" + 0 + "' order by point_no", null);
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                /*progressDialog1 = new ProgressDialog(this, R.style.MyAlertDialogStyle);
                progressDialog1.setMessage("Uploading files to Server.....");
                progressDialog1.show();*/
                //progressDialog1 = ProgressDialog.show(UserProfileActivity.this, "", "Uploading files to server.....", false);
                do {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("id", cursor.getString(cursor.getColumnIndex("point_no")));
                        json.put("d_id", cursor.getString(cursor.getColumnIndex("d_id")));
                        json.put("r_id", cursor.getString(cursor.getColumnIndex("r_id")));
                        json.put("fb_id", cursor.getString(cursor.getColumnIndex("fb_id")));
                        json.put("p_sl_no", cursor.getString(cursor.getColumnIndex("p_sl_no")));
                        json.put("p_lat", cursor.getString(cursor.getColumnIndex("p_lat")));
                        json.put("p_long", cursor.getString(cursor.getColumnIndex("p_long")));
                        json.put("p_type", cursor.getString(cursor.getColumnIndex("p_type")));
                        json.put("p_cond", cursor.getString(cursor.getColumnIndex("p_cond")));
                        json.put("p_rmk", cursor.getString(cursor.getColumnIndex("p_rmk")));
                        json.put("p_pic", cursor.getString(cursor.getColumnIndex("p_pic")));
                        json.put("patch_no", cursor.getString(cursor.getColumnIndex("patch_no")));
                        json.put("ring_no", cursor.getString(cursor.getColumnIndex("ring_no")));
                        json.put("p_loc_type", cursor.getString(cursor.getColumnIndex("p_loc_type")));
                        json.put("p_no", cursor.getString(cursor.getColumnIndex("p_no")));
                        json.put("p_paint_status", cursor.getString(cursor.getColumnIndex("p_paint_status")));
                        json.put("fb_name", cursor.getString(cursor.getColumnIndex("fb_name")));
                        json.put("uid", cursor.getString(cursor.getColumnIndex("uid")));
                        json.put("img_status", cursor.getString(cursor.getColumnIndex("img_status")));
                        json.put("delete_status", cursor.getString(cursor.getColumnIndex("delete_status")));
                        json.put("shifting_status", cursor.getString(cursor.getColumnIndex("shifting_status")));
                        json.put("past_lat", cursor.getString(cursor.getColumnIndex("past_lat")));
                        json.put("past_long", cursor.getString(cursor.getColumnIndex("past_long")));
                        json.put("survey_dir", cursor.getString(cursor.getColumnIndex("surv_direction")));
                        json.put("accuracy", cursor.getString(cursor.getColumnIndex("p_accuracy")));
                        json.put("survey_dt", cursor.getString(cursor.getColumnIndex("survey_dt")));
                        jsonArray.put(json);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                } while (cursor.moveToNext());

            }
            sendOtherSurveyDatatoServer(jsonArray);
        } else {
            Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
        }
        cursor.close();
        db.close();

    }catch (Exception ee)
    {
        ee.printStackTrace();
    }

    }
    private void getShiftinhDataforSync(String userid) {
        try {
            jsonArray = new JSONArray();
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from m_shifting_pillar_reg where uid='" + userid + "' and sync_status ='" + 0 + "'", null);
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
               /* ProgreesDialog2 = new ProgressDialog(this , R.style.MyAlertDialogStyle);
                ProgreesDialog2.setMessage("Uploading files to Server.....");
                ProgreesDialog2.show();*/
                    //ProgreesDialog2 = ProgressDialog.show(UserProfileActivity.this, "", "Uploading files to server.....", false);
                    do {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("slat", cursor.getString(cursor.getColumnIndex("s_lat")));
                            json.put("slong", cursor.getString(cursor.getColumnIndex("s_long")));
                            json.put("sremark", cursor.getString(cursor.getColumnIndex("s_rmk")));
                            json.put("spic", cursor.getString(cursor.getColumnIndex("s_pic")));
                            json.put("spicstatus", cursor.getString(cursor.getColumnIndex("simg_status")));
                            json.put("sfbname", cursor.getString(cursor.getColumnIndex("fb_name")));
                            json.put("suid", cursor.getString(cursor.getColumnIndex("uid")));
                            json.put("sfbid", cursor.getString(cursor.getColumnIndex("fb_id")));
                            json.put("spno", cursor.getString(cursor.getColumnIndex("p_no")));
                            json.put("ssyncsts", cursor.getString(cursor.getColumnIndex("sync_status")));
                            json.put("sdelsts", cursor.getString(cursor.getColumnIndex("sdelete_status")));
                            jsonArray.put(json);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }

                    } while (cursor.moveToNext());

                }
            } else {
                Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
            }
            cursor.close();
            db.close();
            sendShiftingDatatoServer(jsonArray);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
    public void sendDatatoServer(JSONArray jsonArray) {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                try {
                    progressDialog1 = new ProgressDialog(this, R.style.MyAlertDialogStyle);
                    progressDialog1.setMessage("Uploading files to Server.....");
                    progressDialog1.show();
                    fp_data = new JSONObject();
                    fp_data.put("fpdata", jsonArray);
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String URL = AllApi.F_UDT_PILL_DATA;
                    requestQueue.getCache().remove(URL);
                    final String requestBody = fp_data.toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // SendImageFile();
                            try {
                                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                Cursor c = db.rawQuery("update m_pillar_reg set p_sts='1' where uid='" + userid + "' and d_id='" + divid + "'", null);
                                if (c.getCount() >= 0) {
                                    progressDialog1.dismiss();
                                    Toast.makeText(UserProfileActivity.this, "Data Synchronization successfully completed", Toast.LENGTH_SHORT).show();
                                }
                                c.close();
                                db.close();
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                            finally {
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
                            params.put("Authorization", "Bearer "+_token);
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
                Toast.makeText(UserProfileActivity.this, "You do not have Internet Connection", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
    public void sendOtherSurveyDatatoServer(JSONArray jsonArray) {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                try {
                    progressDialog1 = new ProgressDialog(this, R.style.MyAlertDialogStyle);
                    progressDialog1.setMessage("Uploading files to Server.....");
                    progressDialog1.show();
                    fp_data = new JSONObject();
                    fp_data.put("fpdata", jsonArray);
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String URL = AllApi.F_CFM_UDT_PILL_DATA;
                    requestQueue.getCache().remove(URL);
                    final String requestBody = fp_data.toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // SendImageFile();
                            try {
                                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                Cursor c = db.rawQuery("update m_survey_pillar_reg set p_sts='1' where uid='" + userid + "' and d_id='" + divid + "'", null);
                                if (c.getCount() >= 0) {
                                    progressDialog1.dismiss();
                                    Toast.makeText(UserProfileActivity.this, "Data Synchronization successfully completed", Toast.LENGTH_SHORT).show();
                                }
                                c.close();
                                db.close();
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json; charset=UTF-8");
                            params.put("Authorization", "Bearer "+_token);
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
                Toast.makeText(UserProfileActivity.this, "You do not have Internet Connection", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
    public void sendShiftingDatatoServer(JSONArray jsonArray) {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                try {
                    progressDialog1 = new ProgressDialog(this, R.style.MyAlertDialogStyle);
                    progressDialog1.setMessage("Uploading files to Server.....");
                    progressDialog1.show();
                    fp_data = new JSONObject();
                    fp_data.put("sfpdata", jsonArray);
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String URL = AllApi.F_SFT_FRJVC_PILL_DATA;
                    requestQueue.getCache().remove(URL);
                    final String requestBody = fp_data.toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // SendImageFile();
                            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                            Cursor c = db.rawQuery("update m_shifting_pillar_reg set sync_status='1' where uid='" + userid + "'", null);
                            if (c.getCount() >= 0) {
                                progressDialog1.dismiss();
                                Toast.makeText(UserProfileActivity.this, "Data Synchronization successfully completed", Toast.LENGTH_SHORT).show();
                            }
                            c.close();
                            db.close();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json; charset=UTF-8");
                            params.put("Authorization", "Bearer "+_token);
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
                Toast.makeText(UserProfileActivity.this, "You do not have Internet Connection", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainContainerActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

   /* private void uploadImage(byte[] imageBytes, final String imgname) {
        progress = new progressdialog(userprofileactivity.this, r.style.mytheme);
        progress.setcancelable(false);
        progress.setprogressstyle(android.r.style.widget_holo_progressbar);
        progress.show();
        try {
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
                            int aa = c.getCount();
                            if (c.getCount() >= 0) {
                                Response1 responseBody = response.body();
                                Toast.makeText(UserProfileActivity.this, responseBody.getPath(), Toast.LENGTH_SHORT).show();
                            }
                            c.close();
                            db.close();
                            //SendImageFile();
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
                Toast.makeText(UserProfileActivity.this, "Internet Connection is Not Available", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }*/
   /* public void SendImageFile() {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor co = db.rawQuery("update m_pillar_reg set img_status='0' where uid='" + userid + "' and d_id='" + divid + "'", null);
        Cursor c = db.rawQuery("select * from m_pillar_reg where d_id='" + divid + "' and uid='" + userid + "' and img_status='0' ", null);
        int count = c.getCount();
        while (c.moveToNext()) {
            try {
                uploadImage(Utility.getByeArr(Utility.setPic(c.getString(c.getColumnIndex("p_pic")))), c.getString(c.getColumnIndex("p_pic")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();
        co.close();
        db.close();
    }*/

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(UserProfileActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
                File directory = getExternalFilesDir(null);
                String folder = directory.getAbsolutePath();

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                OutputStream output = new FileOutputStream(folder + "/" + fileName);

                byte[] data = new byte[16384];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }

}

