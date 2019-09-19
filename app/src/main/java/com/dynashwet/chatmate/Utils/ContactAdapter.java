package com.dynashwet.chatmate.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynashwet.chatmate.Models.Contacts;
import com.dynashwet.chatmate.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends BaseAdapter {

    Context mContext;
    List<Contacts> contactsList;

    public ContactAdapter(Context mContext, List<Contacts> contactsList) {
        this.mContext = mContext;
        this.contactsList = contactsList;
    }

    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=View.inflate(mContext, R.layout.contact_item,null);
//< get controls >
        TextView textview_contact_Name= (TextView) view.findViewById(R.id.text_contact_name);
        CircleImageView imageView = (CircleImageView) view.findViewById(R.id.image_contact_profile);
//</ get controls >

//< show values >
        textview_contact_Name.setText(contactsList.get(position).getName());
        Picasso.with(mContext).load(contactsList.get(position).getImage()).into(imageView);
//</ show values >


        view.setTag(contactsList.get(position).getName());
        return view;
    }
}
