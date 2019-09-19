package com.dynashwet.chatmate.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dynashwet.chatmate.Adapter.NotificationAdapter;
import com.dynashwet.chatmate.Adapter.SearchAdapter;
import com.dynashwet.chatmate.Models.Notification;
import com.dynashwet.chatmate.R;
import com.dynashwet.chatmate.RequestHandler;
import com.dynashwet.chatmate.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    ListView searchList;
    EditText input;
    List<Notification> list = new ArrayList<>();
    private String UID;

    private static String [] names;
    private static String [] images;
    private static String [] UIDs;

    SearchAdapter adapter;
//    private static String [] names;

    public SearchFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        UID = pref.getString("UserID", "");

        getSearchList(UID);
        adapter = new SearchAdapter(getActivity(), list);

    }


    public void filter(String text){
        List<Notification> temp = new ArrayList();
        for(Notification d: list){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getTitle().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchList = view.findViewById(R.id.searchList);
        input = view.findViewById(R.id.inputSearch);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //  filter(searchModels, String.valueOf(s));
                if(s.equals("")){
//                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    filter(s.toString());
//                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    private void getSearchList(String UID){
        StringRequest request = new StringRequest(Request.Method.POST, AppConstant.Search,
                success-> {
                    try {
                        JSONObject object = new JSONObject(success);
                        String msg = object.getString("Msg");
                        if(msg.equals("UserList")){
                            JSONArray friends = object.getJSONArray("result");

                            names = new String[friends.length()];
                            images = new String[friends.length()];
                            UIDs = new String[friends.length()];
                            for(int i=0; i<friends.length(); i++){
                                Notification notification = new Notification();
                                JSONObject object1 = friends.getJSONObject(i);
                                names[i] = object1.getString("Name");
                                images[i] = AppConstant.BASE_URL+object1.getString("ProfilePic");
                                UIDs[i] = object1.getString("UID");
                                notification.setTitle(names[i]);
                                notification.setImage(images[i]);
                                notification.setFromID(UIDs[i]);
                                list.add(notification);
                            }

//                            adapter = new NotificationAdapter(getActivity(), list, TAG);
                            searchList.setAdapter(adapter);
                        }
                        else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Dyna-UserID",UID);
//                params.put("Dyna-UserName",pid);
                return params;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
    }
}
