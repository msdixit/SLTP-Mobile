package com.sparc.frjvcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DGPSDataTaggMenuActivity extends AppCompatActivity {

    SharedPreferences shared;
    String  sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid,jobid,div_name,range_name,fb_name;
    public static final String data = "data";
    ImageView data_view,data_point_dwld;
    TextView dgpsfbName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpsdata_tagg_menu);

        data_view=findViewById(R.id.dataview);
        data_point_dwld=findViewById(R.id.data_point_dwld);

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

        dgpsfbName.setText(fb_name);

        data_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DGPSDataExportActivity.class);
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
                Intent i = new Intent(getApplicationContext(), DFPSRTXDataExportActivity.class);
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
    }
}
