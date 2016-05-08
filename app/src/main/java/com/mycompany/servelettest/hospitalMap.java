package com.mycompany.servelettest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class hospitalMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static String url_donor = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/location";
    HashMap<Marker, Integer> hospi;
    HashMap<Marker, Integer> don;
    JSONArray jsonArrayHospital, jsonArrayDonor;
    Double hospitalLat, hospitalLon;
    Marker hospitalMarker;
    Marker donorMarker;
    JSONParser jparser = new JSONParser();
    boolean donorStatus = false;
    LocationManager locationManager;
    Location location;
    String whoCliked;
    String bloodGroup;
    int userid;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("shishir", "I am here dude");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Location Disabled Enable it", Toast.LENGTH_SHORT).show();
        }
        // Add a to current position
        Double currentLat, currentLon;
        Double lat1, lat2, lon1, lon2;
        Double factor;

        //current latitutde and longitude
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

//        //defining bounding box rectangle for 10 miles
//        lat1 = currentLat-(10.0 / 69.0);
//        lat2 = currentLat+(10.0/69.0);
//        factor = 10.0/(69.0*Math.abs(Math.cos(Math.toRadians(currentLat))));
//        lon1 = currentLon - factor;
//        lon2 = currentLon + factor;
//        Log.e("error value" ,factor.toString());
//        Log.e("first Point","("+lat1+","+lon1+")");
//        Log.e("Second Point","("+lat1+","+lon2+")");
//        Log.e("Third Point","("+lat2+","+lon1+")");
//        Log.e("Fourth Point","("+lat2+","+lon2+")");
//
//        PolygonOptions rectOption = new PolygonOptions().add(new LatLng(lat1,lon1),new LatLng(lat1,lon2),new LatLng(lat2,lon2),new LatLng(lat2,lon1));
//        rectOption.strokeColor(Color.RED);
//        //rectOption.fillColor(Color.BLUE);
//        Polygon polygon = mMap.addPolygon(rectOption);


        mMap.clear();
        mMap.setOnInfoWindowClickListener(this);
        LatLng home = new LatLng(currentLat, currentLon);
        //adding circle
        CircleOptions circleOptions = new CircleOptions()
                .center(home).radius(6437); // In meters
        circleOptions.strokeWidth(0);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(0x40f44336);
// Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circleOptions.getCenter(), 13));
        Marker myLocation = mMap.addMarker(new MarkerOptions().position(home)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.curr3)));


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(home, 12);
        mMap.animateCamera(cameraUpdate);
        MarkLocationsHospital();


    }


    // Marking hospitals
    public void MarkLocationsHospital() {
        whoCliked = "Hospital";
        hospi = new HashMap<Marker, Integer>();
        Intent intent = getIntent();
        String message = intent.getStringExtra(selectBloodGroup.HOSP_MESSAGE);
        userid = Integer.parseInt(intent.getStringExtra("USERID"));
        Log.e("User_id", "" + userid);
        bloodGroup = intent.getStringExtra("bloodGroup");
        Log.e("Message received dude", message);

        // bloodGroup = bloodGroup.replace("+","%2B");


        // LatLngBounds.Builder builder = new LatLngBounds.Builder();
        try {
            jsonArrayHospital = new JSONArray(message);
            //Log.e("Array 1st element",jsonArrayHospital.get(0).toString());
            for (int i = 0; i < jsonArrayHospital.length(); i++) {
//                Log.e("Inisde Hospital",jsonArrayDonor.getJSONObject(i).toString());
                LatLng l = new LatLng(jsonArrayHospital.getJSONObject(i).getDouble("latitude"), jsonArrayHospital.getJSONObject(i).getDouble("longitude"));
                Marker m = mMap.addMarker(new MarkerOptions().position(l).title(jsonArrayHospital.getJSONObject(i).getString("name")).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                // builder.include(l);
                hospi.put(m, i);
            }

//            LatLngBounds bounds = builder.build();
//            int padding = 50; // offset from edges of the map in pixels
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//            mMap.animateCamera(cu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Marking Donors
    public void MarkLocationDonors() {
        LatLng home = new LatLng(location.getLatitude(), location.getLongitude());
        //adding circle
        CircleOptions circleOptions = new CircleOptions().center(home).radius(16093); // In meters
        circleOptions.strokeWidth(0);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(0x40f44336);
// Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circleOptions.getCenter(), 12));
//        Marker myLocation = mMap.addMarker(new MarkerOptions().position(home).title("Your are here")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(home, 12);
        mMap.animateCamera(cameraUpdate);
        whoCliked = "Donor";
        Log.e("Donor Selected", "Inside donor");
        don = new HashMap<Marker, Integer>();
        try {
            Log.e("MarkLocation", jsonArrayDonor.toString());
            for (int i = 0; i < jsonArrayDonor.length(); i++) {
                Log.e("Inisde Donor", jsonArrayDonor.getJSONObject(i).toString());
                LatLng l = new LatLng(jsonArrayDonor.getJSONObject(i).getDouble("latitude"), jsonArrayDonor.getJSONObject(i).getDouble("longitude"));
                Marker m = mMap.addMarker(new MarkerOptions().position(l).title(jsonArrayDonor.getJSONObject(i).getString("user_id")).icon(BitmapDescriptorFactory.fromResource(R.drawable.blood)).snippet(jsonArrayDonor.getJSONObject(i).getString("blood_group")));
                //Marker m = mMap.addMarker(new MarkerOptions().position(l).title(jsonArrayDonor.getJSONObject(i).getString("user_id")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).snippet(jsonArrayDonor.getJSONObject(i).getString("blood_group")));
                don.put(m, i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        //if Hospital marker is clicked
        if (whoCliked.equals("Hospital")) {
            try {
                hospitalMarker = marker;
                hospitalLat = jsonArrayHospital.getJSONObject(hospi.get(marker)).getDouble("latitude");
                hospitalLon = jsonArrayHospital.getJSONObject(hospi.get(marker)).getDouble("longitude");

//            String name = jsonArrayHospital.getJSONObject(h.get(marker)).getString("name");
//            Toast.makeText(this, "It's "+name,
//                    Toast.LENGTH_SHORT).show();

                new don().execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // if door marker is clicked
        else if (whoCliked.equals("Donor")) {
            try {
                // put the code to send notification to donor send data from two objects
                // obtain userid from jsonArrayDonor.getJSONObject(don.get(donorMarker)) to get details of donor
                // obtain details of client from it's userid
                //obtain details of hospital from jsonArrayHospital.getJSONObject(hospi.get(marker))

                donorMarker = marker;
                Log.e("Details:", jsonArrayDonor.getJSONObject(don.get(donorMarker)).toString());
                Intent doner = new Intent(this, donorActiviry.class);
                doner.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                doner.putExtra("name", jsonArrayDonor.getJSONObject(don.get(donorMarker)).getString("name"));
                doner.putExtra("donor_user_id", jsonArrayDonor.getJSONObject(don.get(donorMarker)).getString("user_id"));
                doner.putExtra("phone", jsonArrayDonor.getJSONObject(don.get(donorMarker)).getDouble("phone_no"));
                doner.putExtra("Hospital", jsonArrayHospital.getJSONObject(hospi.get(hospitalMarker)).getString("name"));
                doner.putExtra("hospitalAddress", jsonArrayHospital.getJSONObject(hospi.get(hospitalMarker)).getString("address"));
                doner.putExtra("Name", getIntent().getStringExtra("Name"));
                doner.putExtra("client_user_id", userid);
                doner.putExtra("Hospital_id", jsonArrayHospital.getJSONObject(hospi.get(hospitalMarker)).getString("srno"));
                startActivity(doner);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    //Fetching Donors near to hosptital

    private class don extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            String role = "don";
            Log.e("don", "inside don");
            Log.e("hosp-message", hospitalLat.toString());
            Log.e("hosp-message", hospitalLat.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lat", hospitalLat.toString()));
            params.add(new BasicNameValuePair("lon", hospitalLon.toString()));
            params.add(new BasicNameValuePair("blood", bloodGroup));
            params.add(new BasicNameValuePair("role", role));
            params.add(new BasicNameValuePair("user", "" + userid));
            Log.e("hosp-Message", "before sending request");
            jsonArrayDonor = jparser.makeHttpRequestArray(url_donor, "GET", params);
            if (jsonArrayDonor == null) {
                Log.e("hosp-message", "null object returned");
                return "fail";
            } else {
                Log.e("hos-Message", "object returned");
                Log.e("hosp-message", jsonArrayDonor.toString());
                return "success";
            }
            // Start changing from here
//            try{
//                s = json.getString("info");
//                Log.d("Msg",json.getString("info"));
//                if(s.equals("success")){
//                    Intent login = new Intent(getApplicationContext(),home.class);
//                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(login);
//                    finish();
//                }
//                else if(s.equals("fail")){
//                    Log.e("Login status","Login fail");
//                    return s;
//                }
//            }
//            catch (JSONException e){
//                e.printStackTrace();
//            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("fail")) {
                try {
                    Toast.makeText(getApplicationContext(), "NO donor nearby " + jsonArrayHospital.getJSONObject(hospi.get(hospitalMarker)).getString("name"), Toast.LENGTH_SHORT).show();
                    donorStatus = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mMap.clear();
                TextView title = (TextView) findViewById(R.id.mapTitle);
                title.setText(R.string.donorTile);
                donorStatus = true;
                if (donorStatus) {
                    Log.e("Status", "Donor Status");
                    MarkLocationDonors();
                }
            }
        }


    }


}
