package com.carinsa;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.carinsa.grandlyon.GrandLyon;
import com.carinsa.model.Parking;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView searchBar;
    private MapView map = null;
    private MyItemizedOverlay myItemizedOverlay = null;
    private MyLocationNewOverlay myLocationOverlay = null;
    private GrandLyon grandlyon = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private FloatingActionButton fab;

    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    private final Handler handler = new Handler();


    @Override public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        checkPermissionsState();

        setupSearchBar(savedInstanceState);


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

            setupMap();

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

    private void setupMap() {
        fab = findViewById(R.id.fab);
        map = findViewById(R.id.map);
        map.setClickable(true);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15);

        Drawable marker;
        /* marker star for future use*/
        marker=getResources().getDrawable(android.R.drawable.star_big_on);
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);

        myItemizedOverlay = new MyItemizedOverlay(marker);
        map.getOverlays().add(myItemizedOverlay);

        //customised geopoint
        GeoPoint myPoint1 = new GeoPoint(0.0, 0.0);
        myItemizedOverlay.addItem(myPoint1, "myPoint1", "myPoint1");

        searchBar = findViewById(R.id.search_view);
        Log.e("1", "test");
        searchBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }

            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }


            public boolean onQueryTextChange(String newText) {
                //callSearch(newText);
                return true;
            }


            private void callSearch(String query) {
                Log.e("1", query);
            }
        });

        //marker starter
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        //startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);// position the marker in center
        map.getOverlays().add(startMarker);
        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        //my position
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
        map.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();


        grandlyon = new GrandLyon(this);
        grandlyon.fetchParkings();

        //click effect for fab
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCenterInMyCurrentLocation();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Check something after 1 second
                if(grandlyon.fetchStatus()!=1){
                    handler.postDelayed(this, 100);
                }else{
                    Parking p = grandlyon.getClosestAvailableParking(45.75,4.85,5000);
                    addMarker(p.getLat(),p.getLng());
                }
            }
        }, 500); // first trigger 3000ms. asynchrone!!
    }

    protected void addMarker(double lat, double lon){
        GeoPoint parkingGeo = new GeoPoint(lat, lon);
        Marker parkingMarker = new Marker(map);

        parkingMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener(){
                @Override
                public boolean onMarkerClick(Marker marker,
                                      MapView mapView){
                    Log.e("tap","TAP");
                    return true;
                }
          });
        parkingMarker.setPosition(parkingGeo);
        map.getOverlays().add(parkingMarker);
    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void setCenterInMyCurrentLocation() {
        if (mLastLocation != null) {
            map.getController().setCenter(new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Getting current location", LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    mLastLocation = location;
                }
            }
        });


    }




    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        handler.postDelayed(new Runnable() {
        public void run() {
            setCenterInMyCurrentLocation();

        }
    }, 2000);  // first trigger 3000ms. asynchrone!!



//add

    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void setupSearchBar(Bundle savedInstanceState) {
        try {
            grandlyon.serialyzeAdresses(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //On récupère le tableau de String créé dans le fichier string.xml
        String[] tableauString = grandlyon.getAdresses();

        //On récupère l'AutoCompleteTextView que l'on a créé dans le fichier main.xml
        final AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.search_view);

        //On récupère le bouton que l'on a créé dans le fichier main.xml
        ImageButton boutonRecherche = (ImageButton) findViewById(R.id.ButtonEnvoyer);

        //On crée la liste d'autocomplétion à partir de notre tableau de string appelé tableauString
        //android.R.layout.simple_dropdown_item_1line permet de définir le style d'affichage de la liste
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tableauString);

        //On affecte cette liste d'autocomplétion à notre objet d'autocomplétion
        autoComplete.setAdapter(adapter);

        //Enfin on rajoute un petit écouteur d'évènement sur le bouton pour afficher
        //dans un Toast ce que l'on a rentré dans notre AutoCompleteTextView
        boutonRecherche.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                double[] coords= getLocationFromAddress(autoComplete.getText().toString());
                if (coords != null){
                    Parking p = grandlyon.getClosestAvailableParking(coords[0],coords[1],5000);
                    addMarker(p.getLat(),p.getLng());
                    map.getController().setCenter(new GeoPoint(p.getLat(),p.getLng()));
                    IMapController mapController = map.getController();
                    mapController.setZoom(15);
                    Toast.makeText(MainActivity.this, autoComplete.getText(), LENGTH_SHORT).show();
                }else{
                     //TODO
                }
            }
        });
    }

    public double[] getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;
        double[] res = new double[2];

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);

            res[0]=location.getLatitude();
            res[1]=location.getLongitude();

            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}