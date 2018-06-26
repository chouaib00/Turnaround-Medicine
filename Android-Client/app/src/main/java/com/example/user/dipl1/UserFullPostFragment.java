package com.example.user.dipl1;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.user.dipl1.appeal.Appeal;
import com.example.user.dipl1.appeal.AppealRepository;
import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.entity.PostTagsEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.profile.MainActivity;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;
import com.example.user.dipl1.utils.PostStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.whalemare.sheetmenu.SheetMenu;


public class UserFullPostFragment extends Fragment {
    ArrayList<String> listTags;

    int postId = 0;

    CircleImageView userIcon;
    TextView userName;
    TextView postDate;
    TextView postDesc;
    SubsamplingScaleImageView postPhoto;
    TagContainerLayout mTagContainerLayout;

    QueryRepository queryRepository;
    FullPostEntity fullPost;

    TextView showCommentsAction;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    ImageButton editPost;

    Handler handler;

    TextView appealBtn;
    TextView blockingBtn;
    FullPostEntity currentPost = null;

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postId = getArguments().getInt("ID");

        queryRepository = new QueryRepository();
        queryRepository.InitializeRetrofit(getContext().getApplicationContext());
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onStart() {
        super.onStart();

        listTags = new ArrayList<>();

        handler = new Handler(){
            public void handleMessage(Message message){
                if (message.arg1 == 1001){
                    Toast.makeText(getContext(), "Жалоба отправлена", Toast.LENGTH_SHORT).show();
                }else if (message.arg1 == 1002){
                    Toast.makeText(getContext(), "Жалоба отклонена", Toast.LENGTH_SHORT).show();
                }else if (message.arg1 == 1003){
                    Toast.makeText(getContext(), "Ошибка отправки", Toast.LENGTH_SHORT).show();
                }else if(message.arg1 == 937){
                    Toast.makeText(getContext(), "Обсуждение закрыто", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Пост переведен в приватный статус", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();

        handler = null;
        listTags = null;
        userIcon = null;
        postPhoto = null;
        mTagContainerLayout = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_full_post, container, false);


        try {
            editPost = (ImageButton) view.findViewById(R.id.deletePostUserButton);
            editPost.setVisibility(View.INVISIBLE);

            appealBtn = (TextView) view.findViewById(R.id.appealBtn);
            appealBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendAppeal();
                }
            });

            blockingBtn = (TextView) view.findViewById(R.id.blockingBtn);
            blockingBtn.setVisibility(View.INVISIBLE);
            blockingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blockPost();
                }
            });


            editPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fullPost.getPost_status().equals(PostStatus.PUBLIC)){
                        if (
                                Security.getCurrentUser(getContext().getApplicationContext()).getUser_id() == fullPost.getUser_id().getUser_id()
                                ){
                            SheetMenu.with(getContext())
                                    .setAutoCancel(true)
                                    .setTitle("Редактирование изображения")
                                    .setMenu(R.menu.profile_menu)
                                    .showIcons(true)
                                    .setClick(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            if (item.getItemId() == R.id.setPrivateBtn){
                                                //
                                                if (fullPost != null){
                                                    fullPost.setPost_status(PostStatus.PRIVATE);

                                                    queryRepository.getApi().addPost(fullPost).enqueue(new Callback<FullPostEntity>() {
                                                        @Override
                                                        public void onResponse(Call<FullPostEntity> call, Response<FullPostEntity> response) {
                                                            if (response.body() != null){
                                                                handler.sendEmptyMessage(1);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<FullPostEntity> call, Throwable t) {

                                                        }
                                                    });
                                                }
                                                //
                                            }
                                            return true;
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            });

            userIcon = (CircleImageView) view.findViewById(R.id.fullPostUserIcon);

            userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MainActivity.INTENT_OBJECT, fullPost.getUser_id());
                    CommandHelper.replaceFragment(MainActivity.class, getActivity(), R.id.container, bundle);
                }
            });

            userName = (TextView) view.findViewById(R.id.UserNameFull);
            postDate = (TextView) view.findViewById(R.id.UserDateFull);
            postDesc = (TextView) view.findViewById(R.id.UserDescriptionFull);
            postPhoto = (SubsamplingScaleImageView) view.findViewById(R.id.UserPostFull);
            postPhoto.setMaxScale(10f);
            postPhoto.setMinScale(1f);

            fillPostInformation();

            mTagContainerLayout = (TagContainerLayout)view.findViewById(R.id.UserTagsFull);
            mTagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(int position, String text) {
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = SearchFragment.class;
                    //fragmentClass = DoctorFullPostFragment.class;

                    Bundle bundle = new Bundle();
                    bundle.putString("VALUE", text);

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
                public void onTagLongClick(int position, String text) {

                }

                @Override
                public void onTagCrossClick(int position) {

                }
            });

            fillTagsContainer();


            showCommentsAction = (TextView) view.findViewById(R.id.showCommentsLink);

            showCommentsAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(showCommentsAction.getText().toString().contains(getString(R.string.showComment))){
                        Fragment fragment = null;
                        Class fragmentClass = null;
                        fragmentClass = CommentsFragment.class;

                        // example

                        Bundle bundle = new Bundle();
                        bundle.putInt("postId", fullPost.getPost_id());
                        bundle.putInt("userId", fullPost.getUser_id().getUser_id());
                        bundle.putBoolean("blocking", fullPost.isBlocking());
                        // example

                        try{
                            fragment = (Fragment) fragmentClass.newInstance();
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = null;
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            transaction = fragmentManager.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.replace(R.id.commentsContainer, fragment);
                            transaction.commit();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void fillPostInformation(){
        try {
            callRemoteProcedureForFullPost().enqueue(new Callback<FullPostEntity>() {
                @Override
                public void onResponse(Call<FullPostEntity> call, Response<FullPostEntity> response) {
                    FullPostEntity fullPostEntity = response.body();
                    fullPost = fullPostEntity;
                    currentPost = fullPostEntity;

                    userName.setText(
                                    fullPostEntity.getUser_id().getUser_name()
                    );

                    postDate.setText(sdf.format(fullPostEntity.getPost_date()));
                    postDesc.setText(fullPostEntity.getDescription());

                    if (fullPostEntity.getPhoto().length > 0) {
                        postPhoto.setImage(ImageSource.bitmap(BitmapFactory.decodeByteArray(
                                fullPostEntity.getPhoto(),
                                0,
                                fullPostEntity.getPhoto().length
                        )));
                    }else{
                        postPhoto.setLayoutParams(new RelativeLayout.LayoutParams(1, 1));
                    }

                    userIcon.setImageBitmap(
                            BitmapFactory.decodeByteArray(fullPostEntity.getUser_id().getUser_icon(),
                                    0,
                                    fullPostEntity.getUser_id().getUser_icon().length )
                    );

                    if (fullPostEntity.getUser_id().getUser_id() == Security.getCurrentUser(getContext().getApplicationContext()).getUser_id()){
                        editPost.setVisibility(View.VISIBLE);
                        blockingBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<FullPostEntity> call, Throwable t) {
                    Toast.makeText(getContext(), "Not find", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillTagsContainer(){
        try {
            callRemoteProcedureForTags().enqueue(new Callback<List<PostTagsEntity>>() {
                @Override
                public void onResponse(Call<List<PostTagsEntity>> call, Response<List<PostTagsEntity>> response) {
                    List<PostTagsEntity> postTagsEntities = response.body();

                    for (PostTagsEntity p:
                         postTagsEntities) {
                        listTags.add(p.getTag_value());
                    }

                    mTagContainerLayout.setTags(listTags);
                }

                @Override
                public void onFailure(Call<List<PostTagsEntity>> call, Throwable t) {
                    Toast.makeText(getContext(), "Tags is not loaded", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Call<FullPostEntity> callRemoteProcedureForFullPost() {
        return queryRepository.getApi().getFullPostById(postId);
    }

    private Call<List<PostTagsEntity>> callRemoteProcedureForTags(){
        return queryRepository.getApi().getFullPostTagsById(postId);
    }

    private void sendAppeal(){
        try {
            final AppealRepository appealRepository = new AppealRepository();
            appealRepository.InitializeRetrofit();

            View view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.appeal_layout, null);

            final RadioButton rb1 = (RadioButton) view.findViewById(R.id.arb1);
            final RadioButton rb2 = (RadioButton) view.findViewById(R.id.arb2);
            final RadioButton rb3 = (RadioButton) view.findViewById(R.id.arb3);
            final RadioButton rb4 = (RadioButton) view.findViewById(R.id.arb4);
            final RadioButton rb5 = (RadioButton) view.findViewById(R.id.arb5);
            final EditText appealTextView = (EditText) view.findViewById(R.id.appealMessageView);

            rb5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        appealTextView.setVisibility(View.VISIBLE);
                    else
                        appealTextView.setVisibility(View.INVISIBLE);
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                            String message = null;

                            if (rb1.isChecked())
                                message = rb1.getText().toString();
                            else if (rb2.isChecked())
                                message = rb2.getText().toString();
                            else if (rb3.isChecked())
                                message = rb3.getText().toString();
                            else if (rb4.isChecked())
                                message = rb4.getText().toString();
                            else if (rb5.isChecked())
                                message = appealTextView.getText().toString();

                            Appeal appeal = new Appeal();
                            appeal.AppealAuthorName = Security.getCurrentUser(getContext().getApplicationContext()).getUser_name();
                            appeal.AppealDate = sdf.format(new Date());
                            appeal.AppealPostOrUserId = currentPost.getPost_id();
                            appeal.AppealStatus = false;
                            appeal.AppealToUser = false;
                            appeal.AppealMessage = message;

                            appealRepository.getApi().sendAppeal(appeal).enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if (response.body().equals(1)){
                                        Message msg = new Message();
                                        msg.arg1 = 1001;
                                        handler.sendMessage(msg);
                                    }else{
                                        Message msg = new Message();
                                        msg.arg1 = 1002;
                                        handler.sendMessage(msg);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Message msg = new Message();
                                    msg.arg1 = 1003;
                                    handler.sendMessage(msg);
                                }
                            });

                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setView(view);

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void blockPost(){
        try {
            queryRepository.getApi().blockFullPost(fullPost.getPost_id(), true).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.body().equals(currentPost.getPost_id())){
                        Message message = new Message();
                        message.arg1 = 937;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
