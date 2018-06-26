package com.example.user.dipl1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.dipl1.custom_ui.CustomEditText;
import com.example.user.dipl1.custom_ui.DrawableClickListener;
import com.example.user.dipl1.entity.UserEntity;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;
import com.example.user.dipl1.utils.CommandHelper;
import com.example.user.dipl1.utils.UserStatus;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class UserSettingsFragment extends Fragment {

    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder = null;
    MenuItem menuItem;

    EditText userName;
    EditText userEmail;
    CustomEditText userAdr;
    EditText phoneNumber;

    LoginButton loginButton;
    CallbackManager mFacebookCallbackManager;


    SignInButton signInButton;
    GoogleApiClient mGoogleApiClient;

    LinearLayout profDataPanel;
    EditText specialField;
    ImageButton fileUnload;

    QueryRepository repository;

    UserEntity currentUser;
    String facebookId = "";
    String googleId = "";
    private static final int RC_SIGN_IN = 9001;

    private static final int REQUEST = 111;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            currentUser = Security.getCurrentUser(getContext());

            FacebookSdk.sdkInitialize(getApplicationContext());
            mFacebookCallbackManager = CallbackManager.Factory.create();
        }catch(Exception e){}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        try {
            profDataPanel = (LinearLayout) view.findViewById(R.id.profDataPanel);

            if (currentUser.getUser_status().equals(UserStatus.USER)) {
                profDataPanel.setVisibility(View.INVISIBLE);
            }

            userAdr = (CustomEditText) view.findViewById(R.id.userAdrText);
            userName = (EditText) view.findViewById(R.id.userNameField);
            userEmail = (EditText) view.findViewById(R.id.userEmailField);
            phoneNumber = (EditText) view.findViewById(R.id.mobileNumber);
            loginButton = (LoginButton) view.findViewById(R.id.fcbConnectBtn);
            signInButton = (SignInButton) view.findViewById(R.id.gglConnectBtn);
            specialField = (EditText) view.findViewById(R.id.specialField);
            fileUnload = (ImageButton) view.findViewById(R.id.fileUnloadBtn);

            fileUnload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setType("image/*");
                    startActivityForResult(i, REQUEST);
                }
            });

            userAdr.setText(currentUser.getUser_adress() != null ? currentUser.getUser_adress() : "");
            userName.setText(currentUser.getUser_name());
            userEmail.setText(currentUser.getUser_email());
            phoneNumber.setText(currentUser.getUser_phone_number() != null ? currentUser.getUser_phone_number() : "");
            specialField.setText(currentUser.getUser_work() != null ? currentUser.getUser_work() : "");

            facebookId = currentUser.getFacebook_id();
            googleId = currentUser.getGoogle_id();

            loginButton.setReadPermissions("email");
            loginButton.registerCallback(mFacebookCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {
                            facebookId = loginResult.getAccessToken().getUserId();
                            Toast.makeText(getContext(), "Привязка к Facebook осуществлена успешно", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    }
            );

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInWithGoogle();
                }
            });


            repository = new QueryRepository();
            repository.InitializeRetrofit(getContext().getApplicationContext());

            userAdr.setDrawableClickListener(new DrawableClickListener() {
                @Override
                public void onClick(DrawablePosition target) {
                    switch (target) {
                        case LEFT:
                            builder = new PlacePicker.IntentBuilder();
                            try {
                                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                            break;

                        default:
                            break;
                    }
                }
            });

            return view;
        }catch(Exception e){
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PLACE_PICKER_REQUEST) {
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(getContext(), data);
                    String placeName = place.getAddress().toString();
                    userAdr.setText(placeName);
                }
            } else if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    googleId = account.getId();
                    Toast.makeText(getContext(), "Привязка к Google осуществлена успешно", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == REQUEST && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap img = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                    Bitmap cImg = Security.createCopyrightBitmap(img);
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    cImg.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                    currentUser.setSertificate(bs.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }catch(Exception e){

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            super.onCreateOptionsMenu(menu, inflater);
            menuItem = menu.add("Save");
            menuItem.setIcon(R.drawable.zzz_content_save_settings);

            menuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    try {
                        updateUserData();
                        return true;
                    }catch (Exception e){
                        return false;
                    }

                }
            });
        }catch(Exception e){

        }
    }

    private void updateUserData(){
        currentUser.setUser_name(userName.getText().toString());
        currentUser.setUser_email(userEmail.getText().toString());
        currentUser.setUser_adress(userAdr.getText().toString());
        currentUser.setUser_icon(CommandHelper.createIconForUser(
                userName.getText().toString().charAt(0),
                getContext()
        ));

        currentUser.setUser_phone_number(phoneNumber.getText().toString());
        currentUser.setUser_work(specialField.getText().toString());

        currentUser.setFacebook_id(facebookId);
        currentUser.setGoogle_id(googleId);

        repository.getApi().updateUserData(currentUser).enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                postToastMessage("Данные успешно обновлены");
            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                postToastMessage("Произошла ошибка " + t.getMessage());
            }
        });
    }

    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithGoogle() {
        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }catch(Exception e){

        }
    }
}
