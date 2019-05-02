package com.carinsa.grandlyon;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.carinsa.model.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrandLyon {
    private static List<String> adresses = new ArrayList<>();
    private Parking[] parkings;
    private Context ctx;
    private int fetched = -1;

    public GrandLyon(Context ctx) {
        this.ctx = ctx;
    }

    private static final String GL_USERNAME = "alexandre.van-beurden@insa-lyon.fr";
    private static final String GL_PASSWORD = "7os9E5+2sPJ7FdWdpDqcxlD9k";  //alex's secret garden
    private static final String GL_URL_PARK = "https://download.data.grandlyon.com/ws/rdata/pvo_patrimoine_voirie.pvoparkingtr/all.json";
    private static final String GL_URL_GEO = "https://download.data.grandlyon.com/ws/rdata/pvo_patrimoine_voirie.pvoparkingtr/the_geom.json";

    private RequestQueue requestQueue;
    private JsonObjectRequest parkingReq = new JsonObjectRequest(Request.Method.GET, GL_URL_PARK, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.w("req1", response.toString());
            try {
                JSONArray values = response.getJSONArray("values");
                parkings = new Parking[values.length()];
                for (int i = 0; i < values.length(); i++) {
                    JSONObject parking = values.getJSONObject(i);
                    String name = parking.getString("nom");
                    String state_str = parking.getString("etat_code");
                    int state = -1;
                    if (state_str.equals("1")) {
                        state = 1;
                    } else if (state_str.equals("2")) {
                        state = 2;
                    } else if (state_str.equals("3")) {
                        state = 3;
                    }
                    String available_str = parking.getString("etat");
                    int available = -1;
                    if (available_str.equals("Parking complet")) {
                        available = 0;
                    } else {
                        Pattern pattern = Pattern.compile("([0-9]+) places? libres?");
                        Matcher matcher = pattern.matcher(available_str);
                        if (matcher.matches()) {
                            available = Integer.parseInt(matcher.group(1));
                        }
                    }
                    Parking park = new Parking(name, 0, 0, state, available);
                    parkings[i] = park;


                }
                fetched = 2;
                requestQueue.add(geomReq);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //Failure Callback
                }
            }) {
        @Override
        public Map getHeaders() {
            Map<String, String> params = new HashMap<>();
            String creds = String.format("%s:%s", GL_USERNAME, GL_PASSWORD);
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
            params.put("Authorization", auth);
            return params;
        }
    };
    private JsonObjectRequest geomReq = new JsonObjectRequest(Request.Method.GET, GL_URL_GEO, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.w("req1", response.toString());
            try {
                JSONArray values = response.getJSONArray("values");
                for (int i = 0; i < values.length(); i++) {
                    String coord = values.getString(i);
                    Pattern pattern = Pattern.compile("MULTIPOINT\\(([0-9]+\\.[0-9]+) ([0-9]+\\.[0-9]+)\\)");
                    Matcher matcher = pattern.matcher(coord);
                    double lat = 0;
                    double lng = 0;
                    if (matcher.matches()) {
                        lat = Double.parseDouble(matcher.group(2));
                        lng = Double.parseDouble(matcher.group(1));
                    }
                    parkings[i].setLat(lat);
                    parkings[i].setLng(lng);
                }
                fetched = 1;
                for (int i = 0; i < parkings.length; i++) {
                    Log.w("el", parkings[i].toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //Failure Callback
                }
            }) {
        @Override
        public Map getHeaders() {
            Map<String, String> params = new HashMap<>();
            String creds = String.format("%s:%s", GL_USERNAME, GL_PASSWORD);
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
            params.put("Authorization", auth);
            return params;
        }
    };

    public void fetchParkings() {
        fetched = 0;
        parkings = new Parking[0];
        Cache cache = new DiskBasedCache(ctx.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();


        requestQueue.add(parkingReq);

    }

    public int fetchStatus() {
        return fetched;
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
        double minDist = 100000;
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