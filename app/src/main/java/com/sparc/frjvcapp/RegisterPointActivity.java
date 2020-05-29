package com.sparc.frjvcapp;

import android.Manifest;
import android.app.Activity;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RegisterPointActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String data = "data";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String PNG_FILE_SUFFIX = ".jpg";
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private static final int ACTION_TAKE_GALLERY_PIC = 0;
    private final int REQUEST_CODE_STORAGE_PERMS = 321;
    public String imgValue = "blank";
    String[] loc_type = {"Existing", "Proposed"};
    String[] pillar_type = {"Concrete", "Stone Cairn", "Brick","Others"};
    String[] pillar_paint_status = {"Required", "Not Required"};
    String[] pillar_cond = {"Good", "Needs Repair", "Needs Replacement"};
    String[] pillar_shift_status = {"Required", "Not Required"};
    String[] survey_direction = {"Clockwise", "Anticlockwise"};
    MaterialSpinner loctype, pill_type, pill_cond, paint_status, pillshiftsts,direction;
    String locationtype, pillartype, pillacond, pillarpaintstatus, sharediv, sharerange, sharefb, sharefbtype, sharefbname, userid, pilshiftsts,surdir;
    String b_type, p_value, pt_value, status_value, cnd_value;
    ImageView takepic, setpic;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    int targetW;
    int targetH;
    String imagepath1;
    boolean imagestatus1 = false;
    String image1;
    int clickedStatus = 0;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    TextView lat, lon, accuracyy, fbname;
    EditText slno, remark, txtpatchno, txtringno, pillarsno;
    DbHelper dbHelper;
    SharedPreferences shared;
    LinearLayout ll;
    String id;
    SQLiteDatabase db;
    ImageView refresh;
    int point_no;
    String kmlstatus;
    private String mCurrentPhotoPath;
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
        setContentView(R.layout.activity_register_point);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        loctype = findViewById(R.id.loctype);
        pill_type = findViewById(R.id.ptype);
        pill_cond = findViewById(R.id.pcond);
        paint_status = findViewById(R.id.paintstatus);
        pillshiftsts = findViewById(R.id.shiftingstatus);
        direction=findViewById(R.id.direction);

        fbname = findViewById(R.id.fbname);
        takepic = findViewById(R.id.takepic);
        setpic = findViewById(R.id.setpic);
        lat = findViewById(R.id.txtlat);
        lon = findViewById(R.id.txtlong);
        accuracyy = findViewById(R.id.txtAcc);
        slno = findViewById(R.id.txtslno);
        remark = findViewById(R.id.remark);
        txtpatchno = findViewById(R.id.txtpatchno);
        txtringno = findViewById(R.id.txtringno);
        pillarsno = findViewById(R.id.txtPillno);
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedRefresh();
            }
        });
        ll = findViewById(R.id.li3);
        dbHelper = new DbHelper(getApplicationContext());
        shared = getApplicationContext().getSharedPreferences(data, MODE_PRIVATE);
        sharediv = shared.getString("fbdivcode", "0");
        sharerange = shared.getString("fbrangecode", "0");
        sharefb = shared.getString("fbcode", "0");
        sharefbtype = shared.getString("fbtype", "0");
        sharefbname = shared.getString("fbname", "0");
        userid = shared.getString("userid", "0");
        fbname.setText(sharefbname + " " + sharefbtype);
        txtpatchno.setText("1");
        txtringno.setText("0");
        point_no = getSLNO(sharefb);
        slno.setText(String.valueOf(point_no));
        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        id = i.getStringExtra("id");


        (findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(getApplicationContext(), ListMapActivity.class);
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

        takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (setpic.getDrawable() == null) {
                        SelectImage();
                    } else {
                        if (setpic.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp).getConstantState()) {
                            setpic.setImageResource(0);
                            SelectImage();
                        } else {
                            SelectImage();
                        }
                    }
                }
            }
        });

        ArrayAdapter<String> dtadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, loc_type);
        loctype.setAdapter(dtadapter);
        loctype.setPaddingSafe(0, 0, 0, 0);
        loctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationtype = (String) parent.getItemAtPosition(position);
                if (!locationtype.equals("Select Location Type")) {
                    CheckPillarStatus(locationtype);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<String> ppadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, pillar_type);
        pill_type.setAdapter(ppadapter);
        pill_type.setPaddingSafe(0, 0, 0, 0);
        pill_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pillartype = (String) parent.getItemAtPosition(position);
                if (!pillartype.equals("Material")) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<String> ptadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, pillar_cond);
        pill_cond.setAdapter(ptadapter);
        pill_cond.setPaddingSafe(0, 0, 0, 0);
        pill_cond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pillacond = (String) parent.getItemAtPosition(position);
                if (!pillacond.equals("Select Pillar Condition")) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ArrayAdapter<String> psadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, pillar_paint_status);
        paint_status.setAdapter(psadapter);
        paint_status.setPaddingSafe(0, 0, 0, 0);
        paint_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pillarpaintstatus = (String) parent.getItemAtPosition(position);
                if (!pillarpaintstatus.equals("Select Paint Status")) {
                    //componentMaster();

                    //status_value=statusValue;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ArrayAdapter<String> pststsadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, pillar_shift_status);
        pillshiftsts.setAdapter(pststsadapter);
        pillshiftsts.setPaddingSafe(0, 0, 0, 0);
        pillshiftsts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pilshiftsts = (String) parent.getItemAtPosition(position);
                if (!pilshiftsts.equals("Select Shifting Status")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ArrayAdapter<String> diradapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_row, survey_direction);
        direction.setAdapter(diradapter);
        direction.setPaddingSafe(0, 0, 0, 0);
        direction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                surdir = (String) parent.getItemAtPosition(position);
                if (!surdir.equals("Select Survey Direction")) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
            buildGoogleApiClient();
        }
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
            pillartype = "NA";
            pillacond = "NA";
            pillarpaintstatus = "NA";
            pilshiftsts = "NA";
        }
    }

    private void SaveData() {

        if (slno.getText().toString() == "") {
            Toast.makeText(this, "Serial Number can not ne blank", Toast.LENGTH_LONG).show();
        } else if (lat.getText().toString() == "") {
            Toast.makeText(this, "Latitude value can not ne blank", Toast.LENGTH_LONG).show();
        } else if (lon.getText().toString() == "") {
            Toast.makeText(this, "Longitude value can not ne blank", Toast.LENGTH_LONG).show();
        } else if (locationtype.equals("Select Location Type")) {
            Toast.makeText(this, "Please Select the Location Type", Toast.LENGTH_LONG).show();
        } else if (locationtype.equals("Existing") && pillartype.equals("Material")) {
            Toast.makeText(this, "Material", Toast.LENGTH_LONG).show();
        } else if (locationtype.equals("Existing") && pillacond.equals("Select Pillar Condition")) {
            Toast.makeText(this, "Please Select the Pilar condition", Toast.LENGTH_LONG).show();
        } else if (locationtype.equals("Existing") && pillarpaintstatus.equals("Select Shifting Status")) {
            Toast.makeText(this, "Please Select Shifting Status", Toast.LENGTH_LONG).show();
        }else if (surdir.equals("Select Survey Direction")) {
            Toast.makeText(this, "Please Select Survey Direction", Toast.LENGTH_LONG).show();
        }else if (imagepath1==null) {
            Toast.makeText(this, "Please Select the image.Go back and get the point again.", Toast.LENGTH_LONG).show();
        } else {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from m_pillar_reg where uid='" + userid + "' and d_id='" + sharediv + "' and r_id='" + sharerange + "' and fb_id='" + sharefb + "' and p_lat='" + lat.getText().toString() + "' and p_long='" + lon.getText().toString() + "' order by p_no", null);
            if (cursor.getCount() > 0) {
                Toast.makeText(this, "This Latitude and Longitude already available.Please click the refresh button", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                //alertDialogBuilder.setMessage("Are you sure to save this pillar data?");
                final View customLayout = getLayoutInflater().inflate(R.layout.save_custome_dialod_register_pillar, null);
                alertDialogBuilder.setView(customLayout);
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent ii = getIntent();
                                Intent i;
                                final String id = ii.getStringExtra("id");
                                String sl = slno.getText().toString();
                                String latitude = lat.getText().toString();
                                String longitude = lon.getText().toString();
                                String accuracy = accuracyy.getText().toString();
                                String rem = remark.getText().toString();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                M_pillar_reg mpr1 = new M_pillar_reg(sharediv, sharerange, sharefb, pillarsno.getText().toString(),
                                        latitude, longitude, pillartype, pillacond, rem, imagepath1, "0",
                                        txtpatchno.getText().toString(), txtringno.getText().toString(), locationtype, sl,
                                        pillarpaintstatus, fbname.getText().toString(), userid, point_no, "0", "0",pilshiftsts,surdir,accuracy,formatter.format(date));//+"_"+pilshiftsts,surdir,accuracy

                                //dbHelper.open();

                                //dbHelper.close();
                                if (pilshiftsts.equals("Required")) {
                                    Intent si = new Intent(getApplicationContext(), StoreShiftingPillarDataActivity.class);
                                    si.putExtra("kml_status", kmlstatus);
                                    si.putExtra("slno", sl);
                                    si.putExtra("userid", userid);
                                    si.putExtra("fbname", fbname.getText().toString());
                                    si.putExtra("fbcode", sharefb);
                                    si.putExtra("mpillar", (Serializable) mpr1);
                                    finishAffinity();
                                    startActivity(si);
                                } else {
                                    try {
                                        dbHelper.open();
                                        long a=dbHelper.insertPillarData(mpr1);
                                        dbHelper.close();
                                        Toast.makeText(getApplicationContext(), "Your Pillar registered Successfully", Toast.LENGTH_LONG).show();
                                        i = new Intent(getApplicationContext(), RegisterPointActivity.class);
                                        i.putExtra("kml_status", kmlstatus);
                                        finishAffinity();
                                        startActivity(i);
                                    } catch (Exception ee) {
                                        throw ee;
                                    } finally {
                                        if (dbHelper != null) {
                                            dbHelper.close();
                                        }
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
            cursor.close();
            db.close();
        }

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
                    dispatchTakePictureIntent(1);
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
                    f = null;
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
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_" + sharefb + "_";
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
                    handleBigCameraPhoto();

                }
                break;
            } // ACTION_TAKE_PHOTO_B
            case ACTION_TAKE_GALLERY_PIC: {
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
                    if (Build.VERSION.SDK_INT >= 19) {
                        mCurrentPhotoPath = UtilityGetPath.getRealPathFromURI_API19(getApplicationContext(), selectedImageUri);
                        imgValue = "captured";
                    } else {
                        mCurrentPhotoPath = UtilityGetPath.getRealPathFromURI_API11to18(getApplicationContext(), selectedImageUri);
                        imgValue = "captured";
                    }
                }
            }
        } // switch
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            String path = compressImage(mCurrentPhotoPath);
            galleryAddPic(path);
        }

    }

    public String compressImage(String imageUri) {
        imagepath1 = imageUri;
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
            if (setpic.getDrawable() == null) {


                setpic.setImageBitmap(bmp);
            } else {
                setpic.setImageBitmap(bmp);
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
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return filename;

    }

    public String getFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
        String uriSting = mCurrentPhotoPath;
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

    private void setPic() {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */


        // if (personImg.getDrawable() == null) {
        targetW = setpic.getWidth();
        targetH = setpic.getHeight();

        imagepath1 = mCurrentPhotoPath;
        //Utility.getByeArr(Utility.setPic(imagepath1));

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
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        //  mCurrentPhotoPath=Utility.getByeArr(Utility.setPic(mCurrentPhotoPath));
        File f = new File(path);
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

    //location code
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        connectedRefresh();

    }

    public void connectedRefresh() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        // lat = String.valueOf(location.getLatitude());
        // lon = String.valueOf(location.getLongitude());
        lat.setText(String.valueOf(location.getLatitude()));
        lon.setText(String.valueOf(location.getLongitude()));
        accuracyy.setText(String.valueOf(location.getAccuracy()));
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ListMapActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

}
