package com.sparc.frjvcapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.sparc.frjvcapp.pojo.M_dgps_pill_pic;
import com.sparc.frjvcapp.pojo.M_dgps_pilldata;
import com.sparc.frjvcapp.pojo.M_dgps_revisit_pillar_data_dwnld;
import com.sparc.frjvcapp.pojo.M_dgpssurvey_pillar_data;
import com.sparc.frjvcapp.pojo.M_fb;
import com.sparc.frjvcapp.pojo.M_pillar_reg;
import com.sparc.frjvcapp.pojo.M_range;
import com.sparc.frjvcapp.pojo.M_revisit_dgps_pill_data;
import com.sparc.frjvcapp.pojo.M_revisit_dgps_pill_pic;
import com.sparc.frjvcapp.pojo.M_shifting_pillar_details;
import com.sparc.frjvcapp.pojo.M_survey_pillar_data;
import com.sparc.frjvcapp.pojo.m_fb_survey_data;

import java.util.ArrayList;


public class DbHelper {

    public static final String DATABASE_NAME = "sltp.db";
    public static final String m_range = "m_range";
    public static final String m_fb = "m_fb";
    public static final String m_pillar_reg = "m_pillar_reg";
    public static final String m_survey_pillar_reg = "m_survey_pillar_reg";
    public static final String m_shifting_pillar_reg = "m_shifting_pillar_reg";
    private static final String m_fb_Survey_pill_data = "m_fb_Survey_pill_data";
    private static final String m_dgps_Survey_pill_data = "m_dgps_Survey_pill_data";
    private static final String m_fb_dgps_survey_pill_data = "m_fb_dgps_survey_pill_data";
    private static final String m_fb_dgps_survey_pill_pic = "m_fb_dgps_survey_pill_pic";
    private static final String m_revisit_dgps_download_data = "m_revisit_dgps_download_data";
    private static final String m_fb_revisit_dgps_survey_pill_data = "m_fb_revisit_dgps_survey_pill_data";


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
            "m_fb_mmv_path TEXT," +
            "m_fb_updated_pillar_kml Text)";
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
            "delete_status TEXT," +
            "shifting_status TEXT," +
            "surv_direction TEXT," +
            "p_accuracy TEXT," +
            "survey_dt TEXT)";

    private static final String CREATE_m_survey_pillar_reg_Table = "CREATE TABLE IF NOT EXISTS " + m_survey_pillar_reg + "( " +
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
            "delete_status TEXT," +
            "shifting_status TEXT," +
            "past_lat TEXT," +
            "past_long TEXT," +
            "surv_direction TEXT," +
            "p_accuracy TEXT," +
            "survey_dt TEXT)";

    private static final String CREATE_m_shifting_pillar_reg_Table = "CREATE TABLE IF NOT EXISTS " + m_shifting_pillar_reg + "( " +
            "id INTEGER PRIMARY KEY, " +
            "fb_id Text, " +
            "s_lat TEXT, " +
            "s_long TEXT, " +
            "s_rmk TEXT," +
            "s_pic TEXT," +
            "p_no INTEGER," +
            "fb_name TEXT," +
            "uid TEXT," +
            "sdelete_status TEXT," +
            "sync_status TEXT," +
            "simg_status TEXT)";

    private static final String CREATE_m_fb_Survey_pill_data = "CREATE TABLE IF NOT EXISTS " + m_fb_Survey_pill_data + "( " +
            "id INTEGER PRIMARY KEY, " +
            "m_fb_id TEXT, " +
            "m_fb_name TEXT, " +
            "m_fb_pillar_no TEXT," +
            "m_p_lat TEXT," +
            "m_p_long text," +
            "m_pillar_avl_sts Text)";

    private static final String CREATE_m_fb_dgps_survey_pill_data = "CREATE TABLE IF NOT EXISTS " + m_fb_dgps_survey_pill_data + "( " +
            "id INTEGER NOT NULL PRIMARY KEY, " +
            "d_id Text, " +
            "r_id Text, " +
            "fb_id Text, " +
            "pill_no INTEGER, " +
            "job_id TEXT, " +
            "u_id TEXT, " +
            "survey_durn TEXT," +
            "f_pic_status TEXT," +
            "b_pic_status TEXT," +
            "i_pic_status TEXT," +
            "o_pic_status TEXT," +
            "div_pic_status TEXT," +
            "patch_no TEXT," +
            "ring_no TEXT," +
            "forest_person TEXT," +
            "surveyor_name TEXT," +
            "survey_time TEXT," +
            "div_name TEXT," +
            "range_name TEXT," +
            "fb_name TEXT," +
            "sync_status TEXT," +
            "ack_status TEXT," +
            "delete_status TEXT," +
            "survey_segment TEXT," +
            "completion_sts TEXT," +
            "f_pic_name TEXT," +
            "b_pic_name TEXT," +
            "i_pic_name TEXT," +
            "o_pic_name TEXT," +
            "div_pic_name TEXT," +
            "device_imei_no TEXT," +
            "pillar_sfile_path TEXT," +
            "pillar_sfile_status TEXT," +
            "frjvc_lat TEXT," +
            "frjvc_long TEXT," +
            "d_pill_no TEXT," +
            "d_old_id TEXT," +
            "pillar_rfile_path TEXT," +
            "pillar_rfile_status TEXT," +
            "completion_status TEXT," +
            "rtx_survey_min TEXT," +
            "rtx_survey_second TEXT," +
            "survey_status TEXT," +
            "reason TEXT," +
            "remark TEXT," +
            "pillar_jfile_path TEXT," +
            "pillar_jfile_status TEXT,"+
            "pndjv_pill_no TEXT)";

    private static final String CREATE_m_dgps_Survey_pill_data = "CREATE TABLE IF NOT EXISTS " + m_dgps_Survey_pill_data + "( " +
            "id INTEGER PRIMARY KEY, " +
            "m_fb_id TEXT, " +
            "m_fb_name TEXT, " +
            "m_fb_pillar_no TEXT," +
            "m_p_lat TEXT," +
            "m_p_long TEXT," +
            "m_pillar_avl_sts TEXT," +
            "m_dgps_surv_sts TEXT," +
            "m_dgps_file_sts TEXT," +
            "o_Id TEXT," +
            "m_survey_status TEXT,"+
            "m_pndjv_pill_no TEXT)";

    private static final String CREATE_m_fb_dgps_survey_pill_pic = "CREATE TABLE IF NOT EXISTS " + m_fb_dgps_survey_pill_pic + "( " +
            "id INTEGER PRIMARY KEY, " +
            "pic_pill_no TEXT, " +
            "u_id TEXT, " +
            "pic_status TEXT," +
            "pic_name TEXT," +
            "pic_view TEXT,"+
            "pndjv_pill_no TEXT)";

    private static final String CREATE_m_revisit_dgps_download_data = "CREATE TABLE IF NOT EXISTS " + m_revisit_dgps_download_data + "( " +
            "id INTEGER PRIMARY KEY, " +
            "m_fb_id TEXT, " +
            "m_fb_name TEXT, " +
            "m_fb_pillar_no TEXT," +
            "m_p_lat TEXT," +
            "m_p_long TEXT," +
            "m_pillar_avl_sts TEXT," +
            "m_dgps_surv_sts TEXT," +
            "m_dgps_file_sts TEXT," +
            "o_Id TEXT," +
            "m_survey_status TEXT)";

    private static final String CREATE_m_fb_revisit_dgps_survey_pill_data = "CREATE TABLE IF NOT EXISTS " + m_fb_revisit_dgps_survey_pill_data + "( " +
            "id INTEGER NOT NULL PRIMARY KEY, " +
            "d_id Text, " +
            "r_id Text, " +
            "fb_id Text, " +
            "pill_no INTEGER, " +
            "job_id TEXT, " +
            "u_id TEXT, " +
            "survey_durn TEXT," +
            "f_pic_status TEXT," +
            "b_pic_status TEXT," +
            "i_pic_status TEXT," +
            "o_pic_status TEXT," +
            "div_pic_status TEXT," +
            "patch_no TEXT," +
            "ring_no TEXT," +
            "forest_person TEXT," +
            "surveyor_name TEXT," +
            "survey_time TEXT," +
            "div_name TEXT," +
            "range_name TEXT," +
            "fb_name TEXT," +
            "sync_status TEXT," +
            "ack_status TEXT," +
            "delete_status TEXT," +
            "survey_segment TEXT," +
            "completion_sts TEXT," +
            "f_pic_name TEXT," +
            "b_pic_name TEXT," +
            "i_pic_name TEXT," +
            "o_pic_name TEXT," +
            "div_pic_name TEXT," +
            "device_imei_no TEXT," +
            "pillar_sfile_path TEXT," +
            "pillar_sfile_status TEXT," +
            "frjvc_lat TEXT," +
            "frjvc_long TEXT," +
            "d_pill_no TEXT," +
            "d_old_id TEXT," +
            "pillar_rfile_path TEXT," +
            "pillar_rfile_status TEXT," +
            "completion_status TEXT," +
            "rtx_survey_min TEXT," +
            "rtx_survey_second TEXT," +
            "survey_status TEXT," +
            "reason TEXT," +
            "remark TEXT," +
            "pillar_jfile_path TEXT," +
            "pillar_jfile_status TEXT,"+
            "pndjv_pill_no TEXT)";




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
        long id = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("r_name", range.getR_name());
            contentValues.put("r_id", range.getR_id());
            contentValues.put("d_id", range.getD_id());
            id = mDb.insert(m_range, null, contentValues);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }

    public long inserFBData(M_fb fb) {
        long id = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("m_fb_id", fb.getFb_id());
            contentValues.put("m_fb_name", fb.getFb_name());
            contentValues.put("m_fb_range_id", fb.getFb_range_id());
            contentValues.put("div_id", fb.getDiv_id());
            contentValues.put("fb_type", fb.getFb_type());
            contentValues.put("m_fb_mmv_path", fb.getFb_mmv_path());
            contentValues.put("m_fb_cmv_path", fb.getFb_cmv_path());
            contentValues.put("m_fb_updated_pillar_kml", fb.getM_fb_updated_pillar_kml());
            id = mDb.insert(m_fb, null, contentValues);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }

    public long insertPillarData(M_pillar_reg mpr) {
        long id = 0;
        try {
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
            contentValues.put("shifting_status", mpr.getShifting_status());
            contentValues.put("surv_direction", mpr.getSurv_direction());
            contentValues.put("p_accuracy", mpr.getAccuracy());
            contentValues.put("survey_dt", mpr.getSurvey_dt());

            id = mDb.insert(m_pillar_reg, null, contentValues);

        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }

    public long insertSurveyPillarData(m_fb_survey_data mpr) {
        long id = 0;
        try {
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
            contentValues.put("shifting_status", mpr.getShifting_status());
            contentValues.put("past_lat", mpr.getPast_lat());
            contentValues.put("past_long", mpr.getPast_long());
            contentValues.put("surv_direction", mpr.getSurv_direction());
            contentValues.put("p_accuracy", mpr.getAccuracy());
            contentValues.put("survey_dt", mpr.getSurvey_dt());

            id = mDb.insert(m_survey_pillar_reg, null, contentValues);

        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }

    public long insertSurveyedPointDataData(M_survey_pillar_data fb) {
        long id = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("m_fb_id", fb.getFb_id());
            contentValues.put("m_fb_name", fb.getFb_name());
            contentValues.put("m_fb_pillar_no", fb.getPillar_no());
            contentValues.put("m_p_lat", fb.getP_lat());
            contentValues.put("m_p_long", fb.getP_long());
            contentValues.put("m_pillar_avl_sts", fb.getP_syrvey_sts());
            id = mDb.insert(m_fb_Survey_pill_data, null, contentValues);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }

    public long insertShiftingPillarData(M_shifting_pillar_details mspd) {
        long id = 0;
        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put("s_lat", mspd.getP_lat());
            contentValues.put("s_long", mspd.getP_long());
            contentValues.put("s_rmk", mspd.getP_rmk());
            contentValues.put("s_pic", mspd.getP_pic());
            contentValues.put("simg_status", mspd.getImg_status());
            contentValues.put("fb_name", mspd.getFb_name());
            contentValues.put("uid", mspd.getUid());
            contentValues.put("fb_id", mspd.getFb_id());
            contentValues.put("p_no", mspd.getP_no());
            contentValues.put("sync_status", mspd.getSync_status());
            contentValues.put("sdelete_status", mspd.getDelete_status());
            id = mDb.insert(m_shifting_pillar_reg, null, contentValues);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }

    public long insertDGPSSurveyPillarData(M_dgps_pilldata mpr) {
        long id = 0;
        mDb.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("d_id", mpr.getD_id());
            contentValues.put("r_id", mpr.getR_id());
            contentValues.put("fb_id", mpr.getFb_id());
            contentValues.put("pill_no", mpr.getPill_no());
            contentValues.put("job_id", mpr.getJob_id());
            contentValues.put("u_id", mpr.getU_id());
            contentValues.put("survey_durn", mpr.getSurvey_durn());
            contentValues.put("f_pic_status", mpr.getF_pic_status());
            contentValues.put("b_pic_status", mpr.getB_pic_status());
            contentValues.put("i_pic_status", mpr.getI_pic_status());
            contentValues.put("o_pic_status", mpr.getO_pic_status());
            contentValues.put("div_pic_status", mpr.getDiv_pic_status());
            contentValues.put("patch_no", mpr.getPatch_no());
            contentValues.put("ring_no", mpr.getRing_no());
            contentValues.put("forest_person", mpr.getForest_person());
            contentValues.put("surveyor_name", mpr.getSurveyor_name());
            contentValues.put("survey_time", mpr.getSurvey_time());
            contentValues.put("div_name", mpr.getDiv_name());
            contentValues.put("range_name", mpr.getRange_name());
            contentValues.put("fb_name", mpr.getFb_name());
            contentValues.put("sync_status", mpr.getSync_status());
            contentValues.put("ack_status", mpr.getAck_status());
            contentValues.put("delete_status", mpr.getDelete_status());
            contentValues.put("survey_segment", mpr.getSurvey_segment());
            contentValues.put("completion_sts", mpr.getCompletion_sts());
            contentValues.put("f_pic_name", mpr.getF_pic_name());
            contentValues.put("b_pic_name", mpr.getB_pic_name());
            contentValues.put("i_pic_name", mpr.getI_pic_name());
            contentValues.put("o_pic_name", mpr.getO_pic_name());
            contentValues.put("div_pic_name", mpr.getDiv_pic_name());
            contentValues.put("device_imei_no", mpr.getDevice_imei_no());
            contentValues.put("pillar_sfile_path", mpr.getPillar_sfile_path());
            contentValues.put("pillar_sfile_status", mpr.getPillar_sfile_status());
            contentValues.put("frjvc_lat", mpr.getFrjvc_lat());
            contentValues.put("frjvc_long", mpr.getFrjvc_long());
            contentValues.put("d_pill_no", mpr.getD_pill_no());
            contentValues.put("d_old_id", mpr.getD_old_id());
            contentValues.put("pillar_rfile_path", mpr.getPillar_rfile_path());
            contentValues.put("pillar_rfile_status", mpr.getPillar_rfile_status());
            contentValues.put("completion_status", mpr.getCompletion_status());
            contentValues.put("rtx_survey_min", mpr.getRtx_min());
            contentValues.put("rtx_survey_second", mpr.getRtx_sec());
            contentValues.put("survey_status", mpr.getSurvey_status());
            contentValues.put("reason", mpr.getReason());
            contentValues.put("remark", mpr.getRemark());
            contentValues.put("pillar_jfile_path", mpr.getPillar_jfile_path());
            contentValues.put("pillar_jfile_status", mpr.getPillar_jfile_status());
            contentValues.put("pndjv_pill_no", mpr.getPndjv_pill_no());
            id = mDb.insert(m_fb_dgps_survey_pill_data, null, contentValues);
            mDb.setTransactionSuccessful();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return id;
    }

    public long insertdgpsSurveyedPointDataData(M_dgpssurvey_pillar_data fb) {
        long id = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("m_fb_id", fb.getFb_id());
            contentValues.put("m_fb_name", fb.getFb_name());
            contentValues.put("m_fb_pillar_no", fb.getPillar_no());
            contentValues.put("m_p_lat", fb.getP_lat());
            contentValues.put("m_p_long", fb.getP_long());
            contentValues.put("m_pillar_avl_sts", fb.getP_syrvey_sts());
            contentValues.put("m_dgps_surv_sts", fb.getM_dgps_surv_sts());
            contentValues.put("m_dgps_file_sts", fb.getM_dgps_file_sts());
            contentValues.put("o_Id", fb.getO_Id());
            contentValues.put("m_survey_status", fb.getM_survey_status());
            contentValues.put("m_pndjv_pill_no", fb.getM_pndjv_pill_no());
            id = mDb.insert(m_dgps_Survey_pill_data, null, contentValues);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }

    public long insertDGPSSurveyPillarPic(M_dgps_pill_pic mpr) {
        long id = 0;
        mDb.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("pic_pill_no", mpr.getPic_pill_no());
            contentValues.put("u_id", mpr.getU_id());
            contentValues.put("pic_status", mpr.getPic_status());
            contentValues.put("pic_name", mpr.getPic_name());
            contentValues.put("pic_view", mpr.getPic_view());
            contentValues.put("pndjv_pill_no", mpr.getPic_view());
            id = mDb.insert(m_fb_dgps_survey_pill_pic, null, contentValues);
            mDb.setTransactionSuccessful();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return id;
    }

    public long insertdgpsRevisitSurveyedPointDataData(M_dgps_revisit_pillar_data_dwnld fb) {
        long id = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("m_fb_id", fb.getFb_id());
            contentValues.put("m_fb_name", fb.getFb_name());
            contentValues.put("m_fb_pillar_no", fb.getPillar_no());
            contentValues.put("m_p_lat", fb.getP_lat());
            contentValues.put("m_p_long", fb.getP_long());
            contentValues.put("m_pillar_avl_sts", fb.getP_syrvey_sts());
            contentValues.put("m_dgps_surv_sts", fb.getM_dgps_surv_sts());
            contentValues.put("m_dgps_file_sts", fb.getM_dgps_file_sts());
            contentValues.put("o_Id", fb.getO_Id());
            contentValues.put("m_survey_status", fb.getM_survey_status());
            id = mDb.insert(m_revisit_dgps_download_data, null, contentValues);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return id;
    }
    public long insertRevisitDGPSSurveyPillarData(M_revisit_dgps_pill_data mpr) {
        long id = 0;
        mDb.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("d_id", mpr.getD_id());
            contentValues.put("r_id", mpr.getR_id());
            contentValues.put("fb_id", mpr.getFb_id());
            contentValues.put("pill_no", mpr.getPill_no());
            contentValues.put("job_id", mpr.getJob_id());
            contentValues.put("u_id", mpr.getU_id());
            contentValues.put("survey_durn", mpr.getSurvey_durn());
            contentValues.put("f_pic_status", mpr.getF_pic_status());
            contentValues.put("b_pic_status", mpr.getB_pic_status());
            contentValues.put("i_pic_status", mpr.getI_pic_status());
            contentValues.put("o_pic_status", mpr.getO_pic_status());
            contentValues.put("div_pic_status", mpr.getDiv_pic_status());
            contentValues.put("patch_no", mpr.getPatch_no());
            contentValues.put("ring_no", mpr.getRing_no());
            contentValues.put("forest_person", mpr.getForest_person());
            contentValues.put("surveyor_name", mpr.getSurveyor_name());
            contentValues.put("survey_time", mpr.getSurvey_time());
            contentValues.put("div_name", mpr.getDiv_name());
            contentValues.put("range_name", mpr.getRange_name());
            contentValues.put("fb_name", mpr.getFb_name());
            contentValues.put("sync_status", mpr.getSync_status());
            contentValues.put("ack_status", mpr.getAck_status());
            contentValues.put("delete_status", mpr.getDelete_status());
            contentValues.put("survey_segment", mpr.getSurvey_segment());
            contentValues.put("completion_sts", mpr.getCompletion_sts());
            contentValues.put("f_pic_name", mpr.getF_pic_name());
            contentValues.put("b_pic_name", mpr.getB_pic_name());
            contentValues.put("i_pic_name", mpr.getI_pic_name());
            contentValues.put("o_pic_name", mpr.getO_pic_name());
            contentValues.put("div_pic_name", mpr.getDiv_pic_name());
            contentValues.put("device_imei_no", mpr.getDevice_imei_no());
            contentValues.put("pillar_sfile_path", mpr.getPillar_sfile_path());
            contentValues.put("pillar_sfile_status", mpr.getPillar_sfile_status());
            contentValues.put("frjvc_lat", mpr.getFrjvc_lat());
            contentValues.put("frjvc_long", mpr.getFrjvc_long());
            contentValues.put("d_pill_no", mpr.getD_pill_no());
            contentValues.put("d_old_id", mpr.getD_old_id());
            contentValues.put("pillar_rfile_path", mpr.getPillar_rfile_path());
            contentValues.put("pillar_rfile_status", mpr.getPillar_rfile_status());
            contentValues.put("completion_status", mpr.getCompletion_status());
            contentValues.put("rtx_survey_min", mpr.getRtx_min());
            contentValues.put("rtx_survey_second", mpr.getRtx_sec());
            contentValues.put("survey_status", mpr.getSurvey_status());
            contentValues.put("reason", mpr.getReason());
            contentValues.put("remark", mpr.getRemark());
            contentValues.put("pillar_jfile_path", mpr.getPillar_jfile_path());
            contentValues.put("pillar_jfile_status", mpr.getPillar_jfile_status());
            id = mDb.insert(m_fb_revisit_dgps_survey_pill_data, null, contentValues);
            mDb.setTransactionSuccessful();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return id;
    }
    public long insertRevisitDGPSSurveyPillarPic(M_revisit_dgps_pill_pic mpr) {
        long id = 0;
        mDb.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("pic_pill_no", mpr.getPic_pill_no());
            contentValues.put("u_id", mpr.getU_id());
            contentValues.put("pic_status", mpr.getPic_status());
            contentValues.put("pic_name", mpr.getPic_name());
            contentValues.put("pic_view", mpr.getPic_view());
            id = mDb.insert(m_fb_dgps_survey_pill_pic, null, contentValues);
            mDb.setTransactionSuccessful();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
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
        res.close();
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
        ArrayList<String> array_list = null;
        try {
            array_list = new ArrayList<String>();
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
            Cursor res = mDb.rawQuery("select m_fb_cmv_path,m_fb_mmv_path from m_fb where m_fb_range_id='" + rid + "' and div_id='" + divid + "' and m_fb_id='" + fid + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("m_fb_cmv_path")));
                array_list.add(res.getString(res.getColumnIndex("m_fb_mmv_path")));
                res.moveToNext();
            }
            res.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return array_list;
    }

    public ArrayList getCMVFiles(String divid, String rid, String fid) {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
            Cursor res = mDb.rawQuery("select m_fb_cmv_path from m_fb where m_fb_range_id='" + rid + "' and div_id='" + divid + "' and m_fb_id='" + fid + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("m_fb_cmv_path")));
                res.moveToNext();
            }
            res.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return array_list;
    }

    public ArrayList getMMVFiles(String divid, String rid, String fid) {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
            Cursor res = mDb.rawQuery("select m_fb_mmv_path from m_fb where m_fb_range_id='" + rid + "' and div_id='" + divid + "' and m_fb_id='" + fid + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("m_fb_mmv_path")));
                res.moveToNext();
            }
            res.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return array_list;
    }

    public ArrayList getCMVMMVFiles(String divid) {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
            Cursor res = mDb.rawQuery("select m_fb_cmv_path,m_fb_mmv_path from m_fb where div_id='" + divid + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("m_fb_cmv_path")));
                array_list.add(res.getString(res.getColumnIndex("m_fb_mmv_path")));
                res.moveToNext();
            }
            res.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return array_list;
    }

    public int getPillarData(String fb_id) {
        ArrayList<String> array_list = new ArrayList<String>();
        int slno = 0;
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);COALESCE(MAX(p_no),0)+1 as slno
            Cursor res = mDbHelper.getReadableDatabase().rawQuery("select COALESCE(MAX(point_no),0)+1 as slno from m_pillar_reg where fb_id='" + fb_id + "'", null);
            if (res == null) {
                slno = 1;
            } else {
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    slno = res.getInt(res.getColumnIndex("slno"));
                    res.moveToNext();
                }
                res.close();
                //slno=array_list[0];
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return slno;
    }

    public ArrayList getCMVFilesForDownload(String divid, String rid, String fid) {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
            Cursor res = mDb.rawQuery("select m_fb_cmv_path from m_fb where m_fb_range_id='" + rid + "' and div_id='" + divid + "' and m_fb_id='" + fid + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("m_fb_cmv_path")));
                res.moveToNext();
            }
            res.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return array_list;
    }

    public ArrayList getMMVFilesForDownload(String divid, String rid, String fid) {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
            Cursor res = mDb.rawQuery("select m_fb_mmv_path from m_fb where m_fb_range_id='" + rid + "' and div_id='" + divid + "' and m_fb_id='" + fid + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("m_fb_mmv_path")));
                res.moveToNext();
            }
            res.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return array_list;
    }

    public ArrayList getUpdatedPillarPointFiles(String divid, String rid, String fid) {
        ArrayList<String> array_list = new ArrayList<String>();
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);
            Cursor res = mDb.rawQuery("select m_fb_updated_pillar_kml from m_fb where m_fb_range_id='" + rid + "' and div_id='" + divid + "' and m_fb_id='" + fid + "'", null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex("m_fb_updated_pillar_kml")));
                res.moveToNext();
            }
            res.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return array_list;
    }

    public int getDGPSPillarData(String fb_id) {
        ArrayList<String> array_list = new ArrayList<String>();
        int slno = 0;
        try {
            //mDb.rawQuery("update m_fb set m_fb_cmv_path='cmv_1_1_AN1_1.kml' where m_fb_cmv_path IS NOT NULL and m_fb_cmv_path<>''",null);COALESCE(MAX(p_no),0)+1 as slno
            Cursor res = mDbHelper.getReadableDatabase().rawQuery("select COALESCE(MAX(pill_no),0)+1 as slno from m_fb_dgps_survey_pill_data where fb_id='" + fb_id + "'", null);
            if (res == null) {
                slno = 1;
            } else {
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    slno = res.getInt(res.getColumnIndex("slno"));
                    res.moveToNext();
                }
                res.close();//slno=array_list[0];
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return slno;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            db.setForeignKeyConstraintsEnabled(true);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_m_range_Table);
            db.execSQL(CREATE_m_fb_Table);
            db.execSQL(CREATE_m_pillar_reg_Table);
            db.execSQL(CREATE_m_shifting_pillar_reg_Table);
            db.execSQL(CREATE_m_fb_Survey_pill_data);
            db.execSQL(CREATE_m_dgps_Survey_pill_data);
            db.execSQL(CREATE_m_survey_pillar_reg_Table);
            db.execSQL(CREATE_m_fb_dgps_survey_pill_data);
            db.execSQL(CREATE_m_fb_dgps_survey_pill_pic);
            db.execSQL(CREATE_m_revisit_dgps_download_data);
            db.execSQL(CREATE_m_fb_revisit_dgps_survey_pill_data);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + m_range);
            db.execSQL("DROP TABLE IF EXISTS " + m_fb);
            db.execSQL("DROP TABLE IF EXISTS " + m_pillar_reg);
            db.execSQL("DROP TABLE IF EXISTS " + m_shifting_pillar_reg);
            db.execSQL("DROP TABLE IF EXISTS " + m_fb_Survey_pill_data);
            db.execSQL("DROP TABLE IF EXISTS " + m_dgps_Survey_pill_data);
            db.execSQL("DROP TABLE IF EXISTS " + m_survey_pillar_reg);
            db.execSQL("DROP TABLE IF EXISTS " + m_fb_dgps_survey_pill_data);
            db.execSQL("DROP TABLE IF EXISTS " + m_fb_dgps_survey_pill_pic);
            db.execSQL("DROP TABLE IF EXISTS " + m_revisit_dgps_download_data);
            db.execSQL("DROP TABLE IF EXISTS " + m_fb_revisit_dgps_survey_pill_data);
            onCreate(db);
        }
    }
}
