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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class DGPSDataTaggMenuActivity extends AppCompatActivity {

    SharedPreferences shared;
    String sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid, jobid, div_name, range_name, fb_name;
    public static final String data = "data";
    ImageView data_view, data_point_dwld;
    TextView dgpsfbName;
    private SQLiteDatabase db;
    public HashMap<String, String> fbKey;
    ArrayAdapter<String> dataAdapter = null;
    TextView txt_fb_name,txt_fb_id;
    String spill_no, fbid,c_status;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpsdata_tagg_menu);

        data_view = findViewById(R.id.dataview);
        data_point_dwld = findViewById(R.id.data_point_dwld);

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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        data_point_dwld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String completion_Status=getDGPSForestBlockData(sharefb);
                if(completion_Status.equals("0")) {
                    final View customLayout = getLayoutInflater().inflate(R.layout.add_completion_ststus, null);
                    alertDialogBuilder.setView(customLayout);
                    txt_fb_name = customLayout.findViewById(R.id.txtViewdiv);
                    txt_fb_id = customLayout.findViewById(R.id.txtViewran);

                    txt_fb_id.setText(sharefb);
                    txt_fb_name.setText(fb_name);

                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    try {
                                        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                                        c = db.rawQuery("update m_fb_dgps_survey_pill_data set completion_status='1' where fb_id='" + sharefb + "'", null);
                                        if (c.getCount() >= 0) {
                                            try {
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

                    alertDialogBuilder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // Toast.makeText(getApplicationContext(), "You canceled the request...please try again", Toast.LENGTH_LONG).show();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else{
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
        }
    });
    }


    private String getDGPSForestBlockData(String fb_ID) {
        List<String> fbName = new ArrayList<String>();
        //rangeName.add("Select Range");
        fbKey = new HashMap<>();
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            db.rawQuery("update m_fb_dgps_survey_pill_data set completion_status='0' where fb_id='" + fb_ID + "'", null);
            Cursor cursor = db.rawQuery("select distinct completion_status from m_fb_dgps_survey_pill_data where d_id='" + sharediv + "' and r_id ='" + sharerange + "' and fb_id='" + fb_ID + "' order by fb_name", null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    c_status = cursor.getString(cursor.getColumnIndex("completion_status"));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    return c_status;
    }
}
