package com.sparc.frjvcapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.github.pengrad.mapscaleview.MapScaleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DGPSMapViewActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String data = "data";
    private static final float DEFAULT_ZOOM = 13f;
    public boolean is_maploaded = false;
    public HashMap<String, String> rangeKey;
    FloatingActionButton fbtn;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    MarkerOptions markerOptions;
    DbHelper dbHelper;
    int zoom_status = 0;
    SQLiteDatabase db;
    SwitchCompat switchCompat;
    String kmlstatus;
    int kmlLayerStatus;
    MapScaleView scaleView;
    private GoogleMap googleMap;
    private LatLng latLng, destination;
    String sharediv, sharerange, sharefb, sharefbtype, userid, jobid, div_name, range_name, fb_name;
    public static int baseMapMenuPos = 0;
    public static LayoutInflater inflater1, inflater2;
    public static View alertLayout, alertLayout2;
    public static AlertDialog.Builder alert, alert2;
    public static TextView headerText;
    AlertDialog dialog, dialog2;
    TextView message;
    CheckBox c1, c2, c3, c4, c5, c6;
    int i, j, k, l, m, n = 0;
    boolean cmvsta;
    String gps_lat,gps_long;
    int counter,updated_pill_no,pndjv_pill_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dgpsmap_view);

        BottomNavigationView bottomView = findViewById(R.id.dgpsnavigationView);

        //switchCompat = findViewById(R.id.chk);
        scaleView = (MapScaleView) findViewById(R.id.dgpsscaleView);
        bottomView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) this);

        dbHelper = new DbHelper(this);
        SharedPreferences shared = getApplicationContext().getSharedPreferences(data, MODE_PRIVATE);
        sharediv = shared.getString("fbdivcode", "0");
        sharerange = shared.getString("fbrangecode", "0");
        sharefb = shared.getString("fbcode", "0");
        userid = shared.getString("userid", "0");
        sharefbtype = shared.getString("fbtype", "0");
        fb_name = shared.getString("fbname", "0");
        jobid = shared.getString("jobid", "0");
        div_name = shared.getString("div_name", "0");
        range_name = shared.getString("range_name", "0");
        fb_name = shared.getString("fb_name", "0");
        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        getCMVData();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
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
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
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
        gps_lat=String.valueOf(location.getLatitude());
        gps_long=String.valueOf(location.getLongitude());
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {

            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            if (mLastLocation == null) {
                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),
                        DEFAULT_ZOOM);
            }
            //Place current location marker
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
           /* markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            mCurrLocationMarker = googleMap.addMarker(markerOptions);*/

           /*else{
               gps_lat="";
               gps_long="";
           }*/


            if (destination != null) {
                //requestDirection();
            }

            if (is_maploaded == false) {
                if (zoom_status == 0) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    zoom_status += 1;
                }


                //stop location updates
                if (mGoogleApiClient != null) {
                }
            }
        }

    }

    private void moveCamera(LatLng latLng, float zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    @Override
    public void onCameraMove() {
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ChooseSurvetTypeActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("kml_status", kmlstatus);
        startActivity(i);

    }

    @Override
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraChangeListener(this);
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
        // googleMap.setOnCameraMoveListener(this);
        //googleMap.setOnCameraIdleListener(this);
        //googleMap.setOnCameraChangeListener(this);
        //=googleMap1;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                if (nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) {
                    buildGoogleApiClient();
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    GoogleMap finalGoogleMap = googleMap;
                    if (checkSurveyData(sharefb)) {
                        googleMap.clear();
                        getSurveyPointData(sharefb);
                    } else {

                    }
                  /* googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng dest) {
                            MarkerOptions marker = new MarkerOptions().position(
                                    dest)
                                    .title("Destination ");
                            marker.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            destination = dest;
                            finalGoogleMap.addMarker(marker);
                            if (destination != null) {
                                requestDirection();

                            }
                        }
                    });*/
                } else {
                    buildGoogleApiClient();
                    googleMap.setMyLocationEnabled(true);
                    TileProvider coordTileProvider = new DGPSMapViewActivity.CoordTileProvider(this.getApplicationContext());
                    googleMap1.addTileOverlay(new TileOverlayOptions().tileProvider(coordTileProvider));
                    if (checkSurveyData(sharefb)) {
                        googleMap.clear();
                        getSurveyPointData(sharefb);
                    } else {

                    }
                }
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
        //getAllSureypoints(userid, divid, rangeid, fbid);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String[] a = marker.getSnippet().split(",");
                String[] p = marker.getTitle().split(":");
                String[] array_lat = a[0].split(":");
                String[] array_long = a[1].split(":");
                String[] array_status = a[2].split(":");
                String[] array_file = a[3].split(":");
                String[] array_o_id = a[4].split(":");
                String[] array_pnjvd = a[5].split(":");
                String pillar_no = p[1];
                String lat = array_lat[1];
                String lon = array_long[1];
                int status = Integer.parseInt(array_status[1]);
                int file = Integer.parseInt(array_file[1]);
                String o_id = array_o_id[1];
                String pnjdv_id = array_pnjvd[1];

                if (status == 0 && file == 0) {
                    Intent intent1 = new Intent(getApplicationContext(), DGPSDataCollectActivity.class);
                    intent1.putExtra("lat", lat);
                    intent1.putExtra("lon", lon);
                    intent1.putExtra("checksts", k);
                    intent1.putExtra("pill_no", pillar_no);
                    intent1.putExtra("id", "DGPSMap");
                    intent1.putExtra("kml_status", kmlstatus);
                    intent1.putExtra("old_id", o_id);
                    startActivity(intent1);
                } else if (status == 1 && file == 0) {
                    Intent intent1 = new Intent(getApplicationContext(), DGPSViewPillarDetailActivity.class);
                    intent1.putExtra("lat", lat);
                    intent1.putExtra("lon", lon);
                    intent1.putExtra("checksts", k);
                    intent1.putExtra("pill_no", pillar_no);
                    intent1.putExtra("id", "DGPSMap");
                    intent1.putExtra("kml_status", kmlstatus);
                    intent1.putExtra("old_id", o_id);
                    intent1.putExtra("pnjdv_id", pnjdv_id);
                    startActivity(intent1);
                }
            }
        });


    }

    /* Map part for offline to call tileprovider*/
    private static class CoordTileProvider implements TileProvider {

        private static final int TILE_SIZE_DP = 256;

        private final float mScaleFactor;

        private final Bitmap mBorderTile;


        public CoordTileProvider(Context context) {
            /* Scale factor based on density, with a 0.6 multiplier to increase tile generation
             * speed */
            mScaleFactor = context.getResources().getDisplayMetrics().density * 0.6f;
            Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Paint.Style.STROKE);
            mBorderTile = Bitmap.createBitmap((int) (TILE_SIZE_DP * mScaleFactor),
                    (int) (TILE_SIZE_DP * mScaleFactor), android.graphics.Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBorderTile);
            canvas.drawRect(0, 0, TILE_SIZE_DP * mScaleFactor, TILE_SIZE_DP * mScaleFactor,
                    borderPaint);
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            Bitmap coordTile = drawTileCoords(x, y, zoom);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            coordTile.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] bitmapData = stream.toByteArray();
            return new Tile((int) (TILE_SIZE_DP * mScaleFactor),
                    (int) (TILE_SIZE_DP * mScaleFactor), bitmapData);
        }

        private Bitmap drawTileCoords(int x, int y, int zoom) {
            // Synchronize copying the bitmap to avoid a race condition in some devices.
            Bitmap copy = null;
            synchronized (mBorderTile) {
                copy = mBorderTile.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
            }
            return copy;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.dgpsnavigation_basemap) {
            PopupMenu popupMenu = new PopupMenu(DGPSMapViewActivity.this, this.findViewById(R.id.dgpsnavigation_basemap));
            popupMenu.getMenuInflater().inflate(R.menu.basemap_menu, popupMenu.getMenu());
            if (baseMapMenuPos == 0) {
                popupMenu.getMenu().getItem(0).setChecked(true);
            } else {
                popupMenu.getMenu().getItem(baseMapMenuPos).setChecked(true);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.normal:
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            item.setChecked(true);
                            baseMapMenuPos = 0;
                            return true;
                        case R.id.Imagery_Basemap:
                            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            item.setChecked(true);
                            baseMapMenuPos = 1;
                            return true;
                        default:
                            return DGPSMapViewActivity.super.onOptionsItemSelected(item);
                    }
                }
            });
            popupMenu.show();
        } else if (id == R.id.dgpsnavigation_legend) {
            inflater1 = getLayoutInflater();
            alertLayout = inflater1.inflate(R.layout.dgps_map_legend, null);
            alert = new AlertDialog.Builder(alertLayout.getContext());
            alert.setView(alertLayout);
            headerText = (TextView) alertLayout.findViewById(R.id.name_textView);
            dialog = alert.create();
            dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setLayout(600, 400);
            headerText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (headerText.getRight() - headerText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            dialog.dismiss();

                            return true;
                        }
                    }

                    return true;
                }
            });
            dialog.show();
        }else if(id == R.id.newPoint)
        {

            String p_no=getDGPSPillData()+"-"+getDGPSPNDJVData();//PNDJV-Pillar not Identified During FRJVC Survey
            Intent intent1 = new Intent(getApplicationContext(), DGPSDataCollectActivity.class);
            intent1.putExtra("lat", gps_lat);
            intent1.putExtra("lon", gps_long);
            intent1.putExtra("checksts", k);
            intent1.putExtra("pill_no", p_no);
            intent1.putExtra("id", "DGPSMap");
            intent1.putExtra("kml_status", kmlstatus);
            // intent1.putExtra("counter", p_no);
            intent1.putExtra("old_id", 0);
            startActivity(intent1);
        }
        else {
            Intent i = new Intent(getApplicationContext(), DGPSDataCollectActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("id", "ListMap");
            i.putExtra("kml_status", kmlstatus);
            finishAffinity();
            startActivity(i);
        }
        return false;
    }

    private boolean checkSurveyData(String fbid) {
        try {
            dbHelper.open();
            rangeKey = new HashMap<>();
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select m_p_lat,m_p_long,m_fb_pillar_no from m_dgps_Survey_pill_data where m_fb_id='" + fbid + "' order by m_fb_pillar_no", null);
            if (cursor.getCount() > 0) {
                cmvsta = true;
            } else {
                cmvsta = false;
            }
            cursor.close();
            db.close();
            dbHelper.close();
            //dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return cmvsta;
    }

    private void getSurveyPointData(String fbid) {
        try {
            dbHelper.open();
            ArrayList<String> pillarno = new ArrayList<String>();
            ArrayList<String> lat = new ArrayList<String>();
            ArrayList<String> lon = new ArrayList<String>();
            ArrayList<String> status = new ArrayList<String>();
            ArrayList<String> file = new ArrayList<String>();
            ArrayList<String> o_id = new ArrayList<String>();
            ArrayList<String> survey_status = new ArrayList<String>();
            ArrayList<String> pnjdv_no = new ArrayList<String>();
            ArrayList<String> pillar_avial_sts = new ArrayList<String>();

            rangeKey = new HashMap<>();
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select * from m_dgps_Survey_pill_data where m_fb_id='" + fbid + "' and (m_pillar_avl_sts='0' or m_pillar_avl_sts='2')order by m_fb_pillar_no", null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        counter++;
                        pillarno.add(cursor.getString(cursor.getColumnIndex("m_fb_pillar_no")));
                        lat.add(cursor.getString(cursor.getColumnIndex("m_p_lat")));
                        lon.add(cursor.getString(cursor.getColumnIndex("m_p_long")));
                        status.add(cursor.getString(cursor.getColumnIndex("m_dgps_surv_sts")));
                        file.add(cursor.getString(cursor.getColumnIndex("m_dgps_file_sts")));
                        o_id.add(cursor.getString(cursor.getColumnIndex("o_Id")));
                        survey_status.add(cursor.getString(cursor.getColumnIndex("m_survey_status")));
                        pnjdv_no.add(cursor.getString(cursor.getColumnIndex("m_pndjv_pill_no")));
                        pillar_avial_sts.add(cursor.getString(cursor.getColumnIndex("m_pillar_avl_sts")));
                    } while (cursor.moveToNext());
                }
                cmvsta = true;
                cursor.close();
                db.close();
                for (int j = 0; j < pillarno.size(); j++) {
                    addSurveyPointtoMap(Double.parseDouble(lat.get(j)), Double.parseDouble(lon.get(j)), pillarno.get(j), Integer.parseInt(status.get(j)), Integer.parseInt(file.get(j)), Integer.parseInt(o_id.get(j)),Integer.parseInt(survey_status.get(j)),Integer.parseInt(pnjdv_no.get(j)),Integer.parseInt(pillar_avial_sts.get(j)));
                }
            } else {
                cmvsta = false;
            }
            //dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void addSurveyPointtoMap(double key, double value, String pillno, int Status, int file_sts, int o_id,int survey_status,int pndjv_pill_no,int pill_avail_sts) {
        if (Status == 0 && file_sts == 0 && survey_status==0) {
            if(pill_avail_sts==0)
            {
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.yellow);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Existing Pillar:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id+",Pnjdv_no:"+ pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }else{
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.blue);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Propose Pillar:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id+",Pnjdv_no:"+ pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }
        }
        else if (Status == 1 && file_sts == 0 && survey_status==1) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.red);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            if(pill_avail_sts==0) {
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Exisiting Pillar:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id + ",Pnjdv_no:" + pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }else{
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Propose Pillar:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id + ",Pnjdv_no:" + pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }
        }
        else if (Status == 1 && file_sts == 1 && survey_status==1) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.green);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            if(pill_avail_sts==0) {
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Existing Pillar:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id+",Pnjdv_no:"+ pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }else{
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Propose Pillar:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id+",Pnjdv_no:"+ pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }
        }
        else if (Status == 1 && file_sts == 0 && survey_status==2) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_grey);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            if(pill_avail_sts==0) {
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Existing Pillar" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id+",Pnjdv_no:"+ pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }else{
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(key, value)).title("Propose Pillar:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + Status + ",File:" + file_sts + ",OldID:" + o_id+",Pnjdv_no:"+ pndjv_pill_no).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }
        }
    }
    private int getDGPSPillData() {
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * from m_fb_dgps_survey_pill_data where delete_status='0' and fb_id='" + sharefb + "' order by survey_time desc limit 1", null);
            int count = c==null?0:c.getCount();
            if (count >= 1) {
                if (c.moveToFirst()) {
                    do {
                        updated_pill_no = Integer.parseInt(c.getString(c.getColumnIndex("pill_no")));
                    }
                    while (c.moveToNext());
                }else{
                    updated_pill_no=0;
                }
            }else{
                updated_pill_no=0;
            }
            c.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return updated_pill_no;
    }
    private int getDGPSPNDJVData() {
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * from m_fb_dgps_survey_pill_data where delete_status='0' and fb_id='" + sharefb + "' order by survey_time desc limit 1", null);
            int count= c==null?1:c.getCount();
            if (count >= 1) {
                if (c.moveToFirst()) {
                    do {
                        pndjv_pill_no = Integer.parseInt(c.getString(c.getColumnIndex("pndjv_pill_no")))+1;
                    }
                    while (c.moveToNext());
                }else{
                    pndjv_pill_no=1;
                }
            }else{
                pndjv_pill_no=1;
            }
            c.close();
            db.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return pndjv_pill_no;
    }
    private void getCMVData() {

        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVFiles(sharediv, sharerange, sharefb);
            String path = GetFilePath();
            File f=new File(path + "/" + mfb.get(i));
            if(f.exists()) {
                for (int i = 0; i < mfb.size(); i++) {
                    new DownloadKmlFile(path + "/" + mfb.get(i)).execute();
                }
            }else{
                Toast.makeText(this,"Please download the CMV",Toast.LENGTH_LONG).show();
            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    private String GetFilePath() {
        File directory = getExternalFilesDir(null);
        String folder = directory.getAbsolutePath();
        return folder;
    }
    private class DownloadKmlFile extends AsyncTask<String, Void, byte[]> {
        private final String mUrl;
        //testing
        public String title=null;

        public DownloadKmlFile(String url) {
            mUrl = url;
        }

        protected byte[] doInBackground(String... params) {
            try {
                InputStream is = new FileInputStream(mUrl);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(byte[] byteArr) {
            try {
                final KmlLayer kmlLayer = new KmlLayer(googleMap, new ByteArrayInputStream(byteArr),
                        getApplicationContext());
                kmlLayer.addLayerToMap();
                //testing

                for (KmlContainer container : kmlLayer.getContainers()) {
                    if (container.hasProperty("name")) {
                        title=container.getProperty("name");
                    }
                }
                // Set a listener for geometry clicked events.
                kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                        /*Log.i("KmlClick", "Feature clicked: " + title);
                        Toast.makeText(ListMapActivity.this,
                                "Feature clicked: " + title,
                                Toast.LENGTH_SHORT).show();*/
                    }
                });
                //testing
               /* kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                        Toast.makeText(ListMapActivity.this,
                                "Feature clicked: " + feature.getId(),
                                Toast.LENGTH_SHORT).show();
                    }
                });*/
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
