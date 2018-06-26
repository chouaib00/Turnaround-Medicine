package com.example.user.dipl1.utils;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.dipl1.R;
import com.example.user.dipl1.SearchFragment;
import com.example.user.dipl1.entity.PostTagsEntity;
import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.ArrayList;
import java.util.List;

public class TextTagsAdapter extends TagsAdapter {

    private List<PostTagsEntity> dataSet = new ArrayList<>();
    private FragmentActivity fragmentManager;

    public TextTagsAdapter(@NonNull List<PostTagsEntity> list, @NonNull FragmentActivity fragmentManager) {
        dataSet.clear();
        dataSet = list;

        this.fragmentManager = fragmentManager;
    }


    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {

        final PostTagsEntity tag = getItem(position);

        TextView tv = new TextView(context);
        try {
            tv.setText(tag.getTag_value());
            tv.setGravity(Gravity.CENTER);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("VALUE", tag.getTag_value());

                    CommandHelper.replaceFragment(SearchFragment.class, fragmentManager, R.id.container, bundle);
                }
            });

            tv.setTextColor(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tv;
    }

    @Override
    public PostTagsEntity getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return position % 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {
        view.setBackgroundColor(themeColor);
    }
}
