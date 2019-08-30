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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
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
        LocationListener, DirectionCallback, GoogleMap.OnMarkerClickListener,GoogleMap.OnCameraIdleListener,GoogleMap.OnCameraMoveListener,GoogleMap.OnCameraChangeListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map);



        switchCompat = findViewById(R.id.chk);
        scaleView = (MapScaleView) findViewById(R.id.scaleView);


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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        fbtn = findViewById(R.id.fab);
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterPointActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("id", "ListMap");
                i.putExtra("kml_status",kmlstatus);
                finishAffinity();
                startActivity(i);
            }
        });

        if (kmlstatus.equals("1")) {
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
                });
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
            mCurrLocationMarker = googleMap.addMarker(markerOptions);


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
        db = openOrCreateDatabase("sltp.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select p_lat,p_long,p_no from m_pillar_reg where uid='" + userid + "' and d_id='" + divid + "' and r_id='" + rangeid + "' and fb_id='" + fbid + "' order by point_no", null);
        cursor.moveToFirst();
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

    private void addPointtoMap(double key, double value, String pillno) {
        googleMap.addMarker(new MarkerOptions().position(
                new LatLng(key, value)).title("Pillar No: " + pillno).snippet("Lat:" + key + ",Long:" + value).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).zIndex(1.0f));
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
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
                    });
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
                Intent intent1 = new Intent(getApplicationContext(), UpdatePillarPointDataActivity.class);
                String[] a = marker.getSnippet().split(":");
                String[] aa;
                aa = a[1].split(",");
                String lat = aa[0];
                String lon = a[2];
                intent1.putExtra("lat", lat);
                intent1.putExtra("lon", lon);
                intent1.putExtra("kml_status",kmlstatus);
                startActivity(intent1);
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

    private String GetFilePath() {
        File directory = getExternalFilesDir(null);
        String folder = directory.getAbsolutePath();
        return folder;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MiddleMapListActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("kml_status",kmlstatus);
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
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
