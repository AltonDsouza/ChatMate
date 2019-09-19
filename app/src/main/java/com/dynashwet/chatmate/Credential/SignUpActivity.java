package com.dynashwet.chatmate.Credential;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.Models.Comments;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText phone;
    private TextInputLayout phoneLo;
    private Button getOtp;
    private LinearLayout linearLayout;
    private String phonePattern="((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        fVBID();

    }

    private void fVBID() {
        phone = (EditText)findViewById(R.id.phoneR);
        phoneLo = (TextInputLayout) findViewById(R.id.phoneRLO);
        getOtp = (Button) findViewById(R.id.getOTP);
        linearLayout = findViewById(R.id.linearroot);
        getOtp.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        if(phone.getText().toString().length()==0){
            phoneLo.setError("Cannot be empty!");
            phoneLo.requestFocus();
        }
        else if(!phone.getText().toString().matches(phonePattern)){
            phoneLo.setError("Enter a Valid Phone Number.");
            phoneLo.requestFocus();
        }


        else{
//            sendRegOTP();
            isUserExists(phone.getText().toString());
//            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("PhoneNo",phone.getText().toString());
//            editor.commit();
//
//            startActivity(new Intent(getApplicationContext(), EnterPassword.class));
        }
    }

    private void isUserExists(String contact){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.IsUserExist,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if(msg.equals("Success")){//Already registered

                                Snackbar snackbar = Snackbar
                                        .make(linearLayout, "User with this contact already exists!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                            else if(msg.equals("Error")){//Not registered
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("PhoneNo",phone.getText().toString());
                                editor.commit();

                                startActivity(new Intent(getApplicationContext(), EnterPassword.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Contact", contact);
//                params.put("UID", UID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
//
//    private void sendRegOTP() {
//
//        pd.setMessage("Sending OTP......!");
//        pd.show();
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.GetOtp,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            // Toast.makeText(SignUp.this, response, Toast.LENGTH_SHORT).show();
//                            msg = object.getString("msg");
//                            Log.e("inside",msg);
//
//                            if(msg.equals("SUCCESS")) {
//                                Toast.makeText(getApplicationContext(), "OTP Send To Mobile Number", Toast.LENGTH_SHORT).show();
//
//                                OTPs=object.getString("otp");
//                                Log.e("OTP",OTPs);
//
//                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//                                SharedPreferences.Editor editor = pref.edit();
//                                editor.putString("OTPs", OTPs);
//                                editor.putString("PhoneNo",phone.getText().toString());
//                                editor.commit();
//
////                                Intent i =new Intent(SignUpActivity.this,EnterOTP.class);
////                                startActivity(i);
//                            }
//
//                            else if(msg.equals("Exists")) {
//                                Toast.makeText(getApplicationContext(), "Phone No Already Exists", Toast.LENGTH_SHORT).show();
//                            }
//
//                            else{
//                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//                            }
//                            pd.dismiss();
//
//
//                        }
//                        catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        pd.dismiss();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Contact", phone.getText().toString());
////                params.put("Type","0");
//                return params;
//            }
//        };
//        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(postRequest);
//
//    }
}
