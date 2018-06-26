package com.example.user.dipl1.profile;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.user.dipl1.R;
import com.example.user.dipl1.RecyclerClickListener;
import com.example.user.dipl1.UserFullPostFragment;
import com.example.user.dipl1.UserImageAdapter;
import com.example.user.dipl1.entity.FollowingEntity;
import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.entity.InviteEntity;
import com.example.user.dipl1.entity.UserEntity;
import com.example.user.dipl1.local_db.PhotoTable;
import com.example.user.dipl1.messaging.User;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;
import com.example.user.dipl1.utils.UserStatus;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Fragment {

    public static final String INTENT_OBJECT = "USER_OBJECT";
    private UserEntity currentUser;

    private TextView profileUserName;
    private ImageView isDoctorIcon;
    private CircleImageView profileUserAvatar;
    private TextView publishingCountView;
    private TextView commentsCountView;
    private TextView followersCountView;
    private TextView userInfoView;
    private Button followBtn;
    private LinearLayout blockWithFollowBtn;

    private RecyclerView photosRecyclerView;
    GridLayoutManager manager;

    UserImageAdapter adapter = null;
    QueryRepository repository;
    Dictionary<String, Integer> profileParams = new Hashtable<>();

    Handler handler;

    Geocoder geocoder;

    Button showAsMap;
    Button showAsPhone;

    List<FollowingEntity> followers;
    RecyclerView followersRV;
    FollowerAdapter followerAdapter;

    boolean isFollowing = false;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    SubsamplingScaleImageView sertificateView;
    android.support.v7.app.AlertDialog showingSertificate;

    ImageView invitesButton;

    @Override
    public void onStop() {
        super.onStop();

        photosRecyclerView = null;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repository = new QueryRepository();
        repository.InitializeRetrofit(getContext().getApplicationContext());

        currentUser = (UserEntity) getArguments().getSerializable(INTENT_OBJECT);

        handler = new Handler(){
            public void handleMessage(Message message){
                if (message.arg1 == 1){
                    publishingCountView.setText(profileParams.get("posts").toString());
                    commentsCountView.setText(profileParams.get("comments").toString());
                    followersCountView.setText(profileParams.get("followers").toString());
                }else if(message.arg1 == 200){
                    followBtn.setText(getResources().getString(R.string.unfollow));
                    isFollowing = true;
                    Toast.makeText(getContext(), "Вы подписались на пользователя " + currentUser.getUser_name(), Toast.LENGTH_SHORT).show();
                }else if(message.arg1 == 500){
                    Toast.makeText(getContext(), "Ошибка при подписке", Toast.LENGTH_SHORT).show();
                }else if (message.arg1 == 303){

                    Toast.makeText(getContext(), "Пользователь " + currentUser.getUser_name() + " приглашен в чат", Toast.LENGTH_SHORT).show();

                } else if(message.arg1 == 3){
                    followBtn.setText(getResources().getString(R.string.follow));
                    isFollowing = false;
                    Toast.makeText(getContext(), "Вы отписались от пользователя " + currentUser.getUser_name(), Toast.LENGTH_SHORT).show();
                }else if (message.arg1 == 101){

                    if (blockWithFollowBtn.getVisibility() == View.VISIBLE) {
                        for (FollowingEntity item:
                                followers) {
                            if (item.getUserId().getUser_id() == Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()){
                                isFollowing = true;
                                break;
                            }
                        }

                        if (isFollowing){
                            followBtn.setText(getResources().getString(R.string.unfollow));
                        }else{
                            followBtn.setText(getResources().getString(R.string.follow));
                        }

                        followBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isFollowing){
                                    FollowingEntity followingEntity = new FollowingEntity();
                                    followingEntity.setUserId(Security.getCurrentUser(getContext().getApplicationContext()));
                                    followingEntity.setFollowOn(currentUser);
                                    repository.getApi().followOn(followingEntity).enqueue(new retrofit2.Callback<FollowingEntity>() {
                                        @Override
                                        public void onResponse(Call<FollowingEntity> call, Response<FollowingEntity> response) {
                                            if (response.body() != null){
                                                Message message = new Message();
                                                message.arg1 = 200;
                                                handler.sendMessage(message);
                                                followBtn.setText(getResources().getString(R.string.unfollow));
                                            }else{
                                                Message message = new Message();
                                                message.arg1 = 500;
                                                handler.sendMessage(message);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FollowingEntity> call, Throwable t) {

                                        }
                                    });
                                }else{
                                    repository.getApi().unfollowOn(currentUser.getUser_id(), Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()).enqueue(new retrofit2.Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Message message = new Message();
                                            message.arg1 = 3;
                                            handler.sendMessage(message);

                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });
                                }
                            }
                        });

                    }

                }
            };
        };

        adapter = new UserImageAdapter(getContext(), getActivity().getSupportFragmentManager());
        initProfile();
        fillGridForOtherUser(currentUser.getUser_id());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        invitesButton = (ImageView) view.findViewById(R.id.inviteBtn);

        if (currentUser.getUser_status().equals(UserStatus.USER) && !Security.getCurrentUser(getContext().getApplicationContext()).getUser_status().equals(UserStatus.USER)){
            invitesButton.setVisibility(View.VISIBLE);

            invitesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InviteEntity inviteEntity = new InviteEntity();
                    inviteEntity.setInviteFrom(Security.getCurrentUser(getContext().getApplicationContext()));
                    inviteEntity.setInviteTo(currentUser);
                    repository.getApi().addInvite(inviteEntity).enqueue(new Callback<InviteEntity>() {
                        @Override
                        public void onResponse(Call<InviteEntity> call, Response<InviteEntity> response) {
                            Message message = new Message();
                            message.arg1 = 303;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onFailure(Call<InviteEntity> call, Throwable t) {
                            Message message = new Message();
                            message.arg1 = 303;
                            handler.sendMessage(message);
                        }
                    });
                }
            });
        }else{
            invitesButton.setVisibility(View.INVISIBLE);
        }

        manager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);

        showAsMap = (Button) view.findViewById(R.id.showAsMapBtn);
        showAsPhone = (Button) view.findViewById(R.id.showAsPhoneBtn);
        geocoder = new Geocoder(getContext().getApplicationContext(), Locale.getDefault());

        showAsMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(currentUser.getUser_adress(), 1);
                    String uri = "geo:" + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();
                    Uri gmmIntentUri = Uri.parse(uri);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        showAsPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + currentUser.getUser_phone_number()));
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        profileUserName = (TextView) view.findViewById(R.id.profileUserName);
        profileUserName.setText(currentUser.getUser_name());

        isDoctorIcon = (ImageView) view.findViewById(R.id.isDoctorIcon);
        isDoctorIcon.setVisibility(View.INVISIBLE);
        if (currentUser.getUser_status().equals(UserStatus.DOCTOR)) {
            isDoctorIcon.setVisibility(View.VISIBLE);
            isDoctorIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSertificate();
                }
            });
        }

        profileUserAvatar = (CircleImageView) view.findViewById(R.id.profileUserAvatar);
        profileUserAvatar.setImageBitmap(
                BitmapFactory.decodeByteArray(
                        currentUser.getUser_icon(), 0, currentUser.getUser_icon().length
                )
        );

        publishingCountView = (TextView) view.findViewById(R.id.publishingCountView);
        commentsCountView = (TextView) view.findViewById(R.id.commentsCountView);
        followersCountView = (TextView) view.findViewById(R.id.followersCountView);
        followersCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowers();
            }
        });

        //request

        userInfoView = (TextView) view.findViewById(R.id.userInfoView);
        userInfoView.setText(
                        currentUser.getUser_name() + "\n" +
                                (currentUser.getUser_status().equals(UserStatus.DOCTOR) ? "Врач" : "Пользователь") + "\n" +
                                currentUser.getUser_email()
        );

        if (currentUser.getUser_phone_number() != null)
            if (currentUser.getUser_phone_number().length() > 0)
                userInfoView.setText(userInfoView.getText().toString() + "\n" + currentUser.getUser_phone_number());

        if (currentUser.getUser_adress() != null)
            if (currentUser.getUser_adress().length() > 0)
                userInfoView.setText(userInfoView.getText().toString() + "\n" + currentUser.getUser_adress());

        if (currentUser.getUser_work() != null)
            if (currentUser.getUser_work().length() > 0)
                userInfoView.setText(userInfoView.getText().toString() + "\n" + currentUser.getUser_work());

        Linkify.addLinks(userInfoView, Linkify.ALL);

        blockWithFollowBtn = (LinearLayout) view.findViewById(R.id.blockD);

        if (currentUser.getUser_id() == Security.getCurrentUser(getActivity().getApplicationContext()).getUser_id()){
            blockWithFollowBtn.setVisibility(View.INVISIBLE);
        }

        followBtn = (Button) view.findViewById(R.id.followUnfollowBtn);

        //if

        photosRecyclerView = (RecyclerView) view.findViewById(R.id.profileImages);
        photosRecyclerView.setLayoutManager(manager);
        photosRecyclerView.setAdapter(adapter);

        photosRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getContext()) {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                int postId = adapter.getItem(position).getPhoto_id();

                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = UserFullPostFragment.class;

                Bundle bundle = new Bundle();
                bundle.putInt("ID", postId);

                try{
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = null;
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.container, fragment);
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

        return view;
    }

    private void fillGridForOtherUser(int userId){
        repository.getApi().getPostsByUser(userId).enqueue(new Callback<List<FullPostEntity>>() {
            @Override
            public void onResponse(Call<List<FullPostEntity>> call, Response<List<FullPostEntity>> response) {

                for (FullPostEntity p:
                        response.body()) {
                    PhotoTable photo = new PhotoTable.PhotoBuilder()
                            .photo(p.getPhoto())
                            .photo_date(p.getPost_date().toString())
                            .photo_id(p.getPost_id())
                            .build();

                    adapter.addItemNotify(photo);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<FullPostEntity>> call, Throwable t) {

            }
        });
    }

    private void initProfile(){
        repository.getApi().getProfileParams(currentUser.getUser_id()).enqueue(new Callback<Hashtable<String, Integer>>() {
            @Override
            public void onResponse(Call<Hashtable<String, Integer>> call, Response<Hashtable<String, Integer>> response) {
                synchronized (profileParams) {
                    profileParams = response.body();
                    Message  message = new Message();
                    message.arg1 = 1;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call<Hashtable<String, Integer>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        repository.getApi().getAllFollowers(currentUser.getUser_id()).enqueue(new Callback<List<FollowingEntity>>() {
            @Override
            public void onResponse(Call<List<FollowingEntity>> call, Response<List<FollowingEntity>> response) {
                followers = response.body();
                Message message = new Message();
                message.arg1 = 101;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<List<FollowingEntity>> call, Throwable t) {

            }
        });
    }

    private void showFollowers(){
        View view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.followers_layout, null);
        builder = new AlertDialog.Builder(getContext())
                .setTitle("Подписчики")
                .setView(view);

        followersRV = (RecyclerView) view.findViewById(R.id.followersRLayout);
        followerAdapter = new FollowerAdapter(followers);
        followersRV.setLayoutManager(new LinearLayoutManager(getContext()));
        followersRV.setAdapter(followerAdapter);

        followersRV.addOnItemTouchListener(new RecyclerClickListener(getContext()) {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(MainActivity.INTENT_OBJECT, followerAdapter.persons.get(position).getUserId());
                CommandHelper.replaceFragment(MainActivity.class, getActivity(), R.id.container, bundle);
                alertDialog.cancel();
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSertificate(){
        if (currentUser.getSertificate() != null){
            View view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.sertificate_layout, null);

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext())
                    .setTitle("Сертификат")
                    .setView(view);

            sertificateView = (SubsamplingScaleImageView) view.findViewById(R.id.sertificateView);
            sertificateView.setMinScale(1);
            sertificateView.setMaxScale(10);
            sertificateView.setImage(ImageSource.bitmap(
                    BitmapFactory.decodeByteArray(
                            currentUser.getSertificate(),
                            0,
                            currentUser.getSertificate().length
                    )
            ));

            showingSertificate = builder.create();
            showingSertificate.show();
        }
    }
}
