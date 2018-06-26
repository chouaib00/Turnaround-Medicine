package com.example.user.dipl1;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.pagination.PaginationScrollListener;
import com.example.user.dipl1.utils.Mode;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Stack;

import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {

    UserPostAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private Integer currentPage = 0;
    QueryRepository queryRepository;

    private int counter;
    private String tagText = "";
    private String descText = "";

    private Stack<Integer> stackOfIdentificators;

    @Override
    public void onPause() {
        super.onPause();
        try {
            isLoading = false;
            isLastPage = false;
            currentPage = 0;
            counter = 0;
            tagText = "";
            descText = "";
            stackOfIdentificators = null;
            adapter = null;
            Log.d("SEARCH: ", "PAUSE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getIdsByTag(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (currentPage) {
                        try {
                            Response<List<Integer>> response = queryRepository.getApi().getPostIdsByTag(tagText).execute();

                            if (response.body() != null) {
                                for (Integer item :
                                        response.body()) {
                                    stackOfIdentificators.push(item);
                                }
                            }

                            currentPage = stackOfIdentificators.pop();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getIdsByDesc(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (currentPage) {
                        try {
                            Response<List<Integer>> response = queryRepository.getApi().getPostIdsByDescription(descText, Mode.PUBLIC).execute();

                            if (response.body() != null) {
                                for (Integer item :
                                        response.body()) {
                                    stackOfIdentificators.push(item);
                                }
                            }

                            currentPage = stackOfIdentificators.pop();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        try {
            currentPage = 0;

            queryRepository = new QueryRepository();
            queryRepository.InitializeRetrofit(getContext().getApplicationContext());

            stackOfIdentificators = new Stack<>();

            rv = (RecyclerView) view.findViewById(R.id.searchResultsView);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBarSearchView);

            counter = 0;

            adapter = new UserPostAdapter(this.getContext());

            linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
            rv.setLayoutManager(linearLayoutManager);

            ScaleInBottomAnimator animator = new ScaleInBottomAnimator();
            animator.setAddDuration(1000);
            animator.setMoveDuration(1000);
            animator.setRemoveDuration(1000);

            rv.setItemAnimator(animator);

            rv.setAdapter(adapter);

            if (getArguments().getString("VALUE") != null) {
                tagText = getArguments().getString("VALUE");
                getIdsByTag();
                Log.d("onCreate", "tag");
            }

            if (getArguments().getString("DESC_VALUE") != null) {
                descText = getArguments().getString("DESC_VALUE");
                getIdsByDesc();
                Log.d("onCreate", "desc");
            }

            rv.addOnItemTouchListener(new RecyclerClickListener(this.getContext()) {
                @Override
                public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = UserFullPostFragment.class;
                    //fragmentClass = DoctorFullPostFragment.class;

                    int post_id = adapter.getItem(position).getPost_id();
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", post_id);

                    try{
                        fragment = (Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = null;
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        transaction = fragmentManager.beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });

            rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    currentPage = stackOfIdentificators.pop();

                    // mocking network delay for API call
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextPage();
                        }
                    }, 10);
                }

                @Override
                public int getTotalPageCount() {
                    return stackOfIdentificators.size();
                }

                @Override
                public boolean isLastPage() {
                    return isLastPage;
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });

            loadFirstPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void loadFirstPage() {
        try {
            Log.d("SEARCH_FRAGMENT: ", "loadFirstPage");

            queryRepository.getApi().getCommentsCount(currentPage).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    counter = response.body().intValue();
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });

            callTopRatedMoviesApi().enqueue(new Callback<FullPostEntity>() {
                @Override
                public void onResponse(Call<FullPostEntity> call, Response<FullPostEntity> response) {
                    // Got data. Send it to adapter

                    FullPostEntity results = fetchResults(response);
                    progressBar.setVisibility(View.GONE);
                    adapter.add(results, counter);

                    if (!stackOfIdentificators.empty()) adapter.addLoadingFooter();
                    else isLastPage = true;
                }

                @Override
                public void onFailure(Call<FullPostEntity> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FullPostEntity fetchResults(Response<FullPostEntity> response) {
        FullPostEntity topRatedMovies = response.body();
        return topRatedMovies;
    }

    private void loadNextPage() {
        try {
            Log.d("SEARCH_FRAGMENT ", "loadNextPage: " + currentPage);

            queryRepository.getApi().getCommentsCount(currentPage).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    counter = response.body().intValue();
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });

            callTopRatedMoviesApi().enqueue(new Callback<FullPostEntity>() {
                @Override
                public void onResponse(Call<FullPostEntity> call, Response<FullPostEntity> response) {
                    adapter.removeLoadingFooter();
                    isLoading = false;

                    FullPostEntity results = fetchResults(response);
                    adapter.add(results, counter);

                    if (!stackOfIdentificators.empty()) {
                        adapter.addLoadingFooter();
                    } else {
                        isLastPage = true;
                    }

                }

                @Override
                public void onFailure(Call<FullPostEntity> call, Throwable t) {
                    t.printStackTrace();
                    loadNextPage();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Call<FullPostEntity> callTopRatedMoviesApi() {
        synchronized (currentPage) {
            return queryRepository.getApi().getPostById(currentPage);
        }
    }
}
