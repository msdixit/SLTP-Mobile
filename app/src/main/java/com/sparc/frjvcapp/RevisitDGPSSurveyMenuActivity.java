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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sparc.frjvcapp.pojo.M_dgps_revisit_pillar_data_dwnld;
import com.sparc.frjvcapp.pojo.M_dgpssurvey_pillar_data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RevisitDGPSSurveyMenuActivity extends AppCompatActivity {
    SharedPreferences shared, _shareToken;
    String sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid, jobid, div_name, range_name, fb_name, _token, spinner_duration, spinner_segment;
    public static final String data = "data";
    public static final String userlogin = "userlogin";
    ImageView data_collect, data_view, data_export, data_sync, data_point_dwld, mapview;
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
        setContentView(R.layout.activity_revisit_dgpssurvey_menu);

        data_view = findViewById(R.id.dataview);
        data_export = findViewById(R.id.exporttofolder);
        data_sync = findViewById(R.id.synchronize);
        data_point_dwld = findViewById(R.id.data_point_dwld);
        mapview = findViewById(R.id.mapview);
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
        dbHelper = new DbHelper(this);

        dgpsfbName.setText(fb_name);

        data_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RevisitDGPSDataViewActivity.class);
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
                Intent i = new Intent(getApplicationContext(), RevisitDGPSDataTaggMenuActivity.class);
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
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                    if (CheckDataAvalability(sharefb)) {
                        getDataForSurveyPoints(sharefb);
                    } else {

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RevisitDGPSMapViewActivity.class);
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
                Intent i = new Intent(getApplicationContext(), RevisitDGPSSyncMenuActivity.class);
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
    private boolean CheckDataAvalability(String sharefb) {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        boolean b=false;
        Cursor cursor = db.rawQuery("select * from m_revisit_dgps_download_data where m_fb_id='"+sharefb+"'", null);
        try {
            if (cursor.getCount() > 0) {
                db.execSQL("delete from m_revisit_dgps_download_data where m_fb_id='"+sharefb+"'");
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
            String URL =  BuildConfig.R_DGPS_PILL_DOWNLOAD+ fbid;
            progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
            progressDialog.setMessage("Please wait...Your Revisit Point data is downloading");
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            M_dgps_revisit_pillar_data_dwnld m_fb = new M_dgps_revisit_pillar_data_dwnld(object.getString("lat"), object.getString("longi"), object.getString("pillar_no"), "", fbid, object.getString("status"),"0","0",object.getString("point_id"),"0");//object.getString("point_path")
                            dbHelper.open();
                            dbHelper.insertdgpsRevisitSurveyedPointDataData(m_fb);
                            dbHelper.close();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "The Survey Point Data is downloaded.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "You don't have any points", Toast.LENGTH_SHORT).show();
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
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

