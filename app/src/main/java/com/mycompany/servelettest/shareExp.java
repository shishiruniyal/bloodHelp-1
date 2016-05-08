package com.mycompany.servelettest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class shareExp extends AppCompatActivity {
    private static String url_exp = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/history";
    Button btnLogout, btnShare;
    EditText exp;
    JSONParser jparser = new JSONParser();
    JSONObject json;
    SessionManager manager = new SessionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String status = manager.getPreferences(shareExp.this, "status");
            Log.d("status", status);

            if (status.equals("0")) {
                Intent i = new Intent(shareExp.this, MainActivity.class);
                startActivity(i);
            }

        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shareexp);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnShare = (Button) findViewById(R.id.btnShare);
        exp = (EditText) findViewById(R.id.etExp);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setPreferences(shareExp.this, "status", "0");
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewExperienceShared().execute();
            }
        });
    }

    private class NewExperienceShared extends AsyncTask<String, String, String> {

        String expr = exp.getText().toString();

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", Integer.toString(getIntent().getIntExtra("userid", 0))));
            params.add(new BasicNameValuePair("exp", expr));
            params.add(new BasicNameValuePair("type", "experience"));
            Log.e("Message", "before sending request");
            json = jparser.makeHttpRequest(url_exp, "GET", params);
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
                    Intent homeReg = new Intent(getApplicationContext(), home.class);
                    homeReg.putExtra("userid", getIntent().getIntExtra("userid", 0));
                    homeReg.putExtra("role", getIntent().getStringExtra("role"));
                    homeReg.putExtra("name",getIntent().getStringExtra("name"));
                    startActivity(homeReg);
                    finish();
                } else if (s.equals("fail")) {
                    Log.e("update status", "fail");
                    return s;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.equals("fail")) {
                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Experenced Shared Successfully", Toast.LENGTH_SHORT).show();

            }
        }

    }

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

}
