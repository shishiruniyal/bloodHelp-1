package com.mycompany.servelettest;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    static int user_id;
    private static String url_server = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/update";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    JSONParser jparser = new JSONParser();
    JSONObject json;
    Location newLocation = null;
    Intent intent;
    int counter = 0;

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.e("Inside Service", "onCreate");
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Inside Service", "onStartCommand");
        if (intent != null) {
            user_id = Integer.parseInt(intent.getStringExtra("id"));
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();

            String fineLocation, coarseLocation;
            fineLocation = "" + ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            coarseLocation = "" + ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            Log.e("Fine", fineLocation);
            Log.e("Coarse", coarseLocation);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("LocationService", "Here Dude");
            }
            long minTime = 0; //in milliseconds, 0 sec.
            float minDistance = 0; //in meters, 0 m.
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);

        } else {
            Log.e("Alert Message", "Intent null");
        }
        return START_REDELIVER_INTENT;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");

        String fineLocation, coarseLocation;
        fineLocation = "" + ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        coarseLocation = "" + ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        Log.e("Fine", fineLocation);
        Log.e("Coarse", coarseLocation);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("shishir", "I am here dude");

        }

        locationManager.removeUpdates(listener);
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("******************", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                Log.e("Vaibhav", "i am here");
                Log.e("Vaibhav", "Location :" + loc.getLatitude() + "," + loc.getLongitude());
                newLocation = loc;
                new updateLocation().execute();
                sendBroadcast(intent);

            }


        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

    // modifying code
    private class updateLocation extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Log.e("background", "Async Task Process Started");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lat", "" + newLocation.getLatitude()));
            params.add(new BasicNameValuePair("lon", "" + newLocation.getLongitude()));
            params.add(new BasicNameValuePair("user", "" + user_id));
            Log.e("Message", "before sending request");
            json = jparser.makeHttpRequest(url_server, "GET", params);
            if (json == null) {
                Log.e("message", "null object returned");
            }
            Log.e("Message", "object returned");
            String s = null;
            Log.e("message", json.toString());
            try {
                s = json.getString("info");
                Log.d("Msg", json.getString("info"));
                if (s.equals("success")) {
                    Log.e("Vaibhav+Akshay", "Done");
                } else if (s.equals("fail")) {
                    Log.e("Vaibhav+Akshay", "Fail");
                    return s;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
    //modifting code ends


}
