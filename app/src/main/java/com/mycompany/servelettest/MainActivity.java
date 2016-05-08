package com.mycompany.servelettest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    public static int userId;
    private static String url_login = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/LoginServlet";
    EditText email, password;
    Button submit;
    TextView signupLink;
    JSONParser jparser = new JSONParser();
    JSONObject json;
    SessionManager manager = new SessionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String status = manager.getPreferences(MainActivity.this, "status");
            String role = manager.getPreferences(MainActivity.this, "role");
            String id = manager.getPreferences(MainActivity.this, "userid");
            String name = manager.getPreferences(MainActivity.this, "name");
            Log.d("status", status);
            Log.e("role", role);
            Log.e("id", id);
            Log.e("name", name);
            if (status.equals("1")) {
                Intent i = new Intent(MainActivity.this, home.class);
                if (role.equals("Donor")) {
                    i.putExtra("role", "Donor");

                } else {
                    i.putExtra("role", "Client");

                }
                i.putExtra("name", name);
                i.putExtra("userid", Integer.parseInt(id));
                startActivity(i);
            }

        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    new Login().execute();
                }
            }
        });
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent reg = new Intent(getApplicationContext(), RegisterActivity.class);
                //reg.putExtra("user_id",userId);
                //reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reg);
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

    public boolean validate() {
        boolean valid = true;

        if (email.getText().length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        }
        else {
            email.setError(null);
        }

        if (password.getText().length() == 0 || password.getText().length() < 5 || password.getText().length() > 15) {
            password.setError("between 5 and 15 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void findViewsById() {
        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        submit = (Button) findViewById(R.id.btLogin);
        signupLink = (TextView) findViewById(R.id.tvRegister);
    }


    private class Login extends AsyncTask<String, String, String> {
        String em = email.getText().toString();
        String pass = password.getText().toString();

        @Override
        protected String doInBackground(String... args) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u", em));
            params.add(new BasicNameValuePair("p", pass));
            Log.e("Message", "before sending request");
            json = jparser.makeHttpRequest(url_login, "GET", params);
            if (json == null) {
                Log.e("message", "null object returned");
                return "network";
            }
            Log.e("Message", "object returned");
            String s = null;
            Log.e("message", json.toString());
            try {
                s = json.getString("info");
                if (s.equals("fail")) {
                    return "fail";
                }
                userId = json.getInt("user_id");
                String role = json.getString("role");
                String name = json.getString("name");
                Log.e("message", s);
                Log.e("name =", name);
                Log.e("message", "" + userId);
                Log.e("message", role);

                manager.setPreferences(MainActivity.this, "status", "1");
                manager.setPreferences(MainActivity.this, "role", role);
                manager.setPreferences(MainActivity.this, "userid", "" + userId);
                manager.setPreferences(MainActivity.this, "name", name);
                String status = manager.getPreferences(MainActivity.this, "status");
                Log.d("status", status);

                Intent login = new Intent(getApplicationContext(), home.class);
                login.putExtra("role", role);
                login.putExtra("userid", userId);
                login.putExtra("name", name);
                startActivity(login);
                finish();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.equals("fail")) {
                Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                password.getText().clear();
            } else if (result != null && result.equals("network")) {
                Toast.makeText(getApplicationContext(), "Unable to connect to network", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

            }
        }


    }
}
