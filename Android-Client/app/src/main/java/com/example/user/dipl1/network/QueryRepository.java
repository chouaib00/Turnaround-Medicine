package com.example.user.dipl1.network;

import android.content.Context;

import com.example.user.dipl1.MyApplication;
import com.example.user.dipl1.security.Security;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class QueryRepository {
    private Retrofit retrofit;
    private MedicalAPI api;

    public static final String BASE_URL = "http://env-7919692.mycloud.by/";

    public void InitializeRetrofit(Context context){
        OkHttpClient okHttp = null;
        try{
            okHttp = new OkHttpClient()
                    .newBuilder()
                    .sslSocketFactory(Security.getSSLConfig(context).getSocketFactory())
            .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    //.client(okHttp)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            api = retrofit.create(MedicalAPI.class);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public MedicalAPI getApi(){
        return api;
    }
}
