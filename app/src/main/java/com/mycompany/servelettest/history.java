package com.mycompany.servelettest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class history extends AppCompatActivity {
    private static String url_exp = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/history";
    Button btnLogout, btnShare;
    TextView hist, saveCount;
    JSONParser jparser = new JSONParser();
    TableLayout t1;
    JSONArray json;
    SessionManager manager = new SessionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String status = manager.getPreferences(history.this, "status");
            Log.d("status", status);

            if (status.equals("0")) {
                Intent i = new Intent(history.this, MainActivity.class);
                startActivity(i);
            }

        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        saveCount = (TextView) findViewById(R.id.etlifeSavedCount);
        t1 = (TableLayout) findViewById(R.id.main_table);
        t1.setColumnShrinkable(1, true);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setPreferences(history.this, "status", "0");
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
            }
        });

        new ShowHistory().execute();
    }

    public void init() {
        try {
            for (int i = 0; i < json.length(); i++) {
                TableRow tbrow1 = new TableRow(this);
                TextView t0v = new TextView(this);
                t0v.setText(i + 1 + "." + "Date :");
                //t0v.setTextColor("#000000");
                //t0v.setWidth(10);
                //t1v.setTextColor(Color.WHITE);
                //t0v.setGravity(Gravity.CENTER);
                tbrow1.addView(t0v);

                TextView t1v = new TextView(this);
                t1v.setText(json.getJSONObject(i).getString("date"));
                //t1v.setTextColor(Color.WHITE);
                //t1v.setGravity(Gravity.CENTER);
                tbrow1.addView(t1v);
                t1.addView(tbrow1);


                TableRow tbrow2 = new TableRow(this);
                TextView t2v = new TextView(this);
                t2v.setText("From :");
                //t1v.setTextColor(Color.WHITE);
                //t2v.setGravity(Gravity.CENTER);
                //t2v.setWidth(20);
                tbrow2.addView(t2v);

                TextView t3v = new TextView(this);
                t3v.setText(json.getJSONObject(i).getString("name"));
                //t1v.setTextColor(Color.WHITE);
                //t3v.setGravity(Gravity.CENTER);
                tbrow2.addView(t3v);
                t1.addView(tbrow2);

                TableRow tbrow3 = new TableRow(this);
                TextView t4v = new TextView(this);
                t4v.setText("At :");
                //t2v.setTextColor(Color.WHITE);
                //t4v.setGravity(Gravity.CENTER);
                //t4v.setWidth(8);
                tbrow3.addView(t4v);
                TextView t5v = new TextView(this);
                t5v.setText(json.getJSONObject(i).getString("hospitalName"));
                //t5v.setWidth(0);
                //t5v.setMinHeight(100);
                // t3v.setTextColor(Color.WHITE);
                //t5v.setGravity(Gravity.CENTER);
                tbrow3.addView(t5v);
                t1.addView(tbrow3);

                TableRow tbrow4 = new TableRow(this);
                TextView t6v = new TextView(this);
                t6v.setText(" ");
                //t2v.setTextColor(Color.WHITE);
                //t4v.setGravity(Gravity.CENTER);
                //t4v.setWidth(8);
                tbrow4.addView(t6v);
                TextView t7v = new TextView(this);
                t7v.setText(" ");
                //t5v.setWidth(0);
                //t5v.setMinHeight(100);
                // t3v.setTextColor(Color.WHITE);
                //t5v.setGravity(Gravity.CENTER);
                tbrow4.addView(t7v);
                t1.addView(tbrow4);

            }
        } catch (JSONException E) {
            E.printStackTrace();
        }
    }

    private class ShowHistory extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", Integer.toString(getIntent().getIntExtra("userid", 0))));
            params.add(new BasicNameValuePair("type", "history"));
            Log.e("Message", "before sending request");
            json = jparser.makeHttpRequestArray(url_exp, "GET", params);
            if (json == null) {
                Log.e("message", "null object returned");
                return "empty";
            }

            Log.e("Message", "object returned");
            Log.e("message", json.toString());
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("empty")) {
                Toast.makeText(getApplicationContext(), "No donation yet!", Toast.LENGTH_SHORT).show();
            } else {
                saveCount.setText("" + json.length());
                init();
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
