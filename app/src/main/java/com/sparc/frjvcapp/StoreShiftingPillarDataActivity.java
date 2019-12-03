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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.sparc.frjvcapp.pojo.M_shifting_pillar_details;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoreShiftingPillarDataActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
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
    TextView txtpatchno, etfbname, txtlatdata, txtlong;
    EditText remark;
    DbHelper dbHelper;
    SharedPreferences shared;
    LinearLayout ll;
    String id;
    SQLiteDatabase db;
    ImageView refresh;
    int point_no;
    String kmlstatus, slno, fbname, fbid, userid;
    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mImageUrl = "";
    M_pillar_reg mpil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_shifting_pillar_data);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dbHelper = new DbHelper(getApplicationContext());

        txtpatchno = findViewById(R.id.txtpatchno);
        etfbname = findViewById(R.id.etfbname);
        txtlatdata = findViewById(R.id.txtlatdata);
        txtlong = findViewById(R.id.txtlong);
        remark = findViewById(R.id.remark);

        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        slno = i.getStringExtra("slno");
        fbname = i.getStringExtra("fbname");
        fbid = i.getStringExtra("fbcode");
        userid = i.getStringExtra("userid");
        mpil = (M_pillar_reg)i.getSerializableExtra("mpillar");

        txtpatchno.setText(slno);
        etfbname.setText(fbname);

        takepic = findViewById(R.id.takepic);
        setpic = findViewById(R.id.setpic);

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

        (findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_" + fbid + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName + fbid, PNG_FILE_SUFFIX, albumF);
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
        txtlatdata.setText(String.valueOf(location.getLatitude()));
        txtlong.setText(String.valueOf(location.getLongitude()));
        //accuracyy.setText((String.valueOf(location.getAccuracy())) + "mtr.");
    }

    private void SaveData() {

        if (remark.getText().toString() == "") {
            Toast.makeText(this, "Serial Number can not ne blank", Toast.LENGTH_LONG).show();
        } else if (txtlatdata.getText().toString() == "") {
            Toast.makeText(this, "Latitude value can not ne blank", Toast.LENGTH_LONG).show();
        } else if (txtlong.getText().toString() == "") {
            Toast.makeText(this, "Longitude value can not ne blank", Toast.LENGTH_LONG).show();
        } else {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from m_shifting_pillar_reg where uid='" + userid + "' and fb_id='" + fbid + "' and s_lat='" + txtlatdata.getText().toString() + "' and s_long='" + txtlong.getText().toString() + "' order by p_no", null);
            if (cursor.getCount() > 0) {
                Toast.makeText(this, "This Latitude and Longitude already available.Please click the refresh button", Toast.LENGTH_LONG).show();
            } else {
                M_shifting_pillar_details mpr = new M_shifting_pillar_details(txtlatdata.getText().toString(),txtlong.getText().toString(),remark.getText().toString(),imagepath1,"0",fbname,userid,fbid,slno,"0","0");//+"_"+pilshiftsts
                try {
                    dbHelper.open();
                    dbHelper.insertShiftingPillarData(mpr);
                    dbHelper.insertPillarData(mpil);
                    dbHelper.close();
                    Toast.makeText(getApplicationContext(), "Your Pillar registered Successfully", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), RegisterPointActivity.class);
                    i.putExtra("kml_status", kmlstatus);
                    i.putExtra("id", "ListMap");
                    finishAffinity();
                    startActivity(i);
                }catch (Exception ee)
                {
                    throw ee;
                }finally {
                    if(dbHelper!=null)
                    {
                        dbHelper.close();
                    }
                }
            }
            cursor.close();
            db.close();
        }

        //dismiss();
    }
}
