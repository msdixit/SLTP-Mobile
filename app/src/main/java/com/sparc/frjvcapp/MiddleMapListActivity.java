package com.sparc.frjvcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class MiddleMapListActivity extends AppCompatActivity {

    public static final String data = "data";
    public static final String userlogin = "userlogin";
    ImageView data_view, map_view, sync;
    SQLiteDatabase db;
    DbHelper dbHelper;
    String divid, rangeid, fbid, userid,kmlstatus;
    JSONArray jsonArray = new JSONArray();
    JSONObject fp_data = new JSONObject();

    // = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_map_list);

        SharedPreferences shared = getApplicationContext().getSharedPreferences(data, MODE_PRIVATE);
        divid = shared.getString("fbdivcode", "0");
        rangeid = shared.getString("fbrangecode", "0");
        fbid = shared.getString("fbcode", "0");

        SharedPreferences login = getApplicationContext().getSharedPreferences(userlogin, MODE_PRIVATE);
        userid = login.getString("uemail", "0");
        String cmv_path, mmv_path;
        dbHelper = new DbHelper(this);

        data_view = findViewById(R.id.dataview);
        map_view = findViewById(R.id.mapview);
        sync = findViewById(R.id.sync);
        Intent i= getIntent();
        kmlstatus=i.getStringExtra("kml_status");

        map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ListMapActivity.class);
                i.putExtra("kml_status",kmlstatus);
                startActivity(i);

            }
        });
        data_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ListDataViewActivity.class);
                i.putExtra("kml_status",kmlstatus);
                startActivity(i);
            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getDataforSync(divid,rangeid,fbid,userid);
                getDataforExcel(divid, rangeid, fbid, userid);

            }
        });
    }

    private void getDataforExcel(String divid, String rangeid, String fbid, String userid) {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery("select * from m_pillar_reg where uid='" + userid + "' and d_id='" + divid + "' and r_id='" + rangeid + "' and fb_id='" + fbid + "' order by id", null);
        if (cursor.getCount() > 0) {
            String fileName = divid + rangeid + fbid + userid + ".xls";
            File directory = getExternalFilesDir(null);
            String folder = directory.getAbsolutePath();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try {
                File file = new File(folder, fileName);
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));
                WritableWorkbook workbook;
                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("PillarList", 0);
                sheet.addCell(new Label(0, 0, "ID"));
                sheet.addCell(new Label(1, 0, "Division ID"));
                sheet.addCell(new Label(2, 0, "Range ID"));
                sheet.addCell(new Label(3, 0, "ForestBlock ID"));
                sheet.addCell(new Label(4, 0, "Insc. Pillar No"));
                sheet.addCell(new Label(5, 0, "Lat"));
                sheet.addCell(new Label(6, 0, "Long"));
                sheet.addCell(new Label(7, 0, "Pillar Type"));
                sheet.addCell(new Label(8, 0, "Pillar Condition"));
                sheet.addCell(new Label(9, 0, "Remark"));
                sheet.addCell(new Label(10, 0, "Photo"));
                sheet.addCell(new Label(11, 0, "Patch No"));
                sheet.addCell(new Label(12, 0, "Ring No"));
                sheet.addCell(new Label(13, 0, "Location Type"));
                sheet.addCell(new Label(14, 0, "Pillar No"));
                sheet.addCell(new Label(15, 0, "Paint Status"));
                sheet.addCell(new Label(16, 0, "FB Name"));
                sheet.addCell(new Label(17, 0, "User ID"));
                //cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        int i = cursor.getPosition() + 1;
                        sheet.addCell(new Label(0, i, cursor.getString(cursor.getColumnIndex("point_no"))));
                        sheet.addCell(new Label(1, i, cursor.getString(cursor.getColumnIndex("d_id"))));
                        sheet.addCell(new Label(2, i, cursor.getString(cursor.getColumnIndex("r_id"))));
                        sheet.addCell(new Label(3, i, cursor.getString(cursor.getColumnIndex("fb_id"))));
                        sheet.addCell(new Label(4, i, cursor.getString(cursor.getColumnIndex("p_sl_no"))));
                        sheet.addCell(new Label(5, i, cursor.getString(cursor.getColumnIndex("p_lat"))));
                        sheet.addCell(new Label(6, i, cursor.getString(cursor.getColumnIndex("p_long"))));
                        sheet.addCell(new Label(7, i, cursor.getString(cursor.getColumnIndex("p_type"))));
                        sheet.addCell(new Label(8, i, cursor.getString(cursor.getColumnIndex("p_cond"))));
                        sheet.addCell(new Label(9, i, cursor.getString(cursor.getColumnIndex("p_rmk"))));
                        sheet.addCell(new Label(10, i, cursor.getString(cursor.getColumnIndex("p_pic"))));
                        sheet.addCell(new Label(11, i, cursor.getString(cursor.getColumnIndex("patch_no"))));
                        sheet.addCell(new Label(12, i, cursor.getString(cursor.getColumnIndex("ring_no"))));
                        sheet.addCell(new Label(13, i, cursor.getString(cursor.getColumnIndex("p_loc_type"))));
                        sheet.addCell(new Label(14, i, cursor.getString(cursor.getColumnIndex("p_no"))));
                        sheet.addCell(new Label(15, i, cursor.getString(cursor.getColumnIndex("p_paint_status"))));
                        sheet.addCell(new Label(16, i, cursor.getString(cursor.getColumnIndex("fb_name"))));
                        sheet.addCell(new Label(17, i, cursor.getString(cursor.getColumnIndex("uid"))));
                    } while (cursor.moveToNext());
                    cursor.close();
                    workbook.write();
                    workbook.close();
                    Toast.makeText(this, "Data exported in excel sheet", Toast.LENGTH_SHORT).show();

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "You dont have any data for Synchronization", Toast.LENGTH_LONG).show();
        }
        cursor.close();
        db.close();
        //sendDatatoServer(jsonArray);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), SearchMapFile.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
}
