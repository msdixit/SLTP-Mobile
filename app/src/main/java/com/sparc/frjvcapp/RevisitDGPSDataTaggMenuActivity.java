package com.sparc.frjvcapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RevisitDGPSDataTaggMenuActivity extends AppCompatActivity {

    SharedPreferences shared;
    String sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid, jobid, div_name, range_name, fb_name;
    public static final String data = "data";
    ImageView tagStatic, tagRtx, tag_jxl_file;
    TextView dgpsfbName;
    private SQLiteDatabase db;
    public HashMap<String, String> fbKey;
    TextView txt_fb_name, txt_fb_id;
    String fbid;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revisit_dgpsdata_tagg_menu);

        tagStatic = findViewById(R.id.tagStatic);
        tagRtx = findViewById(R.id.tagRtx);
        tag_jxl_file = findViewById(R.id.jxl_file_tag);

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

        dgpsfbName.setText(fb_name);

        tagStatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RevisitDGPSStaticDataExportActivity.class);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        tagRtx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int completion_Status = getDGPSForestBlockData(sharefb);
                if(completion_Status!=-1) {
                    if (completion_Status>=0) {
                        final View customLayout = getLayoutInflater().inflate(R.layout.add_completion_ststus, null);
                        alertDialogBuilder.setView(customLayout);
                        txt_fb_name = customLayout.findViewById(R.id.txtViewdiv);
                        txt_fb_id = customLayout.findViewById(R.id.txtViewran);

                        txt_fb_id.setText(sharefb);
                        txt_fb_name.setText(fb_name);

                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {
                                            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                            c = db.rawQuery("update m_fb_revisit_dgps_survey_pill_data set completion_status='1' where fb_id='" + sharefb + "'", null);
                                            if (c.getCount() >= 0) {
                                                try {
                                                    Intent i = new Intent(getApplicationContext(), RevisitDGPSRTXDataExportActivity.class);
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
                                                } catch (Exception ee) {
                                                    ee.printStackTrace();
                                                } finally {

                                                }
                                            }
                                            c.close();
                                            db.close();
                                        } catch (Exception ee) {
                                            ee.printStackTrace();
                                        }
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // Toast.makeText(getApplicationContext(), "You canceled the request...please try again", Toast.LENGTH_LONG).show();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        Intent i = new Intent(getApplicationContext(), RevisitDGPSRTXDataExportActivity.class);
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
                }else{
                    Toast.makeText(getApplicationContext(),"No Data available for tagging RTX",Toast.LENGTH_LONG).show();
                }
            }
        });
        tag_jxl_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int completion_Status = getDGPSForestBlockDataForJXL(sharefb);
                if (completion_Status !=-1) {
                    if (completion_Status>=0) {
                        final View customLayout = getLayoutInflater().inflate(R.layout.add_completion_ststus, null);
                        alertDialogBuilder.setView(customLayout);
                        txt_fb_name = customLayout.findViewById(R.id.txtViewdiv);
                        txt_fb_id = customLayout.findViewById(R.id.txtViewran);

                        txt_fb_id.setText(sharefb);
                        txt_fb_name.setText(fb_name);

                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {
                                            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                            c = db.rawQuery("update m_fb_revisit_dgps_survey_pill_data set completion_status='2' where fb_id='" + sharefb + "'", null);
                                            if (c.getCount() >= 0) {
                                                try {
                                                    Intent i = new Intent(getApplicationContext(), RevisitDGPSJXLDataExportActivity.class);
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
                                                } catch (Exception ee) {
                                                    ee.printStackTrace();
                                                } finally {

                                                }
                                            }
                                            c.close();
                                            db.close();
                                        } catch (Exception ee) {
                                            ee.printStackTrace();
                                        }
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // Toast.makeText(getApplicationContext(), "You canceled the request...please try again", Toast.LENGTH_LONG).show();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    else {
                        Intent i = new Intent(getApplicationContext(), RevisitDGPSJXLDataExportActivity.class);
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
                }else{
                    Toast.makeText(getApplicationContext(),"No Data available for tagging JOB",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private int getDGPSForestBlockData(String fb_ID) {
        int status=-1;
        //rangeName.add("Select Range");
        fbKey = new HashMap<>();
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            db.rawQuery("update m_fb_revisit_dgps_survey_pill_data set completion_status='0' where fb_id='" + fb_ID + "'", null);
            Cursor cursor = db.rawQuery("select distinct completion_status from m_fb_revisit_dgps_survey_pill_data where d_id='" + sharediv + "' and r_id ='" + sharerange + "' and fb_id='" + fb_ID + "' order by fb_name", null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("completion_status")));

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return status;
    }
    private int getDGPSForestBlockDataForJXL(String fb_ID) {
        int status=-1;
        //rangeName.add("Select Range");
        fbKey = new HashMap<>();
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            db.rawQuery("update m_fb_revisit_dgps_survey_pill_data set completion_status='0' where fb_id='" + fb_ID + "'", null);
            Cursor cursor = db.rawQuery("select distinct completion_status from m_fb_revisit_dgps_survey_pill_data where d_id='" + sharediv + "' and r_id ='" + sharerange + "' and fb_id='" + fb_ID + "' order by fb_name", null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("completion_status")));

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return status;
    }
}
