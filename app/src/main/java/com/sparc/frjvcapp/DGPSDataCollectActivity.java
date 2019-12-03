package com.sparc.frjvcapp;

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
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sparc.frjvcapp.pojo.M_pillar_reg;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;

public class DGPSDataCollectActivity extends AppCompatActivity  {
    public static final String data = "data";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String PNG_FILE_SUFFIX = ".jpg";
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private static final int ACTION_TAKE_GALLERY_PIC = 0;
    private final int REQUEST_CODE_STORAGE_PERMS = 321;
    public String imgValue = "blank";
    String[] point_type = {"BP", "GT","FRA"};
    String[] gt_remark = {"Road Crossing", "Drain Crossing", "Culvert","Specify"};
    String[] duration = {"Instant","5", "15","30","Manual"};
    String[] fra_obsr_point = {"Centroid", "Corner"};
    String[] pillar_shift_status = {"Required", "Not Required"};
    MaterialSpinner pointtype, bpduration, gtremark, gtduration, fraobservpoint;
    EditText edttxtpillarno, remark,edttxtpatchno,edttxtringno,fraoverview,edttxtrtxlat,edttxtrtxlong,edtForestoffnm,edtJobID;
    ImageView setpicforward, takepicforward,setpicbackward,takepicbackward,setpicinward,takepicinward,setpicoutward,takepicoutward,setpictop,takepictop;
    TextView txtViewdiv,txtViewran,txtViewfb,txtViewusername,fbname;
    LinearLayout lh1,lh2,lh3;
    String  sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid,jobid;
    String b_type, p_value, pt_value, status_value, cnd_value;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    int targetW;
    int targetH;

    boolean imagestatus1 = false;
    String image1;
    int clickedStatus = 0;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    DbHelper dbHelper;
    SharedPreferences shared;

    String id;
    SQLiteDatabase db;
    TextView etName;
    //ImageView refresh;
    int point_no;
    String kmlstatus;
    //set the image
    private String mCurrentPhotoPath_F,mCurrentPhotoPath_B,mCurrentPhotoPath_I,mCurrentPhotoPath_O,mCurrentPhotoPath_T;
    //get the image name
    String imagepath1_F,imagepath1_B,imagepath1_I,imagepath1_O,imagepath1_T;

    private Character pic_status;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mImageUrl = "";

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

        //Matterial Spinner
        /*pointtype=findViewById(R.id.pointtype);*/
        bpduration=findViewById(R.id.bpduration);
       /* gtremark=findViewById(R.id.gtremark);
        gtduration=findViewById(R.id.gtduration);
        fraobservpoint=findViewById(R.id.fraobservpoint);*/

        //EditText
        edttxtpillarno=findViewById(R.id.edttxtpillarno);
        remark=findViewById(R.id.remark);
        edttxtpatchno=findViewById(R.id.edttxtpatchno);
        edttxtringno=findViewById(R.id.edttxtringno);
        edtJobID=findViewById(R.id.edtJobID);
       /* fraoverview=findViewById(R.id.fraoverview);*/
       /* edttxtrtxlat=findViewById(R.id.edttxtrtxlat);
        edttxtrtxlong=findViewById(R.id.edttxtrtxlong);*/
        edtForestoffnm=findViewById(R.id.edtForestoffnm);

        //Image View
        setpicforward=findViewById(R.id.setpicforward);
        takepicforward=findViewById(R.id.takepicforward);
        setpicbackward=findViewById(R.id.setpicbackward);
        takepicbackward=findViewById(R.id.takepicbackward);
        setpicinward=findViewById(R.id.setpicinward);
        takepicinward=findViewById(R.id.takepicinward);
        setpicoutward=findViewById(R.id.setpicoutward);
        takepicoutward=findViewById(R.id.takepicoutward);
        setpictop=findViewById(R.id.setpictop);
        takepictop=findViewById(R.id.takepictop);

        //TextView
        txtViewdiv=findViewById(R.id.txtViewdiv);
        txtViewran=findViewById(R.id.txtViewran);
        txtViewfb=findViewById(R.id.txtViewfb);
        fbname=findViewById(R.id.fbname);
      /*  txtViewusername=findViewById(R.id.txtViewusername);*/

        //Layout
        lh1=findViewById(R.id.lh1);
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
        fbname.setText(sharefbname + " " + sharefbtype);
        edttxtpatchno.setText("1");
        edttxtringno.setText("0");
        edtJobID.setText(jobid);
        point_no = getSLNO(sharefb);
        edttxtpillarno.setText(String.valueOf(point_no));
        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        id = i.getStringExtra("id");



        (findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(getApplicationContext(), SelectFBForDGPSActivity.class);
                i.putExtra("kml_status", kmlstatus);
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
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checkLocationPermission();
            buildGoogleApiClient();
        }*/
    }

    private int getSLNO(String sharefb) {
        //List<String> fbName = new ArrayList<String>();
        //return dbHelper.getPillarData(sharefb);
        return 0;
    }

   /* private void CheckPillarStatus(String statusValue) {
        if (statusValue.equals("Existing")) {
            ll.setVisibility(View.VISIBLE);
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
            ll.setVisibility(View.GONE);
            pillartype = "NA";
            pillacond = "NA";
            pillarpaintstatus = "NA";
            pilshiftsts = "NA";
        }
    }*/
    private void SaveData() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                //alertDialogBuilder.setMessage("Are you sure to save this pillar data?");
                final View customLayout = getLayoutInflater().inflate(R.layout.save_custom_dialog_layout, null);
                etName =customLayout.findViewById(R.id.txtView);
                //String data=getJobID(sharediv,sharerange,sharefb,userid);
                etName.setText(data);
                alertDialogBuilder.setView(customLayout);
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                ClipboardManager cm = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("JobID", etName.getText().toString());
                                cm.setPrimaryClip(clipData);
                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("SurveyMobile.Droid");
                                if (launchIntent != null) {
                                    startActivity(launchIntent);
                                } else {
                                    Toast.makeText(DGPSDataCollectActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(getApplicationContext(), "You canceled the request...please try again", Toast.LENGTH_LONG).show();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            //cursor.close();
//            db.close();


        //dismiss();
    }



    private void SelectImage() {
        final CharSequence[] items = {"Camera", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    dispatchTakePictureIntent(1,'F',edttxtpillarno.getText().toString());
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
                    dispatchTakePictureIntent(1,'B',edttxtpillarno.getText().toString());
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
                    dispatchTakePictureIntent(1,'I',edttxtpillarno.getText().toString());
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
                    dispatchTakePictureIntent(1,'O',edttxtpillarno.getText().toString());
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
                    dispatchTakePictureIntent(1,'T',edttxtpillarno.getText().toString());
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

    private void dispatchTakePictureIntent(int actionCode,Character character,String pll_no) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;
                if(character=='F') {
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
                }
                else if(character=='B') {
                    try {
                        f = setUpPhotoFile(character, pll_no);
                        pic_status = character;
                        mCurrentPhotoPath_B = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                         mCurrentPhotoPath_B= null;
                    }
                    break;
                }
                else if(character=='I') {
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
                }
                else if(character=='O') {
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
                }
                else if(character=='T') {
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

    private File setUpPhotoFile(Character character,String pill_no) throws IOException {
        File f=null;
        if(character=='F')
        {
            f= createImageFile(character, pill_no);
            mCurrentPhotoPath_F= f.getAbsolutePath();
        }
        else if(character=='B')
        {
            f= createImageFile(character, pill_no);
            mCurrentPhotoPath_B= f.getAbsolutePath();
        }
        else if(character=='I')
        {
            f= createImageFile(character, pill_no);
            mCurrentPhotoPath_I= f.getAbsolutePath();
        }
        else if(character=='O')
        {
            f= createImageFile(character, pill_no);
            mCurrentPhotoPath_O= f.getAbsolutePath();
        }
        else if(character=='T')
        {
            f= createImageFile(character, pill_no);
            mCurrentPhotoPath_T= f.getAbsolutePath();
        }
        return f;
    }

    private File createImageFile(Character character,String pill_no) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_" + sharefb + "_"+ character +"_"+pill_no+"_";
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
                    if(pic_status == 'F') {
                        handleBigCameraPhoto(pic_status);
                    }
                    else if(pic_status == 'B') {
                        handleBigCameraPhoto(pic_status);
                    }
                    else if(pic_status == 'I') {
                        handleBigCameraPhoto(pic_status);
                    }
                    else if(pic_status == 'O') {
                        handleBigCameraPhoto(pic_status);
                    }
                    else if(pic_status == 'T') {
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
                    }
                    else if (pic_status == 'B') {
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
                    }
                    else if (pic_status == 'I') {
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
                    }
                    else if (pic_status == 'O') {
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
                    }
                    else if (pic_status == 'T') {
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
        if(character=='F')
        {
            if (mCurrentPhotoPath_F != null) {
                String path = compressImage(mCurrentPhotoPath_F,character);
                galleryAddPic(path);
            }
        }
        else if(character=='B')
        {
            if (mCurrentPhotoPath_B != null) {
                String path = compressImage(mCurrentPhotoPath_B,character);
                galleryAddPic(path);
            }
        }
        else if(character=='I')
        {
            if (mCurrentPhotoPath_I != null) {
                String path = compressImage(mCurrentPhotoPath_I,character);
                galleryAddPic(path);
            }
        }
        else if(character=='O')
        {
            if (mCurrentPhotoPath_O != null) {
                String path = compressImage(mCurrentPhotoPath_O,character);
                galleryAddPic(path);
            }
        }
        else if(character=='T')
        {
            if (mCurrentPhotoPath_T != null) {
                String path = compressImage(mCurrentPhotoPath_T,character);
                galleryAddPic(path);
            }
        }
        /*if (mCurrentPhotoPath != null) {
            String path = compressImage(mCurrentPhotoPath);
            galleryAddPic(path);
        }*/

    }

    public String compressImage(String imageUri,Character character) {
        String filename="";
        if(character=='F')
        {
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
            filename= getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else if(character=='B')
        {
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
            filename= getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(character=='I')
        {
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
            filename= getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else if(character=='O')
        {
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
            filename= getFilename(character);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else if(character=='T')
        {
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
            filename= getFilename(character);
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
        String uriSting="";
        if(character=='F') {
            uriSting = mCurrentPhotoPath_F;

        }
        else if(character=='B') {
            uriSting = mCurrentPhotoPath_B;

        }
        else if(character=='I') {
            uriSting = mCurrentPhotoPath_I;

        }
        else if(character=='O') {
            uriSting = mCurrentPhotoPath_O;

        }
        else if(character=='T') {
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


    /*private void setPic() {

        *//* There isn't enough memory to open up more than a couple camera photos
         So pre-scale the target bitmap into which the file is decoded

         Get the size of the ImageView *//*


        // if (personImg.getDrawable() == null) {
        targetW = setpicforward.getWidth();
        targetH = setpicforward.getHeight();

        imagepath1 = mCurrentPhotoPath;
        //Utility.getByeArr(Utility.setPic(imagepath1));

        clickedStatus = 1;
        Log.d("ImagePath", "1" + imagepath1);
        //  } else {
        //   Toast.makeText(HomeScreen.this, "Cannot capture more photos", Toast.LENGTH_SHORT).show();
        //  }

        int targetW = setpicforward.getWidth();
        int targetH = setpicforward.getHeight();

        *//* Get the size of the image *//*
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        *//*   Figure out which way needs to be reduced less *//*
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        *//* Set bitmap options to scale the image decode target *//*
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        *//*  Decode the JPEG file into a Bitmap *//*
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        *//*  Rotate the file according to the device to portait mode*//*
        Bitmap bmp = rotatePhoto(mCurrentPhotoPath, bitmap);
        *//*Associate the Bitmap to the ImageView *//*

        if (setpicforward.getDrawable() == null) {


            setpicforward.setImageBitmap(bmp);
        } else {
            setpicforward.setImageBitmap(bmp);
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
    }*/

    //location code
    /*protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }*/

   /* @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }*/
}
