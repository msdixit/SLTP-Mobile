package com.sparc.frjvcapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sparc.frjvcapp.pojo.M_pillar_reg;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class UpdatePillarPointDataActivity extends AppCompatActivity {
    public static final String data="data";
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private static final int ACTION_TAKE_GALLERY_PIC = 0;
    private final int REQUEST_CODE_STORAGE_PERMS = 321;
    public String imgValue = "blank";
    String[] loc_type={"Existing","Proposed"};
    String[] pillar_type = {"Concrete", "Stone Cairn", "Brick"};
    String[] pillar_paint_status = {"Required", "Not Required"};
    String[] pillar_cond = {"Good", "Repair", "Replace"};
   // MaterialSpinner loctype, pill_type, pill_cond,paint_status;
    String locationtype, pillartype, pillacond,pillarpaintstatus,sharediv,sharerange,sharefb,sharefbtype,sharefbname,userid;
    ImageView takepic, setpic;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    int targetW;
    int targetH;
    String imagepath1,kmlstatus;
    int clickedStatus = 0;
    TextView lat, lon,fbname;
EditText slno,remark,txtpatchno,txtringno,pillarsno,loctype, pill_type, pill_cond,paint_status;
    DbHelper dbHelper;
        SharedPreferences shared;
    LinearLayout ll;
    String id;
    SQLiteDatabase db;
    ArrayAdapter<String> dtadapter,ppadapter,ptadapter,psadapter;
    int pillar_point;
    ProgressDialog progressDialog;
    private String mCurrentPhotoPath;
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
        setContentView(R.layout.activity_update_pillar_point_data);
        loctype = findViewById(R.id.loctype);
        pill_type =findViewById(R.id.ptype);
        pill_cond =findViewById(R.id.pcond);
        paint_status=findViewById(R.id.paintstatus);

        fbname= findViewById(R.id.fbname);
        takepic = findViewById(R.id.takepic);
        setpic = findViewById(R.id.setpic);
        lat = findViewById(R.id.txtlat);
        lon = findViewById(R.id.txtlong);
        slno=findViewById(R.id.txtslno);
        remark=findViewById(R.id.remark);
        txtpatchno=findViewById(R.id.txtpatchno);
        txtringno=findViewById(R.id.txtringno);
        pillarsno=findViewById(R.id.txtPillno);
        Intent z = getIntent();
        kmlstatus = z.getStringExtra("kml_status");

        ll=findViewById(R.id.li3);

        dbHelper=new DbHelper(getApplicationContext());

        shared= getApplicationContext().getSharedPreferences(data, MODE_PRIVATE);
        sharediv=shared.getString("fbdivcode", "0");
        sharerange=shared.getString("fbrangecode", "0");
        sharefb=shared.getString("fbcode", "0");
        sharefbtype=shared.getString("fbtype", "0");
        sharefbname=shared.getString("fbname", "0");
        userid=shared.getString("userid", "0");
        fbname.setText(sharefbname+" "+sharefbtype);
        //slno.setText(String.valueOf(getSLNO(sharefb)));

        Intent i=getIntent();

        (findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(getApplicationContext(), ListMapActivity.class);
                i.putExtra("kml_status",kmlstatus);
               // finishAffinity();
                startActivity(i);


            }
        });
//        (findViewById(R.id.update)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateData();
//            }
//        });

//        takepic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//                    if (setpic.getDrawable() == null) {
//                        setpic.setImageResource(R.drawable.black_tree);
//                    } else {
//                        if (setpic.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp).getConstantState()) {
//                            setpic.setImageResource(0);
//                            SelectImage();
//                        } else {
//                            SelectImage();
//                        }
//                    }
//                }
//            }
//        });

//        dtadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, loc_type);
//        loctype.setAdapter(dtadapter);
//        loctype.setPaddingSafe(0, 0, 0, 0);
//        loctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                locationtype = (String) parent.getItemAtPosition(position);
//                if (!locationtype.equals("Select Location Type")) {
//                    CheckPillarStatus(locationtype);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        ppadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, pillar_type);
//        pill_type.setAdapter(ppadapter);
//        pill_type.setPaddingSafe(0, 0, 0, 0);
//        pill_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                pillartype = (String) parent.getItemAtPosition(position);
//                if (!pillartype.equals("Select Pillar Type")) {
//                    //componentMaster();
//                    //p_value=posValue;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//         ptadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, pillar_cond);
//        pill_cond.setAdapter(ptadapter);
//        pill_cond.setPaddingSafe(0, 0, 0, 0);
//
//        pill_cond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                pillacond = (String) parent.getItemAtPosition(position);
//                if (!pillacond.equals("Select Pillar Condition")) {
//                    //componentMaster();
//                    //pt_value=typeValue;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        psadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, pillar_paint_status);
//        paint_status.setAdapter(psadapter);
//        paint_status.setPaddingSafe(0, 0, 0, 0);
//        paint_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                pillarpaintstatus = (String) parent.getItemAtPosition(position);
//                if (!pillarpaintstatus.equals("Select Paint Status")) {
//                    //componentMaster();
//
//                    //status_value=statusValue;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        getDataforUpdate(i.getStringExtra("lat"),i.getStringExtra("lon"),userid);
    }

    //Retrive data for a single lat long
    private void getDataforUpdate(String latitude, String lonngitude, String userid) {
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from m_pillar_reg where uid='" + userid + "' and d_id='" + sharediv + "' and r_id='" + sharerange + "' and fb_id='" + sharefb + "' and p_lat='"+latitude+"' and p_long='"+lonngitude+"'", null);
        if (cursor.getCount()>0) {
            if(cursor.moveToFirst()) {
                do {
                    slno.setText(cursor.getString(cursor.getColumnIndex("p_no")));
                    lat.setText(latitude);
                    lon.setText(lonngitude);
                    remark.setText(cursor.getString(cursor.getColumnIndex("p_rmk")));
                    txtpatchno.setText(cursor.getString(cursor.getColumnIndex("patch_no")));
                    txtringno.setText(cursor.getString(cursor.getColumnIndex("ring_no")));
                    pillarsno.setText(cursor.getString(cursor.getColumnIndex("p_sl_no")));
                    loctype.setText(cursor.getString(cursor.getColumnIndex("p_loc_type")));
                    pill_type.setText(cursor.getString(cursor.getColumnIndex("p_type")));
                    pill_cond.setText(cursor.getString(cursor.getColumnIndex("p_cond")));
                    paint_status.setText(cursor.getString(cursor.getColumnIndex("p_paint_status")));
                    setImage(cursor.getString(cursor.getColumnIndex("p_pic")));
                }while (cursor.moveToNext());
            }

        }
        cursor.close();
        db.close();

    }

    private int getSLNO(String sharefb) {
        //List<String> fbName = new ArrayList<String>();
        return dbHelper.getPillarData(sharefb);
        //return 0;
    }

    private void CheckPillarStatus(String statusValue) {
        if (statusValue.equals("Existing")) {
            ll.setVisibility(View.VISIBLE);
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
            ll.setVisibility(View.GONE);
        }
    }

    private void updateData() {
        if(slno.getText().toString()=="")
        {
            Toast.makeText(this,"Serial Number can not ne blank",Toast.LENGTH_LONG).show();
        }
        else if(lat.getText().toString()=="")
        {
            Toast.makeText(this,"Latitude can not ne blank",Toast.LENGTH_LONG).show();
        }
        else if(lon.getText().toString()=="")
        {
            Toast.makeText(this,"Longitude can not ne blank",Toast.LENGTH_LONG).show();
        }
        else
        {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from m_pillar_reg where uid='" + userid + "' and d_id='" + sharediv + "' and r_id='" + sharerange + "' and fb_id='" + sharefb + "' and p_lat='"+lat.getText().toString()+"' and p_long='"+lon.getText().toString()+"' order by p_no", null);
            //cursor.getCount();
            if (cursor.getCount()>0) {
                Toast.makeText(this,"This Latitude and Longitude already available.Please click the refresh button",Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent ii = getIntent();
                Intent i;
                final String id = ii.getStringExtra("id");
                String sl = slno.getText().toString();
                String latitude = lat.getText().toString();
                String longitude = lon.getText().toString();
                String rem = remark.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                M_pillar_reg mpr = new M_pillar_reg(sharediv, sharerange, sharefb, pillarsno.getText().toString(), latitude, longitude, pillartype, pillacond, rem, imagepath1, "0", txtpatchno.getText().toString(), txtringno.getText().toString(), locationtype, sl, pillarpaintstatus, fbname.getText().toString(), userid,pillar_point,"0","0","","","",formatter.format(date));//,"",""
                dbHelper.open();
                dbHelper.insertPillarData(mpr);
                dbHelper.close();
                Toast.makeText(this,"Your Pillar registered Successfully",Toast.LENGTH_LONG).show();
                // slno.setText(String.valueOf(getSLNO(sharefb)));
                i = new Intent(this, RegisterPointActivity.class);
                finishAffinity();
                startActivity(i);
            }
            cursor.close();
            db.close();
        }

        //dismiss();
    }

    private void SelectImage() {
        final CharSequence[] items = {"Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, (dialogInterface, i) -> {
            if (items[i].equals("Camera")) {
                dispatchTakePictureIntent(1);
            } else if (items[i].equals("Gallery")) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_FILE);

            } else if (items[i].equals("Cancel")) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent(int actionCode) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    //f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
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
                    handleBigCameraPhoto();

                }
                break;
            } // ACTION_TAKE_PHOTO_B
            case ACTION_TAKE_GALLERY_PIC: {
                try {
                    if (requestCode == SELECT_FILE) {
                        Uri selectedImageUri = data.getData();
                        if (setpic.getDrawable() == null) {
                            setpic.setImageURI(selectedImageUri);
                        } else {
                            setpic.setImageURI(selectedImageUri);
                            // Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
                        }
                        mCurrentPhotoPath = selectedImageUri.toString();
                        imagepath1 = mCurrentPhotoPath;
                        clickedStatus = 1;
                        if (Build.VERSION.SDK_INT >= 21) {
                            mCurrentPhotoPath = UtilityGetPath.getRealPathFromURI_API19(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        } else {
                            mCurrentPhotoPath = UtilityGetPath.getRealPathFromURI_API11to18(getApplicationContext(), selectedImageUri);
                            imgValue = "captured";
                        }
                    }
                }catch (Exception ee)
                {
                    throw ee;
                }
            }
        } // switch
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
        }

    }

    private void setPic() {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */


        // if (personImg.getDrawable() == null) {
        targetW = setpic.getWidth();
        targetH = setpic.getHeight();

        imagepath1 = mCurrentPhotoPath;
        clickedStatus = 1;
        Log.d("ImagePath", "1" + imagepath1);
        //  } else {
        //   Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
        //  }

        int targetW = setpic.getWidth();
        int targetH = setpic.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
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
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        /*Rotate the file according to the device to portait mode*/
        Bitmap bmp = rotatePhoto(mCurrentPhotoPath, bitmap);
        /* Associate the Bitmap to the ImageView */

        if (setpic.getDrawable() == null) {


            setpic.setImageBitmap(bmp);
        } else {
            setpic.setImageBitmap(bmp);
        }
        //assign value to the image variable
        imgValue = "captured";
        imageupdate();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent);
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

    public void imageupdate() {

    }
    private void setImage(String path) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */


        // if (personImg.getDrawable() == null) {
        targetW = setpic.getWidth();
        targetH = setpic.getHeight();

        imagepath1 = path;
        clickedStatus = 1;
        Log.d("ImagePath", "1" + imagepath1);
        //  } else {
        //   Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
        //  }

        int targetW = setpic.getWidth();
        int targetH = setpic.getHeight();

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

        if (setpic.getDrawable() == null) {


            setpic.setImageBitmap(bmp);
        } else {
            setpic.setImageBitmap(bmp);
        }
        //assign value to the image variable
        imgValue = "captured";
        imageupdate();
    }
}
