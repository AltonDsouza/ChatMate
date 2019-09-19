package com.dynashwet.chatmate.Credential;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    private TextInputLayout FnewPwdLO;
    private EditText FnewPwd;
    private TextInputLayout FconfPwdLO;
    private EditText FconfPwd;
    private TextInputLayout FotpLO;
    private EditText Fotp;
    private TextView FSavePass;

    private ProgressDialog pd;

    private String forPwdOtp;
    private String forgetNo;

    private String otpPattern="^[1-9][0-9]{5}$";

    private String responsemsg;

    private TextView FemailID;
    private String contact;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        fVBID();

        forgetNo = getIntent().getStringExtra("contact");
        FemailID.setText(forgetNo);

        FSavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(FnewPwd.getText().toString().length()==0){
                    FnewPwdLO.setError("New Password Cannot be Empty");
                    FnewPwdLO.requestFocus();
                }
                else if(FconfPwd.getText().toString().length()==0){
                    FconfPwdLO.setError("Confirm Password Cannot be Empty");
                    FconfPwdLO.requestFocus();
                }
                else if(!FconfPwd.getText().toString().matches(FnewPwd.getText().toString())){
                    FconfPwdLO.setError("Password Not Match");
                    FconfPwdLO.requestFocus();
                }

//                else if(!Fotp.getText().toString().matches(otpPattern)){
//                    FotpLO.setError("Enter A Valid OTP");
//                    FotpLO.requestFocus();
//                }
//
//                else if(!Fotp.getText().toString().equals(forPwdOtp)){
//                    FotpLO.setError("Invalid OTP");
//                    FotpLO.requestFocus();
//                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
//                }

                else{

                    forgetPwdReq();
                }

            }
        });
    }

    private void forgetPwdReq() {
        pd.setMessage("Loading......!");
        pd.show();
        String response = null;
        StringRequest postRequest=new StringRequest(Request.Method.POST, AppConstant.ForgotPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.hide();
                        try {
                            JSONObject object=new JSONObject(response);
                            responsemsg=object.getString("Msg");
                            // Toast.makeText(ForgetPassword.this, response, Toast.LENGTH_SHORT).show();
                            if (responsemsg.equals("Success")) {
                                Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.hide();
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Dyna-Contact",forgetNo);
                params.put("Dyna-NewPassword",FnewPwd.getText().toString());
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);

        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void fVBID() {
        FnewPwdLO=(TextInputLayout)findViewById(R.id.FnewPwdLO);
        FnewPwd=(EditText)findViewById(R.id.FnewPwd);

        FconfPwdLO=(TextInputLayout)findViewById(R.id.FconfPwdLO);
        FconfPwd=(EditText)findViewById(R.id.FconfPwd);

        FotpLO=(TextInputLayout)findViewById(R.id.FotpLO);
        Fotp=(EditText)findViewById(R.id.Fotp);

        FSavePass=(TextView)findViewById(R.id.FSavePass);

        pd=new ProgressDialog(this);
        FemailID=(TextView)findViewById(R.id.FemailID);
    }
}
