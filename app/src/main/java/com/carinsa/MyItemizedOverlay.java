package com.carinsa;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> overlayItemList = new ArrayList<>();

    public MyItemizedOverlay(Drawable pDefaultMarker) {
        super(pDefaultMarker);
        // TODO Auto-generated constructor stub
    }

    public void addItem(GeoPoint p, String title, String snippet){
        OverlayItem newItem = new OverlayItem(title, snippet, p);
        overlayItemList.add(newItem);
        populate();
    }


    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected OverlayItem createItem(int arg0) {
        // TODO Auto-generated method stub
        return overlayItemList.get(arg0);
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return overlayItemList.size();
    }

}