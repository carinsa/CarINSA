package com.carinsa;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.EditText;

import android.widget.CompoundButton;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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


import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static android.widget.Toast.LENGTH_SHORT;
import static org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Parking selectedParking = null;
    private Parking lastSelectedParking = null;
    private AutoCompleteTextView searchBar;
    private ImageButton boutonRecherche;
    private MapView map = null;
    private MyItemizedOverlay myItemizedOverlay = null;
    private MyLocationNewOverlay myLocationOverlay = null;
    private BackendAPI bapi = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private FloatingActionButton fab;
    private FloatingActionButton navigate;
    private FloatingTextButton avis1;
    private FloatingTextButton avis2;
    private FloatingTextButton avis3;
    private TextView viewPeek;
    private TextView viewContent;
    private TextView typeInfo;
    private TextView tailleInfo;
    private TextView prixInfo;
    private PopupWindow popupWindow;
    private Button goButton;
    private Spinner tailleParking;
    private int tailleParkingSelected;
    private RadioGroup prixParking;
    private boolean prixParkingSelected;
    private Spinner typeParking;
    private int typeParkingSelected;
    private View popupView;
    private TranslateAnimation animation;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout llBottomSheet;
    private double lastLat;
    private double lastLong;

    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    private final Handler handler = new Handler();

    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private Marker destination = null;
    private Marker selected = null;
    private FloatingActionButton popupButton;
    private Toolbar mToolbar;
    private Button clear;


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


        this.setContentView(R.layout.activity_main);

        llBottomSheet = (LinearLayout) findViewById(R.id.bottom_fragment);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        navigate = findViewById(R.id.navigator);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        popupButton = (FloatingActionButton) findViewById(R.id.popupButton);
        popupButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popParking();
            }
        });

        fab = findViewById(R.id.fab);
        map = findViewById(R.id.map);
        map.getZoomController().setVisibility(NEVER);
        map.setClickable(true);
        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Log.e("MapView", "normal click");
                searchBar.clearFocus();
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



        searchBar = findViewById(R.id.search_view);

        clear = (Button) findViewById(R.id.clear);
        clear.setVisibility(View.INVISIBLE);

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
                }
            }


        });

        searchBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                clear.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                clear.setVisibility(View.GONE);
            }

        });
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){

                    hideKeypad();
                }
            }
        });



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
        });*/

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
        //bapi = new BackendAPI(requestQueue, "10");
        bapi.fetchParkings(new Runnable() {
            @Override
            public void run() {
                Parking[] parkings = bapi.getAllParkings();
                for (int i = 0; i < parkings.length; i++) {
                    Log.e("p", parkings[i].toString());
                    addMarker(parkings[i]);
                }
                //bapi.addSpot();
            }
        });
        //click effect for fab
        fab.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setCenterInMyCurrentLocation();
            }
        });



    }

    public void clearText(View view) {
        searchBar.setText("");
        clear.setVisibility(View.GONE);

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


                selectMarker(marker, mapView);
//                popParking(parking);

//                Log.e("tap", parking.toString());
                return true;
            }
        });
        parkingMarker.setPosition(parkingGeo);
        parkingMarker.setRelatedObject(p);

        if (p.isSpot()) {
            parkingMarker.setIcon(getResources().getDrawable(R.drawable.markeruser50));
        }
        else if (p.getAvailableSpots() > 0) {
            parkingMarker.setIcon(getResources().getDrawable(R.drawable.markeravailable50));
        } else if (p.getAvailableSpots() == 0) {
            parkingMarker.setIcon(getResources().getDrawable(R.drawable.markerfull50));
        } else {
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

    private void hideKeypad() {
        EditText edtView = (EditText) findViewById(R.id.search_view);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtView.getWindowToken(), 0);
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
        }, 1000);  // first trigger 3000ms. asynchrone!!
        myLocationOverlay.enableMyLocation();



    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        myLocationOverlay.disableMyLocation();
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
        searchBar = (AutoCompleteTextView) findViewById(R.id.search_view);

        //On récupère le bouton que l'on a créé dans le fichier main.xml
        boutonRecherche = (ImageButton) findViewById(R.id.ButtonEnvoyer);

        //Event sur le bouton recherche
        boutonRecherche.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                double[] coords = getLocationFromAddress(searchBar.getText().toString());
                if (coords != null) {
                    if (destination != null) {
                        destination.remove(map);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    Parking p = bapi.getClosestAvailableParking(coords[0], coords[1], 1000);
                    IMapController mapController = map.getController();
                    if(p == null)
                    {
                        mapController.animateTo(new GeoPoint(coords[0], coords[1]));
                        Toast.makeText(MainActivity.this, "Aucun Parking proche libre trouvé.", LENGTH_SHORT).show();
                    }else{
                        GeoPoint address = new GeoPoint(p.getLat(), p.getLng());
                        Marker m = null;

                        for (int i = 0; i < markers.size(); i++) {
                            Parking park = (Parking) markers.get(i).getRelatedObject();
                            if (park == null) {
                                continue;
                            }
                            if (park.toString().equals(p.toString())) {
                                m = markers.get(i);
                            }
                        }
                        if (m != null) {
                            selectMarker(m, map);
                            TextView viewPeek = llBottomSheet.findViewById(R.id.bottom_peek);
                            viewPeek.setText("Parking proche : " + selectedParking.getName());
                        } else {
                            Toast.makeText(MainActivity.this, searchBar.getText(), LENGTH_SHORT).show();
                            mapController.animateTo(address);
                        }
                    }

                    GeoPoint parkingGeo = new GeoPoint(coords[0], coords[1]);
                    destination = new Marker(map);

                    destination.setPosition(parkingGeo);
                    destination.setIcon(getResources().getDrawable(R.drawable.markerdestination50));
                    destination.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker,
                                                     MapView mapView) {
                            //TODO
                            return true;
                        }
                    });
                    map.getOverlays().add(destination);
                    markers.add(destination);
                } else {
                    //TODO
                }
            }
        });

        //On crée la liste d'autocomplétion à partir de notre tableau de string appelé tableauString
        //android.R.layout.simple_dropdown_item_1line permet de définir le style d'affichage de la liste
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tableauString);

        //On affecte cette liste d'autocomplétion à notre objet d'autocomplétion
        searchBar.setAdapter(adapter);
        //Event sur le bouton ok de la searchbar
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    boutonRecherche.callOnClick();
                }
                return false;
            }
        });

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                boutonRecherche.callOnClick();
            }
        });
        //Enfin on rajoute un petit écouteur d'évènement sur le bouton pour afficher
        //dans un Toast ce que l'on a rentré dans notre AutoCompleteTextView

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
    public void popParking(){


        popupView = View.inflate(this, R.layout.popup, null);
        popupWindow = new PopupWindow(popupView, 900,
                WindowManager.LayoutParams.WRAP_CONTENT);

//        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setFocusable(true);

        popupWindow.setOutsideTouchable(true);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(200);

        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 500);
        popupView.startAnimation(animation);

        tailleParking = popupView.findViewById(R.id.SpinnerCapacity);
        prixParking = popupView.findViewById(R.id.radioPrice);
        typeParking = popupView.findViewById(R.id.SpinnerFeedbackType);
        goButton = popupView.findViewById(R.id.ButtonSendFeedback);

        tailleParkingSelected = 0;
        tailleParking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tailleParkingSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                tailleParkingSelected = 0;
            }
        });

        prixParkingSelected = true;
        prixParking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioP){
                    prixParkingSelected = true;
                }else if(checkedId == R.id.radioG){
                    prixParkingSelected = false;
                }
            }
        });

        typeParkingSelected = 0;
        typeParking.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                typeParkingSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                typeParkingSelected = 0;
            }
        });

        goButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.e("lat",Double.toString(mLastLocation.getLatitude()));
                Log.e("long",Double.toString(mLastLocation.getLongitude()));
                Log.e("type Parking", Integer.toString(typeParkingSelected));
                Log.e("prix Parking", Boolean.toString(prixParkingSelected));
                Log.e("taille Parking", Integer.toString(tailleParkingSelected));
                Parking p = bapi.getClosestAvailableParking(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1000);

                if(p==null){
                    bapi.addSpot(mLastLocation.getLatitude(), mLastLocation.getLongitude(), typeParkingSelected, prixParkingSelected, tailleParkingSelected);
                    Snackbar.make(llBottomSheet, "Votre contribution a été prise en compte, Merci !", Snackbar.LENGTH_SHORT).show();
                }else{
                    Log.e("parking proche", p.getName());
                    Snackbar.make(llBottomSheet, "Un parking proche existe déjà.", Snackbar.LENGTH_SHORT).show();
                }

                popupWindow.dismiss();
            }
        });
    }

    public void selectMarker(Marker marker, MapView mapView) {
        if (selected != null) {
            Parking p = (Parking) selected.getRelatedObject();
            if (p.isSpot()) {
                selected.setIcon(getResources().getDrawable(R.drawable.markeruser50));
            }
            else if (p.getAvailableSpots() > 0) {
                selected.setIcon(getResources().getDrawable(R.drawable.markeravailable50));
            } else if (p.getAvailableSpots() == 0) {
                selected.setIcon(getResources().getDrawable(R.drawable.markerfull50));
            } else {
                selected.setIcon(getResources().getDrawable(R.drawable.markerunknown50));
            }
        }
        selected = marker;
        selected.setIcon(getResources().getDrawable(R.drawable.markerselected50));
        lastSelectedParking = selectedParking;
        selectedParking = (Parking) marker.getRelatedObject();
        mapView.getController().animateTo(new GeoPoint(selectedParking.getLat(), selectedParking.getLng()));

        lastLong = selectedParking.getLng();
        lastLat = selectedParking.getLat();
        mapView.getController().animateTo(new GeoPoint(lastLat, lastLong));

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        viewPeek = llBottomSheet.findViewById(R.id.bottom_peek);
        viewContent = llBottomSheet.findViewById(R.id.bottom_content);

        typeInfo = llBottomSheet.findViewById(R.id.typeInfo);
        tailleInfo= llBottomSheet.findViewById(R.id.tailleInfo);
        prixInfo = llBottomSheet.findViewById(R.id.prixInfo);

        viewPeek.setText(selectedParking.getName());
        if (selectedParking.getAvailableSpots() != -1) {
            String str = selectedParking.getAvailableSpots() + " " + "places libres";
            viewContent.setText(str);
        }else{

            viewContent.setText("Pas d'information sur les places disponibles");
        }

        if(selectedParking.isSpot())
        {
            typeInfo.setText(selectedParking.getType());
            tailleInfo.setText(selectedParking.getCapacity());
            prixInfo.setText(selectedParking.isFree());
        }else{
            typeInfo.setText("");
            tailleInfo.setText("");
            prixInfo.setText("");
        }
        avis1 = findViewById(R.id.complet);
        avis3 = findViewById(R.id.ferme);
        avis2 = findViewById(R.id.libre);

        setTextAvis();
        setupNavigation();
        reinitializeButtons();
        setupAvisBouttons();
    }
    private void setupNavigation() {
        navigate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String label = selectedParking.getName();
                String uriBegin = "google.navigation:q=";
                String query = lastLat + "," + lastLong + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + encodedQuery;
                Uri uri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setPackage("com.google.android.apps.maps");
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivity(mapIntent);
                }
            }
        });
    }
    private void setupAvisBouttons() {
        avis1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(selectedParking.isFarFrom(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10000))
                {
                    Snackbar.make(llBottomSheet, "Vous êtes trop loin du parking séléctionné", Snackbar.LENGTH_SHORT).show();

                }else{
                    bapi.rateParking(selectedParking, 0);
                    avis2.setBackgroundColor(Color.parseColor("#19c1e6"));
                    avis3.setBackgroundColor(Color.parseColor("#19c1e6"));
                    Snackbar.make(llBottomSheet, "Votre contribution a été prise en compte, Merci !", Snackbar.LENGTH_SHORT).show();
                    setTextAvis();
                    if(selectedParking.getAvis().isAvisComplet()){

                        avis1.setBackgroundColor(Color.parseColor("#808080"));
                    }else{
                        avis1.setBackgroundColor(Color.parseColor("#19c1e6"));

                    }
                }

            }

        });
        avis2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(selectedParking.isFarFrom(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10000))
                {
                    Snackbar.make(llBottomSheet, "Vous êtes trop loin du parking séléctionné", Snackbar.LENGTH_SHORT).show();
                }else{

                    bapi.rateParking(selectedParking, 1);
                    avis1.setBackgroundColor(Color.parseColor("#19c1e6"));
                    avis3.setBackgroundColor(Color.parseColor("#19c1e6"));
                    Snackbar.make(llBottomSheet, "Votre contribution a été prise en compte, Merci !", Snackbar.LENGTH_SHORT).show();
                    setTextAvis();
                    if (selectedParking.getAvis().isAvisLibre()) {
                        avis2.setBackgroundColor(Color.parseColor("#808080"));
                    } else {
                        avis2.setBackgroundColor(Color.parseColor("#19c1e6"));

                    }
                }

            }
        });
        avis3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(selectedParking.isFarFrom(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 10000))
                {
                    Snackbar.make(llBottomSheet, "Vous êtes trop loin du parking séléctionné", Snackbar.LENGTH_SHORT).show();
                }else {
                    bapi.rateParking(selectedParking, 2);
                    avis2.setBackgroundColor(Color.parseColor("#19c1e6"));
                    avis1.setBackgroundColor(Color.parseColor("#19c1e6"));
                    Snackbar.make(llBottomSheet, "Votre contribution a été prise en compte, Merci !", Snackbar.LENGTH_SHORT).show();
                    setTextAvis();
                    if (selectedParking.getAvis().isAvisFerme()) {

                        avis3.setBackgroundColor(Color.parseColor("#808080"));
                    } else {
                        avis3.setBackgroundColor(Color.parseColor("#19c1e6"));
                    }
                }

            }
        });
    }


    private void reinitializeButtons(){

            boolean complet = selectedParking.getAvis().isAvisComplet();
            boolean libre = selectedParking.getAvis().isAvisLibre();
            boolean ferme = selectedParking.getAvis().isAvisFerme();
            avis1.setBackgroundColor(Color.parseColor("#19c1e6"));
            avis2.setBackgroundColor(Color.parseColor("#19c1e6"));
            avis3.setBackgroundColor(Color.parseColor("#19c1e6"));
            if(complet) {
                avis1.setBackgroundColor(Color.parseColor("#808080"));
            }
            if(libre){
                avis2.setBackgroundColor(Color.parseColor("#808080"));
            }
            if(ferme){
                avis3.setBackgroundColor(Color.parseColor("#808080"));
            }
    }

    private void setTextAvis(){

        TextView viewContent2 = llBottomSheet.findViewById(R.id.bottom_content2);
        TextView viewContent3 = llBottomSheet.findViewById(R.id.bottom_content3);
        TextView viewContent4 = llBottomSheet.findViewById(R.id.bottom_content4);
        int complet = selectedParking.getAvis().getComplet();
        int libre = selectedParking.getAvis().getLibre();
        int ferme = selectedParking.getAvis().getFerme();

        viewContent2.setText(complet + " Personnes ont déclaré que ce parking est complet.");
        viewContent3.setText(libre + " Personnes ont déclaré que ce parking est libre.");
        viewContent4.setText(ferme + " Personnes ont déclaré que ce parking est ferme.");
    }
}

}

