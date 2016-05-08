package com.mycompany.servelettest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by som on 25/4/16.
 */
public class RegisterHome extends AppCompatActivity {
    private static String url_reg = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/firstTime";
    Button btnLogout;
    JSONParser jparser = new JSONParser();
    JSONObject json;
    SessionManager manager = new SessionManager();
    Location l;
    LocationManager locationManager;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String status = manager.getPreferences(RegisterHome.this, "status");
            Log.d("status", status);
            if (status.equals("0")) {
                Intent i = new Intent(RegisterHome.this, MainActivity.class);
                startActivity(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        name = getIntent().getStringExtra("name");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView t1 = (TextView) findViewById(R.id.nameField);
        t1.setText(name);
        showDialog();
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setPreferences(RegisterHome.this, "status", "0");
                Intent login = new Intent(getApplicationContext(), ScreenSlideActivity.class);
                //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }

//    public  void callLocationService(){
//
//
//        Intent i = new Intent(this,LocationService.class);
//        int user_id = getIntent().getIntExtra("user_id",0);
//        i.putExtra("id",user_id);
//        startService(i);
//    }


    public void showDialog() {

        Log.e("SHISHIR", "In dialog");

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Want to be a donor ?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
                            Log.e("Shishir", "Provider enable");
                        } else {
                            Log.e("Shishir", "provider disabled");
                            showSettingsAlert("NETWORK_PROVIDER");
                        }

                        // dialog.cancel();
                        //asynk task here
                        new Client2Donor().execute();
                        //  callLocationService();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        // Toast display "You will be only a client"
                        Intent homeReg = new Intent(getApplicationContext(), home.class);
                        homeReg.putExtra("userid", getIntent().getIntExtra("user_id", 0));
                        homeReg.putExtra("name", getIntent().getStringExtra("name"));
                        homeReg.putExtra("role", "Client");
                        startActivity(homeReg);
                        finish();
                    }
                });

        AlertDialog alert = builder1.create();
        alert.show();
    }


    //alert box for enabling location
    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                RegisterHome.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        RegisterHome.this.startActivity(intent);

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

    private class Client2Donor extends AsyncTask<String, String, String> {

        int user_id = getIntent().getIntExtra("user_id", 0);
        String name = getIntent().getStringExtra("name");

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", Integer.toString(user_id)));
            params.add(new BasicNameValuePair("lat", "" + 0));
            params.add(new BasicNameValuePair("lon", "" + 0));
            Log.e("Message", "before sending request");
            json = jparser.makeHttpRequest(url_reg, "GET", params);
            if (json == null) {
                Log.e("message", "null object returned");
            }
            Log.e("Message", "object returned");
            String s = null;
            Log.e("message", json.toString());
            try {
                s = json.getString("info");
                Log.d("Msg", json.getString("info"));
                //int userId = json.getInt("user_id");
                if (s.equals("success")) {
                    manager.setPreferences(RegisterHome.this, "status", "1");
                    String status = manager.getPreferences(RegisterHome.this, "status");
                    Log.d("status", status);
                    Intent homeReg = new Intent(getApplicationContext(), home.class);
                    homeReg.putExtra("userid", user_id);
                    homeReg.putExtra("role", "Donor");
                    homeReg.putExtra("name", name);
                    startActivity(homeReg);
                    finish();
                } else if (s.equals("fail")) {
                    Log.e("Register status", "fail");
                    return s;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}

