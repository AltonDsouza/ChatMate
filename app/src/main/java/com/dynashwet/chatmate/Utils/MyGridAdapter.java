package com.dynashwet.chatmate.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.dynashwet.chatmate.Models.PostImages;
import com.dynashwet.chatmate.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyGridAdapter extends BaseAdapter {
    private Context context;
    private List<PostImages> images;

    public MyGridAdapter(Context context, List<PostImages> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
//        View view1 = View.inflate(context, R.layout.grid_image_item, null);
//        ImageView imageView = view1.findViewById(R.id.image);
//
//        Picasso.with(context).load(images.get(position).getImage()).into(imageView);
//        return view1;

        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new
                    GridView.LayoutParams(330, 330));
            imageView.setScaleType(
                    ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) view;
        }
        Picasso.with(context).load(images.get(position).getImage()).into(imageView);
        return imageView;

    }
}
