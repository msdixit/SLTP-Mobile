package com.sparc.frjvcapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    String divValue, rangeValue, fbValue, divid, forbname, fbid, userid, _token, locationRange, rangeCode, vanName, vanValue, foresttype;
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
        _token=shared.getString("token","0");

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
                fbid = fbKey.get(fbValue.substring(0, fbValue.lastIndexOf(" ")));
                if (!fbValue.equals("Select Forest Block")) {
                    //componentMaster();
                    getForestType(rangeCode, divid, fbid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
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
                    if(_token!="") {
                        checkCMVData();
                        //checkOtherData(); && state == true && circle == true && ran == true && div == true
                        if (cmvsta1 == true) {
                            DeleteRestoreData();
                        } else if (cmvsta1 == false) {
                            DownloadCMVMMVFiles();
                        }
                    }else{
                        Toast.makeText(SelectFBForDGPSActivity.this,"Invalid User access,You cannot use this module",Toast.LENGTH_LONG).show();
                    }
                }
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
                        Intent i = new Intent(getApplicationContext(), ChooseSurvetTypeActivity.class);
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
                        editor.putString("div_name", divValue);
                        editor.putString("range_name", rangeValue);
                        editor.putString("fb_name", fbValue);
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
                fbName.add(cursor.getString(cursor.getColumnIndex("m_fb_name"))+ " " + cursor.getString(cursor.getColumnIndex("fb_type")));
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
    private void checkCMVData() {
        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVMMVFiles(divid, rangeCode, fbid);
            String path = GetFilePath();
            for (int i = 0; i < mfb.size(); i++) {
                if (!mfb.get(i).equals("null")) {
                    // getCMVData(mfb.get(i));
                    File f = new File(path + "/" + mfb.get(i));
                    if (i == 0) {
                        if (f.exists()) {
                            cmvsta1 = true;
                        } else {
                            cmvsta1 = false;
                        }
                    }
                } else {
                    cmvavls1 = false;
                }
                //progressDialog1.dismiss();
            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    private String GetFilePath() {
        File directory = getExternalFilesDir(null);
        String folder = directory.getAbsolutePath();
        return folder;
    }
    private void DeleteRestoreData() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.download_custom_layout, null);
        alertDialogBuilder.setView(customLayout);
        //alertDialogBuilder.setMessage("You already have CMV and MMV files for " + forbname + " .If you still want to download press Ok otherwise press Cancel.");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        DeleteCMVMMVData();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void DeleteCMVMMVData() {
        //ArrayList<String> mfb = dbHelper.getCMVMMVFiles(divid,rangeCode,fbid);
        String path = GetFilePath();
        File dir = new File(path);
        for (File file : dir.listFiles())
            if (!file.isDirectory())
                file.delete();
        DownloadCMVMMVFiles();//state, circle, ran, div
    }
    private void DownloadCMVMMVFiles() {//boolean state, boolean circle, boolean div, boolean ran
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVFiles(divid, rangeCode, fbid);
            String path = GetFilePath();
            for (int i = 0; i < mfb.size(); i++) {
                if (!mfb.get(i).equals("null")) {
                    getCMVData(mfb.get(i));
                }
            }
           /* if (state == true && circle == true && ran == true && div == true) {

            } else {
                for (int i = 0; i < master.length; i++) {
                    getCMVData(master[i]);
                }
            }*/
        } else {
            Toast.makeText(this, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }
    public void getCMVData(String filename) {
        try {
            if (!filename.equals("null")) {

                new DownloadFile().execute(BuildConfig.F_KML_API + filename);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("CMV file for this fb is not available....");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //Toast.makeText(this,"CMV/MMV file for this fb is not available....",Toast.LENGTH_LONG).show();
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
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
            this.progressDialog = ProgressDialog.show(SelectFBForDGPSActivity.this, "", "Please wait...Your CMV data is downloading", false);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            if (f_url[0].substring(f_url[0].lastIndexOf('/') + 1) != "null" || f_url[0].substring(f_url[0].lastIndexOf('/') + 1) != "") {
                try {
                    URL url = new URL(f_url[0]);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.connect();
                    int lengthOfFile = connection.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
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
                        publishProgress("" + (int) ((total * 100) / lengthOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    return "Downloaded at: " + folder + fileName;

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
                return "CMV files is missing in the server.Please contact your Admin";
            } else {
                return "These is some issue in downloading the file.Please contact your Admin";
            }
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
            Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }




}
