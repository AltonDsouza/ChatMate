package com.dynashwet.chatmate.Credential;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dynashwet.chatmate.R;

public class EnterOtp extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private Button button;
    private String otpPattern="^[1-9][0-9]{5}$";
    private String OTPs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        fVBID();

//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//        OTPs = pref.getString("OTPs", "");
    }

    private void fVBID() {
        editText = (EditText)findViewById(R.id.otpET);
        button = (Button)findViewById(R.id.submitOTP);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(!editText.getText().toString().matches(otpPattern)){
            editText.setError("Enter Valid OTP");
            editText.requestFocus();
        }

        else{
            // verifyOTP();

//            if(OTPs.equals(editText.getText().toString())){}
                Intent i =new Intent(getApplicationContext(),EnterPassword.class);
                startActivity(i);

//            else{
//                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
