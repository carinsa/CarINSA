package com.carinsa.grandlyon;

import com.carinsa.model.*;
import android.content.Context;
import android.util.Log;

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

public class GrandLyon {
    private Parking[] parkings;
    private Context ctx;
    private boolean fetched=false;
    public GrandLyon(Context ctx){
        this.ctx=ctx;
    }

    public void fetchParkings( ){
        fetched=false;
        parkings=new Parking[0];
        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(ctx.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                //TO DO:
                (Request.Method.GET, "http://10.43.2.67/DEV/parkings/json.php", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parkings=new Parking[response.length()];
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonobject = response.getJSONObject(i);
                                String name = jsonobject.getString("name");
                                Double lat = jsonobject.getDouble("lat");
                                Double lng = jsonobject.getDouble("lng");
                                int state = jsonobject.getInt("state");
                                int available = jsonobject.getInt("available");
                                Parking park = new Parking(name,lat,lng,state,available);
                                parkings[i]=park;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("http",response.toString());
                        fetched=true;
                    }

                    /*@Override
                    public void onResponse(JSONObject response) {
                        //textView.setText("Response: " + response.toString());
                        parkings=new Parking[12];
                        Log.d("http",response.toString());
                    }*/
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("http",error.toString());

                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
    public Parking[] getAllParkings(){
        return parkings;
    }
    public Parking[] getParkings(double lat, double lng, int radius){
        Parking[] park_temp = new Parking[parkings.length];
        int size=0;
        for(int i=0;i<parkings.length;i++){
            if(distance(lat,lng,parkings[i].getLat(),parkings[i].getLng())<=radius){
                park_temp[size]=parkings[i];
                size++;
            }
        }
        Parking[] park_ret=new Parking[size];
        for(int i=0;i<size;i++){
            park_ret[i]=park_temp[i];
        }
        return park_temp;

    }
    public Parking getClosestAvailableParking(double lat, double lng, int radius){
        Parking minPark=null;
        double minDist=100000;
        for(int i=0;i<parkings.length;i++){
            double dist=distance(lat,lng,parkings[i].getLat(),parkings[i].getLng());
            if(parkings[i].getAvailableSpots()>0 && dist<=radius && dist<minDist){
                minDist=dist;
                minPark=parkings[i];
            }
        }
        return minPark;

    }
    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance/2)*Math.sin(latDistance/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(lonDistance/2)*Math.sin(lonDistance/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }
}