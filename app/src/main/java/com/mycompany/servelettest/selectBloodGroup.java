package com.mycompany.servelettest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class selectBloodGroup extends AppCompatActivity {
    public static final String HOSP_MESSAGE = "Hospital data";
    private static String url_login = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/location";
    Button btnLogout, submit;
    Spinner selectedBloodGrp;
    LocationManager locationManager;
    JSONParser jparser = new JSONParser();
    JSONArray jArray;
    Double latitude, longitude;
    String bloodGroup, name;
    int user;
    SessionManager manager = new SessionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String status = manager.getPreferences(selectBloodGroup.this, "status");
            Log.d("status", status);

            if (status.equals("0")) {
                Intent i = new Intent(selectBloodGroup.this, MainActivity.class);
                startActivity(i);
            }

        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosebloodgrp);


        //Asking user to enable his location from settings
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            Log.e("Shishir", "Provider enable");
        } else {
            Log.e("Shishir", "provider disabled");
            showSettingsAlert("NETWORK_PROVIDER");
        }


        user = getIntent().getIntExtra("userid", 0);
        name = getIntent().getStringExtra("name");


        submit = (Button) findViewById(R.id.btnSelectBloodGrp);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        selectedBloodGrp = (Spinner) findViewById(R.id.selectbloodGroup);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        selectedBloodGrp.setAdapter(adapter);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setPreferences(selectBloodGroup.this, "status", "0");
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodGroup = selectedBloodGrp.getSelectedItem().toString();
                Log.e("button", bloodGroup);
                switchMap();
                //call map funtion here
                // Intent showHospitals = new Intent(getApplicationContext(), home.class);
                //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(showHospitals);
            }
        });
    }

// don't touch it
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
//        startActivity(intent);
//        finish();
//        System.exit(0);
//    }


    public void switchMap() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("shishir", "I am here dude");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Location Disabled Enable it", Toast.LENGTH_SHORT).show();
        } else {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.e("lat,lon", latitude.toString() + "," + longitude.toString());
            new hosp().execute();
        }

    }

    //alert box for enabling location
    public void showSettingsAlert(String provider) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(
                selectBloodGroup.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        selectBloodGroup.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Enable location to use APP", Toast.LENGTH_SHORT).show();
                        dialog.cancel();

                    }
                });

        alertDialog.show();
    }


    // Fetching hospitals in background

    private class hosp extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            String role = "hosp";
            Log.e("Main Activity Hospital", latitude.toString());
            Log.e("Main Activity Hospital", longitude.toString());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lat", latitude.toString()));
            params.add(new BasicNameValuePair("lon", longitude.toString()));
            params.add(new BasicNameValuePair("role", role));
            Log.e("Message", "before sending request");
            jArray = jparser.makeHttpRequestArray(url_login, "GET", params);
            if (jArray == null) {
                Log.e("message", "null object returned");
                return "fail";
            } else {
                Log.e("Message", "object returned");
                Log.e("message", jArray.toString());
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
                Toast.makeText(getApplicationContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();

            } else {

                Intent hosp = new Intent(getApplicationContext(), hospitalMap.class);
                hosp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                hosp.putExtra(HOSP_MESSAGE, jArray.toString());
                hosp.putExtra("bloodGroup", bloodGroup);
                hosp.putExtra("USERID", "" + user);
                hosp.putExtra("Name", name);


                startActivity(hosp);

            }
        }


    }

}
