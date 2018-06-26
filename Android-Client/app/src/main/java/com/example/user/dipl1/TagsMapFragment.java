package com.example.user.dipl1;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.dipl1.entity.PostTagsEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.utils.TextTagsAdapter;
import com.moxun.tagcloudlib.view.TagCloudView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagsMapFragment extends Fragment {

    private TagCloudView tagCloudView;
    private QueryRepository queryRepository;
    private TextTagsAdapter tagsAdapter;

    public TagsMapFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            queryRepository = new QueryRepository();
            queryRepository.InitializeRetrofit(getContext().getApplicationContext());

            queryRepository.getApi().getDistinctTags().enqueue(new Callback<List<PostTagsEntity>>() {
                @Override
                public void onResponse(Call<List<PostTagsEntity>> call, Response<List<PostTagsEntity>> response) {
                    tagsAdapter = new TextTagsAdapter(response.body(), getActivity());
                    if (tagCloudView != null)
                        tagCloudView.setAdapter(tagsAdapter);
                }

                @Override
                public void onFailure(Call<List<PostTagsEntity>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tags_map, container, false);

        try {
            tagCloudView = (TagCloudView) v.findViewById(R.id.tagCloudView);

            if (tagsAdapter != null)
                tagCloudView.setAdapter(tagsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

}
