package com.sparc.frjvcapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sparc.frjvcapp.pojo.M_fb;
import com.sparc.frjvcapp.pojo.M_pillar_reg;
import com.sparc.frjvcapp.pojo.M_range;

import java.util.ArrayList;


public class DbHelper {

    public static final String DATABASE_NAME = "sltp.db";
    public static final String m_range = "m_range";
    public static final String m_fb = "m_fb";
    public static final String m_pillar_reg = "m_pillar_reg";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_m_range_Table = "CREATE TABLE IF NOT EXISTS " + m_range + "( " +
            "id INTEGER PRIMARY KEY, " +
            "r_name TEXT, " +
            "r_id TEXT," +
            "d_id TEXT)";
    private static final String CREATE_m_fb_Table = "CREATE TABLE IF NOT EXISTS " + m_fb + "( " +
            "id INTEGER PRIMARY KEY, " +
            "m_fb_id TEXT, " +
            "m_fb_name TEXT, " +
            "m_fb_range_id TEXT," +
            "div_id TEXT," +
            "fb_type text," +
            "m_fb_cmv_path TEXT," +
            "m_fb_mmv_path TEXT)";
    private static final String CREATE_m_pillar_reg_Table = "CREATE TABLE IF NOT EXISTS " + m_pillar_reg + "( " +
            "id INTEGER PRIMARY KEY, " +
            "d_id Text, " +
            "r_id Text, " +
            "fb_id Text, " +
            "p_sl_no Text, " +
            "p_lat TEXT, " +
            "p_long TEXT, " +
            "p_type TEXT," +
            "p_cond TEXT," +
            "p_rmk TEXT," +
            "p_pic TEXT," +
            "p_sts Text," +
            "patch_no TEXT," +
            "ring_no TEXT," +
            "p_loc_type TEXT," +
            "p_no INTEGER," +
            "p_paint_status TEXT," +
            "fb_name TEXT," +
            "uid TEXT," +
            "point_no INTEGER," +
            "img_status TEXT," +
            "delete_status TEXT)";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DbHelper(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 2);
    }

    public DbHelper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        // mDb = mDbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long insertRangeData(M_range range) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("r_name", range.getR_name());
        contentValues.put("r_id", range.getR_id());
        contentValues.put("d_id", range.getD_id());


        long id = mDb.insert(m_range, null, contentValues);
        return id;
    }

    public long inserFBData(M_fb fb) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("m_fb_id", fb.getFb_id());
        contentValues.put("m_fb_name", fb.getFb_name());
        contentValues.put("m_fb_range_id", fb.getFb_range_id());
        contentValues.put("div_id", fb.getDiv_id());
        contentValues.put("fb_type", fb.getFb_type());
        contentValues.put("m_fb_mmv_path", fb.getFb_mmv_path());
        contentValues.put("m_fb_cmv_path", fb.getFb_cmv_path());
        long id = mDb.insert(m_fb, null, contentValues);
        return id;
    }

    public long insertPillarData(M_pillar_reg mpr) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("d_id", mpr.getD_id());
        contentValues.put("r_id", mpr.getR_id());
        contentValues.put("fb_id", mpr.getFb_id());
        contentValues.put("p_sl_no", mpr.getP_sl_no());
        contentValues.put("p_lat", mpr.getP_lat());
        contentValues.put("p_long", mpr.getP_long());
        contentValues.put("p_type", mpr.getP_type());
        contentValues.put("p_cond", mpr.getP_cond());
        contentValues.put("p_rmk", mpr.getP_rmk());
        contentValues.put("p_pic", mpr.getP_pic());
        contentValues.put("p_sts", mpr.getP_sts());
        contentValues.put("patch_no", mpr.getPatch_no());
        contentValues.put("ring_no", mpr.getRing_no());
        contentValues.put("p_loc_type", mpr.getP_loc_type());
        contentValues.put("p_no", mpr.getP_no());
        contentValues.put("p_paint_status", mpr.getP_paint_status());
        contentValues.put("fb_name", mpr.getFb_name());
        contentValues.put("uid", mpr.getUid());
        contentValues.put("point_no", mpr.getPoint_no());
        contentValues.put("img_status", mpr.getImg_status());
        contentValues.put("delete_status", mpr.getDelete_status());

        long id = mDb.insert(m_pillar_reg, null, contentValues);
        return id;
    }

    public ArrayList getRangeData(String divisionid) {
        ArrayList<String> array_list = new ArrayList<String>();
        //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
        Cursor res = mDb.rawQuery("select * from m_range where d_id='" + divisionid + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex("r_name")));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList getFBData(String divisionid) {
        ArrayList<String> array_list = new ArrayList<String>();
        //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
        Cursor res = mDb.rawQuery("select * from m_fb where div_id='" + divisionid + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {

        }
        return array_list;
    }

    public ArrayList getCMVMMVFiles(String divid, String rid, String fid) {
        ArrayList<String> array_list = new ArrayList<String>();
        //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
        Cursor res = mDb.rawQuery("select m_fb_cmv_path,m_fb_mmv_path from m_fb where m_fb_range_id='" + rid + "' and div_id='" + divid + "' and m_fb_id='" + fid + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex("m_fb_cmv_path")));
            array_list.add(res.getString(res.getColumnIndex("m_fb_mmv_path")));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList getCMVMMVFiles(String divid) {
        ArrayList<String> array_list = new ArrayList<String>();
        //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
        Cursor res = mDb.rawQuery("select m_fb_cmv_path,m_fb_mmv_path from m_fb where div_id='" + divid + "'", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex("m_fb_cmv_path")));
            array_list.add(res.getString(res.getColumnIndex("m_fb_mmv_path")));
            res.moveToNext();
        }
        return array_list;
    }

    public int getPillarData(String fb_id) {
        ArrayList<String> array_list = new ArrayList<String>();
        int slno = 0;
        //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);COALESCE(MAX(p_no),0)+1 as slno
        Cursor res = mDbHelper.getReadableDatabase().rawQuery("select COALESCE(MAX(point_no),0)+1 as slno from m_pillar_reg where fb_id='" + fb_id + "'", null);
        if (res == null) {
            slno = 1;
        } else {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                slno = res.getInt(res.getColumnIndex("slno"));
                res.moveToNext();
            }           //slno=array_list[0];
        }
        return slno;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_m_range_Table);
            db.execSQL(CREATE_m_fb_Table);
            db.execSQL(CREATE_m_pillar_reg_Table);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + m_range);
            db.execSQL("DROP TABLE IF EXISTS " + m_fb);
            db.execSQL("DROP TABLE IF EXISTS " + m_pillar_reg);
            onCreate(db);
        }
    }
}
