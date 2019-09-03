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
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class SearchMapFile extends AppCompatActivity {


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
    boolean cmvsta1, mmvsta1, cmvavls1, mmvavls1;
    ProgressDialog progressDialog1;
    ArrayAdapter<String> divadapter;
    private String url = "http://odishaforestlandsurvey.in/kml/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map_file);
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
                    checkCMVData();
                    if (cmvsta1 == true && mmvsta1 == true) {
                        DeleteRestoreData();
                    } else if (cmvsta1 == false && mmvsta1 == true) {
                        DownloadCMVMMVFiles();

                    } else if (cmvsta1 == true && mmvsta1 == false) {
                        DownloadCMVMMVFiles();

                    } else if (cmvsta1 == false && mmvsta1 == false) {
                        DownloadCMVMMVFiles();
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
                    int a = checkData();
                    if (a == 1) {
                        if (cmvsta == true && mmvsta == true) {
                            Intent i = new Intent(getApplicationContext(), MiddleMapListActivity.class);
                            i.putExtra("kml_status", "1");
                            SharedPreferences sharedPreferences = getSharedPreferences(data, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("fbrangecode", rangeCode);
                            editor.putString("fbdivcode", divid);
                            editor.putString("fbcode", fbid);
                            editor.putString("fbtype", foresttype);
                            editor.putString("fbname", forbname);
                            editor.putString("userid", userid);
                            editor.apply();
                            startActivity(i);
                        } else if (cmvsta == false && mmvsta == true) {
                            DownloadCMVMMVFiles();
                            //Toast.makeText(getApplicationContext(), "CMV kml file for this fb is not available....Please contact to your admin", Toast.LENGTH_LONG).show();
                        } else if (cmvsta == true && mmvsta == false) {
                            DownloadCMVMMVFiles();
                            //Toast.makeText(getApplicationContext(), "MMV kml file for this fb is not available....Please contact to your admin", Toast.LENGTH_LONG).show();
                        } else if (cmvsta == false && mmvsta == false) {
                            Toast.makeText(getApplicationContext(), "CMV and MMV kml file for this fb is available....Please click the download button", Toast.LENGTH_LONG).show();
                        }
                    } else if (a == 2) {
                        if (cmvavls == false && mmvavls == false) {
                            Intent i = new Intent(getApplicationContext(), MiddleMapListActivity.class);
                            i.putExtra("kml_status", "0");
                            SharedPreferences sharedPreferences = getSharedPreferences(data, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("fbrangecode", rangeCode);
                            editor.putString("fbdivcode", divid);
                            editor.putString("fbcode", fbid);
                            editor.putString("fbtype", foresttype);
                            editor.putString("fbname", forbname);
                            editor.putString("userid", userid);
                            editor.apply();
                            startActivity(i);
                        }
                    } else {
                        Toast.makeText(SearchMapFile.this, "Please contact to your Admin", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void DownloadCMVMMVFiles() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVMMVFiles(divid, rangeCode, fbid);
            String path = GetFilePath();
            for (int i = 0; i < mfb.size(); i++) {
                if (!mfb.get(i).equals("null")) {
                    getCMVData(mfb.get(i));
                }
            }
        }else{
            Toast.makeText(this, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private int checkData() {
        int status = 0;
        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVMMVFiles(divid, rangeCode, fbid);
            String path = GetFilePath();

            for (int i = 0; i < mfb.size(); i++) {
                if (!mfb.get(i).equals("null")) {

                    File f = new File(path + "/" + mfb.get(i));
                    if (i == 0) {
                        if (f.exists()) {
                            cmvsta = true;
                        } else {
                            cmvsta = false;
                        }
                    } else {
                        if (f.exists()) {
                            mmvsta = true;
                        } else {
                            mmvsta = false;
                        }
                    }
                    status = 1;
                } else {
                    if (i == 0) {
                        cmvavls = false;
                    } else {
                        mmvavls = false;
                    }
                    status = 2;
                }
                //progressDialog1.dismiss();

            }

            dbHelper.close();
        }
        catch (Exception ee) {
            ee.printStackTrace();
        }
        return status;
    }

    private void DeleteRestoreData() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You already have CMV and MMV files for " + forbname + " .If you still want to download press Ok otherwise press Cancel.");
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
        DownloadCMVMMVFiles();
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
                    } else {
                        if (f.exists()) {
                            mmvsta1 = true;
                        } else {
                            mmvsta1 = false;
                        }
                    }
                }
                else {
                    cmvavls1 = false;
                    mmvavls1 = false;
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

    public void getCMVData(String filename) {
        try {
            if (!filename.equals("null")) {

                new SearchMapFile.DownloadFile().execute(url + filename);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("CMV/MMV file for this fb is not available....");
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
        Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

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
            this.progressDialog = ProgressDialog.show(SearchMapFile.this, "", "Please wait...Your CMV and MMV data is downloading", false);
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
                    URLConnection connection = url.openConnection();
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
                return "CMV/MMV files is missing in the server.Please contact your Admin";
            } else {
                return "These is some issue in downloading the file.Please contact your Admin";
            }

            //return "CMV/MMV files is missing in the server";
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