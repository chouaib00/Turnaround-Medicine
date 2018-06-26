package com.example.user.dipl1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.user.dipl1.entity.NotificationsEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.realm.Notification;
import com.example.user.dipl1.security.Security;

import java.text.SimpleDateFormat;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNotifyFragment extends Fragment {

    private Realm realm;
    private UserNotificationAdapter userNotificationAdapter;
    private QueryRepository queryRepository;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    public void onPause() {
        super.onPause();

        listView = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            queryRepository = new QueryRepository();
            queryRepository.InitializeRetrofit(getContext().getApplicationContext());

            Realm.init(getContext());
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_notify, container, false);

        try {
            progressBar = (ProgressBar) view.findViewById(R.id.notificationsPB);

            listView = (ListView)view.findViewById(R.id.user_notify_list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    queryRepository.getApi()
                            .readNotification(userNotificationAdapter.getItem(position)
                                    .getNotificationId())
                            .enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if (response.body().equals(200)){

                                        Fragment fragment = null;
                                        Class fragmentClass = null;
                                        fragmentClass = UserFullPostFragment.class;
                                        //fragmentClass = DoctorFullPostFragment.class;

                                        int post_id = userNotificationAdapter.getItem(position).getPostId();
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

                                    }else{
                                        Log.d("Notifications: ", "!!! Not read - error !!!");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {

                                }
                            });
                }
            });

            getAllNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void getAllNotifications(){

        try {
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }

        queryRepository.getApi().getAllNotificationsByUserId(Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()).enqueue(new Callback<List<NotificationsEntity>>() {
            @Override
            public void onResponse(Call<List<NotificationsEntity>> call, Response<List<NotificationsEntity>> response) {

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    for (NotificationsEntity n:
                         response.body()) {

                        if (n.getUserFromId().getUser_id() != Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()) {

                            realm.beginTransaction();

                            Notification notification = realm.createObject(Notification.class);
                            notification.setNotificationId(n.getNotificationId());
                            notification.setNotificationDate(sdf.format(n.getNotificationDate()));
                            notification.setNotificationRead(n.getNotificationRead());
                            notification.setPostIcon(n.getPostId().getPhoto());
                            notification.setPostId(n.getPostId().getPost_id());
                            notification.setUserFLF(n.getUserFromId().getUser_name());

                            realm.commitTransaction();

                        }
                    }

                    OrderedRealmCollection<Notification> notifications;

                    realm.beginTransaction();
                    notifications = realm.where(Notification.class).findAll().sort("notificationDate", Sort.DESCENDING);
                    realm.commitTransaction();

                    progressBar.setVisibility(View.INVISIBLE);

                    userNotificationAdapter = new UserNotificationAdapter(getContext(), notifications);
                    listView.setAdapter(userNotificationAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationsEntity>> call, Throwable t) {

            }
        });
    }
}
