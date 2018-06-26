package com.example.user.dipl1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.user.dipl1.custom_ui.GridSpacingItemDecoration;
import com.example.user.dipl1.entity.PostFavoritesEntity;
import com.example.user.dipl1.favorites.RecyclerFavoritesAdapter;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;

import java.util.List;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserFavoritesFragment extends Fragment {

    RecyclerFavoritesAdapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    QueryRepository queryRepository;
    Handler handler;


    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            queryRepository = new QueryRepository();
            queryRepository.InitializeRetrofit(getContext().getApplicationContext());

            adapter = new RecyclerFavoritesAdapter(this.getContext());
            gridLayoutManager = new GridLayoutManager(this.getContext(), 2, GridLayoutManager.VERTICAL, false);
            fillData();

            handler = new Handler(){
              public void handleMessage(Message message){
                  if (message.arg1 == 1){
                      rv.setAdapter(adapter);
                  }
              }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_favorites, container, false);

        try {
            rv = (RecyclerView) view.findViewById(R.id.user_favorites_list);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBarFavorites);
            progressBar.setVisibility(View.INVISIBLE);

            rv.setHasFixedSize(true);
            rv.addItemDecoration(new GridSpacingItemDecoration(4));
            rv.setLayoutManager(gridLayoutManager);

            LandingAnimator animator = new LandingAnimator();
            animator.setAddDuration(1000);
            animator.setMoveDuration(1000);
            animator.setRemoveDuration(1000);

            rv.setItemAnimator(animator);

            rv.addOnItemTouchListener(new RecyclerClickListener(this.getContext()) {
                @Override
                public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

                    int postId = adapter.getItem(position).getPost_id().getPost_id();
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", postId);

                    CommandHelper.replaceFragment(
                            UserFullPostFragment.class,
                            getActivity(),
                            R.id.container,
                            bundle
                    );
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void fillData(){
        try {
            queryRepository.getApi().getAllFavoritesByUser(Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()).enqueue(new Callback<List<PostFavoritesEntity>>() {
                @Override
                public void onResponse(Call<List<PostFavoritesEntity>> call, Response<List<PostFavoritesEntity>> response) {
                    try{
                        adapter.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        Message  message = new Message();
                        message.arg1 = 1;
                        handler.sendMessage(message);

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<PostFavoritesEntity>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
