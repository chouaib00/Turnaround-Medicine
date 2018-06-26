package com.example.user.dipl1;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.dipl1.entity.InviteEntity;
import com.example.user.dipl1.messaging.User;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.UserStatus;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitesFragment extends Fragment {

    ListView invitesListView;
    InviteAdapter adapter;
    QueryRepository repository;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invites, container, false);

        try {
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 1){
                        invitesListView.setAdapter(adapter);
                    }
                }
            };

            repository = new QueryRepository();
            repository.InitializeRetrofit(getContext().getApplicationContext());

            if (Security.getCurrentUser(getContext().getApplicationContext()).getUser_status().equals(UserStatus.USER)){
                repository.getApi().getAllInvitesByTo(Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()).enqueue(new Callback<List<InviteEntity>>() {
                    @Override
                    public void onResponse(Call<List<InviteEntity>> call, Response<List<InviteEntity>> response) {
                        adapter = new InviteAdapter(getContext(), response.body());

                        Message msg = new Message();
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<List<InviteEntity>> call, Throwable t) {

                    }
                });
            }else{
                repository.getApi().getAllInvitesByFrom(Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()).enqueue(new Callback<List<InviteEntity>>() {
                    @Override
                    public void onResponse(Call<List<InviteEntity>> call, Response<List<InviteEntity>> response) {
                        adapter = new InviteAdapter(getContext(), response.body());

                        Message msg = new Message();
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<List<InviteEntity>> call, Throwable t) {

                    }
                });
            }

            invitesListView = (ListView) v.findViewById(R.id.invitesListView);
            invitesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.direct.apps.user.direct", "com.direct.apps.user.direct.MainActivity"));

                    User userFrom = new User();
                    userFrom.setUserId(adapter.getItem(position).getInviteFrom().getUser_id());
                    userFrom.setUserName(adapter.getItem(position).getInviteFrom().getUser_name());

                    User userTo = new User();
                    userTo.setUserId(adapter.getItem(position).getInviteTo().getUser_id());
                    userTo.setUserName(adapter.getItem(position).getInviteTo().getUser_name());

                    Gson from = new Gson();
                    String gFrom = from.toJson(userFrom);

                    Gson to = new Gson();
                    String gTo = to.toJson(userTo);

                    intent.putExtra("from", gFrom);
                    intent.putExtra("to", gTo);

                    if (Security.getCurrentUser(getContext().getApplicationContext()).getUser_status().equals(UserStatus.USER))
                        intent.putExtra("role", "user");
                    else
                        intent.putExtra("role", "doctor");

                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

}
