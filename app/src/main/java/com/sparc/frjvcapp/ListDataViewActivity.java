package com.sparc.frjvcapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sparc.frjvcapp.Adapter.DataViewAdapter;
import com.sparc.frjvcapp.config.AllApi;
import com.sparc.frjvcapp.pojo.DataViewDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListDataViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String data = "data";
    public static final String userlogin = "userlogin";
    public DataViewAdapter adapter;
    String kmlstatus;
    ProgressDialog progress;
    Integer i = 1;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    SharedPreferences shared, userdata;
    String sharerange, sharefb, userid;
    int syncstatus = 0;
    Cursor c;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
    private SQLiteDatabase db;
    private ArrayList<DataViewDetails> arrayList;
    private ArrayList<DataViewDetails> syncarrylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data_view);
        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.notsync);
        fab2 = findViewById(R.id.sync);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        progress = new ProgressDialog(ListDataViewActivity.this, R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.show();
        arrayList = new ArrayList<DataViewDetails>();
        syncarrylist = new ArrayList<DataViewDetails>(arrayList.size() + 1);
        adapter = new DataViewAdapter(getApplicationContext(), arrayList);

        new displayCard().execute("");
    }

    public void getDataFromDataBase() {
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            arrayList.clear();
            c = db.rawQuery("SELECT * from m_pillar_reg where r_id = '" + sharerange + "' and  fb_id = '" + sharefb + "'  and uid='" + userid + "' and p_sts='" + 0 + "' and delete_status='0' order by p_sl_no", null);
            int count = c.getCount();
            if (count >= 1) {
                if (c.moveToFirst()) {
                    do {

                        DataViewDetails dataViewDetails = new DataViewDetails();
                        dataViewDetails.setPillarNo(c.getString(c.getColumnIndex("p_no")));
                        dataViewDetails.setLat(c.getString(c.getColumnIndex("p_lat")));
                        dataViewDetails.setLon(c.getString(c.getColumnIndex("p_long")));
                        dataViewDetails.setImage(c.getString(c.getColumnIndex("p_pic")));
                        dataViewDetails.setSyncStatus("0");
                        arrayList.add(dataViewDetails);
                    }
                    while (c.moveToNext());
                }
            }
            c.close();
            db.close();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    public void loadData() {
        try {
            adapter = new DataViewAdapter(this, arrayList);
            recyclerView.setAdapter(adapter);
            adapter.setOnTapListener(new OnTapListener() {
                @Override
                public void OnTapView(int position, String presKey) {
                    final DataViewDetails dataViewDetails = arrayList.get(position);
                    if (presKey.equals("delete")) {
                        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                        Cursor c = db.rawQuery("update m_pillar_reg set delete_status='3' where p_lat='" + dataViewDetails.lat + "' and p_long='" + dataViewDetails.lon + "'", null);
                        if (c.getCount() >= 0) {
                            Toast.makeText(ListDataViewActivity.this, "Data successfully deleted", Toast.LENGTH_SHORT).show();
                        }
                        c.close();
                        db.close();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoadSyncData() {
        adapter = new DataViewAdapter(this, syncarrylist);
        recyclerView.setAdapter(adapter);
        syncstatus = 0;

    }

    public void LoadAllData() {
        if (syncarrylist.size() > 0) {
            syncarrylist.addAll(arrayList);
            adapter = new DataViewAdapter(this, syncarrylist);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = new DataViewAdapter(this, arrayList);
        }
        /*adapter.setOnTapListener(new OnTapListener() {
            @Override
            public void OnTapView(int position, String presKey) {
                final DataViewDetails retriveSurveyData = arrayList.get(position);
                if (presKey.equals("info")) {
                    Bundle b = new Bundle();
                    b.putStringArray("jobkey", new String[]{retriveSurveyData.getUnique()});
                    Intent intent1 = new Intent(getApplicationContext(), surveyDetails.class);
                    intent1.putExtras(b);
                    startActivity(intent1);
                }
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                networkWiseData();
                break;
            case R.id.notsync:
                getDataFromDataBase();
                loadData();
                break;
            case R.id.sync:
                returnSyncData(sharefb, userid);
                syncstatus = 1;
                break;
        }
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            //Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            //Log.d("Raj","open");

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MiddleMapListActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("kml_status", kmlstatus);
        startActivity(i);

    }

    private boolean returnSyncData(String fid, String uid) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = AllApi.F_PILL_PIC_SYNC_DATA_VIEW + fid + "/" + uid;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray arr = new JSONArray(response);
                        if (arr.length() > 0) {
                            syncarrylist.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                //progressDialog.dismiss();
                                JSONObject jsonobject = arr.getJSONObject(i);
                                DataViewDetails dataViewDetails = new DataViewDetails();
                                dataViewDetails.pillarNo = (jsonobject.getString("plr_no"));
                                dataViewDetails.lat = (jsonobject.getString("lat"));
                                dataViewDetails.lon = (jsonobject.getString("longi"));
                                dataViewDetails.image = (jsonobject.getString("path").substring(jsonobject.getString("path").lastIndexOf('/')+1));
                                dataViewDetails.syncStatus = "1";
                                syncarrylist.add(dataViewDetails);
                            }
                            if (syncstatus == 0) {
                                LoadAllData();
                            } else if (syncstatus == 1) {
                                LoadSyncData();
                            }

                        } else {
                            // progress.hide();
                            Toast.makeText(ListDataViewActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //progress.hide();
                        Toast.makeText(ListDataViewActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ListDataViewActivity.this, "Server Error Try Again", Toast.LENGTH_SHORT).show();
                    //progress.hide();
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
        return false;
    }

    public void networkWiseData() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
            getDataFromDataBase();
            returnSyncData(sharefb, userid);
        } else {
            getDataFromDataBase();
            loadData();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class displayCard extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            shared = getApplicationContext().getSharedPreferences(data, MODE_PRIVATE);
            sharerange = shared.getString("fbrangecode", "0");
            sharefb = shared.getString("fbcode", "0");
            userdata = getSharedPreferences(userlogin, MODE_PRIVATE);
            userid = userdata.getString("uemail", "0");
            networkWiseData();
            return null;
        }

        protected void onPostExecute(Void result) {
            progress.dismiss();
        }
    }
}
