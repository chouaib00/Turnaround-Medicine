package com.example.user.dipl1.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.user.dipl1.R;
import com.example.user.dipl1.UserHome;
import com.example.user.dipl1.auth.AuthActivity;
import com.example.user.dipl1.entity.UserEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import static android.content.Context.MODE_PRIVATE;

public class Security {

    //private static final String SALT = "h9FTR2n5Qe6O";

    //Copyright
    private static Bitmap originalForCopyright;
    private static Bitmap copyrightBitmap;
    private static Paint paint;
    private static Canvas canvas;

    private Security() {}

    public static String convertPassMd5(String pass) {
        String passwordWithSalt = pass; //+ SALT;
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(passwordWithSalt.getBytes(), 0, passwordWithSalt.length());
            passwordWithSalt = new BigInteger(1, mdEnc.digest()).toString(16);
            while (passwordWithSalt.length() < 32) {
                passwordWithSalt = "0" + passwordWithSalt;
            }
            password = passwordWithSalt;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    public static boolean verify(String inputPassword, String userPassword){
        //return convertPassMd5(inputPassword + SALT).equals(userPassword) ? true : false;
        return convertPassMd5(inputPassword).equals(userPassword) ? true : false;
    }

    public static UserEntity getCurrentUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences(AuthActivity.USER_KEY, MODE_PRIVATE);
        String savedUser = preferences.getString(AuthActivity.USER_KEY, null);
        Gson gson = new Gson();
        return gson.fromJson(savedUser, UserEntity.class);
    }

    public static void signOut(Context context, Activity activity){
        SharedPreferences preferences = context.getSharedPreferences(AuthActivity.USER_KEY, MODE_PRIVATE);
        preferences.edit().remove(AuthActivity.USER_KEY).commit();
        context.startActivity(new Intent(activity, AuthActivity.class));
    }

    public static void autoSignIn(Context context, Activity activity){
        SharedPreferences preferences = context.getSharedPreferences(AuthActivity.USER_KEY, MODE_PRIVATE);

        if (preferences.getString(AuthActivity.USER_KEY, null) != null){
            context.startActivity(new Intent(activity, UserHome.class));
        }
    }

    private static void paintInit(){
        paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.GRAY);
        paint.setAlpha(30);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(30);
    }

    private static void canvasInit(){

        copyrightBitmap = Bitmap.createBitmap(
                originalForCopyright.getWidth(),
                originalForCopyright.getHeight(),
                Bitmap.Config.RGB_565
        );

        canvas = new Canvas(copyrightBitmap);
        canvas.drawBitmap(originalForCopyright, 0, 0, null);
    }

    public static Bitmap createCopyrightBitmap(Bitmap original){
        originalForCopyright = original;

        paintInit();
        canvasInit();

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText("@Copyright 2018", xPos, yPos, paint);

        return copyrightBitmap;
    }

    //SSL
    public static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Загрузка ЦС из InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        try (InputStream cert = context.getResources().openRawResource(R.raw.vs)) {
            ca = cf.generateCertificate(cert);
        }

        // Создание KeyStore, содержащего наши доверенные центры сертификации
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("tomcat", ca);

        // Создание TrustManager, который доверяет ЦС в нашем KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Создание SSLSocketFactory, использующего наш TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }
}
