package com.example.user.dipl1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class CommandHelper {

    private CommandHelper() {}

    public static Bitmap decodeSampledBitmapFromResource(byte[] pic,
                                                  int reqWidth, int reqHeight) {

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(pic, 0, pic.length, options);

            // Вычисляем inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Читаем с использованием inSampleSize коэффициента
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        } catch (Exception e) {
            e.printStackTrace();
            return BitmapFactory.decodeByteArray(pic, 0, pic.length);
        }
        return BitmapFactory.decodeByteArray(pic, 0, pic.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static void replaceFragment(Class fragmentClass, FragmentActivity activity,
                                       int container, @Nullable Bundle bundle){

        Fragment fragment = null;

        try{

            fragment = (Fragment) fragmentClass.newInstance();

            if (bundle != null) {
                fragment.setArguments(bundle);
            }

            FragmentTransaction transaction = null;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void replaceFragment(Class fragmentClass,  FragmentManager fragmentManager,
                                       int container, @Nullable Bundle bundle){

        Fragment fragment = null;

        try{

            fragment = (Fragment) fragmentClass.newInstance();

            if (bundle != null) {
                fragment.setArguments(bundle);
            }

            FragmentTransaction transaction = null;
            transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] createIconForUser(char userSurname, Context context){
        Bitmap bitmap = new DrawView(context, userSurname).bitmap;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
        return bs.toByteArray();
    }

    private static class DrawView extends View {

        Paint paint;
        Bitmap bitmap;
        char word;

        public DrawView(Context context, char word) {
            super(context);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.word = word;

            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(bitmap);
            Paint p = new Paint();
            p.setTextSize(100); //140
            p.setColor(Color.WHITE);
            p.setTextAlign(Paint.Align.CENTER);

            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((p.descent() + p.ascent()) / 2)) ;

            canvas.drawARGB(1, 127, 26, 229);
            canvas.drawText(String.valueOf(getWord()), xPos, yPos, p); //50 100
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.drawARGB(1, 127, 26, 229);
            canvas.drawBitmap(bitmap, 50, 100, paint);
        }

        public char getWord() {
            return word;
        }

        public void setWord(char word) {
            this.word = word;
        }
    }


}
