package com.example.user.dipl1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dipl1.appeal.AppealRepository;
import com.example.user.dipl1.entity.FullPostEntity;
import com.example.user.dipl1.entity.PostTagsEntity;
import com.example.user.dipl1.entity.UserEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.profile.MainActivity;
import com.example.user.dipl1.realm.NotificationsService;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.Ad;
import com.example.user.dipl1.utils.AdAdapter;
import com.example.user.dipl1.utils.CommandHelper;
import com.example.user.dipl1.utils.PostStatus;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.whalemare.sheetmenu.SheetMenu;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST = 1;
    CircleImageView imageUser;
    TextView userFLF;
    private UserEntity userEntity;
    private QueryRepository queryRepository;

    float dX;
    float dY;
    int lastAction;

    TextView notificationsCounterView;

    public final static String PARAM_COUNT = "count";
    public final static String BROADCAST_ACTION = "com.example.user.dipl1.broadcastaction";
    private BroadcastReceiver broadcastReceiver;

    private BottomNavigationView bottomNavigationView;

    private ListView adListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_user_home);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setLogo(R.drawable.logo_action);
            setSupportActionBar(toolbar);

            adListView = (ListView) findViewById(R.id.adListView);
            initAd();

            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        int counter = intent.getIntExtra(PARAM_COUNT, 0);

                        if (counter < 10) {
                            notificationsCounterView.setText(String.valueOf(counter));
                        }else {
                            notificationsCounterView.setText("9+");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }


            };

            IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationBottom);

            bottomNavigationView.setSelectedItemId(R.id.bottomNews);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.bottomProfile :{
                            try {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(MainActivity.INTENT_OBJECT, Security.getCurrentUser(getApplicationContext()));
                                CommandHelper.replaceFragment(MainActivity.class, getSupportFragmentManager(), R.id.container, bundle);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        case R.id.bottomNews : {
                            try {
                                CommandHelper.replaceFragment(UserNewsFragment.class, getSupportFragmentManager(), R.id.container, null);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        case R.id.bottomFavorites : {
                            try {
                                CommandHelper.replaceFragment(UserFavoritesFragment.class, getSupportFragmentManager(), R.id.container, null);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        case R.id.bottomNotifications : {
                            try {
                                CommandHelper.replaceFragment(UserNotifyFragment.class, getSupportFragmentManager(), R.id.container, null);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        case R.id.bottomSettings : {
                            try {
                                CommandHelper.replaceFragment(UserSettingsFragment.class, getSupportFragmentManager(), R.id.container, null);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return true;
                }
            });


            fab.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            lastAction = MotionEvent.ACTION_DOWN;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            view.setY(event.getRawY() + dY);
                            view.setX(event.getRawX() + dX);
                            lastAction = MotionEvent.ACTION_MOVE;
                            break;
                        case MotionEvent.ACTION_UP:
                            if (lastAction == MotionEvent.ACTION_DOWN) {
                               /* Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType("image*//**//*");
                                startActivityForResult(i, REQUEST);*/

                                SheetMenu.with(UserHome.this)
                                        .setAutoCancel(true)
                                        .setTitle("Созвать консилиум")
                                        .setMenu(R.menu.add_post_menu)
                                        .showIcons(true)
                                        .setClick(new MenuItem.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                if (item.getItemId() == R.id.addPhotoPost) {

                                                    Intent i = new Intent(Intent.ACTION_PICK);
                                                    i.setType("image/*");
                                                    startActivityForResult(i, REQUEST);
                                                } else if (item.getItemId() == R.id.addTextPost) {
                                                    addTextPost();
                                                }
                                                return true;
                                            }
                                        })
                                        .show();
                            }
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = UserNewsFragment.class;

            try{
                fragment = (Fragment)fragmentClass.newInstance();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

            View navHeaderLayout = navigationView.getHeaderView(0);

            imageUser = (CircleImageView) navHeaderLayout.findViewById(R.id.imageUser);
            imageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MainActivity.INTENT_OBJECT, Security.getCurrentUser(getApplicationContext()));
                        CommandHelper.replaceFragment(MainActivity.class, getSupportFragmentManager(), R.id.container, bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            userFLF = (TextView) navHeaderLayout.findViewById(R.id.fiouserview);

            queryRepository = new QueryRepository();
            queryRepository.InitializeRetrofit(getApplicationContext());

            userEntity = Security.getCurrentUser(this);
            MyApplication.userEntity = userEntity;

            imageUser.setImageBitmap(
                    BitmapFactory.decodeByteArray(userEntity.getUser_icon(), 0, userEntity.getUser_icon().length)
            );

            userFLF.setText(userEntity.getUser_name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            Bitmap img = null;

            if (requestCode == REQUEST && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = AddNewPostFragment.class;

                    fragment = (Fragment)fragmentClass.newInstance();

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();

                    img.compress(Bitmap.CompressFormat.JPEG, 50, bs);

                    Bundle param = new Bundle();
                    param.putByteArray("photo", bs.toByteArray());

                    fragment.setArguments(param);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                } catch (InstantiationException e){
                    Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                } catch (IllegalAccessException e){
                    Toast.makeText(this, "Ошибка доступа", Toast.LENGTH_SHORT).show();
                }

            }

            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.user_home, menu);

            final MenuItem navNotification = menu.findItem(R.id.action_notifications);

            navNotification.setActionView(R.layout.notification_action_layout);
            View actionView = navNotification.getActionView();
            notificationsCounterView = (TextView) actionView.findViewById(R.id.cart_badge);
            notificationsCounterView.setText("0");

            notificationsCounterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(navNotification);
                }
            });


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.action_settings) {
            fragmentClass = UserSettingsFragment.class;

            try{
                fragment = (Fragment) fragmentClass.newInstance();
                FragmentTransaction transaction = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }else if(id == R.id.action_notifications){
            fragmentClass = UserNotifyFragment.class;

            try{
                fragment = (Fragment) fragmentClass.newInstance();
                FragmentTransaction transaction = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }else if(id == R.id.signOutItem){
            Security.signOut(getApplicationContext(), this);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            fragmentClass = UserNewsFragment.class;
        } else if (id == R.id.nav_gallery) {

            fragmentClass = MainActivity.class;
        } else if (id == R.id.nav_slideshow) {
            fragmentClass = UserFavoritesFragment.class;
        } else if (id == R.id.nav_manage) {
            fragmentClass = UserNotifyFragment.class;
        } else if (id == R.id.nav_share) {
            fragmentClass = UserSettingsFragment.class;
        } else if (id == R.id.bottomInvites){
            fragmentClass = InvitesFragment.class;
        }

        try{
            fragment = (Fragment)fragmentClass.newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable(MainActivity.INTENT_OBJECT, Security.getCurrentUser(getApplicationContext()));
            fragment.setArguments(bundle);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        if (id != R.id.nav_share) {
            item.setChecked(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Activity: ", "onStart()");
        startService(new Intent(UserHome.this, NotificationsService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Activity: ", "onStop()");
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        stopService(new Intent(UserHome.this, NotificationsService.class));
    }

    private void addTextPost(){
        try {
            View view = (LinearLayout) getLayoutInflater().inflate(R.layout.text_post, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(UserHome.this)
                    .setTitle("Добавление записи с текстовым контентом")
                    .setView(view);

            Button addPost = (Button) view.findViewById(R.id.publihTextPostBtn);
            final EditText tags = (EditText) view.findViewById(R.id.textTagsInput);
            final EditText desc = (EditText) view.findViewById(R.id.textDescInput);
            final CheckBox privatePost = (CheckBox) view.findViewById(R.id.makePrivateTextPost);

            addPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullPostEntity post = new FullPostEntity();
                    post.setPost_status(privatePost.isChecked() ? PostStatus.PRIVATE : PostStatus.PUBLIC);
                    post.setDescription(desc.getText().toString());
                    post.setPhoto(new byte[]{});
                    post.setPost_date(new Date());
                    post.setUser_id(Security.getCurrentUser(getApplicationContext()));

                    queryRepository.getApi().addPost(post).enqueue(new Callback<FullPostEntity>() {
                        @Override
                        public void onResponse(Call<FullPostEntity> call, Response<FullPostEntity> response) {
                            String[] tagsArray = getTagsArray(tags.getText().toString());

                            for (String tag:
                                 tagsArray) {

                                PostTagsEntity postTagsEntity = new PostTagsEntity();
                                postTagsEntity.setPost_id(response.body());
                                postTagsEntity.setTag_value(tag);

                                queryRepository.getApi().addTag(postTagsEntity).enqueue(new Callback<Integer>() {
                                    @Override
                                    public void onResponse(Call<Integer> call, Response<Integer> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Integer> call, Throwable t) {

                                    }
                                });
                            }
                            Toast.makeText(UserHome.this, "Данные опубликованы", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<FullPostEntity> call, Throwable t) {

                        }
                    });
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getTagsArray(String value){
        return value.split(",");
    }

    public void initAd(){
        try {
            AppealRepository appealRepository = new AppealRepository();
            appealRepository.InitializeRetrofit();

            appealRepository.getApi().recvAds().enqueue(new Callback<List<Ad>>() {
                @Override
                public void onResponse(Call<List<Ad>> call, Response<List<Ad>> response) {
                    AdAdapter adAdapter = new AdAdapter(UserHome.this, response.body());
                    adListView.setAdapter(adAdapter);
                }

                @Override
                public void onFailure(Call<List<Ad>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
