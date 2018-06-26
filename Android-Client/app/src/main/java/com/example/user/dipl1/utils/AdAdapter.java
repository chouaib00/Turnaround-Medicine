package com.example.user.dipl1.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.dipl1.R;

import java.util.ArrayList;
import java.util.List;

public class AdAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private List<Ad> objects;

    public AdAdapter(Context context, List<Ad> ads) {
        ctx = context;
        objects = ads;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    @Override
    public Ad getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return objects.get(position).Id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        try {
            if (view == null) {
                view = lInflater.inflate(R.layout.ad_item, parent, false);
            }

            Ad p = getItem(position);

            ((TextView) view.findViewById(R.id.adTitleView)).setText(p.Title);
            ((ImageView) view.findViewById(R.id.adImageView)).setImageBitmap(
                    BitmapFactory.decodeByteArray(p.Image, 0, p.Image.length)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
