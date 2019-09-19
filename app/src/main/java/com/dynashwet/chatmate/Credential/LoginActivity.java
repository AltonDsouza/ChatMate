package com.dynashwet.chatmate.Credential;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.Dashboard.TabActivity;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.dynashwet.chatmate.Credential.EnterPassword.RequestPermissionCode;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private EditText phone, password;
    private Button login, signUp;
    private TextInputLayout phoneLO;
    private TextInputLayout passLO;
    private ProgressDialog pd;
    private String msg, UID, token;
    private String phonePattern="((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fVBID();
        SharedPreferences preferences = this.getSharedPreferences("MyToken",0);
        token = preferences.getString("Token", "");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        enableRuntimePermission();

    }

    private void fVBID() {
        textView = (TextView)findViewById(R.id.forgetPwd);
        phone = (EditText)findViewById(R.id.phone);
        password = (EditText)findViewById(R.id.password);
        phoneLO = (TextInputLayout) findViewById(R.id.phoneLO);
        passLO = (TextInputLayout) findViewById(R.id.passLO);
        login = (Button)findViewById(R.id.login1);
        signUp = (Button)findViewById(R.id.signUp);
        pd = new ProgressDialog(this);

        login.setOnClickListener(this);
        signUp.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    if(v==login)
    {
        //Login API
        if(phone.getText().toString().length()==0){
            phoneLO.setError("Cannot be empty!");
            phoneLO.requestFocus();
        }
        else if(!phone.getText().toString().matches(phonePattern)){
            phoneLO.setError("Enter a Valid Phone no");
            phoneLO.requestFocus();
        }

        else if(password.getText().toString().length()==0){
            passLO.setError("Please Enter Password");
            passLO.requestFocus();
        }
        else{
            LoginRequest();
        }
    }
    else if(v==signUp){
        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }
    else if(v==textView){
        if(!phone.getText().toString().matches(phonePattern)){
            phoneLO.setError("Enter a Valid Phone no");
            phoneLO.requestFocus();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
            intent.putExtra("contact", phone.getText().toString());
            startActivity(intent);
        }
    }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void LoginRequest() {
        pd.setTitle("Login");
        pd.setMessage("Signing in....!");
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            // Toast.makeText(Login.this, response, Toast.LENGTH_SHORT).show();
                            msg = object.getString("Msg");

                            if (msg.equals("Match")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                                UID = object.getString("User");
                                // ContactNo = object.getString("Contact");

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("LogIN", "1");
                                editor.putString("UserID", UID);
                                editor.putString("PhoneNo", phone.getText().toString());

                                editor.commit();

                                Intent i = new Intent(getApplicationContext(), TabActivity.class);
                                startActivity(i);
                                finish();

                            }

                            else if (msg.equals("Block")) {

                                Toast.makeText(getApplicationContext(), "Sorry, You have been blocked by the admin!", Toast.LENGTH_SHORT).show();
                            }

                            else if (msg.equals("NoMatch")) {

                                Toast.makeText(getApplicationContext(), "Incorrect Password!", Toast.LENGTH_SHORT).show();
                            }


                            else {
                                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Contact", phone.getText().toString());
                params.put("Passwd", password.getText().toString());
                params.put("Token", token);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    public void enableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS))
        {
            Toast.makeText(this,"CONTACTS permission allowed!", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(),"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Cancelled, Now your application cannot access CONTACTS and display friends posts.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
