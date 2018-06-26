package com.example.user.dipl1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.pagination.PaginationScrollListener;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;
import com.example.user.dipl1.utils.PostStatus;
import com.example.user.dipl1.utils.UserStatus;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class UserNewsFragment extends Fragment {

    UserPostAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;

    private int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private Integer currentPage = 0;
    QueryRepository queryRepository;

    //private static final String ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION";
    SearchView searchView;

    private Integer counter;

    ImageButton voiceSearchBtn;
    private final int REQ_CODE_SPEECH_INPUT = 666;

    ImageView goToTagsView;

    @Override
    public void onPause() {
        super.onPause();

        try {
            PAGE_START = 0;
            isLoading = false;
            isLastPage = false;
            TOTAL_PAGES = 0;
            currentPage = 0;
            counter = null;
            rv = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_news, container, false);

        try {
            goToTagsView = (ImageView) view.findViewById(R.id.goToTagsView);
            goToTagsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommandHelper.replaceFragment(TagsMapFragment.class, getActivity(), R.id.container, null);
                }
            });

        /*fillData();
        adapter = new UserPostAdapter(this.getContext(), list);

        ListView lvMain = (ListView)view.findViewById(R.id.user_news_list);
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = UserFullPostFragment.class;

                try{
                    fragment = (Fragment)fragmentClass.newInstance();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                FragmentTransaction transaction = null;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.container, fragment);
                transaction.commit();
            }
        });*/

        /*RecyclerView rv = (RecyclerView)view.findViewById(R.id.user_news_recycler);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        rv.setLayoutManager(llm);

        fillData();
        adapter = new UserPostAdapter(list);
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new RecyclerClickListener(this.getContext()) {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                Fragment fragment = null;
                Class fragmentClass = null;
                //fragmentClass = UserFullPostFragment.class;
                fragmentClass = DoctorFullPostFragment.class;

                try{
                    fragment = (Fragment)fragmentClass.newInstance();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                FragmentTransaction transaction = null;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.container, fragment);
                transaction.commit();
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return view;
    }

    void fillData() {
        for (int i = 0; i < 20; i++) {
            list.add(new UserPost(R.drawable.ic_menu_camera, "Ivanov Ivan Ivanovic",
                    "01-01-2017/22:00", R.drawable.ic_menu_camera, 126));
        }
    }

    void fillNewsRecycler(){
        queryRepository.InitializeRetrofit();
    }*/
            rv = (RecyclerView) view.findViewById(R.id.user_news_recycler);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBarNews);

            voiceSearchBtn = (ImageButton) view.findViewById(R.id.voiceSearchBtn);
            voiceSearchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askSpeechInput();
                }
            });

            searchView = (SearchView) view.findViewById(R.id.user_search_news);

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //SpeechRecognitionHelper.run(getActivity());
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = SearchFragment.class;
                    //fragmentClass = DoctorFullPostFragment.class;

                    Bundle bundle = new Bundle();
                    bundle.putString("DESC_VALUE", searchView.getQuery().toString());

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = null;
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        transaction = fragmentManager.beginTransaction();
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            counter = 0;

            queryRepository = new QueryRepository();
            queryRepository.InitializeRetrofit(getContext().getApplicationContext());

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (currentPage) {
                            Response<Integer> max_id = queryRepository.getApi().getPostCount("max").execute();
                            PAGE_START = max_id.body();
                            currentPage = max_id.body();
                        }
                    } catch (Exception e) {
                        Log.d(">>>>>>>>>>>>>>>", "max id exception");
                    }
                }
            });
            t1.start();

            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<Integer> min_id = queryRepository.getApi().getPostCount("min").execute();
                        TOTAL_PAGES = min_id.body();
                    } catch (Exception e) {
                        Log.d(">>>>>>>>>>>>>>>", "min id exception");
                    }
                }
            });
            t2.start();

            adapter = new UserPostAdapter(this.getContext());

            linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
            rv.setLayoutManager(linearLayoutManager);

      /*  ScaleInBottomAnimator animator = new ScaleInBottomAnimator();
        animator.setAddDuration(1000);
        animator.setMoveDuration(1000);
        animator.setRemoveDuration(1000);

        rv.setItemAnimator(animator);*/

            rv.setAdapter(adapter);

            rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    currentPage -= 1;

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
                    return TOTAL_PAGES;
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

            loadFirstPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void loadFirstPage() {
        try {
            Log.d("USER_NEWS_FRAGMENT: ", "loadFirstPage");

            queryRepository.getApi().getCommentsCount(currentPage).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    synchronized (counter) {
                        counter = response.body().intValue();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });

            callTopRatedMoviesApi().enqueue(new Callback<FullPostEntity>() {
                @Override
                public void onResponse(Call<FullPostEntity> call, Response<FullPostEntity> response) {
                    // Got data. Send it to adapter

                    synchronized (counter) {
                        FullPostEntity results = fetchResults(response);
                        progressBar.setVisibility(View.GONE);

                        if (Security.getCurrentUser(getContext().getApplicationContext()).getUser_status().equals(UserStatus.USER)){
                            if (results.getPost_status().equals(PostStatus.PUBLIC)){
                                adapter.add(results, counter);
                            }
                        }else{
                            adapter.add(results, counter);
                        }
                    }

                    if (currentPage >= TOTAL_PAGES) adapter.addLoadingFooter();
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
            Log.d("UserNewsFragment ", "loadNextPage: " + currentPage);

            queryRepository.getApi().getCommentsCount(currentPage).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    try {
                        synchronized (counter) {
                            counter = response.body().intValue();
                        }
                    }catch (Exception e){

                    }
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

                    try {

                        synchronized (counter) {

                            FullPostEntity results = fetchResults(response);

                            if (Security.getCurrentUser(getContext().getApplicationContext()).getUser_status().equals(UserStatus.USER)) {
                                if (results.getPost_status().equals(PostStatus.PUBLIC)) {
                                    adapter.add(results, counter);
                                }
                            } else {
                                adapter.add(results, counter);
                            }

                        }

                        if (currentPage != TOTAL_PAGES) {
                            adapter.addLoadingFooter();
                        } else {
                            isLastPage = true;
                        }

                    }catch (Exception e){

                    }
                }

                @Override
                public void onFailure(Call<FullPostEntity> call, Throwable t) {
                    t.printStackTrace();
                    currentPage--;
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

    private void askSpeechInput() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Hi speak something");
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT: {
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        searchView.setActivated(true);
                        searchView.setQuery(result.get(0), false);
                    }
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
