package com.dynashwet.chatmate.Dashboard;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dynashwet.chatmate.Adapter.FriendListAdapter;
import com.dynashwet.chatmate.Mediator;
import com.dynashwet.chatmate.Models.ChatMessage;
import com.dynashwet.chatmate.Models.Contacts;
import com.dynashwet.chatmate.Models.Singlefriend;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Utils.AppConstant;
import com.dynashwet.chatmate.Utils.ChatContactAdapter;
import com.google.android.gms.common.api.internal.GoogleApiManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactList extends AppCompatActivity {

    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Contacts> contactsList = new ArrayList<>();
    private Cursor cursor;
    private String name, phonenumber,UID;
    private Mediator mediator;
    RecyclerView recyclerView;
    private List<Singlefriend> lstAnime;
    private ProgressDialog progressDialog;
    private ChatContactAdapter adapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.action_contact);
       listView = findViewById(R.id.contactList);
//       floatingActionButton = findViewById(R.id.browseContacts);
       floatingActionButton.setVisibility(View.GONE);
       mediator = new Mediator(this);
       progressDialog = new ProgressDialog(this);
        recyclerView =(RecyclerView) findViewById(R.id.recyclerViewid);
        lstAnime = new ArrayList<>() ;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");
        getFriends();

       populateListView();
    }

    private void populateListView(){
        //Logic to be discussed
        //Send contacts to API and populate UI accordingly
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

            contactsList.add(new Contacts(name, phonenumber));
            //storeContacts.add(phonenumber);
        }
        progressDialog.dismiss();
        cursor.close();

        adapter = new ChatContactAdapter(this, contactsList);
        listView.setAdapter(adapter);
//        Log.e(TAG, storeContacts.toString());
//        mediator.getContactData(storeContacts);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
        startActivity(intent);
    }

    private void getFriends() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.getFriendRequests,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        //  shimmerText.setVisibility(View.GONE);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("result");
                                if(obj.getJSONArray("result").equals("[]"))
                                {
                                    Toast.makeText(getApplicationContext(),"no friends",Toast.LENGTH_LONG);
                                }
                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    Singlefriend anime = new Singlefriend();
                                    anime.setName(d.getString("Name"));
                                    anime.setImg(d.getString("ProfilePic"));

                                    // lstAnime.add(anime);
                                    lstAnime.add(anime);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setuprecyclerview(lstAnime);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID", UID);
                return params;
            }

        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void setuprecyclerview(List<Singlefriend> Singlefriend) {


        FriendListAdapter myadapter = new FriendListAdapter(Singlefriend, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myadapter);


    }

}
