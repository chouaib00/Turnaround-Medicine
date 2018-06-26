package com.example.user.dipl1;

import android.app.Application;

import com.example.user.dipl1.entity.UserEntity;

import java.util.concurrent.Callable;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    public static UserEntity userEntity;
    private static MyApplication ourInstance = new MyApplication();

    public static MyApplication getInstance() {
        return ourInstance;
    }

    private Callable<Void> mLogoutCallable;

    public void setLogoutCallable(Callable<Void> callable)  {
        mLogoutCallable = callable;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Realm.init(this);
            RealmConfiguration config = new RealmConfiguration.Builder().name("medical.realm").build();
            Realm.setDefaultConfiguration(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
