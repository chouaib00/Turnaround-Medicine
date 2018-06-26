package com.example.user.dipl1.auth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.user.dipl1.MyApplication;
import com.example.user.dipl1.R;
import com.example.user.dipl1.UserHome;
import com.example.user.dipl1.entity.UserEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.sirvar.robin.RobinActivity;
import com.sirvar.robin.Theme;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends RobinActivity {

    FancyAlertDialog.Builder alert;
    CallbackManager callbackManager;
    QueryRepository repository;

    static final int RC_SIGN_IN = 9001;
    GoogleApiClient googleApiClient;


    String userStatus = "";
    Handler handler;
    boolean existsEmail = true;
    SharedPreferences sharedPreferences;

    public static final String USER_KEY = "user";

    @Override
    protected void onStart() {
        super.onStart();

        Security.autoSignIn(getApplicationContext(), this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            repository = new QueryRepository();
            repository.InitializeRetrofit(getApplicationContext());

            setLoginTitle("Вход в приложение Turnaround Medicine");
            setSignupTitle("Добро пожаловать в приложение Turnaround Medicine" );
            setForgotPasswordTitle("Forgot Password");
            setImage(getResources().getDrawable(R.drawable.logo_b));
            setTheme(Theme.LIGHT);
            enableSocialLogin();

            FacebookSdk.sdkInitialize(this.getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d("Success", "Login");
                            final String facebookId = loginResult.getAccessToken().getUserId();

                            repository.getApi().getUserByFacebookId(facebookId).enqueue(new Callback<UserEntity>() {
                                @Override
                                public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                                    if (response.body() == null){
                                        Message msg = new Message();
                                        msg.arg1 = 2;
                                        handler.sendMessage(msg);
                                        LoginManager.getInstance().logOut();
                                    }else{
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        Gson gson = new Gson();
                                        String user = gson.toJson(response.body());
                                        editor.putString(USER_KEY, user);
                                        editor.commit();

                                        LoginManager.getInstance().logOut();

                                        Intent intent = new Intent(AuthActivity.this, UserHome.class);
                                        startActivity(intent);
                                    }

                                }

                                @Override
                                public void onFailure(Call<UserEntity> call, Throwable t) {
                                    Message msg = new Message();
                                    msg.arg1 = 2;
                                    handler.sendMessage(msg);
                                }
                            });
                        }

                        @Override
                        public void onCancel() {
                            LoginManager.getInstance().logOut();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            LoginManager.getInstance().logOut();
                        }
                    });

            handler = new Handler(){
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 0) {
                        Toast.makeText(AuthActivity.this, "Введенный e-mail существует в приложении", Toast.LENGTH_SHORT).show();
                    }else if (msg.arg1 == 1){
                        Toast.makeText(AuthActivity.this, "Проверьте правильноть введенных данных", Toast.LENGTH_SHORT).show();
                    }else if(msg.arg1 == 2){
                        Toast.makeText(AuthActivity.this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show();
                    }
                };
            };

            sharedPreferences = getSharedPreferences(USER_KEY, MODE_PRIVATE);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLogin(String email, String password) {
        try {
            customSignIn(email, password); // worked
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSignup(String name, String email, String password) {
        try {
            createAlertDialog(name, email, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onForgotPassword(String email) {
        // no body
    }

    @Override
    protected void onGoogleLogin() {
        signInWithGoogle();
    }

    @Override
    protected void onFacebookLogin() {
        try {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    final GoogleApiClient client = googleApiClient;
                    final String googleId = result.getSignInAccount().getId();

                    repository.getApi().getUserByGoogleId(googleId).enqueue(new Callback<UserEntity>() {
                        @Override
                        public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                            if (response.body() == null){
                                Message msg = new Message();
                                msg.arg1 = 2;
                                handler.sendMessage(msg);

                                handleSignInResult(new Callable<Void>() {
                                    @Override
                                    public Void call() throws Exception {
                                        if (client != null) {

                                            Auth.GoogleSignInApi.signOut(client).setResultCallback(
                                                    new ResultCallback<Status>() {
                                                        @Override
                                                        public void onResult(Status status) {
                                                            Log.d(AuthActivity.class.getCanonicalName() + ">>>>>",
                                                                    status.getStatusMessage());
                                                        }
                                                    }
                                            );

                                        }
                                        return null;
                                    }
                                });

                            }else{
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Gson gson = new Gson();
                                String user = gson.toJson(response.body());
                                editor.putString(USER_KEY, user);
                                editor.commit();

                                handleSignInResult(new Callable<Void>() {
                                    @Override
                                    public Void call() throws Exception {
                                        if (client != null) {

                                            Auth.GoogleSignInApi.signOut(client).setResultCallback(
                                                    new ResultCallback<Status>() {
                                                        @Override
                                                        public void onResult(Status status) {
                                                            Log.d(">>>>>>>>>>>",
                                                                    "logout");
                                                        }
                                                    }
                                            );

                                        }
                                        return null;
                                    }
                                });

                                Intent intent = new Intent(AuthActivity.this, UserHome.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserEntity> call, Throwable t) {
                            Message msg = new Message();
                            msg.arg1 = 2;
                            handler.sendMessage(msg);
                        }
                    });
                }
            }else{
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAlertDialog(final String name, final String email, final String password){
        try {
            alert = new FancyAlertDialog.Builder(AuthActivity.this)
                    .setimageResource(R.drawable.zzz_comment_question_outline)
                    .setTextTitle("Turnaround Medicine")
                    .setTextSubTitle("Являетесь ли Вы работником сферы здравоохранения?")
                    .setBody("Подтверждение Вашей роли медицинского работника повлечет за собой ответсвенность!")
                    .setNegativeColor(R.color.bRos)
                    .setNegativeButtonText("Нет, не являюсь")
                    .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            userStatus = "us";
                            emailIsExists(email, name, password);
                            dialog.cancel();
                        }
                    })
                    .setPositiveButtonText("Да, являюсь")
                    .setPositiveColor(R.color.bGreen)
                    .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            userStatus = "dc";
                            emailIsExists(email, name, password);
                            dialog.cancel();
                        }
                    })
                    .setBodyGravity(FancyAlertDialog.TextGravity.LEFT)
                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setSubtitleGravity(FancyAlertDialog.TextGravity.RIGHT)
                    .setCancelable(false)
                    .build();

            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signInWithGoogle() {
        try {
            if(googleApiClient != null) {
                googleApiClient.disconnect();
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            googleApiClient.connect();

            final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSignInResult(Callable<Void> logout) {
        try {
            if(logout == null) {
                /* Login error */
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
            } else {
                /* Login success */
                MyApplication.getInstance().setLogoutCallable(logout);
                try {
                    logout.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Intent intent = new Intent(AuthActivity.this, UserHome.class);
                //startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void customSignIn(String email, final String password){
        try {
            repository.getApi()
                    .getUserByEmail(email)
                    .enqueue(new Callback<UserEntity>() {
                        @Override
                        public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {

                            if (response.body() == null){
                                Message msg = new Message();
                                msg.arg1 = 1;
                                handler.sendMessage(msg);
                                return;
                            }

                            if (Security.verify(password, response.body().getUser_password())){

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Gson gson = new Gson();
                                String user = gson.toJson(response.body());
                                editor.putString(USER_KEY, user);
                                editor.commit();

                                Intent intent = new Intent(AuthActivity.this, UserHome.class);
                                startActivity(intent);
                            }else{
                                Message msg = new Message();
                                msg.arg1 = 1;
                                handler.sendMessage(msg);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserEntity> call, Throwable t) {
                            Message msg = new Message();
                            msg.arg1 = 1;
                            handler.sendMessage(msg);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewUser(String fullName, String email, String password){
        try {
            final UserEntity user = new UserEntity();
            user.setUser_status(userStatus);
            user.setUser_icon(CommandHelper.createIconForUser(
                    fullName.charAt(0), this));
            user.setUser_name(fullName);
            user.setUser_password(Security.convertPassMd5(password));
            user.setUser_email(email);

            repository.getApi().saveUser(user).enqueue(new Callback<UserEntity>() {
                @Override
                public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Gson gson = new Gson();
                    String user = gson.toJson(response.body());

                    editor.putString(USER_KEY, user);
                    editor.commit();

                    startLoginFragment();

                    //Intent intent = new Intent(AuthActivity.this, UserHome.class);
                    //startActivity(intent);
                }

                @Override
                public void onFailure(Call<UserEntity> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void emailIsExists(final String email, final String fullName, final String password){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean flag = repository.getApi().emailIsExists(email).execute().body();

                        existsEmail = flag;

                        if (flag){
                            Message msg = new Message();
                            msg.arg1 = 0;
                            handler.sendMessage(msg);
                        }else{
                            createNewUser(fullName, email, password);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }


            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

