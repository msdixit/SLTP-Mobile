package com.sparc.frjvcapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.sparc.frjvcapp.Adapter.DGPSDataViewAdapter;
import com.sparc.frjvcapp.Adapter.DataViewAdapter;
import com.sparc.frjvcapp.pojo.DGPSDataViewDetails;
import com.sparc.frjvcapp.pojo.DataViewDetails;

import java.util.ArrayList;

public class DGPSDataViewActivity extends AppCompatActivity {
    public static final String data = "data";
    public static final String userlogin = "userlogin";
    public DGPSDataViewAdapter adapter;
    String kmlstatus;
    ProgressDialog progress;
    Integer i = 1;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    SharedPreferences shared, userdata, _shareToken;
    String sharerange, sharefb, userid, _token;
    int syncstatus = 0;
    Cursor c;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
    private SQLiteDatabase db;
    private ArrayList<DGPSDataViewDetails> arrayList;
    private ArrayList<DGPSDataViewDetails> syncarrylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpsdata_view);

        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        progress = new ProgressDialog(DGPSDataViewActivity.this, R.style.MyTheme);
        progress.setCancelable(false);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.show();
        arrayList = new ArrayList<DGPSDataViewDetails>();
        syncarrylist = new ArrayList<DGPSDataViewDetails>(arrayList.size() + 1);
        adapter = new DGPSDataViewAdapter(getApplicationContext(), arrayList);

        _shareToken = getApplicationContext().getSharedPreferences(userlogin, MODE_PRIVATE);
        _token = _shareToken.getString("token", "0");

        new DGPSDataViewActivity.displayCard().execute("");

    }

    public void getDataFromDataBase() {
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            arrayList.clear();
            c = db.rawQuery("SELECT * from m_fb_dgps_survey_pill_data where r_id = '" + sharerange + "' and  fb_id = '" + sharefb + "'  and u_id='" + userid + "' and delete_status='0' order by pill_no", null);
            int count = c.getCount();
            if (count >= 1) {
                if (c.moveToFirst()) {
                    do {
                        DGPSDataViewDetails dataViewDetails = new DGPSDataViewDetails();
                        if(Integer.parseInt(c.getString(c.getColumnIndex("pndjv_pill_no")))!=0)
                        {
                            dataViewDetails.setPillarNo(c.getString(c.getColumnIndex("pill_no"))+"-"+c.getString(c.getColumnIndex("pndjv_pill_no")));
                        }else {
                            dataViewDetails.setPillarNo(c.getString(c.getColumnIndex("pill_no")));
                        }
                        dataViewDetails.setJob_id(c.getString(c.getColumnIndex("job_id")));
                        dataViewDetails.setImage(c.getString(c.getColumnIndex("f_pic_name")));
                        dataViewDetails.setSyncStatus(c.getString(c.getColumnIndex("sync_status")));
                        arrayList.add(dataViewDetails);
                    }
                    while (c.moveToNext());
                }
            }
            c.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        finally{
        if(arrayList.size()>0)
        {
            LoadSyncData();
        }
        }

    }

    public void LoadSyncData() {
        adapter = new DGPSDataViewAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);
        adapter.setOnTapListener(new OnTapListener() {
            @Override
            public void OnTapView(int position, String presKey) {
                String query1 = null,qurey2=null;
                final DGPSDataViewDetails dataViewDetails = arrayList.get(position);
                if (presKey.equals("delete")) {
                    db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                    if(dataViewDetails.pillarNo.contains("-"))
                    {
                        String values[]=dataViewDetails.pillarNo.split("-");
                        query1="delete from m_fb_dgps_survey_pill_data where pill_no='" + values[0]+ "' and pndjv_pill_no='"+values[1] +"'";
                        qurey2="update m_dgps_Survey_pill_data set m_dgps_surv_sts='0',m_survey_status='0',m_dgps_file_sts='0' where m_fb_pillar_no='" + values[0] + "' and m_pndjv_pill_no='"+values[1]+"'";
                    }else{
                        query1="delete from m_fb_dgps_survey_pill_data where pill_no='" + dataViewDetails.pillarNo + "'";
                        qurey2="update m_dgps_Survey_pill_data set m_dgps_surv_sts='0',m_survey_status='0',m_dgps_file_sts='0' where m_fb_pillar_no='" + dataViewDetails.pillarNo + "' ";
                    }
                    Cursor c = db.rawQuery(query1, null);
                    if (c.getCount() >= 0) {
                        try {
                            Cursor c1 = db.rawQuery(qurey2, null);
                            if (c1.getCount() >= 0) {
                                Toast.makeText(DGPSDataViewActivity.this, "Data successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                            c1.close();
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }

                    }
                    c.close();
                    db.close();
                }
            }
        });
        syncstatus = 0;

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
            getDataFromDataBase();
            return null;
        }

        protected void onPostExecute(Void result) {
            progress.dismiss();
        }
    }
}
