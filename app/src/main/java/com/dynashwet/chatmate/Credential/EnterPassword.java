package com.dynashwet.chatmate.Credential;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dynashwet.chatmate.R;

public class EnterPassword extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout passRLO;
    private TextInputLayout CpassLO;

    private EditText passwordR;
    private EditText Cpassword;

    private Button reg;

    private ProgressDialog pd;

    private String msg;
    private String UID;
    private String PhoneNo;
    public  static final int RequestPermissionCode  = 1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        fVBID();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        PhoneNo = pref.getString("PhoneNo", "");

    }

    private void fVBID() {
        passRLO=(TextInputLayout)findViewById(R.id.passRLO);
        CpassLO=(TextInputLayout)findViewById(R.id.CpassLO);

        passwordR=(EditText)findViewById(R.id.passwordR);
        Cpassword=(EditText)findViewById(R.id.Cpassword);
        reg=(Button)findViewById(R.id.reg);
        pd=new ProgressDialog(this);
        reg.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == reg){
            if(passwordR.getText().toString().length()==0){
                passRLO.setError("Enter Password");
                passRLO.requestFocus();
            }
            else if(Cpassword.getText().toString().length()==0){
                CpassLO.setError("Enter Confirm Password");
                CpassLO.requestFocus();
            }
            else if(!passwordR.getText().toString().equals(Cpassword.getText().toString())){
                CpassLO.setError("Password Not Match");
                CpassLO.requestFocus();
            }
            else{
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("PassWord",passwordR.getText().toString());
                editor.commit();

                startActivity(new Intent(getApplicationContext(), EnterProfile.class));
                finish();
            }

        }
    }



}
