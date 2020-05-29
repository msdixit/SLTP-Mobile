package com.sparc.frjvcapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sparc.frjvcapp.pojo.M_dgps_pilldata;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;

public class DGPSViewPillarDetailActivity extends AppCompatActivity {

    public static final String data = "data";
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String PNG_FILE_SUFFIX = ".jpg";
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private static final int ACTION_TAKE_GALLERY_PIC = 0;
    public String imgValue = "blank";
    String[] duration = {"Instant", "5", "15", "30", "Manual"};
    String[] survey_segment = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    MaterialSpinner bpduration, dpSegment;
    EditText edttxtpillarno, remark, edttxtpatchno, edttxtringno, edtForestoffnm, edtJobID, edtDirection, edtdpillno;
    ImageView setpicforward, setpicbackward, setpicinward, setpicoutward, setpictop;
    TextView txtViewdiv, txtViewran, txtViewfb, fbname;
    LinearLayout lh1;
    String sharediv, sharerange, sharefb, sharefbtype, sharefbname,
            userid, jobid, div_name, range_name, fb_name, spinner_duration, spinner_segment, id, d_frjvc_lat, d_frjvc_long,
            d_frjvc_pill_no, imagepath1_F, imagepath1_B, imagepath1_I, imagepath1_O, imagepath1_T, d_check_sts, _startTime, _endTime;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    String image1;
    int clickedStatus = 0;
    DbHelper dbHelper;
    SharedPreferences shared;
    SQLiteDatabase db;
    TextView etName;
    //ImageView refresh;
    int point_no;
    String kmlstatus;
    //set the image
    private String mCurrentPhotoPath_F, mCurrentPhotoPath_B, mCurrentPhotoPath_I, mCurrentPhotoPath_O, mCurrentPhotoPath_T;
    private Character pic_status;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    int targetW;
    int targetH;
    private String imagepath1;
    int timecheck = 0;
    SimpleDateFormat _startDateFormat, _endDateFormat;
    long _min, _second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpsview_pillar_detail);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
       /* bpduration = findViewById(R.id.bpduration);*/
        edttxtpillarno = findViewById(R.id.edttxtpillarno);
        remark = findViewById(R.id.remark);
        edttxtpatchno = findViewById(R.id.edttxtpatchno);
        edttxtringno = findViewById(R.id.edttxtringno);
        edtJobID = findViewById(R.id.edtJobID);
        edtdpillno = findViewById(R.id.edtdpillno);
        edtDirection = findViewById(R.id.edtDirection);
        edtForestoffnm = findViewById(R.id.edtForestoffnm);

        //Image View
        setpicforward = findViewById(R.id.setpicforward);
        setpicbackward = findViewById(R.id.setpicbackward);
        setpicinward = findViewById(R.id.setpicinward);
        setpicoutward = findViewById(R.id.setpicoutward);
        setpictop = findViewById(R.id.setpictop);

        //TextView
        txtViewdiv = findViewById(R.id.txtViewdiv);
        txtViewran = findViewById(R.id.txtViewran);
        txtViewfb = findViewById(R.id.txtViewfb);
        fbname = findViewById(R.id.fbname);
        /*  txtViewusername=findViewById(R.id.txtViewusername);*/

        //Layout
        lh1 = findViewById(R.id.lh1);
        /*  lh2=findViewById(R.id.lh2);*/
        /* lh3=findViewById(R.id.lh3);*/

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

        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        id = i.getStringExtra("id");
        d_frjvc_lat = i.getStringExtra("lat");
        d_frjvc_long = i.getStringExtra("lon");

        txtViewdiv.setText(div_name);
        txtViewran.setText(range_name);
        txtViewfb.setText(fb_name);

        (findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(getApplicationContext(), DGPSMapViewActivity.class);
                i.putExtra("kml_status", kmlstatus);
                // finishAffinity();
                startActivity(i);


            }
        });
        (findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();


            }
        });
        getDataforUpdate(d_frjvc_lat, d_frjvc_long, userid);
    }

    private void getDataforUpdate(String d_frjvc_lat, String d_frjvc_long, String userid) {
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and r_id='" + sharerange + "' and fb_id='" + sharefb + "' and frjvc_lat='" + d_frjvc_lat + "' and frjvc_long='" + d_frjvc_long + "'", null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        if(Integer.parseInt(cursor.getString(cursor.getColumnIndex("pndjv_pill_no")))!=0)
                        {
                            edttxtpillarno.setText(cursor.getString(cursor.getColumnIndex("pill_no"))+"-"+cursor.getString(cursor.getColumnIndex("pndjv_pill_no")));
                        }else {
                            edttxtpillarno.setText(cursor.getString(cursor.getColumnIndex("pill_no")));
                        }
                        edttxtpatchno.setText(cursor.getString(cursor.getColumnIndex("patch_no")));
                        edttxtringno.setText(cursor.getString(cursor.getColumnIndex("ring_no")));
                        edtDirection.setText(cursor.getString(cursor.getColumnIndex("survey_durn")));
                        edtdpillno.setText(cursor.getString(cursor.getColumnIndex("d_pill_no")));
                        edtForestoffnm.setText(cursor.getString(cursor.getColumnIndex("forest_person")));
                        edtJobID.setText(cursor.getString(cursor.getColumnIndex("job_id")));
                        setImageF(cursor.getString(cursor.getColumnIndex("f_pic_name")));
                        setImageB(cursor.getString(cursor.getColumnIndex("b_pic_name")));
                        setImageI(cursor.getString(cursor.getColumnIndex("i_pic_name")));
                        setImageO(cursor.getString(cursor.getColumnIndex("o_pic_name")));
                        setImageT(cursor.getString(cursor.getColumnIndex("div_pic_name")));
                    } while (cursor.moveToNext());
                }

            }
            cursor.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {

        }
    }

    private void setImageF(String path) {

        targetW = setpicforward.getWidth();
        targetH = setpicforward.getHeight();

        imagepath1 = path;
        clickedStatus = 1;

        int targetW = setpicforward.getWidth();
        int targetH = setpicforward.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        /*Rotate the file according to the device to portait mode*/
        Bitmap bmp = rotatePhoto(path, bitmap);
        /* Associate the Bitmap to the ImageView */

        if (setpicforward.getDrawable() == null) {


            setpicforward.setImageBitmap(bmp);
        } else {
            setpicforward.setImageBitmap(bmp);
        }
        //assign value to the image variable
        imgValue = "captured";
    }

    private void setImageB(String path) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */


        // if (personImg.getDrawable() == null) {
        targetW = setpicbackward.getWidth();
        targetH = setpicbackward.getHeight();

        imagepath1 = path;
        clickedStatus = 1;
        Log.d("ImagePath", "1" + imagepath1);
        //  } else {
        //   Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
        //  }

        int targetW = setpicbackward.getWidth();
        int targetH = setpicbackward.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        /*Rotate the file according to the device to portait mode*/
        Bitmap bmp = rotatePhoto(path, bitmap);
        /* Associate the Bitmap to the ImageView */

        if (setpicbackward.getDrawable() == null) {


            setpicbackward.setImageBitmap(bmp);
        } else {
            setpicbackward.setImageBitmap(bmp);
        }
        //assign value to the image variable
        imgValue = "captured";
    }

    private void setImageI(String path) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */


        // if (personImg.getDrawable() == null) {
        targetW = setpicinward.getWidth();
        targetH = setpicinward.getHeight();

        imagepath1 = path;
        clickedStatus = 1;
        Log.d("ImagePath", "1" + imagepath1);
        //  } else {
        //   Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
        //  }

        int targetW = setpicinward.getWidth();
        int targetH = setpicinward.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        /*Rotate the file according to the device to portait mode*/
        Bitmap bmp = rotatePhoto(path, bitmap);
        /* Associate the Bitmap to the ImageView */

        if (setpicinward.getDrawable() == null) {


            setpicinward.setImageBitmap(bmp);
        } else {
            setpicinward.setImageBitmap(bmp);
        }
        //assign value to the image variable
        imgValue = "captured";
    }

    private void setImageO(String path) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */


        // if (personImg.getDrawable() == null) {
        targetW = setpicoutward.getWidth();
        targetH = setpicoutward.getHeight();

        imagepath1 = path;
        clickedStatus = 1;
        Log.d("ImagePath", "1" + imagepath1);
        //  } else {
        //   Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
        //  }

        int targetW = setpicoutward.getWidth();
        int targetH = setpicoutward.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        /*Rotate the file according to the device to portait mode*/
        Bitmap bmp = rotatePhoto(path, bitmap);
        /* Associate the Bitmap to the ImageView */

        if (setpicoutward.getDrawable() == null) {


            setpicoutward.setImageBitmap(bmp);
        } else {
            setpicoutward.setImageBitmap(bmp);
        }
        //assign value to the image variable
        imgValue = "captured";
    }

    private void setImageT(String path) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */


        // if (personImg.getDrawable() == null) {
        targetW = setpictop.getWidth();
        targetH = setpictop.getHeight();

        imagepath1 = path;
        clickedStatus = 1;
        Log.d("ImagePath", "1" + imagepath1);
        //  } else {
        //   Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
        //  }

        int targetW = setpictop.getWidth();
        int targetH = setpictop.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        /*Rotate the file according to the device to portait mode*/
        Bitmap bmp = rotatePhoto(path, bitmap);
        /* Associate the Bitmap to the ImageView */

        if (setpictop.getDrawable() == null) {


            setpictop.setImageBitmap(bmp);
        } else {
            setpictop.setImageBitmap(bmp);
        }
        //assign value to the image variable
        imgValue = "captured";
    }

    private Bitmap rotatePhoto(String photoPath, Bitmap bitmap) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                return rotatedBitmap;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                return rotatedBitmap;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                return rotatedBitmap;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
                return rotatedBitmap;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void SaveData() {
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor c = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and r_id='" + sharerange + "' and fb_id='" + sharefb + "' and frjvc_lat='" + d_frjvc_lat + "' and frjvc_long='" + d_frjvc_long + "'", null);
            if (c.getCount() >= 0) {
                if (c.moveToFirst()) {
                    String dd = c.getString(c.getColumnIndex("pillar_sfile_status"));
                    if (Integer.parseInt(c.getString(c.getColumnIndex("pillar_sfile_status"))) == 0) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        //alertDialogBuilder.setMessage("Are you sure to save this pillar data?");
                        final View customLayout = getLayoutInflater().inflate(R.layout.save_custome_dialod_register_pillar, null);
                        alertDialogBuilder.setView(customLayout);
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        int sl = Integer.parseInt(edttxtpillarno.getText().toString());
                                        String jobID = edtJobID.getText().toString();
                                        if (UpdatePillarSurveyTime(userid, d_frjvc_lat, d_frjvc_long)) {
                                            try {
                                                //Calculate Start Time
                                                timecheck = 1;
                                                Calendar c = Calendar.getInstance();
                                                System.out.println("Current time =&gt; " + c.getTime());
                                                _startDateFormat = new SimpleDateFormat("hh:mm:ss");
                                                _startTime = _startDateFormat.format(c.getTime());

                                                //Intent to Survey Mobile application for Survey
                                                ClipboardManager cm = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("JobID", jobID);
                                                cm.setPrimaryClip(clipData);
                                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("SurveyMobile.Droid");
                                                if (launchIntent != null) {
                                                    startActivity(launchIntent);
                                                } else {
                                                    Toast.makeText(DGPSViewPillarDetailActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                                                }

                                            } catch (Exception ee) {
                                                ee.printStackTrace();
                                            } finally {
                                                if (dbHelper != null) {
                                                    dbHelper.close();
                                                }
                                            }
                                        } else {

                                        }
                                    }

                                });

                        alertDialogBuilder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Toast.makeText(getApplicationContext(), "You canceled the save request...please try again", Toast.LENGTH_LONG).show();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else if (Integer.parseInt(c.getString(c.getColumnIndex("pillar_sfile_status"))) == 1) {
                        Toast.makeText(this, "This pillar is already registered and Tagged.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "This pillar is already registered and Tagged.", Toast.LENGTH_LONG).show();
                    }

                }

            }
            c.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Calculate and storage of RTX time for Report generation
        if (timecheck != 0) {
            try {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time =&gt; " + c.getTime());
                _endDateFormat = new SimpleDateFormat("hh:mm:ss");
                _endTime = _endDateFormat.format(c.getTime());
                java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
                java.util.Date date1 = df.parse(_startTime);
                java.util.Date date2 = df.parse(_endTime);
                long diff = date2.getTime() - date1.getTime();
                _min = diff / (60 * 1000) % 60;
                _second = diff / 1000 % 60;
                if ((_min >= 4 && _second >= 30) || (_min >= 14 && _second >= 30)) {
                    _min += 1;
                }
                db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
                Cursor cursor = db.rawQuery("update m_fb_dgps_survey_pill_data set rtx_survey_min='" + _min + "',rtx_survey_second='" + _second + "' where u_id='" + userid + "' and pill_no='" + edttxtpillarno.getText().toString() + "' and frjvc_lat='" + d_frjvc_lat + "' and frjvc_long='" + d_frjvc_long + "'", null);
                if (cursor.getCount() >= 0) {
                    Toast.makeText(this, "Observation time has been updated to this pillar..", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
                db.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            } finally {
                timecheck = 0;
            }
        }
        //point_no = getSLNO(sharefb);
        //edttxtpillarno.setText(String.valueOf(point_no));
    }

    private boolean UpdatePillarSurveyTime(String userid, String d_frjvc_lat, String d_frjvc_long) {
        boolean b = false;
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            Cursor c = db.rawQuery("update m_fb_dgps_survey_pill_data set survey_time='" + formatter.format(date) + "' where frjvc_lat='" + d_frjvc_lat + "' and frjvc_long='" + d_frjvc_long + "' and u_id='" + userid + "'", null);
            if (c.getCount() >= 0) {
                b = true;
            }
            c.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return b;
    }
}