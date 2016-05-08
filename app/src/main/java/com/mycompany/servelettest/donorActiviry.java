package com.mycompany.servelettest;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class donorActiviry extends AppCompatActivity {
    private static String url_reg = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/lifeCount";
    JSONObject json;
    JSONParser jparser = new JSONParser();
    String donorId, srno,cId;
    int clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent donor = getIntent();
        setContentView(R.layout.activity_donor_activiry);
        TextView name = (TextView) findViewById(R.id.name);
        TextView user = (TextView) findViewById(R.id.user);
        TextView phone = (TextView) findViewById(R.id.phone);


        donorId = getIntent().getStringExtra("donor_user_id");
        clientId = getIntent().getIntExtra("client_user_id",0);
        cId = String.valueOf(clientId);
        srno = getIntent().getStringExtra("Hospital_id");

        double contact_no = donor.getDoubleExtra("phone", 0.0);
        name.setText(donor.getStringExtra("name"));
        user.setText(donor.getStringExtra("donor_user_id"));
        phone.setText(String.format("%.0f", contact_no));

    }


    //Making call on button press
    public void makeCall(View v) {

        double contact_no = getIntent().getDoubleExtra("phone", 0.0);
        Log.e("here call", String.format("%.0f", contact_no));
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        new lifeCount().execute();
        callIntent.setData(Uri.parse("tel:" + String.format("%.0f", contact_no)));
        startActivity(callIntent);

    }

    public void makeText(View v) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            double contact_no = getIntent().getDoubleExtra("phone", 0.0);
            Log.e("here message", String.format("%.0f", contact_no));

            String message = "Help\nBlood Required \n To: Mr." + getIntent().getStringExtra("Name") + "\n At: " + getIntent().getStringExtra("Hospital") + "\n" + getIntent().getStringExtra("hospitalAddress");
            smsManager.sendTextMessage(String.format("%.0f", contact_no), null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            new lifeCount().execute();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private class lifeCount extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Cuid", cId));
            params.add(new BasicNameValuePair("Duid", donorId));
            params.add(new BasicNameValuePair("Hid", srno));
            Log.e("Message", "before sending request");
            json = jparser.makeHttpRequest(url_reg, "GET", params);
            if (json == null) {
                Log.e("message", "null object returned");
            }
            Log.e("Message", "object returned");
            String s = null;
           // Log.e("message", json.toString());
            try {
                s = json.getString("info");
                Log.d("Msg", json.getString("info"));
                //int userId = json.getInt("user_id");
                if (s.equals("success")) {
                    Log.e("donor-Activity", "donates updated");
                } else if (s.equals("fail")) {
                    Log.e("donor-Activity", "fail");
                    return s;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
