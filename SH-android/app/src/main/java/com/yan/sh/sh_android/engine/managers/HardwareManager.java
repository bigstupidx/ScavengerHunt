package com.yan.sh.sh_android.engine.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by yan on 6/11/17.
 * Tracks connectivity status of the device
 */

public class HardwareManager extends Manager{
    Context mContext;
    private static boolean canPingGoogle;
    private static boolean locationEnabled;
    private static final ExecutorService userQueue = Executors.newCachedThreadPool();

    public HardwareManager(Context context){
        this.startup();
        mContext = context;

        if(internetAvailable(mContext)){
            Timber.i("internet available");
            hasActiveInternetConnection();
        }

        isLocationEnabled();
    }

    private boolean internetAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null);
    }

    private void hasActiveInternetConnection() {
        //Pings google on a background thread, checks to make sure wifi is not spotty despite connection
        userQueue.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                    urlConnection.setRequestProperty("User-Agent", "Test");
                    urlConnection.setRequestProperty("Connection", "close");
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.connect();
                    if(urlConnection.getResponseCode() == 200) {
                        canPingGoogle = true;
                    } else {
                        canPingGoogle = false;
                    }
                }catch(Exception ex){
                    Timber.e(ex, "Error pinging google");
                    canPingGoogle = false;
                }
                onNetworkChange();
            }
        });
    }

    public boolean hasNetworkAccess(){
        return canPingGoogle;
    }

    public boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if(locationEnabled != locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            onLocationChange();
        }
        return locationEnabled;
    }

    private void onNetworkChange(){
        Intent networkChange = new Intent();
        networkChange.setAction("NETWORK_CHANGE");

        //send local broadcast here
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(networkChange);
    }

    private void onLocationChange(){
        Intent locationChange = new Intent();
        locationChange.setAction("LOCATION_CHANGE");

        //send local broadcast
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(locationChange);
    }

    public void checkInternet(){
        if(internetAvailable(mContext)){
            hasActiveInternetConnection();
        } else {
            canPingGoogle = false;
            onNetworkChange();
        }
    }



}
