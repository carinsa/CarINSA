package com.carinsa;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
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
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.carinsa.backendapi.BackendAPI;
import com.carinsa.model.Parking;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static android.widget.Toast.LENGTH_SHORT;
import static org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView searchBar;
    private MapView map = null;
    private MyItemizedOverlay myItemizedOverlay = null;
    private MyLocationNewOverlay myLocationOverlay = null;
    private BackendAPI bapi = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private View popupView;
    private TranslateAnimation animation;
    private  BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout llBottomSheet;

    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    private final Handler handler = new Handler();

    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private Marker destination = null;
    private Marker selected = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
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


        setContentView(R.layout.activity_main);

        llBottomSheet = (LinearLayout) findViewById(R.id.bottom_fragment);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);




        fab = findViewById(R.id.fab);
        map = findViewById(R.id.map);
        map.getZoomController().setVisibility(NEVER);
        map.setClickable(true);
        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Log.e("MapView", "normal click");
                return true;
            }
            public boolean longPressHelper(GeoPoint p) {
                Log.e("MapView", "long click");
                return false;
            }
        }));

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15);

        Drawable marker;
        /* marker star for future use*/
        marker = getResources().getDrawable(android.R.drawable.star_big_on);
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
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        map.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();


        Cache cache = new DiskBasedCache(this.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        bapi = new BackendAPI(requestQueue, Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        bapi.fetchParkings(new Runnable() {
            @Override
            public void run() {
                Parking[] parkings = bapi.getAllParkings();
                for(int i=0;i<parkings.length;i++){
                    Log.e("p",parkings[i].toString());
                    addMarker(parkings[i]);
                }
            }
        });
        //click effect for fab
        fab.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setCenterInMyCurrentLocation();
            }
        });

    }

    protected void addMarker(Parking p) {
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).getRelatedObject().toString().equals(p.toString())) {
                return;
            }
        }
        GeoPoint parkingGeo = new GeoPoint(p.getLat(), p.getLng());
        Marker parkingMarker = new Marker(map);

        parkingMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker,
                                         MapView mapView) {
                selectMarker(marker,mapView);

//                popParking(parking);

//                Log.e("tap", parking.toString());
                return true;
            }
        });
        parkingMarker.setPosition(parkingGeo);
        parkingMarker.setRelatedObject(p);
        if(p.getAvailableSpots()>0) {
            parkingMarker.setIcon(getResources().getDrawable(R.drawable.markeravailable50));
        }
        else if(p.getAvailableSpots()==0) {
            parkingMarker.setIcon(getResources().getDrawable(R.drawable.markerfull50));
        }
        else {
            parkingMarker.setIcon(getResources().getDrawable(R.drawable.markerunknown50));
        }
        map.getOverlays().add(parkingMarker);
        markers.add(parkingMarker);
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


    public void onResume() {
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

    public void onPause() {
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
            bapi.serialyzeAdresses(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //On récupère le tableau de String créé dans le fichier string.xml
        String[] tableauString = bapi.getAdresses();

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
                double[] coords = getLocationFromAddress(autoComplete.getText().toString());
                if (coords != null) {
                    if(destination!=null){
                        destination.remove(map);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    Parking p = bapi.getClosestAvailableParking(coords[0], coords[1], 1000);
                    Parking[] ps = bapi.getParkings(coords[0], coords[1], 1000);
                    GeoPoint address = new GeoPoint(p.getLat(), p.getLng());
                    Marker m=null;
                    IMapController mapController = map.getController();
                    for(int i=0;i<markers.size();i++){
                        Parking park= (Parking) markers.get(i).getRelatedObject();
                        if(park==null){
                            continue;
                        }
                        if(park.toString().equals(p.toString())){
                            m=markers.get(i);
                        }
                    }
                    if(m!=null) {
                        selectMarker(m, map);
                    }
                    else {
                        mapController.animateTo(address);
                    }




                    //mapController.setZoom(15);
                    //map.zoomToBoundingBox(boundingBox, true);


                    GeoPoint parkingGeo = new GeoPoint(coords[0], coords[1]);
                    destination = new Marker(map);

                    destination.setPosition(parkingGeo);
                    destination.setIcon(getResources().getDrawable(R.drawable.markerdestination50));
                    /*destination.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker,
                                                     MapView mapView) {
                            mapView.getOverlayManager().remove(marker);
                            return true;
                        }
                    });*/
                    map.getOverlays().add(destination);
                    markers.add(destination);

                    //mapController.animateTo(new GeoPoint(p.getLat(),p.getLng()));
                    Toast.makeText(MainActivity.this, autoComplete.getText(), LENGTH_SHORT).show();
                } else {
                    //TODO
                }
            }
        });
    }

    public double[] getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;
        double[] res = new double[2];

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);

            res[0] = location.getLatitude();
            res[1] = location.getLongitude();

            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }



    }
    public void popParking(Parking p){

        popupView = View.inflate(this, R.layout.popup, null);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        TextView tv = popupView.findViewById(R.id.position);
        if(p.getAvailableSpots()!=-1) {
            tv.setText(p.getName() + "\n" + p.getAvailableSpots()+" places libres");
        }
        else {
            tv.setText(p.getName());
        }
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);

        popupWindow.setOutsideTouchable(true);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(200);

        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 10, 10);
        popupView.startAnimation(animation);



    }
    public void selectMarker(Marker marker,MapView mapView){
        if(selected!=null){
            Parking p = (Parking) selected.getRelatedObject();
            if(p.getAvailableSpots()>0){
                selected.setIcon(getResources().getDrawable(R.drawable.markeravailable50));
            }
            else if(p.getAvailableSpots()==0){
                selected.setIcon(getResources().getDrawable(R.drawable.markerfull50));
            }
            else {
                selected.setIcon(getResources().getDrawable(R.drawable.markerunknown50));
            }
        }
        selected=marker;
        selected.setIcon(getResources().getDrawable(R.drawable.markerselected50));
        Parking parking = (Parking) marker.getRelatedObject();
        mapView.getController().animateTo(new GeoPoint(parking.getLat(), parking.getLng()));

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        TextView viewPeek = llBottomSheet.findViewById(R.id.bottom_peek);
        TextView viewContent = llBottomSheet.findViewById(R.id.bottom_content);

        viewPeek.setText(parking.getName());
        if(parking.getAvailableSpots()!=-1) {
            String str = parking.getAvailableSpots()+" "+"places libres";
            viewContent.setText(str);
        }
        else {
            viewContent.setText("Pas d'information disponible");
        }
    }
}