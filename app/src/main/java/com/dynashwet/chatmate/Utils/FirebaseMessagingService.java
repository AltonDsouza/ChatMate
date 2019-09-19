package com.dynashwet.chatmate.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dynashwet.chatmate.Chat;
import com.dynashwet.chatmate.Dashboard.TabActivity;
import com.dynashwet.chatmate.R;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyToken", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Token",s);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived: Invoked");

        if(remoteMessage.getData().size()>0){
            Log.e(TAG, remoteMessage.getData().toString());
            String title = "", message = "", fromID = "", ToId = "" ,dp="",name="";
            //send notification

            Map<String, String> params = remoteMessage.getData();
            try {
                JSONObject object = new JSONObject(params.toString());
                Log.e("JSON_OBJECT", object.toString());
                JSONObject jsonArray = object.getJSONObject("data");
                message = jsonArray.getString("message");
                title = jsonArray.getString("title");

                JSONObject jsonArray1 = jsonArray.getJSONObject("payload");
                //                for(int i=0; i>jsonArray1){

                if(jsonArray1.has("From"))
                {
                    fromID = jsonArray1.getString("From");
                    ToId = jsonArray1.getString("ToID");
                    dp = jsonArray1.getString("ProfilePic");
                    name = jsonArray1.getString("Name");
                }

//                Log.e("arrjson1", jsonArray1.toString());


                //Since the image url starts with './', removing the dot part.

            showNotification(title, message, fromID, ToId,dp,name);
        }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private void showNotification(String title, String message, String fromID, String ToId , String dp, String name){
        Intent intent = new Intent();
        if(title.equals("Message")){
            intent = new Intent(this, Chat.class);
            intent.putExtra("to",fromID);
            intent.putExtra("from", ToId);
            intent.putExtra("message", message);
            intent.putExtra("name", name);
            intent.putExtra("dp", dp);
        }
        else
        {
             intent = new Intent(this, TabActivity.class);
        }
        Log.e(TAG, "onNotificationCreated: Notification event fired");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setAutoCancel(false)
                            .setSound(defaultSoundUri)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSmallIcon(R.mipmap.logo_round)
                            .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
