package com.example.user.dipl1;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dipl1.entity.PostCommentsEntity;
import com.example.user.dipl1.entity.UserEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.UserStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsFragment extends Fragment {

    ListView commentsListView;
    EditText inputCommentView;
    ImageButton sendCommentButton;

    CardView addCommentCard;

    UserCommentAdapter adapter;
    ArrayList<PostCommentsEntity> postCommentsEntities;

    QueryRepository queryRepository;
    int postId;
    int userId;
    Response<UserEntity> user;

    TextView noCommentsView;

    Handler handler;

    boolean curFlag;

    @Override
    public void onStart() {
        super.onStart();

        curFlag = false;
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            curFlag = false;
            commentsListView = null;
            adapter = null;
            postCommentsEntities = null;
            user = null;
            handler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        try {
            curFlag = false;

            noCommentsView = (TextView) view.findViewById(R.id.noCommentsView);

            handler = new Handler(){
                public void handleMessage(Message msg){
                    if (msg.arg1 == 1){
                        noCommentsView.setVisibility(View.VISIBLE);
                    }
                }
            };

            queryRepository = new QueryRepository();
            queryRepository.InitializeRetrofit(getContext().getApplicationContext());

            postId = getArguments().getInt("postId");
            userId = getArguments().getInt("userId");

            commentsListView = (ListView) view.findViewById(R.id.commentsListView);

            addCommentCard = (CardView) view.findViewById(R.id.addCommentCard);

            if (Security.getCurrentUser(getContext().getApplicationContext()).getUser_status().equals(UserStatus.DOCTOR)
                    || userId == Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()) {
                addCommentCard.setVisibility(View.VISIBLE);
                curFlag = true;
            }

            if (getArguments().getBoolean("blocking")){
                addCommentCard.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Обсуждение закрыто", Toast.LENGTH_SHORT).show();
            }

            if (addCommentCard.getVisibility() == View.VISIBLE) {
                inputCommentView = (EditText) view.findViewById(R.id.inputCommentView);
                sendCommentButton = (ImageButton) view.findViewById(R.id.sendCommentButton);

                sendCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.isResponse) {
                            PostCommentsEntity p = new PostCommentsEntity();
                            p.setComment_id(0);
                            p.setUser_id(Security.getCurrentUser(getContext().getApplicationContext()));
                            p.setComment_value(inputCommentView.getText().toString());
                            p.setComment_date(new Date());
                            p.setChilds(null);
                            p.setChildsList(null);
                            p.setPost_id(postId);

                            inputCommentView.setText("");

                            queryRepository.getApi().addNewCommentResponse(p, adapter.getItem(adapter.selectedId).getComment_id()).enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if (response.body().equals(200)) {
                                        loadCommentsList();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {

                                }
                            });

                            adapter.isResponse = false;
                        } else {
                            PostCommentsEntity p = new PostCommentsEntity();
                            p.setComment_id(0);
                            p.setUser_id(Security.getCurrentUser(getContext().getApplicationContext()));
                            p.setComment_value(inputCommentView.getText().toString());
                            p.setComment_date(new Date());
                            p.setChilds(null);
                            p.setChildsList(null);
                            p.setPost_id(postId);

                            inputCommentView.setText("");

                            queryRepository.getApi().addNewComment(p).enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    Log.d(">>>>>>>>>>>", response.body().toString());

                                    if (response.body().equals(200)) {
                                        loadCommentsList();
                                    }

                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {

                                }
                            });
                        }

                        setListViewHeightBasedOnChildren(commentsListView);
                    }
                });
            }

            generateData();
            loadUserData();
            loadCommentsList();

            commentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.clickOnItem(position);
                    setListViewHeightBasedOnChildren(commentsListView);
                    //view.getParent().requestDisallowInterceptTouchEvent(true);
                }
            });

            setListViewHeightBasedOnChildren(commentsListView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void generateData() {
        postCommentsEntities = new ArrayList<>();
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        try {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null)
                return;

            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            int totalHeight = 0;
            View view = null;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                view = listAdapter.getView(i, view, listView);
                if (i == 0)
                    view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

                view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += view.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (queryRepository) {
                            user = queryRepository.getApi().getUserById(userId).execute();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d(">>>>>>>>>>>>>", user.body().getUser_name());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCommentsList() {

        try {
            queryRepository.getApi().getCommentsByPostId(postId).enqueue(new Callback<List<PostCommentsEntity>>() {
                @Override
                public void onResponse(Call<List<PostCommentsEntity>> call, Response<List<PostCommentsEntity>> response) {
                    if (response.body() == null) {
                        Log.d(">>>>>>>>>>>>", "NULL " + user.body().getUser_name());
                    } else {
                        postCommentsEntities = new ArrayList<>(response.body());
                        adapter = new UserCommentAdapter(getContext(), postCommentsEntities, getActivity(), curFlag);
                        commentsListView.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(commentsListView);
                        Log.d(">>>>>>>>>>>>", "" + postCommentsEntities.size());

                        if (postCommentsEntities.size() == 0 && Security.getCurrentUser(getContext().getApplicationContext()).getUser_status().equals(UserStatus.USER)){
                            Message message = new Message();
                            message.arg1 = 1;
                            handler.sendMessage(message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<PostCommentsEntity>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
