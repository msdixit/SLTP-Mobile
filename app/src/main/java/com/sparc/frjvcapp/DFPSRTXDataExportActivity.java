package com.sparc.frjvcapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sparc.frjvcapp.Adapter.DGPSPillarViewAdapter;
import com.sparc.frjvcapp.pojo.DGPSPillarDataViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class DFPSRTXDataExportActivity extends AppCompatActivity {
    public static final String data = "data";
    public HashMap<String, String> fbKey;
    ArrayAdapter<String> dataAdapter = null;
    ImageView img_download;
    TextView txtStsFileName;
    private SQLiteDatabase db;
    private ArrayList<String> arrayList;
    private ArrayList<DGPSPillarDataViewModel> arrayList1;
    MaterialSpinner pill_no;
    String spill_no;
    Cursor c;
    String sfinalpath, dfinalpath;
    static File d;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    public DGPSPillarViewAdapter adapter;
    String sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid, jobid, div_name, range_name, fb_name,fbid,frjvc_long;
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfpsrtxdata_export);

        img_download = findViewById(R.id.img_download);
        txtStsFileName = findViewById(R.id.txtStsFileName);
        arrayList = new ArrayList<String>();
        arrayList1 = new ArrayList<DGPSPillarDataViewModel>();

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

        String sfile = Environment.getExternalStorageDirectory().toString();
        String path = "/SurveyMobile.Droid/Export";
        sfinalpath = sfile + path;

        String dfile = Environment.getExternalStorageDirectory().toString();
        String dpath = "/RTXData";
        dfinalpath = dfile + dpath;

        File s = new File(sfinalpath);
        d = new File(dfinalpath);
        if (!d.exists()) {
            d.mkdirs();
        }

        File[] listOfFiles = s.listFiles();
        if (listOfFiles.length > 0) {
            for (int i = 0; i < 1; i++) {
                if (listOfFiles[i].isFile()) {
                    txtStsFileName.setText(listOfFiles[i].getName());
                } else {
                    txtStsFileName.setText("No file available for download");
                    img_download.setVisibility(View.GONE);
                }
            }
        } else {
            txtStsFileName.setText("No file available for download");
            txtStsFileName.setTextSize(15);
            txtStsFileName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            txtStsFileName.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            txtStsFileName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            img_download.setVisibility(View.GONE);
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDGPSForestBlockData();

                //alertDialogBuilder.setMessage("Are you sure to save this pillar data?");
                final View customLayout = getLayoutInflater().inflate(R.layout.assign_fb_to_rtx, null);
                alertDialogBuilder.setView(customLayout);
                pill_no = customLayout.findViewById(R.id.fb_name);

                //final ArrayAdapter<String> durationadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, dataAdapter);
                pill_no.setAdapter(dataAdapter);
                pill_no.setPaddingSafe(0, 0, 0, 0);
                pill_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spill_no = (String) parent.getItemAtPosition(position);
                        fbid = fbKey.get(spill_no);
                        if (!spill_no.equals("Select Forest Block")) {
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                String d_f_name[]=txtStsFileName.getText().toString().split("\\.");//.split(".")
                                String f_file_name=d_f_name[0]+"_"+fbid+"_"+userid+"."+d_f_name[1];
                                new DFPSRTXDataExportActivity.ExportPointRTXData().execute(sfinalpath + "/" + txtStsFileName.getText().toString(), fbid, dfinalpath + "/" +f_file_name);

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


            }
        });

    }
    private class ExportPointRTXData extends AsyncTask<String, String, String> {

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
            /*this.progressDialog = ProgressDialog.show(DGPSDataExportActivity.this, "", "Please wait...Your Point KML data is downloading", false);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);*/
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = new FileInputStream(f_url[0]);
                    os = new FileOutputStream(f_url[2]);

                    // buffer size 1K
                    byte[] data = new byte[16384];

                    int bytesRead;
                    while ((bytesRead = is.read(data)) > 0) {
                        os.write(data, 0, bytesRead);
                    }
                    os.flush();
                } catch (Exception ee) {
                    ee.printStackTrace();
                } finally {
                    is.close();
                    os.flush();
                    os.close();
                }
                return f_url[1] + "&" + f_url[2] + "&" + f_url[0];

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return "Point KML files is missing in the server.Please contact your Admin";
            //return "CMV/MMV files is missing in the server";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            String arr[];
            arr = message.split("&");
            // dismiss the dialog after the file was downloaded
            //this.progressDialog.dismiss();
            try {
                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                c = db.rawQuery("update m_fb_dgps_survey_pill_data set pillar_rfile_status='1',pillar_rfile_path='" + arr[1] + "' where fb_id='" + arr[0] + "'", null);
                if (c.getCount() >= 0) {
                    try {
                        File file = new File(arr[2]);
                        if (file.delete()) {
                            /*if (GetTaggingTableforDGPS(arr[0])) {
                                UpdateTaggingPillarTable(arr[0]);*/
                                Toast.makeText(getApplicationContext(), "Your data taging is successfully Completed", Toast.LENGTH_LONG);
                          /*  }*/

                        } else {
                            Toast.makeText(getApplicationContext(), "Your data tagging is unsuccessfully", Toast.LENGTH_LONG);
                        }
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
    }

    private void getDGPSForestBlockData() {
        List<String> fbName = new ArrayList<String>();
        //rangeName.add("Select Range");
        fbKey = new HashMap<>();
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select distinct fb_name,fb_id from m_fb_dgps_survey_pill_data where d_id='" + sharediv + "' and r_id ='" + sharerange + "' order by fb_name", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                fbName.add(cursor.getString(cursor.getColumnIndex("fb_name")));
                fbKey.put(cursor.getString(cursor.getColumnIndex("fb_name")),cursor.getString(cursor.getColumnIndex("fb_id")));


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, fbName);
    }
}
