package com.mycompany.servelettest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
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

public class home extends AppCompatActivity {
    private static String url_reg = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/switchTo";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Button btnLogout, findDonor, shareExp, history;
    SessionManager manager = new SessionManager();
    TextView t1;
    int userid;
    String name;
    String role;
    JSONObject json;
    JSONParser jparser = new JSONParser();
    static Intent i=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            String status = manager.getPreferences(home.this, "status");
            Log.d("status", status);

            if (status.equals("0")) {
                Intent i = new Intent(home.this, MainActivity.class);
                startActivity(i);
            }

        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userid = getIntent().getIntExtra("userid", 0);
        name = getIntent().getStringExtra("name");
        role = getIntent().getStringExtra("role");

        Button switchRole = (Button) findViewById(R.id.role);
        if (getIntent().getStringExtra("role").equals("Donor")) {
            switchRole.setText("Become Client");
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
                Log.e("Shishir", "Provider enable");
                callLocationService();
            } else {
                Log.e("Shishir", "provider disabled");
                showSettingsAlert("NETWORK_PROVIDER");

            }

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                    callLocationService();
                }

                @Override
                public void onProviderDisabled(String s) {
                    showSettingsAlert("NETWORK_PROVIDER");
                }
            };
            // dialog.cancel();
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;

            }
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            switchRole.setText("Become Donor");
        }

        btnLogout = (Button) findViewById(R.id.btnLogout);
        findDonor = (Button) findViewById(R.id.btnFindDonor);
        shareExp = (Button) findViewById(R.id.btnShare);
        history = (Button) findViewById(R.id.btnHistory);
        t1 = (TextView) findViewById(R.id.nameField);
        t1.setText(name);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setPreferences(home.this, "status", "0");
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
            }
        });
        findDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectBloodGrp = new Intent(getApplicationContext(), selectBloodGroup.class);
                selectBloodGrp.putExtra("userid", userid);
                selectBloodGrp.putExtra("name", name);
                startActivity(selectBloodGrp);
            }
        });
        shareExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareEx = new Intent(getApplicationContext(), shareExp.class);
                shareEx.putExtra("userid", userid);
                shareEx.putExtra("role", getIntent().getStringExtra("role"));
                shareEx.putExtra("name", name);
                startActivity(shareEx);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hist = new Intent(getApplicationContext(), history.class);
                hist.putExtra("userid", getIntent().getIntExtra("userid", 0));
                startActivity(hist);
            }
        });

        switchRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (role.equals("Donor")) {

                    //Start asyn task to make client
                    if(i!=null){
                        stopService(i);
                        i=null;
                    }
                    // stopService(i);
                    new switching().execute();
                } else {
                    Intent r = new Intent(getApplicationContext(), RegisterHome.class);
                    r.putExtra("user_id", getIntent().getIntExtra("userid", 0));
                    r.putExtra("role", "Client");
                    r.putExtra("name", name);
                    startActivity(r);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(home.this, "Location Enabled", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(home.this, "Location access denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

    //alert box for enabling location
    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                home.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        home.this.startActivity(intent);
                        dialog.cancel();
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

    public void callLocationService() {
        Log.e("callLocation", "InsideCallLocation");
        Log.e("callLocation", "" + userid);
        if(i==null) {
            Log.e("Location Service","Starting location service");
            i = new Intent(this, LocationService.class);
            i.putExtra("id", "" + userid);
            startService(i);
        }
        else{
            Log.e("Location Service","already ON");
        }
    }


    private class switching extends AsyncTask<String, String, String> {

        int user_id = getIntent().getIntExtra("userid", 0);

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", Integer.toString(user_id)));
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
                    Intent h = new Intent(getApplicationContext(), home.class);
                    h.putExtra("userid", getIntent().getIntExtra("userid", 0));
                    h.putExtra("role", "Client");
                    h.putExtra("name", name);
                    startActivity(h);
                } else if (s.equals("fail")) {
                    Log.e("Switch", "fail");
                    return s;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
