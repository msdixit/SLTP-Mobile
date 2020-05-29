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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.load.engine.Resource;
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
import com.google.android.gms.maps.model.LatLngBounds;
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


public class ListMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, DirectionCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String data = "data";
    public static final String userlogin = "userlogin";
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
    private String divid, rangeid, fbid, userid;
    private String serverKey = "AIzaSyB-VJ5Dc0Wf-AzmvNlwd48GuzwBN25s8JQ";
    public static int baseMapMenuPos = 0;
    public static LayoutInflater inflater1, inflater2;
    public static View alertLayout, alertLayout2;
    public static AlertDialog.Builder alert, alert2;
    public static TextView headerText;
    AlertDialog dialog, dialog2;
    TextView message;
    CheckBox c1, c2, c3, c4, c5, c6;
    int i, j, k, l, m, n = 0;
    String master[] = {"State_Boundary.kml", "Circle_Boundary.kml", "Range_Boundary.kml", "Division_Boundary.kml"};
    boolean cmvsta,mmvsta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map);

        BottomNavigationView bottomView = findViewById(R.id.navigationView);

        //switchCompat = findViewById(R.id.chk);
        scaleView = (MapScaleView) findViewById(R.id.scaleView);
        bottomView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) this);

        dbHelper = new DbHelper(this);
        SharedPreferences shared = getApplicationContext().getSharedPreferences(data, MODE_PRIVATE);
        divid = shared.getString("fbdivcode", "0");
        rangeid = shared.getString("fbrangecode", "0");
        fbid = shared.getString("fbcode", "0");
        Intent i = getIntent();
        kmlstatus = i.getStringExtra("kml_status");
        SharedPreferences login = getApplicationContext().getSharedPreferences(userlogin, MODE_PRIVATE);
        userid = login.getString("uemail", "0");
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


       /* if (alertLayout2 != null)
        {
            ViewGroup parent = (ViewGroup)alertLayout2.getParent();
            parent.removeAllViews();

            inflater2 = getLayoutInflater();
            alertLayout2 = inflater2.inflate(R.layout.map_layer_layout, null, false);

            c1 = alertLayout2.findViewById(R.id.chkstate);
            c2 = alertLayout2.findViewById(R.id.chkdivision);
            c3 = alertLayout2.findViewById(R.id.chkrange);
            c4 = alertLayout2.findViewById(R.id.chkcmv);
            c5 = alertLayout2.findViewById(R.id.chkmmv);
        } else {
            inflater2 = getLayoutInflater();
            alertLayout2 = inflater2.inflate(R.layout.map_layer_layout, null, false);

            c1 = alertLayout2.findViewById(R.id.chkstate);
            c2 = alertLayout2.findViewById(R.id.chkdivision);
            c3 = alertLayout2.findViewById(R.id.chkrange);
            c4 = alertLayout2.findViewById(R.id.chkcmv);
            c5 = alertLayout2.findViewById(R.id.chkmmv);
        }

            if (c4.isChecked()) {
                if (kmlstatus.equals("1")) {
                    getCMVMMVData(0);
                } else if (kmlstatus.equals("0")) {
                    getCMVMMVData(3);
                }
            }*/





       /* if (kmlstatus.equals("1")) {
            getCMVMMVData(0);
            c4.setChecked(true);
            c5.setChecked(true);
        } else if (kmlstatus.equals("0")) {
            getCMVMMVData(3);
            c4.setChecked(false);
            c5.setChecked(false);
        }*/

        /*fbtn = findViewById(R.id.fab);
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterPointActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("id", "ListMap");
                i.putExtra("kml_status", kmlstatus);
                finishAffinity();
                startActivity(i);
            }
        });*/

        /*if (kmlstatus.equals("1")) {
            getCMVMMVData(0);
            switchCompat.setChecked(true);
        } else if (kmlstatus.equals("0")) {
            getCMVMMVData(3);
            switchCompat.setChecked(false);
        }
        switchCompat.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (kmlstatus.equals("1")) {
                                getCMVMMVData(0);
                            } else if (kmlstatus.equals("0")) {
                                Toast.makeText(ListMapActivity.this, "You don't have CMV and MMV for this FB ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            getCMVMMVData(3);
                            googleMap.clear();
                            getAllSureypoints(userid, divid, rangeid, fbid);
                        }
                    }
                });*/
    }

    /* Map part for both offline and online*/
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onCameraMove() {
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        scaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
    }

    /* Direction for both online*/
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            googleMap.clear();
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mapmarker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70, 84, false);
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Current position").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

            BitmapDrawable destinationmark = (BitmapDrawable) getResources().getDrawable(R.drawable.mapmarker);
            Bitmap b1 = destinationmark.getBitmap();
            Bitmap redMarker = Bitmap.createScaledBitmap(b1, 80, 80, false);
            googleMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .icon(BitmapDescriptorFactory.fromBitmap(redMarker)));
            for (int i = 0; i < direction.getRouteList().size(); i++) {
                Route route = direction.getRouteList().get(i);
                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.parseColor("#4285F4")));
            }
            setCameraWithCoordinationBounds(direction.getRouteList().get(0));
            // getCMVMMVData();
            getAllSureypoints(userid, divid, rangeid, fbid);
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
    }

    /* Map part for both offline and online*/
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
    /* Map part for both online*/

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
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
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
           /* mCurrLocationMarker = googleMap.addMarker(markerOptions);*/


            if (destination != null) {
                requestDirection();
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
        // Map part for both offline
        else {
            //fbtn.setVisibility(View.GONE);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            mCurrLocationMarker = googleMap.addMarker(markerOptions);

        }


    }

    private void getAllSureypoints(String userid, String divid, String rangeid, String fbid) {
        ArrayList<String> pillarno = new ArrayList<String>();
        ArrayList<String> lat = new ArrayList<String>();
        ArrayList<String> lon = new ArrayList<String>();

        rangeKey = new HashMap<>();
        try {
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select p_lat,p_long,p_no from m_pillar_reg where uid='" + userid + "' and d_id='" + divid + "' and r_id='" + rangeid + "' and fb_id='" + fbid + "' order by point_no", null);
            //cursor.moveToFirst();
            if(cursor.getCount()>0) {
                if (cursor.moveToFirst()) {
                    do {
                        pillarno.add(cursor.getString(cursor.getColumnIndex("p_no")));
                        lat.add(cursor.getString(cursor.getColumnIndex("p_lat")));
                        lon.add(cursor.getString(cursor.getColumnIndex("p_long")));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                db.close();
                for (int j = 0; j < pillarno.size(); j++) {
                    addPointtoMap(Double.parseDouble(lat.get(j)), Double.parseDouble(lon.get(j)), pillarno.get(j));
                }
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }



    }
    private void addPointtoMap(double key, double value, String pillno) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.blue);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
        googleMap.addMarker(new MarkerOptions().position(
                new LatLng(key, value)).title("Pillar No:" + pillno).snippet("Lat:" + key + ",Long:" + value + ",Status:" + "New").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
    }

    private void moveCamera(LatLng latLng, float zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /* Map part for both offline and online*/
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
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    /*googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng dest) {
                            MarkerOptions marker = new MarkerOptions().position(
                                    dest)
                                    .title("Destination ");
                            marker.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            destination = dest;
                            googleMap.addMarker(marker);
                            if (destination != null) {
                                requestDirection();

                            }
                        }
                    });*/
                } else {
                    buildGoogleApiClient();
                    googleMap.setMyLocationEnabled(true);
                    TileProvider coordTileProvider = new CoordTileProvider(this.getApplicationContext());
                    googleMap1.addTileOverlay(new TileOverlayOptions().tileProvider(coordTileProvider));
                }
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
        getAllSureypoints(userid, divid, rangeid, fbid);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String[] a = marker.getSnippet().split(",");
                String[] p=marker.getTitle().split(":");
                String[] array_lat= a[0].split(":");
                String[] array_long= a[1].split(":");
                String[] array_status= a[2].split(":");
                String pillar_no=p[1];
                String lat = array_lat[1];
                String lon = array_long[1];
                String status = array_status[1];

                if(status.equals("New"))
                {
                    Intent intent1 = new Intent(getApplicationContext(), UpdatePillarPointDataActivity.class);
                    intent1.putExtra("lat", lat);
                    intent1.putExtra("lon", lon);
                    intent1.putExtra("id", "ListMap");
                    intent1.putExtra("kml_status", kmlstatus);
                    startActivity(intent1);
                }else if(status.equals("Proposed"))
                {
                    Intent intent1 = new Intent(getApplicationContext(), UpdateSurveyPillarPointDataActivity.class);
                    intent1.putExtra("lat", lat);
                    intent1.putExtra("lon", lon);
                    intent1.putExtra("id", "ListMap");
                    intent1.putExtra("pillar_no", pillar_no);
                    intent1.putExtra("kml_status", kmlstatus);
                    startActivity(intent1);
                }

            }
        });

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

    /* Location permission validation*/
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

    public void requestDirection() {
        is_maploaded = true;
        GoogleDirection.withServerKey(serverKey)
                .from(latLng)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void CallMap() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        zoom_status = 1;
    }

    /* CMV and MMV retrival and view on the map*/
    private void getCMVMMVData(int z) {

        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVMMVFiles(divid, rangeid, fbid);
            String path = GetFilePath();
            int a = z;
            if (a < mfb.size()) {
                for (int i = 0; i < mfb.size(); i++) {
                    new DownloadKmlFile(path + "/" + mfb.get(i)).execute();
                }
            } else {
                //     Toast.makeText(this, "You dont have KML files for this FB", Toast.LENGTH_SHORT).show();
            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void getCMVData(int z) {

        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVFiles(divid, rangeid, fbid);
            String path = GetFilePath();
            int a = z;
            if (a < mfb.size()) {
                for (int i = 0; i < mfb.size(); i++) {
                    new DownloadKmlFile(path + "/" + mfb.get(i)).execute();
                }
            } else {
                c4.setChecked(false);
                //Toast.makeText(this, "You dont have CMV KML files for this FB", Toast.LENGTH_SHORT).show();
            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void getMMVData(int z) {

        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getMMVFiles(divid, rangeid, fbid);
            String path = GetFilePath();
            int a = z;
            if (a < mfb.size()) {
                for (int i = 0; i < mfb.size(); i++) {
                    new DownloadKmlFile(path + "/" + mfb.get(i)).execute();
                }
            } else {
                c5.setChecked(false);
                //Toast.makeText(this, "You dont have MMV KML files for this FB", Toast.LENGTH_SHORT).show();
            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void getCircleData() {

        try {
            String path = GetFilePath();
            new DownloadKmlFile(path + "/" + "Circle_Boundary.kml").execute();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void getStateData() {

        try {
            String path = GetFilePath();
            new DownloadKmlFile(path + "/" + "State_Boundary.kml").execute();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void getDivisionData() {

        try {
            String path = GetFilePath();
            new DownloadKmlFile(path + "/" + "Division_Boundary.kml").execute();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void getRangeData() {

        try {
            String path = GetFilePath();
            new DownloadKmlFile(path + "/" + "Range_Boundary.kml").execute();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private String GetFilePath() {
        File directory = getExternalFilesDir(null);
        String folder = directory.getAbsolutePath();
        return folder;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MiddleMapListActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("kml_status", kmlstatus);
        startActivity(i);

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
                    System.out.println(container);
                    if (container.hasProperty("name")) {
                        title=container.getProperty("name");
                    }
                }
                // Set a listener for geometry clicked events.
                kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                         Log.i("KmlClick", "Feature clicked: " + title);
                        Toast.makeText(ListMapActivity.this,
                                "Feature clicked: " + title,
                                Toast.LENGTH_SHORT).show();
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

    private class RemoveKmlFile extends AsyncTask<String, Void, byte[]> {
        private final String Url;

        public RemoveKmlFile(String url) {
            Url = url;
        }

        protected byte[] doInBackground(String... params) {
            try {
                InputStream is = new FileInputStream(Url);
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
                kmlLayer.removeLayerFromMap();

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.navigation_basemap) {
            PopupMenu popupMenu = new PopupMenu(ListMapActivity.this, this.findViewById(R.id.navigation_basemap));
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
                            baseMapMenuPos = 2;
                            return true;
                        default:
                            return ListMapActivity.super.onOptionsItemSelected(item);
                    }
                }
            });
            popupMenu.show();
        }
        else if (id == R.id.navigation_legend) {
            inflater1 = getLayoutInflater();
            alertLayout = inflater1.inflate(R.layout.map_legend_layout, null);
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
        } else if (id == R.id.navigation_layer) {


            inflater2 = getLayoutInflater();
            alertLayout2 = inflater2.inflate(R.layout.map_layer_layout, null, false);

           /* c1 = alertLayout2.findViewById(R.id.chkstate);
            c2 = alertLayout2.findViewById(R.id.chkdivision);*/
            c3 = alertLayout2.findViewById(R.id.chkSurveyPillar);
            c4 = alertLayout2.findViewById(R.id.checkCMV);
            c5 = alertLayout2.findViewById(R.id.CheckMMV);
            message=alertLayout2.findViewById(R.id.message);
            /* c6 = alertLayout2.findViewById(R.id.chkcircle);*/

            if(checkCMVData()==false)
            {
                c4.setVisibility(View.GONE);
                message.setText("CMV KML File not available for this FB.Please contact your lab co-ordinator");
            }
            if(checkMMVData()==false)
            {
                c5.setVisibility(View.GONE);
                message.setText("MMV KML File not available for this FB.Please contact your lab co-ordinator");
            }
            if(checkCMVData()==false && checkMMVData()==false)
            {
                c4.setVisibility(View.GONE);
                c5.setVisibility(View.GONE);
                message.setText("CMV and MMV KML File not available for this FB.Please contact your lab co-ordinator");
            }
            if(checkSurveyData(fbid))
            {
                //c5.setVisibility(View.GONE);
            }
            else
            {
                c3.setVisibility(View.GONE);
               // message.setText("This Fb is yet to be verified by FRJVC Committee.This data will be available after FRJVC Verification.");
            }

            if (i == 1) {
                c4.setChecked(true);
            } else {
                c4.setChecked(false);
            }
            if (j == 1) {
                c5.setChecked(true);
            } else {
                c5.setChecked(false);
            }
            if (k == 1) {
                c3.setChecked(true);
            } else {
                c3.setChecked(false);
            }
            c3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (((CheckBox) view).isChecked()) {
                        try {
                            k = 1;
                            getSurveyPointData(fbid);

                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }finally {
                            getAllSureypoints(userid, divid, rangeid, fbid);
                        }
                    } else {
                        try{
                            googleMap.clear();
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }finally {
                            k = 0;
                            if (c5.isChecked()) {
                                getMMVData(0);
                            }
                            if (c4.isChecked()) {
                                getCMVData(0);
                            }
                            getAllSureypoints(userid, divid, rangeid, fbid);
                        }
                    }
                }
            });
            c4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (((CheckBox) view).isChecked()) {
                        if (kmlstatus.equals("1")) {
                            try {
                                getCMVData(0);
                                i = 1;
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }finally {
                                getAllSureypoints(userid, divid, rangeid, fbid);
                            }


                        } else if (kmlstatus.equals("0")) {
                            //Toast.makeText(ListMapActivity.this, "You don't have CMV and MMV for this FB ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (c4.isChecked()) {
                            try {
                                getCMVData(0);
                                i = 0;
                                googleMap.clear();
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }finally {

                                getAllSureypoints(userid, divid, rangeid, fbid);
                            }
                        } else {
                            try {
                                getCMVData(3);
                                i = 0;
                                googleMap.clear();
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }
                            finally {
                                if (c5.isChecked()) {
                                    getMMVData(0);
                                }
                            }

                        }

                    }

                }
            });
            c5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (((CheckBox) view).isChecked()) {
                        if (kmlstatus.equals("1")) {
                            try {
                                getMMVData(0);
                                j = 1;
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }finally {
                                getAllSureypoints(userid, divid, rangeid, fbid);
                            }

                        } else if (kmlstatus.equals("0")) {
                            //Toast.makeText(ListMapActivity.this, "You don't have CMV and MMV for this FB ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (c5.isChecked()) {

                            getMMVData(3);
                            j = 0;
                            try {
                                googleMap.clear();
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }finally {
                                getAllSureypoints(userid, divid, rangeid, fbid);
                            }

                            //getAllSureypoints(userid, divid, rangeid, fbid);

                        } else {
                            try {
                                getMMVData(3);
                                j = 0;
                                googleMap.clear();
                                // getMMVData(0);
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }finally {
                                if(c4.isChecked()) {
                                    getCMVData(0);
                                }
                            }

                            //getAllSureypoints(userid, divid, rangeid, fbid);

                        }

                    }
                }
            });

            alert2 = new AlertDialog.Builder(alertLayout2.getContext());
            alert2.setView(alertLayout2);
            dialog2 = alert2.create();
            dialog2.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            headerText = alertLayout2.findViewById(R.id.name_textView);
            dialog2.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            dialog2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            headerText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (headerText.getRight() - headerText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            dialog2.dismiss();

                            return true;
                        }
                    }

                    return true;
                }
            });
            dialog2.show();
        } else {
            Intent i = new Intent(getApplicationContext(), RegisterPointActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("id", "ListMap");
            i.putExtra("kml_status", kmlstatus);
            finishAffinity();
            startActivity(i);
        }
        return false;
    }
    private boolean checkCMVData() {
        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getCMVFiles(divid, rangeid, fbid);
            String path = GetFilePath();
            for (int i = 0; i < mfb.size(); i++) {
                if (!mfb.get(i).equals("null")) {

                    File f = new File(path + "/" + mfb.get(i));
                    if (i == 0) {
                        if (f.exists()) {
                            cmvsta = true;
                        } else {
                            cmvsta = false;
                        }
                    }
                }
            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return cmvsta;
    }
    private boolean checkMMVData() {
        try {
            dbHelper.open();
            ArrayList<String> mfb = dbHelper.getMMVFiles(divid, rangeid, fbid);
            String path = GetFilePath();
            for (int i = 0; i < mfb.size(); i++) {
                if (!mfb.get(i).equals("null")) {

                    File f = new File(path + "/" + mfb.get(i));
                    if (i == 0) {
                        if (f.exists()) {
                            mmvsta = true;
                        } else {
                            mmvsta = false;
                        }
                    }
                }
            }
            dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return mmvsta;
    }
    private boolean checkSurveyData(String fbid) {
        try {
            dbHelper.open();
            ArrayList<String> pillarno = new ArrayList<String>();
            ArrayList<String> lat = new ArrayList<String>();
            ArrayList<String> lon = new ArrayList<String>();

            rangeKey = new HashMap<>();
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select m_p_lat,m_p_long,m_fb_pillar_no from m_fb_Survey_pill_data where m_fb_id='" + fbid + "' order by m_fb_pillar_no", null);
            if(cursor.getCount()>0)
            {
                cmvsta=true;
            }
            else {
                cmvsta=false;
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
    private void addSurveyPointtoMap(double key, double value, String pillno,int Status) {
        if(Status==0)
        {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.green);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(key, value)).title("Pillar No:" + pillno).snippet("Lat:" + key +",Long:" + value+ ",Status:" + "Existing").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }else if(Status==1)
        {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.red);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(key, value)).title("Pillar No:" + pillno).snippet("Lat:" + key + ",Long:" + value+ ",Status:" + "Rejected").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }else if(Status==2)
        {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.blue);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(key, value)).title("Pillar No:" + pillno).snippet("Lat:" + key + ",Long:" + value+ ",Status:" + "Proposed").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }else
        {

        }

    }
    private void getSurveyPointData(String fbid)
    {
        try {
            dbHelper.open();
            ArrayList<String> pillarno = new ArrayList<String>();
            ArrayList<String> lat = new ArrayList<String>();
            ArrayList<String> lon = new ArrayList<String>();
            ArrayList<String> status = new ArrayList<String>();

            rangeKey = new HashMap<>();
            db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select m_p_lat,m_p_long,m_fb_pillar_no,m_pillar_avl_sts from m_fb_Survey_pill_data where m_fb_id='" + fbid + "' order by m_fb_pillar_no", null);
            if(cursor.getCount()>0) {
                if (cursor.moveToFirst()) {
                    do {
                        pillarno.add(cursor.getString(cursor.getColumnIndex("m_fb_pillar_no")));
                        lat.add(cursor.getString(cursor.getColumnIndex("m_p_lat")));
                        lon.add(cursor.getString(cursor.getColumnIndex("m_p_long")));
                        status.add(cursor.getString(cursor.getColumnIndex("m_pillar_avl_sts")));
                    } while (cursor.moveToNext());
                }
                cmvsta=true;
                cursor.close();
                db.close();
                for (int j = 0; j < pillarno.size(); j++) {
                    addSurveyPointtoMap(Double.parseDouble(lat.get(j)), Double.parseDouble(lon.get(j)), pillarno.get(j),Integer.parseInt(status.get(j)));
                }
            }else{
                cmvsta=false;
            }
            //dbHelper.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

}
