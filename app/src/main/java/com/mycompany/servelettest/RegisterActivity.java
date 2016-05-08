package com.mycompany.servelettest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {
    //private static String url_login = "http://172.16.81.104:8080/AndroidLogin/LoginServlet";
    private static String url_reg = "http://bloodhelp-shishiruniyal.rhcloud.com/BloodHelp/Register";
    EditText etName, etEmail, etMobile, etAge, etPassword, etConfirmPass;
    Button bRegister;
    Spinner spinner;
    JSONParser jparser = new JSONParser();
    JSONObject json;
    SessionManager manager = new SessionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMobile = (EditText) findViewById(R.id.etMobileNO);
        etAge = (EditText) findViewById(R.id.etAge);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPass = (EditText) findViewById(R.id.etConPassword);
        bRegister = (Button) findViewById(R.id.bRegister);
        spinner = (Spinner) findViewById(R.id.spBloodGroup);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood_group_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
                    return;
                }
                new Register().execute();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        if (etName.getText().length() == 0 || etName.getText().length() < 3) {
            etName.setError("At least 3 characters");
            valid = false;
        }else if(etName.getText().toString().matches("[a-zA-Z]+") == false){
            etName.setError("Name should not contain numeric digits or special symbols");
        }
        else {
            etName.setError(null);
        }

        if (etEmail.getText().length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()) {
            etEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if(etMobile.getText().length() == 0 || !android.util.Patterns.PHONE.matcher(etMobile.getText()).matches()) {
            etMobile.setError("Enter a valid phone number");
            valid = false;
        } else {
            etMobile.setError(null);
        }

        if(etAge.getText().length() == 0){
            etAge.setError("Enter your present age");
            valid = false;
        }else if(Integer.parseInt(etAge.getText().toString()) < 17){
            etAge.setError("Age should be greater than 16");
        }
        else {
            etAge.setError(null);
        }

        if(spinner.getSelectedItem().toString().equals("Select blood group") == true){
            Toast.makeText(getApplicationContext(), "Select blood group", Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
        }

        if (etPassword.getText().length() == 0 || etPassword.getText().length() < 5 || etPassword.getText().length() > 15) {
            etPassword.setError("between 5 and 15 alphanumeric characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if(etConfirmPass.getText().toString().equals(etPassword.getText().toString()) == false){
            etConfirmPass.setError("Password and Confirm Password do not match");
            valid = false;
        }else{
            etConfirmPass.setError(null);
        }

        return valid;
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

    private class Register extends AsyncTask<String, String, String> {

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String mobile = etMobile.getText().toString();
        String age = etAge.getText().toString();
        String bloodGroup = spinner.getSelectedItem().toString();
        String password = etPassword.getText().toString();
        // String confPass = etConfirmPass.getText().toString();

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("n", name));
            params.add(new BasicNameValuePair("e", email));
            params.add(new BasicNameValuePair("m", mobile));
            params.add(new BasicNameValuePair("a", age));
            params.add(new BasicNameValuePair("bl", bloodGroup));
            params.add(new BasicNameValuePair("p", password));
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
                Log.e("Before Message",s);
                Log.e("Msg", json.getString("info"));
                int userId = json.getInt("user_id");
                String name = json.getString("name");
                if (s.equals("success")) {
                    manager.setPreferences(RegisterActivity.this, "status", "1");
                    String status = manager.getPreferences(RegisterActivity.this, "status");
                    Log.e("status", status);
                    Intent homeReg = new Intent(getApplicationContext(), RegisterHome.class);
                    homeReg.putExtra("user_id", userId);
                    homeReg.putExtra("name", name);
                    startActivity(homeReg);
                    finish();
                }
                else if (s.equals("emailalready")){
                    Log.e("Message","In here emailAlready");
                    return s;
                }
                else if (s.equals("mobilealready")){
                    Log.e("Message","In here MobileAlready");
                    return s;

                }
                else if (s.equals("fail")) {
                    Log.e("Register status", "fail");
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
                Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
            }else if(result != null && result.equals("emailalready")){
                Toast.makeText(getApplicationContext(), "email id already exists", Toast.LENGTH_SHORT).show();
            }
            else if (result != null && result.equals("mobilealready")){
                Toast.makeText(getApplicationContext(), "mobile number already exists", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

            }
        }

    }
}
