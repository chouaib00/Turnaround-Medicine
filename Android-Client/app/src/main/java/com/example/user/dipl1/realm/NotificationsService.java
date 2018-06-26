package com.example.user.dipl1.realm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.user.dipl1.UserHome;
import com.example.user.dipl1.network.QueryRepository;
import com.example.user.dipl1.security.Security;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsService extends Service {

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private QueryRepository queryRepository;
    private int notificationsCounter = 0;

    public NotificationsService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("NS: ", "onCreate()");

        queryRepository = new QueryRepository();
        queryRepository.InitializeRetrofit(getApplicationContext());

        runNetworkTask();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NS: ", "onStartCommand(params...)");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("NS: ", "onDestroy()");
        mTimer.cancel();
    }

    private void runNetworkTask(){

        if(mTimer != null){
            mTimer.cancel();
        }

        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 60 * 1000);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            queryRepository.getApi().getUnreadNotificationsCount(Security.getCurrentUser(getApplicationContext()).getUser_id()).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    notificationsCounter = response.body();

                    try {
                        Intent intent = new Intent(UserHome.BROADCAST_ACTION);
                        intent.putExtra(UserHome.PARAM_COUNT, notificationsCounter);
                        sendBroadcast(intent);
                    }catch (Exception e){

                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    try {
                        Intent intent = new Intent(UserHome.BROADCAST_ACTION);
                        intent.putExtra(UserHome.PARAM_COUNT, notificationsCounter);
                        sendBroadcast(intent);
                    }catch (Exception e){

                    }
                }
            });
        }
    }
}
