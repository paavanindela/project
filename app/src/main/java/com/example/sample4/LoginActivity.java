package com.example.sample4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends AppCompatActivity {
    EditText etusername, etpassword,etemail;
    Button btn_login,btn_register;
    Intent intent;
    boolean[] b = {false,false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etpassword = findViewById(R.id.et_password);
        etusername = findViewById(R.id.et_user_name);
        etemail = findViewById(R.id.et_email);
        btn_login = findViewById(R.id.btn_submit);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendPost1();
                int ctr = 0;
                while(!b[1]){ctr++; if(ctr>2000000000) break;}
                if(ctr>2000000000)
                {
                    Toast.makeText(getBaseContext(), "Request Time Out", Toast.LENGTH_LONG).show();
                }
                else if(b[0])
                    {
                        Toast.makeText(getBaseContext(), "User Registration Success", Toast.LENGTH_LONG).show();
                        intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        Shared shared = new Shared(getApplicationContext());
                        shared.secondtime(etusername.getText().toString());
                    }
                    else
                        Toast.makeText(getBaseContext(), "Username Already taken", Toast.LENGTH_LONG).show();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendPost2();
                int ctr = 0;
                while(!b[1]){ctr++; if(ctr>2000000000) break;}
                if(ctr>2000000000)
                {
                    Toast.makeText(getBaseContext(), "Request Time Out", Toast.LENGTH_LONG).show();
                }
                else if(b[0])
                {
                    Toast.makeText(getBaseContext(), "User Authentication Success", Toast.LENGTH_LONG).show();
                    intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Shared shared = new Shared(getApplicationContext());
                    shared.editor.putString(shared.Filename,etusername.getText().toString());
                    shared.secondtime(etusername.getText().toString());
                }
                else
                Toast.makeText(getBaseContext(), "Wrong username/password", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void sendPost1()  {
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", etusername.getText().toString());
            obj.put("password", etpassword.getText().toString());
            obj.put("email", etemail.getText().toString());
        } catch (JSONException e) {
            Log.e("JSONERROR", String.valueOf(e));
            e.printStackTrace();
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://paavankumar.pythonanywhere.com/register_student");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    Log.i("JSON", obj.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(obj.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    Log.d("JSON", sb.toString());
                    if(String.valueOf(conn.getResponseCode()).equals("201"))
                        b[0] = true;
                    conn.disconnect();
                    b[1] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.d("FLAG", String.valueOf(b[0]));
    }
    public void sendPost2()  {
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", etusername.getText().toString());
            obj.put("password", etpassword.getText().toString());
        } catch (JSONException e) {
            Log.e("JSONERROR", String.valueOf(e));
            e.printStackTrace();
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://paavankumar.pythonanywhere.com/login_student");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    Log.i("JSON", obj.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(obj.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());
                    /*BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    Log.d("JSON", sb.toString());*/
                    if(String.valueOf(conn.getResponseCode()).equals("200"))
                        b[0] = true;
                    conn.disconnect();
                    b[1] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}