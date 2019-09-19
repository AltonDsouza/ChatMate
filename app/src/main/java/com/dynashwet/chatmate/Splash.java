package com.dynashwet.chatmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.dynashwet.chatmate.Credential.LoginActivity;
import com.dynashwet.chatmate.Dashboard.TabActivity;


/*
Author:    Alton Dsouza
 */
public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT =1000;

    private boolean FirstLogin;

    private String LogIN;
    private boolean isSecondRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        final SharedPreferences pref=getApplicationContext().getSharedPreferences("MyPref",0);
        FirstLogin=pref.getBoolean("once",false);
        LogIN=pref.getString("LogIN","");
        isSecondRun = pref.getBoolean("isFirstRun", true);

        if(isSecondRun){
            firstRun();
        }
        else {
            splash();
        }

    }





    private void firstRun(){
        boolean isFirstRun = getSharedPreferences("MyPref", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if(isFirstRun){
            //Place Alert Dialog here
            new AlertDialog.Builder(this)
                    .setTitle("Terms and conditions")
                    .setMessage(getString(R.string.terms))
                    .setNegativeButton("Decline",(dialogInterface, i) -> {
                        finish();
                        System.exit(0);
                    })
                    .setPositiveButton("Accept", (dialogInterface, i) -> {
                        getSharedPreferences("MyPref", MODE_PRIVATE)
                                .edit()
                                .putBoolean("isFirstRun", false)
                                .apply();

                        splash();
                    }).show();
        }
    }






    public void splash(){
        new Handler().postDelayed(() ->  {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

            if (cd.isConnectingToInternet()) {

                if(!LogIN.equals("1")){
                    Intent i=new Intent(Splash.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                      /*  SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("once",true);
                        editor.commit();*/
                }

                else{
                    Intent i=new Intent(Splash.this, TabActivity.class);
                    startActivity(i);
                    finish();
                }

            } else {
                cd.showAlertDialog(Splash.this, "No Internet Connection", "Please Connect To Internet to Procceed");
            }

        }, SPLASH_TIME_OUT);
    }
}
