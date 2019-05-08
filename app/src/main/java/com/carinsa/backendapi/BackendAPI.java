package com.carinsa.backendapi;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.carinsa.model.Avis;
import com.carinsa.model.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BackendAPI {
    private static List<String> adresses = new ArrayList<>();
    private Parking[] parkings;
    private RequestQueue rq;
    private String uid;
    private int fetchStatus=-1;
    private Runnable callback;

    private static final String URL_GETPARKINGS = "http://192.168.1.100/DEV/parkings/getParkings.php";
    private static final String URL_RATEPARKING = "http://192.168.1.100/DEV/parkings/setRating.php";
    private static final String URL_GETSPOTS = "http://192.168.1.100/DEV/parkings/getUserSpots.php";
    private static final String URL_ADDSPOT = "http://192.168.1.100/DEV/parkings/addUserSpot.php";

    public BackendAPI(RequestQueue rq,String uid){
        this.rq=rq;
        this.uid=uid;
    }

    public void fetchParkings(final Runnable callback){
        this.callback=callback;
        fetchStatus=0;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_GETPARKINGS+"?u="+uid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("json",response.toString());
                    JSONArray values=response.getJSONArray("parkings");
                    parkings = new Parking[values.length()];
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject object = values.getJSONObject(i);
                        String pkgid=object.getString("pkgid");
                        String name=object.getString("name");
                        Double lat=object.getDouble("lat");
                        Double lng=object.getDouble("lng");
                        int available=object.getInt("available");

                        Parking p=new Parking(pkgid,name,lat,lng,available);

                        JSONObject avisObj = object.getJSONObject("ratings");
                        int complet=avisObj.getInt("full");
                        int libre=avisObj.getInt("available");
                        int ferme=avisObj.getInt("close");
                        int ouvert=avisObj.getInt("open");

                        avisObj = object.getJSONObject("my-ratings");
                        boolean avisComplet=avisObj.getBoolean("full");
                        boolean avisLibre=avisObj.getBoolean("available");
                        boolean avisFerme=avisObj.getBoolean("close");
                        boolean avisOuvert=avisObj.getBoolean("open");

                        Avis avis=new Avis(complet,libre,ferme,ouvert,avisComplet,avisLibre,avisFerme,avisOuvert);
                        p.setAvis(avis);
                        parkings[i]=p;
                    }
                    JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, URL_GETSPOTS+"?u="+uid, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.e("json",response.toString());
                                JSONArray values=response.getJSONArray("spots");
                                Parking[] spots = new Parking[values.length()];
                                for (int i = 0; i < values.length(); i++) {
                                    JSONObject object = values.getJSONObject(i);
                                    String spotid=object.getString("spotid");
                                    String name=object.getString("name");
                                    Double lat=object.getDouble("lat");
                                    Double lng=object.getDouble("lng");

                                    int type=object.getInt("type");
                                    boolean free=object.getBoolean("free");
                                    int capacity=object.getInt("capacity");
                                    int nb=object.getInt("nb");

                                    Parking s = new Parking(spotid,name,lat,lng,type,free,capacity,nb);

                                    JSONObject avisObj = object.getJSONObject("ratings");
                                    int complet=avisObj.getInt("full");
                                    int libre=avisObj.getInt("available");
                                    int ferme=avisObj.getInt("close");
                                    int ouvert=avisObj.getInt("open");

                                    avisObj = object.getJSONObject("my-ratings");
                                    boolean avisComplet=avisObj.getBoolean("full");
                                    boolean avisLibre=avisObj.getBoolean("available");
                                    boolean avisFerme=avisObj.getBoolean("close");
                                    boolean avisOuvert=avisObj.getBoolean("open");

                                    Avis avis=new Avis(complet,libre,ferme,ouvert,avisComplet,avisLibre,avisFerme,avisOuvert);
                                    s.setAvis(avis);

                                    spots[i]=s;
                                }
                                Parking[] ps=combine(parkings,spots);
                                parkings=ps;

                                fetchStatus=1;
                                callback.run();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("json",error.toString());
                        }
                    });
                    rq.add(jsonObjectRequest1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("json",error.toString());
            }
        });

        rq.add(jsonObjectRequest);

    }
    private static Parking[] combine(Parking[] a, Parking[] b){
        int length = a.length + b.length;
        Parking[] result = new Parking[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    public int fetchStatus() {
        return fetchStatus;
    }

    public void rateParking(Parking p, int rating){
        if(rating>=0 && rating<=3){
            if(rating==0){
                if(p.getAvis().isAvisComplet()){
                    p.getAvis().setAvisComplet(false);
                    p.getAvis().setComplet(p.getAvis().getComplet() - 1);
                }
                else {
                    p.getAvis().setAvisComplet(true);
                    p.getAvis().setComplet(p.getAvis().getComplet() + 1);
                    if(p.getAvis().isAvisLibre()){
                        p.getAvis().setAvisLibre(false);
                        p.getAvis().setLibre(p.getAvis().getLibre() - 1);
                    }
                    if(p.getAvis().isAvisFerme()){
                        p.getAvis().setAvisFerme(false);
                        p.getAvis().setFerme(p.getAvis().getFerme() - 1);
                    }
                }
            }
            else if(rating==1){
                if(p.getAvis().isAvisLibre()){
                    p.getAvis().setAvisLibre(false);
                    p.getAvis().setComplet(p.getAvis().getLibre() - 1);
                }
                else {
                    p.getAvis().setAvisLibre(true);
                    p.getAvis().setLibre(p.getAvis().getLibre() + 1);
                    if(p.getAvis().isAvisComplet()){
                        p.getAvis().setAvisComplet(false);
                        p.getAvis().setComplet(p.getAvis().getComplet() - 1);
                    }
                    if(p.getAvis().isAvisFerme()){
                        p.getAvis().setAvisFerme(false);
                        p.getAvis().setFerme(p.getAvis().getFerme() - 1);
                    }
                }
            }
            else if(rating==2){
                if(p.getAvis().isAvisFerme()){
                    p.getAvis().setAvisFerme(false);
                    p.getAvis().setFerme(p.getAvis().getFerme() - 1);
                }
                else {
                    p.getAvis().setAvisFerme(true);
                    p.getAvis().setFerme(p.getAvis().getFerme() + 1);
                    if(p.getAvis().isAvisLibre()){
                        p.getAvis().setAvisLibre(false);
                        p.getAvis().setLibre(p.getAvis().getLibre() - 1);
                    }
                    if(p.getAvis().isAvisComplet()){
                        p.getAvis().setAvisComplet(false);
                        p.getAvis().setComplet(p.getAvis().getComplet() - 1);
                    }
                }
            }
            Log.e("REQ",URL_RATEPARKING+"?u="+uid+"&p="+p.getPkgid()+"&r="+rating);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_RATEPARKING+"?u="+uid+"&p="+p.getPkgid()+"&r="+rating, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("json",response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("json",error.toString());
                }
            });
            rq.add(jsonObjectRequest);
        }
    }
    public void addSpot(double lat, double lng, int type, boolean free, int available){
        int vfree=0;
        if(free){
            vfree=1;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_ADDSPOT+"?u="+uid+"&lat="+lat+"&lng="+lng+"&free="+vfree+"&available="+available+"&type="+type, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("json",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("json",error.toString());
            }
        });
        rq.add(jsonObjectRequest);
    }

    public Parking[] getAllParkings() {
        return parkings;
    }

    public Parking[] getParkings(double lat, double lng, int radius) {
        Parking[] park_temp = new Parking[parkings.length];
        int size = 0;
        for (int i = 0; i < parkings.length; i++) {
            if (distance(lat, lng, parkings[i].getLat(), parkings[i].getLng()) <= radius) {
                park_temp[size] = parkings[i];
                size++;
            }
        }
        Parking[] park_ret = new Parking[size];
        for (int i = 0; i < size; i++) {
            park_ret[i] = park_temp[i];
        }
        return park_ret;

    }

    public Parking getClosestAvailableParking(double lat, double lng, int radius) {
        Parking minPark = null;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < parkings.length; i++) {
            double dist = distance(lat, lng, parkings[i].getLat(), parkings[i].getLng());
            if (parkings[i].getAvailableSpots() > 0 && dist <= radius && dist < minDist) {
                minDist = dist;
                minPark = parkings[i];
            }
        }
        return minPark;

    }

    private static double distance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    public void serialyzeAdresses(Context ctx) throws IOException {
        AssetManager am = ctx.getAssets();
        InputStream is = am.open("csv/ruesLyon.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(";");
            if(values.length >3) adresses.add(values[1] + " " + values[3]);
        }
        Log.e("nombre d'adresses", Integer.toString(adresses.size()));
    }

    public String[] getAdresses() {
        String[] res = new String[adresses.size()];
        System.arraycopy(adresses.toArray(), 0, res, 0, adresses.size());
        return res;
    }
}
