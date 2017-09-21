package com.yan.sh.sh_android.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yan.sh.sh_android.R;
import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.engine.objects.Objective;
import com.yan.sh.sh_android.util.Utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Objective focusedObjective;
    private final static int REQUEST_LOCATION_ID = 99;
    private ArrayList<LatLng> latLngArrayList;
    private final static int REFOCUS_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //add back button
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,REFOCUS_ID,0,"Re-focus");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case REFOCUS_ID:
                zoomToBounds();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, Utilities.dpToPixels(20, this), 0, 0);

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            focusedObjective = Engine.objective().getObjectiveById(intent.getStringExtra("id"));
            Timber.i(intent.getStringExtra("id"));
            getActionBar().setTitle(focusedObjective.getName());
        }

        createObjectiveMarkers();
        enableLocation();
        zoomToBounds();
    }

    private void enableLocation(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Timber.i("Location granted");
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            }
        }
    }

    private void createObjectiveMarkers(){
        if(focusedObjective != null){
            LatLng latLng = new LatLng(focusedObjective.getLat(), focusedObjective.getLon());
            mMap.addMarker(new MarkerOptions().position(latLng).title(focusedObjective.getName()));
            return;
        }

        ArrayList<Objective> objectives = Engine.objective().getObjectives();
        if(objectives == null || objectives.size() <= 0){
            return;
        }

        latLngArrayList = new ArrayList<>();
        for(Objective objective : objectives){
            if(!objective.getCompleted()){
                LatLng latLng = new LatLng(objective.getLat(), objective.getLon());
                mMap.addMarker(new MarkerOptions().position(latLng).title(objective.getName()));
                latLngArrayList.add(latLng);
            }
        }
    }

    private void zoomToBounds(){
        //check if we have a focused objective
        if(focusedObjective != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(focusedObjective.getLat(), focusedObjective.getLon()), 15));
            return;
        }

        Double latTop = null;
        Double latBot = null;
        Double lonTop = null;
        Double lonBot = null;
        Location location = getLastKnownLocation();

        if(location != null) {
            latTop = location.getLatitude();
            latBot = location.getLatitude();
            lonTop = location.getLongitude();
            lonBot = location.getLongitude();
        }

        if((latLngArrayList == null || latLngArrayList.size() <= 0) && location == null){
            return;
        }

        if((location == null && latLngArrayList.size() == 1)){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngArrayList.get(0)));
            return;
        }

        for(LatLng latLng : latLngArrayList){
            if(latTop == null){
                latTop = latLng.latitude;
                latBot = latLng.latitude;
            } else if(latLng.latitude > latTop){
                latTop = latLng.latitude;
            } else if (latLng.latitude < latBot) {
                latBot = latLng.latitude;
            }

            if(lonTop == null){
                lonTop = latLng.longitude;
                lonBot = latLng.longitude;
            } else if(latLng.longitude > lonTop){
                lonTop = latLng.longitude;
            } else if (latLng.longitude < lonBot){
                lonBot = latLng.longitude;
            }
        }
        Timber.i(latTop.toString() + latBot.toString() + lonTop.toString() + lonBot.toString());
        LatLng northEast = new LatLng(latTop, lonTop);
        LatLng southWest = new LatLng(latBot, lonBot);

        int padding = Utilities.dpToPixels(30, this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southWest, northEast), padding));
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap.clear();
        mMap = null;
        latLngArrayList = null;
        focusedObjective = null;
    }

    //TODO : custom annotation with camera button
}
