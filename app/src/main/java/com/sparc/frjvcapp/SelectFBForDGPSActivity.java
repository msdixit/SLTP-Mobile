package com.sparc.frjvcapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class SelectFBForDGPSActivity extends AppCompatActivity {
    public static final String userlogin = "userlogin";
    public static final String data = "data";
    public HashMap<String, String> fbKey;
    public HashMap<String, String> rangeKey;
    ArrayList<String> arrdiv = new ArrayList<String>();
    ArrayList<String> maprange = new ArrayList<String>();
    MaterialSpinner division, range, fb;
    String divValue, rangeValue, fbValue, divid, forbname, fbid, userid, divCode, locationRange, rangeCode, vanName, vanValue, foresttype;
    //private TextInputEditText txtdivision;
    Button submit, download;
    DbHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<String> array_list;
    boolean cmvsta, mmvsta, cmvavls, mmvavls;
    boolean cmvsta1, mmvsta1, cmvavls1, mmvavls1, state, circle, ran, div;
    ProgressDialog progressDialog1;
    ArrayAdapter<String> divadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_fbfor_dgps);

        dbHelper = new DbHelper(this);

        division = findViewById(R.id.division);
        range = findViewById(R.id.range);
        fb = findViewById(R.id.fb);
        submit = findViewById(R.id.submit);
        download = findViewById(R.id.download);


        SharedPreferences shared = getSharedPreferences(userlogin, MODE_PRIVATE);
        arrdiv.add(shared.getString("udivname", "0"));
        divid = shared.getString("udivid", "0");
        userid = shared.getString("uemail", "0");
        //Division.
        //name.setText(shared.getString("uname", "0"));
        //dept.setText(shared.getString("upos", "0"));
        //division.setSelection();

        divadapter = new ArrayAdapter<String>(this, R.layout.spinner_row, arrdiv);
        division.setAdapter(divadapter);
        division.setPaddingSafe(0, 0, 0, 0);
        division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                divValue = (String) parent.getItemAtPosition(position);
                if (!divValue.equals("Select Division")) {
                    //componentMaster();
                    getRangeDetails(divid);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rangeValue = (String) parent.getItemAtPosition(position);
                rangeCode = rangeKey.get(rangeValue);
                if (!rangeValue.equals("Select Range")) {
                    //componentMaster();
                    // getFBData(rangeCode);
                    getFBDetails(rangeCode, divid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fbValue = (String) parent.getItemAtPosition(position);
                fbid = fbKey.get(fbValue);
                if (!fbValue.equals("Select Forest Block")) {
                    //componentMaster();
                    getForestType(rangeCode, divid, fbid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (divValue.equalsIgnoreCase("Select Division")) {
                    //Snackbar.make(view,"Select Project",Snackbar.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar.make(view, "Select Division", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    snackbar.show();
                } else if (rangeValue.equalsIgnoreCase("Select Range")) {
                    //Snackbar.make(view,"Select Component",Snackbar.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar.make(view, "Select Range", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    snackbar.show();
                } else if (fbValue.equalsIgnoreCase("Select Forest Block")) {
                    Snackbar.make(view, "", Snackbar.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar.make(view, "Select Forest Block", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    snackbar.show();
                } else {
                    String jobid=getJobID(divid,rangeCode,fbid,foresttype);
                    if(jobid!="") {
                        Intent i = new Intent(getApplicationContext(), DGPSDataCollectActivity.class);
                        SharedPreferences sharedPreferences = getSharedPreferences(data, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("fbrangecode", rangeCode);
                        editor.putString("fbdivcode", divid);
                        editor.putString("fbcode", fbid);
                        editor.putString("fbtype", foresttype);
                        editor.putString("fbname", forbname);
                        editor.putString("userid", userid);
                        editor.putString("jobid", jobid);
                        editor.apply();
                        startActivity(i);
                    }else {
                        Snackbar.make(view, "", Snackbar.LENGTH_LONG).show();
                        Snackbar snackbar = Snackbar.make(view, "Please check the List.", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                        snackbar.show();
                    }
                }
            }
        });
    }
    private void getForestType(String rangeCode, String divid, String fbid) {
        array_list = new ArrayList<String>();
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from m_fb where div_id='" + divid + "' and  m_fb_range_id ='" + rangeCode + "' and m_fb_id ='" + fbid + "'", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                foresttype = cursor.getString(cursor.getColumnIndex("fb_type"));
                forbname = cursor.getString(cursor.getColumnIndex("m_fb_name"));
                array_list.add(cursor.getString(cursor.getColumnIndex("m_fb_cmv_path")));
                array_list.add(cursor.getString(cursor.getColumnIndex("m_fb_mmv_path")));
                ///Toast.makeText(this,foresttype,Toast.LENGTH_SHORT);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

    }

    private void getRangeDetails(String divCode) {
        //SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_SHARED, 0);
        List<String> rangeName = new ArrayList<String>();
        //rangeName.add("Select Range");
        rangeKey = new HashMap<>();
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from m_range where d_id='" + divCode + "' order by r_name", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                rangeName.add(cursor.getString(cursor.getColumnIndex("r_name")));
                rangeKey.put(cursor.getString(cursor.getColumnIndex("r_name")),
                        cursor.getString(cursor.getColumnIndex("r_id")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, rangeName);
        dataAdapter.setDropDownViewResource(R.layout.spinner_row);
        range.setAdapter(dataAdapter);
        range.setPaddingSafe(0, 0, 0, 0);
    }

    private void getFBDetails(String rangeCode, String divCode) {
        //SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_SHARED, 0);
        List<String> fbName = new ArrayList<String>();
        //rangeName.add("Select Range");
        fbKey = new HashMap<>();
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from m_fb where div_id='" + divCode + "' and  m_fb_range_id ='" + rangeCode + "'order by m_fb_name", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                fbName.add(cursor.getString(cursor.getColumnIndex("m_fb_name")));
                fbKey.put(cursor.getString(cursor.getColumnIndex("m_fb_name")),
                        cursor.getString(cursor.getColumnIndex("m_fb_id")));


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, fbName);
        dataAdapter.setDropDownViewResource(R.layout.spinner_row);
        fb.setAdapter(dataAdapter);
        fb.setPaddingSafe(0, 0, 0, 0);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainContainerActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
    private String getJobID(String divis,String rangeid,String fbid,String type) {
        String JobID;
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        JobID=divis+rangeid+fbid+type;
        return JobID;
    }

}
