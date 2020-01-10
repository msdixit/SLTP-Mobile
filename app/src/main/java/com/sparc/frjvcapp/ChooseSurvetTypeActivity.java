package com.sparc.frjvcapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.sparc.frjvcapp.pojo.M_dgpssurvey_pillar_data;
import com.sparc.frjvcapp.pojo.M_survey_pillar_data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ChooseSurvetTypeActivity extends AppCompatActivity {
    SharedPreferences shared;
    String  sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid,jobid,div_name,range_name,fb_name,spinner_duration,spinner_segment;
    public static final String data = "data";
    ImageView data_collect,data_view,data_export,data_sync,data_point_dwld,mapview;
    TextView dgpsfbName;
    SQLiteDatabase db;
    DbHelper dbHelper;
    private ProgressDialog progressDialog;
    JSONArray jsonArray;
    JSONObject fp_data;
    ProgressDialog progressDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survet_type);


        /*data_collect=findViewById(R.id.datacollect);*/
        data_view=findViewById(R.id.dataview);
        data_export=findViewById(R.id.exporttofolder);
        data_sync=findViewById(R.id.synchronize);
        data_point_dwld=findViewById(R.id.data_point_dwld);
        mapview=findViewById(R.id.mapview);
        dgpsfbName=findViewById(R.id.dgpsfbName);

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
        dbHelper = new DbHelper(this);

        dgpsfbName.setText(fb_name);

        data_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DGPSDataViewActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences(data, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("fbrangecode", sharerange);
                editor.putString("fbdivcode", sharediv);
                editor.putString("fbcode", sharefb);
                editor.putString("fbtype", sharefbtype);
                editor.putString("fbname", sharefbname);
                editor.putString("userid", userid);
                editor.putString("jobid", jobid);
                editor.putString("div_name", div_name);
                editor.putString("range_name", range_name);
                editor.putString("fb_name", fb_name);
                editor.apply();
                startActivity(i);
            }
        });
        data_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DGPSDataTaggMenuActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences(data, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("fbrangecode", sharerange);
                editor.putString("fbdivcode", sharediv);
                editor.putString("fbcode", sharefb);
                editor.putString("fbtype", sharefbtype);
                editor.putString("fbname", sharefbname);
                editor.putString("userid", userid);
                editor.putString("jobid", jobid);
                editor.putString("div_name", div_name);
                editor.putString("range_name", range_name);
                editor.putString("fb_name", fb_name);
                editor.apply();
                startActivity(i);
            }
        });
        data_point_dwld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckDataAvalability(sharefb)) {
                    getDataForSurveyPoints(sharefb);
                }else{

                }
            }
        });
        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DGPSMapViewActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences(data, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("fbrangecode", sharerange);
                editor.putString("fbdivcode", sharediv);
                editor.putString("fbcode", sharefb);
                editor.putString("fbtype", sharefbtype);
                editor.putString("fbname", sharefbname);
                editor.putString("userid", userid);
                editor.putString("jobid", jobid);
                editor.putString("div_name", div_name);
                editor.putString("range_name", range_name);
                editor.putString("fb_name", fb_name);
                editor.apply();
                startActivity(i);
            }
        });
        data_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DGPSSyncMenuActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences(data, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("fbrangecode", sharerange);
                editor.putString("fbdivcode", sharediv);
                editor.putString("fbcode", sharefb);
                editor.putString("fbtype", sharefbtype);
                editor.putString("fbname", sharefbname);
                editor.putString("userid", userid);
                editor.putString("jobid", jobid);
                editor.putString("div_name", div_name);
                editor.putString("range_name", range_name);
                editor.putString("fb_name", fb_name);
                editor.apply();
                startActivity(i);
               /*if(checkDataForSynchronization())
               {
                   BindDGPSData(sharediv,userid);

               }else{
                   Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
               }*/
            }
        });
    }

    /*private void BindDGPSData(String divid, String userid) {
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
                    String URL = "http://odishaforestlandsurvey.in/api/values/addDGPSpillar";
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
                                    Toast.makeText(ChooseSurvetTypeActivity.this, "Data Synchronization successfully completed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChooseSurvetTypeActivity.this, "You do not have Internet Connection", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    private boolean checkDataForSynchronization() {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        boolean b=false;
        Cursor cursor = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and sync_status ='" + 0 + "' order by pill_no", null);
        try {
            if (cursor.getCount() > 0) {
                b = true;
            } else {
                b = true;
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }finally {
            cursor.close();
            db.close();
        }
        return b;
    }*/

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), SelectFBForDGPSActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
    private boolean CheckDataAvalability(String sharefb) {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        boolean b=false;
        Cursor cursor = db.rawQuery("select * from m_dgps_Survey_pill_data where m_fb_id='"+sharefb+"'", null);
        try {
            if (cursor.getCount() > 0) {
                db.execSQL("delete from m_dgps_Survey_pill_data where m_fb_id='"+sharefb+"'");
                b = true;
            } else {
                b = true;
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }finally {
            cursor.close();
            db.close();
        }
        return b;
    }
    private void getDataForSurveyPoints(String fbid) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = "http://14.98.253.212/sltp/api/values/getPillarPointDetails/" + fbid;
            progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
            progressDialog.setMessage("Please wait...Your Point data is downloading");
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            M_dgpssurvey_pillar_data m_fb = new M_dgpssurvey_pillar_data(object.getString("latitude"), object.getString("longitude"), object.getString("pillar_no"), "", fbid, object.getString("status"),"0","0",object.getString("id"));//object.getString("point_path")
                            dbHelper.open();
                            dbHelper.insertdgpsSurveyedPointDataData(m_fb);
                            dbHelper.close();

                        }
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "The Survey Point Data is downloaded.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "you have no points.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data is not available in Server", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
