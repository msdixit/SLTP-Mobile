package com.sparc.frjvcapp;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sparc.frjvcapp.pojo.M_dgps_pill_pic;
import com.sparc.frjvcapp.pojo.M_dgps_pilldata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

public class DGPSDataCollectActivity extends AppCompatActivity {
    public static final String data = "data";
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String PNG_FILE_SUFFIX = ".jpg";
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private static final int ACTION_TAKE_GALLERY_PIC = 0;
    public String imgValue = "blank";
    String[] duration = {"Clockwise", "Anticlockwise"};
    String[] survey_segment = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    MaterialSpinner bpduration, dpSegment;
    EditText edttxtpillarno, remark, edttxtpatchno, edttxtringno, edtForestoffnm, edtJobID, edtdpillno;
    ImageView setpicforward, takepicforward, setpicbackward, takepicbackward, setpicinward, takepicinward, setpicoutward, takepicoutward, setpictop, takepictop;
    TextView txtViewdiv, txtViewran, txtViewfb, fbname;
    LinearLayout lh1;
    String sharediv, sharerange, sharefb, sharefbtype, sharefbname,
            userid, jobid, div_name, range_name, fb_name, spinner_duration, spinner_segment, id, d_frjvc_lat, d_frjvc_long,d_old_id,
            d_frjvc_pill_no, imagepath1_F, imagepath1_B, imagepath1_I, imagepath1_O, imagepath1_T, d_check_sts;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    Map<Character, String> image_name;
    String imei;
    int clickedStatus = 0;
    DbHelper dbHelper;
    SharedPreferences shared;

    SQLiteDatabase db;
    TextView etName;
    int point_no;
    String kmlstatus;
    private String mCurrentPhotoPath_F, mCurrentPhotoPath_B, mCurrentPhotoPath_I, mCurrentPhotoPath_O, mCurrentPhotoPath_T;
    private Character pic_status;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpsdata_collect);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        dbHelper = new DbHelper(getApplicationContext());
        //Matterial Spinner

        bpduration = findViewById(R.id.bpduration);

        //EditText
        edttxtpillarno = findViewById(R.id.edttxtpillarno);
        remark = findViewById(R.id.remark);
        edttxtpatchno = findViewById(R.id.edttxtpatchno);
        edttxtringno = findViewById(R.id.edttxtringno);
        edtJobID = findViewById(R.id.edtJobID);
        edtdpillno = findViewById(R.id.edtdpillno);
        edtForestoffnm = findViewById(R.id.edtForestoffnm);

        //Image View
        setpicforward = findViewById(R.id.setpicforward);
        takepicforward = findViewById(R.id.takepicforward);
        setpicbackward = findViewById(R.id.setpicbackward);
        takepicbackward = findViewById(R.id.takepicbackward);
        setpicinward = findViewById(R.id.setpicinward);
        takepicinward = findViewById(R.id.takepicinward);
        setpicoutward = findViewById(R.id.setpicoutward);
        takepicoutward = findViewById(R.id.takepicoutward);
        setpictop = findViewById(R.id.setpictop);
        takepictop = findViewById(R.id.takepictop);

        //TextView
        txtViewdiv = findViewById(R.id.txtViewdiv);
        txtViewran = findViewById(R.id.txtViewran);
        txtViewfb = findViewById(R.id.txtViewfb);
        fbname = findViewById(R.id.fbname);

        lh1 = findViewById(R.id.lh1);

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

        fbname.setText(sharefbname + " " + sharefbtype);
        edttxtpatchno.setText("1");
        edttxtringno.setText("0");
        edtJobID.setText(jobid);
        //point_no = getSLNO(sharefb);


        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        id = i.getStringExtra("id");
        d_frjvc_lat = i.getStringExtra("lat");
        d_frjvc_long = i.getStringExtra("lon");
        d_frjvc_pill_no = i.getStringExtra("pill_no");
        d_old_id= i.getStringExtra("old_id");
        /*  d_check_sts = i.getStringExtra("checksts");*/
        point_no = Integer.parseInt(d_frjvc_pill_no);
        edttxtpillarno.setText(String.valueOf(point_no));


        txtViewdiv.setText(div_name);
        txtViewran.setText(range_name);
        txtViewfb.setText(fb_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei  = getUniqueIMEIId(this);
        }else{

        }

        (findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(getApplicationContext(), DGPSMapViewActivity.class);
                i.putExtra("kml_status", kmlstatus);
                /* i.putExtra("check_sts", d_check_sts);*/
                startActivity(i);
            }
        });
        (findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });

        takepicforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (setpicforward.getDrawable() == null) {
                        SelectImage();
                    } else {
                        if (setpicforward.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp).getConstantState()) {
                            setpicforward.setImageResource(0);
                            SelectImage();
                        } else {
                            SelectImage();
                        }
                    }
                }
            }
        });
        takepicbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (setpicbackward.getDrawable() == null) {
                        SelectImageBackward();
                    } else {
                        if (setpicbackward.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp).getConstantState()) {
                            setpicbackward.setImageResource(0);
                            SelectImageBackward();
                        } else {
                            SelectImageBackward();
                        }
                    }
                }
            }
        });
        takepicinward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (setpicinward.getDrawable() == null) {
                        SelectImageInward();
                    } else {
                        if (setpicinward.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp).getConstantState()) {
                            setpicinward.setImageResource(0);
                            SelectImageInward();
                        } else {
                            SelectImageInward();
                        }
                    }
                }
            }
        });
        takepicoutward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (setpicoutward.getDrawable() == null) {
                        SelectImageOutward();
                    } else {
                        if (setpicoutward.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp).getConstantState()) {
                            setpicoutward.setImageResource(0);
                            SelectImageOutward();
                        } else {
                            SelectImageOutward();
                        }
                    }
                }
            }
        });
        takepictop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (setpictop.getDrawable() == null) {
                        SelectImageTop();
                    } else {
                        if (setpictop.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp).getConstantState()) {
                            setpictop.setImageResource(0);
                            SelectImageTop();
                        } else {
                            SelectImageTop();
                        }
                    }
                }
            }
        });

        final ArrayAdapter<String> durationadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, duration);
        bpduration.setAdapter(durationadapter);
        bpduration.setPaddingSafe(0, 0, 0, 0);
        bpduration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_duration = (String) parent.getItemAtPosition(position);
                if (!spinner_duration.equals("Select Duration")) {
                    //CheckPillarStatus(locationtype);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*final ArrayAdapter<String> segmentadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, survey_segment);
        dpSegment.setAdapter(segmentadapter);
        dpSegment.setPaddingSafe(0, 0, 0, 0);
        dpSegment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_segment = (String) parent.getItemAtPosition(position);
                if (!spinner_segment.equals("Select Segment Type")) {
                    //CheckPillarStatus(locationtype);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //point_no = getSLNO(sharefb);
        //edttxtpillarno.setText(String.valueOf(point_no));
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ChooseSurvetTypeActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

    private int getSLNO(String sharefb) {
        //List<String> fbName = new ArrayList<String>();
        //return dbHelper.getPillarData(sharefb);
        return dbHelper.getDGPSPillarData(sharefb);
    }

    private void SaveData() {

        if (edttxtpillarno.getText().toString() == "" || edttxtpillarno.getText().toString() == "0") {
            Toast.makeText(this, "Serial Number can not ne blank or Zero", Toast.LENGTH_LONG).show();
        } else if (spinner_duration.equals("Select Duration")) {
            Toast.makeText(this, "Please Select Duration", Toast.LENGTH_LONG).show();
        } else if (edtForestoffnm.getText().toString() == "") {
            Toast.makeText(this, "Please Provide the Forest official name", Toast.LENGTH_LONG).show();
        } else if (imagepath1_F == "" || imagepath1_F == null) {
            Toast.makeText(this, "Front view of pillar is not available", Toast.LENGTH_LONG).show();
        } else if (imagepath1_B == "" || imagepath1_B == null) {
            Toast.makeText(this, "Back view of pillar is not available", Toast.LENGTH_LONG).show();
        } else if (imagepath1_I == "" || imagepath1_I == null) {
            Toast.makeText(this, "Inward view of pillar is not available", Toast.LENGTH_LONG).show();
        } else if (imagepath1_O == "" || imagepath1_O == null) {
            Toast.makeText(this, "Outward view of pillar is not available", Toast.LENGTH_LONG).show();
        } else if (imagepath1_T == "" || imagepath1_T == null) {
            Toast.makeText(this, "Withdevice view of pillar is not available", Toast.LENGTH_LONG).show();
        } else {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor c = db.rawQuery("select * from m_fb_dgps_survey_pill_data where u_id='" + userid + "' and d_id='" + sharediv + "' and r_id='" + sharerange + "' and fb_id='" + sharefb + "' and frjvc_lat='" + d_frjvc_lat + "' and frjvc_long='" + d_frjvc_long + "'", null);
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    if (Integer.parseInt(c.getString(c.getColumnIndex("pillar_sfile_status"))) == 0) {
                        Toast.makeText(this, "This pillar is already registered.Please tag the pillar with its Static Observation data.", Toast.LENGTH_LONG).show();
                    } else if (Integer.parseInt(c.getString(c.getColumnIndex("pillar_sfile_status"))) == 1) {
                        Toast.makeText(this, "This pillar is already registered and Tagged.", Toast.LENGTH_LONG).show();
                    } else {

                    }
                }
            } else {
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
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                M_dgps_pilldata mpr1 = new M_dgps_pilldata(sharediv, sharerange, sharefb, sl, jobID, userid, spinner_duration,
                                        "0", "0", "0", "0", "0",
                                        edttxtpatchno.getText().toString(), edttxtringno.getText().toString(), edtForestoffnm.getText().toString(),
                                        userid, formatter.format(date), txtViewdiv.getText().toString(), txtViewran.getText().toString(),
                                        txtViewfb.getText().toString(), "0", "0", "0", "",
                                        "", imagepath1_F, imagepath1_B, imagepath1_I, imagepath1_O, imagepath1_T, imei, "", "0"
                                        , d_frjvc_lat, d_frjvc_long, edtdpillno.getText().toString(),d_old_id,"","0","0");//+"_"+pilshiftsts,surdir,accuracy
                                try {
                                    dbHelper.open();
                                    long status = dbHelper.insertDGPSSurveyPillarData(mpr1);
                                    dbHelper.close();
                                    if (status >= 0) {
                                        if (checkDGPSDataAvalability()) {
                                            image_name = new HashMap<Character, String>();
                                            image_name.put('F', imagepath1_F);
                                            image_name.put('B', imagepath1_B);
                                            image_name.put('I', imagepath1_I);
                                            image_name.put('O', imagepath1_O);
                                            image_name.put('T', imagepath1_T);
                                            long a = insertDGPSImage((HashMap<Character, String>) image_name, userid, Integer.parseInt(d_old_id));
                                            if (a == 5) {
                                                ClipboardManager cm = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clipData = ClipData.newPlainText("JobID", jobID);
                                                cm.setPrimaryClip(clipData);
                                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("SurveyMobile.Droid");
                                                if (launchIntent != null) {
                                                    // finishAffinity();
                                                    reset();
                                                    startActivity(launchIntent);
                                                } else {
                                                    Toast.makeText(DGPSDataCollectActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Pillar pictures is not stored", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "NoT Pillar registered Successfully", Toast.LENGTH_LONG).show();
                                        }
                                    }


                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                } finally {
                                    if (dbHelper != null) {
                                        dbHelper.close();
                                    }
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
            }
        }
    }

    private int insertDGPSImage(HashMap<Character, String> image_name, String userid, int sl) {
        long status = 0;
        int count = 0;
        try {
            for (Map.Entry entry : image_name.entrySet()) {
                M_dgps_pill_pic mpic = new M_dgps_pill_pic(sl, userid, "0", entry.getValue().toString(), entry.getKey().toString());
                dbHelper.open();
                status = dbHelper.insertDGPSSurveyPillarPic(mpic);
                if (status > 0) {
                    count += 1;
                }
                dbHelper.close();
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return count;
    }

    private boolean checkDGPSDataAvalability() {
        boolean b = false;
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor c = db.rawQuery("update m_dgps_Survey_pill_data set m_dgps_surv_sts='1' where m_p_lat='" + d_frjvc_lat + "' and m_p_long='" + d_frjvc_long + "'", null);
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

    public static String getUniqueIMEIId(Context context) {
        String imei="";
       /* try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = telephonyManager.getImei();
                }else{
                    imei=telephonyManager.getDeviceId();
                }
            }
            *//*Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return Build.SERIAL;
            }*//*
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String uniqueID = UUID.randomUUID().toString();
        return imei;
    }

    private void reset() {
        imagepath1_F = null;
        imagepath1_B = null;
        imagepath1_I = null;
        imagepath1_O = null;
        imagepath1_T = null;
        mCurrentPhotoPath_F = null;
        mCurrentPhotoPath_B = null;
        mCurrentPhotoPath_I = null;
        mCurrentPhotoPath_O = null;
        mCurrentPhotoPath_T = null;
        edtForestoffnm.setText("");
        edtdpillno.setText("");
        /* dpSegment.setSelection(0);*/
        bpduration.setSelection(0);
        setpicoutward.setImageResource(0);
        setpictop.setImageResource(0);
        setpicbackward.setImageResource(0);
        setpicforward.setImageResource(0);
        setpicinward.setImageResource(0);
    }

    private void SelectImage() {
        final CharSequence[] items = {"Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    dispatchTakePictureIntent(1, 'F', edttxtpillarno.getText().toString());
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void SelectImageBackward() {
        final CharSequence[] items = {"Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    dispatchTakePictureIntent(1, 'B', edttxtpillarno.getText().toString());
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void SelectImageInward() {
        final CharSequence[] items = {"Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    dispatchTakePictureIntent(1, 'I', edttxtpillarno.getText().toString());
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void SelectImageOutward() {
        final CharSequence[] items = {"Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    dispatchTakePictureIntent(1, 'O', edttxtpillarno.getText().toString());
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void SelectImageTop() {
        final CharSequence[] items = {"Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    dispatchTakePictureIntent(1, 'T', edttxtpillarno.getText().toString());
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent(int actionCode, Character character, String pll_no) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;
                if (character == 'F') {
                    try {
                        f = setUpPhotoFile(character, pll_no);
                        pic_status = character;
                        mCurrentPhotoPath_F = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath_F = null;
                    }
                    break;
                } else if (character == 'B') {
                    try {
                        f = setUpPhotoFile(character, pll_no);
                        pic_status = character;
                        mCurrentPhotoPath_B = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath_B = null;
                    }
                    break;
                } else if (character == 'I') {
                    try {
                        f = setUpPhotoFile(character, pll_no);
                        pic_status = character;
                        mCurrentPhotoPath_I = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath_I = null;
                    }
                    break;
                } else if (character == 'O') {
                    try {
                        f = setUpPhotoFile(character, pll_no);
                        pic_status = character;
                        mCurrentPhotoPath_O = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath_O = null;
                    }
                    break;
                } else if (character == 'T') {
                    try {
                        f = setUpPhotoFile(character, pll_no);
                        pic_status = character;
                        mCurrentPhotoPath_T = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath_T = null;
                    }
                    break;
                }

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile(Character character, String pill_no) throws IOException {
        File f = null;
        if (character == 'F') {
            f = createImageFile(character, pill_no);
            mCurrentPhotoPath_F = f.getAbsolutePath();
        } else if (character == 'B') {
            f = createImageFile(character, pill_no);
            mCurrentPhotoPath_B = f.getAbsolutePath();
        } else if (character == 'I') {
            f = createImageFile(character, pill_no);
            mCurrentPhotoPath_I = f.getAbsolutePath();
        } else if (character == 'O') {
            f = createImageFile(character, pill_no);
            mCurrentPhotoPath_O = f.getAbsolutePath();
        } else if (character == 'T') {
            f = createImageFile(character, pill_no);
            mCurrentPhotoPath_T = f.getAbsolutePath();
        }
        return f;
    }

    private File createImageFile(Character character, String pill_no) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_" + sharefb + "_" + character + "_" + pill_no + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName + sharefb, PNG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private String getAlbumName() {
        return "CameraSample";
    }

    //activity start result for camera new
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        // requestLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
//                break;
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    if (pic_status == 'F') {
                        handleBigCameraPhoto(pic_status);
                    } else if (pic_status == 'B') {
                        handleBigCameraPhoto(pic_status);
                    } else if (pic_status == 'I') {
                        handleBigCameraPhoto(pic_status);
                    } else if (pic_status == 'O') {
                        handleBigCameraPhoto(pic_status);
                    } else if (pic_status == 'T') {
                        handleBigCameraPhoto(pic_status);
                    }

                }
                break;
            } // ACTION_TAKE_PHOTO_B
            case ACTION_TAKE_GALLERY_PIC: {
                if (requestCode == SELECT_FILE) {
                    if (pic_status == 'F') {
                        Uri selectedImageUri = data.getData();
                        if (setpicforward.getDrawable() == null) {
                            setpicforward.setImageURI(selectedImageUri);
                        } else {
                            setpicforward.setImageURI(selectedImageUri);
                            // Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
                        }
                        mCurrentPhotoPath_F = selectedImageUri.toString();
                        imagepath1_F = mCurrentPhotoPath_F;
                        clickedStatus = 1;
                        if (Build.VERSION.SDK_INT >= 19) {
                            mCurrentPhotoPath_F = UtilityGetPath.getRealPathFromURI_API19(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        } else {
                            mCurrentPhotoPath_F = UtilityGetPath.getRealPathFromURI_API11to18(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        }
                    } else if (pic_status == 'B') {
                        Uri selectedImageUri = data.getData();
                        if (setpicbackward.getDrawable() == null) {
                            setpicbackward.setImageURI(selectedImageUri);
                        } else {
                            setpicbackward.setImageURI(selectedImageUri);
                            // Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
                        }
                        mCurrentPhotoPath_B = selectedImageUri.toString();
                        imagepath1_B = mCurrentPhotoPath_B;
                        clickedStatus = 1;
                        if (Build.VERSION.SDK_INT >= 19) {
                            mCurrentPhotoPath_B = UtilityGetPath.getRealPathFromURI_API19(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        } else {
                            mCurrentPhotoPath_B = UtilityGetPath.getRealPathFromURI_API11to18(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        }
                    } else if (pic_status == 'I') {
                        Uri selectedImageUri = data.getData();
                        if (setpicbackward.getDrawable() == null) {
                            setpicbackward.setImageURI(selectedImageUri);
                        } else {
                            setpicbackward.setImageURI(selectedImageUri);
                            // Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
                        }
                        mCurrentPhotoPath_I = selectedImageUri.toString();
                        imagepath1_I = mCurrentPhotoPath_I;
                        clickedStatus = 1;
                        if (Build.VERSION.SDK_INT >= 19) {
                            mCurrentPhotoPath_I = UtilityGetPath.getRealPathFromURI_API19(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        } else {
                            mCurrentPhotoPath_I = UtilityGetPath.getRealPathFromURI_API11to18(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        }
                    } else if (pic_status == 'O') {
                        Uri selectedImageUri = data.getData();
                        if (setpicbackward.getDrawable() == null) {
                            setpicbackward.setImageURI(selectedImageUri);
                        } else {
                            setpicbackward.setImageURI(selectedImageUri);
                            // Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
                        }
                        mCurrentPhotoPath_O = selectedImageUri.toString();
                        imagepath1_O = mCurrentPhotoPath_O;
                        clickedStatus = 1;
                        if (Build.VERSION.SDK_INT >= 19) {
                            mCurrentPhotoPath_O = UtilityGetPath.getRealPathFromURI_API19(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        } else {
                            mCurrentPhotoPath_O = UtilityGetPath.getRealPathFromURI_API11to18(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        }
                    } else if (pic_status == 'T') {
                        Uri selectedImageUri = data.getData();
                        if (setpicbackward.getDrawable() == null) {
                            setpicbackward.setImageURI(selectedImageUri);
                        } else {
                            setpicbackward.setImageURI(selectedImageUri);
                            // Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
                        }
                        mCurrentPhotoPath_T = selectedImageUri.toString();
                        imagepath1_T = mCurrentPhotoPath_T;
                        clickedStatus = 1;
                        if (Build.VERSION.SDK_INT >= 19) {
                            mCurrentPhotoPath_T = UtilityGetPath.getRealPathFromURI_API19(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        } else {
                            mCurrentPhotoPath_T = UtilityGetPath.getRealPathFromURI_API11to18(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        }
                    }
                }
            }
        } // switch
    }

    private void handleBigCameraPhoto(Character character) {
        if (character == 'F') {
            if (mCurrentPhotoPath_F != null) {
                String path = compressImage(mCurrentPhotoPath_F, character);
                galleryAddPic(path);
            }
        } else if (character == 'B') {
            if (mCurrentPhotoPath_B != null) {
                String path = compressImage(mCurrentPhotoPath_B, character);
                galleryAddPic(path);
            }
        } else if (character == 'I') {
            if (mCurrentPhotoPath_I != null) {
                String path = compressImage(mCurrentPhotoPath_I, character);
                galleryAddPic(path);
            }
        } else if (character == 'O') {
            if (mCurrentPhotoPath_O != null) {
                String path = compressImage(mCurrentPhotoPath_O, character);
                galleryAddPic(path);
            }
        } else if (character == 'T') {
            if (mCurrentPhotoPath_T != null) {
                String path = compressImage(mCurrentPhotoPath_T, character);
                galleryAddPic(path);
            }
        }
        /*if (mCurrentPhotoPath != null) {
            String path = compressImage(mCurrentPhotoPath);
            galleryAddPic(path);
        }*/

    }

    public String compressImage(String imageUri, Character character) {
        String filename = "";
        if (character == 'F') {
            imagepath1_F = imageUri;
            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
                if (setpicforward.getDrawable() == null) {
                    setpicforward.setImageBitmap(bmp);
                } else {
                    setpicforward.setImageBitmap(bmp);
                }
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            filename = getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (character == 'B') {
            imagepath1_B = imageUri;
            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
                if (setpicbackward.getDrawable() == null) {
                    setpicbackward.setImageBitmap(bmp);
                } else {
                    setpicbackward.setImageBitmap(bmp);
                }
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            filename = getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (character == 'I') {
            imagepath1_I = imageUri;
            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
                if (setpicinward.getDrawable() == null) {
                    setpicinward.setImageBitmap(bmp);
                } else {
                    setpicinward.setImageBitmap(bmp);
                }
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            filename = getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (character == 'O') {
            imagepath1_O = imageUri;
            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
                if (setpicoutward.getDrawable() == null) {
                    setpicoutward.setImageBitmap(bmp);
                } else {
                    setpicoutward.setImageBitmap(bmp);
                }
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            filename = getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (character == 'T') {
            imagepath1_T = imageUri;
            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
                if (setpictop.getDrawable() == null) {
                    setpictop.setImageBitmap(bmp);
                } else {
                    setpictop.setImageBitmap(bmp);
                }
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            filename = getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        return filename;
    }

    public String getFilename(Character character) {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
        String uriSting = "";
        if (character == 'F') {
            uriSting = mCurrentPhotoPath_F;

        } else if (character == 'B') {
            uriSting = mCurrentPhotoPath_B;

        } else if (character == 'I') {
            uriSting = mCurrentPhotoPath_I;

        } else if (character == 'O') {
            uriSting = mCurrentPhotoPath_O;

        } else if (character == 'T') {
            uriSting = mCurrentPhotoPath_T;

        }
        return uriSting;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        //  mCurrentPhotoPath=Utility.getByeArr(Utility.setPic(mCurrentPhotoPath));
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent);
    }

}
