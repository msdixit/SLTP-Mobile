package com.sparc.frjvcapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;


/*import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import org.osmdroid.wms.WMSEndpoint;

import org.osmdroid.wms.WMSLayer;
import org.osmdroid.wms.WMSParser;
import org.osmdroid.wms.WMSTileSource;*/

import android.preference.PreferenceManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class MapViewerActivity extends AppCompatActivity{
    /*MapView map = null;
    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    private MapView mapView;
    private Location currentLocation;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    protected ImageButton btCenterMap;
    private LocationManager lm;
    WMSEndpoint cap;*/
    AlertDialog show = null;
    AlertDialog layerPicker = null;
    AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_viewer);
        //BottomNavigationView bottomView = findViewById(R.id.navigationView);
        /*Context ctx = this;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        final DisplayMetrics dm = ctx.getResources().getDisplayMetrics();

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);

        this.mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx),
                mapView);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), mapView);

        mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(true);

        mapView.getController().setZoom(15);
        mapView.setTilesScaledToDpi(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setFlingEnabled(true);
        mapView.getOverlays().add(this.mLocationOverlay);
        mapView.getOverlays().add(this.mCompassOverlay);
        mapView.getOverlays().add(this.mScaleBarOverlay);

        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);
        mCompassOverlay.enableCompass();

        btCenterMap = findViewById(R.id.ic_center_map);

        btCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i(TAG, "centerMap clicked ");
                if (currentLocation != null) {
                    GeoPoint myPosition = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mapView.getController().animateTo(myPosition);
                }
            }
        });

        mMinimapOverlay = new MinimapOverlay(ctx, mapView.getTileRequestCompleteHandler());
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);
        mapView.getOverlays().add(this.mMinimapOverlay);
        downloadAndParse();*/
    }

    /*private void downloadAndParse() {

        String[] serverURL = {"http://203.129.207.132:8080/geoserver/CDLTP_Rourkela_Vector/wms?"};
        WMSTileSource wmsTileSource = new WMSTileSource("OGC:WMS", serverURL , "CDLTP_Rourkela_Vector%3ARourkela_fb_boundary_cmv", "1.1.0", "EPSG:32654", "", 256);
        mapView.setTileSource(wmsTileSource);

        *//*new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ok = false;
                Exception root = null;
                try {
                    URLConnection c = null;
                    InputStream is = null;
                    try {
                        c = (URLConnection) new URL("http://203.129.207.132:8080/geoserver/CDLTP_Rourkela_Vector/ows?service=WMS&version=1.1.0&request=GetMap&layers=CDLTP_Rourkela_Vector:Rourkela_fb_boundary_cmv").openConnection();
                        is = c.getInputStream();
                        cap = WMSParser.parse(is);
                        ok = true;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        root = ex;
                    } finally {
                        if (is != null) try {
                            is.close();
                        } catch (Exception ex) {
                        }
                        if (c != null)
                            try {
                                //c.();
                            } catch (Exception ex) {
                            }
                    }


                } catch (Exception ex) {
                    root = ex;
                    ex.printStackTrace();
                }

                if (ok) {
                    promptUserForLayerSelection();
                } else {

                    showErrorMessage(root);
                }
            }


        }).start();*//*
    }

    private void showErrorMessage(final Exception root) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("There was an error communicating with the server: \n" + root.getMessage());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void promptUserForLayerSelection() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getApplicationContext());
                builderSingle.setIcon(R.drawable.filter_icon);
                builderSingle.setTitle("Select A Layer");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_singlechoice);
                for (int i = 0; i < cap.getLayers().size(); i++) {
                    arrayAdapter.add(cap.getLayers().get(i).getTitle());
                }


                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layerPicker.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        for (WMSLayer layer : cap.getLayers()) {
                            if (strName.equals(layer.getTitle())) {
                                WMSTileSource source = WMSTileSource.createFrom(cap, layer);
                                if (layer.getBbox() != null) {
                                    //center map on this location
                                    try {
                                        //double centerLat = (Double.parseDouble(layer.getBbox().getMaxy()) + Double.parseDouble(layer.getBbox().getMiny())) / 2;
                                        //double centerLon = (Double.parseDouble(layer.getBbox().getMaxx()) + Double.parseDouble(layer.getBbox().getMinx())) / 2;
                                        //mMapView.getController().animateTo(new GeoPoint(centerLat, centerLon));

                                        mapView.zoomToBoundingBox(layer.getBbox(), true);
                                        mapView.zoomToBoundingBox(layer.getBbox(), true);
                                        mapView.zoomToBoundingBox(layer.getBbox(), true);

                                    } catch (java.lang.Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                mapView.setTileSource(source);

                                break;
                            }
                        }
                        layerPicker.dismiss();

                    }
                });
                layerPicker = builderSingle.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            lm.removeUpdates(this);
        } catch (Exception ex) {
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (show != null && show.isShowing()) {
            show.dismiss();
        }
        if (layerPicker != null && layerPicker.isShowing()) {
            layerPicker.dismiss();
        }
        mCompassOverlay.disableCompass();
        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.disableMyLocation();
        mScaleBarOverlay.enableScaleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            //this fails on AVD 19s, even with the appcompat check, says no provided named gps is available
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }

        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.enableMyLocation();
        mScaleBarOverlay.disableScaleBar();
    }

    private void checkPermissionsState() {
        int internetPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int networkStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);

        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE);

        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                wifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED) {

            //setupMap();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE},
                    MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean somePermissionWasDenied = false;
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            somePermissionWasDenied = true;
                        }
                    }
                    if (somePermissionWasDenied) {
                        Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                        //setupMap();
                    }
                } else {
                    Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    @Override
    public void onBackPressed() {
        //clearData();
        Intent intent = new Intent(getApplicationContext(), MainContainerActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onLocationChanged(Location location) {
        //Cur currentLocation = new GeoPoint(location);
        //displayMyCurrentLocationOverlay();
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/


}
