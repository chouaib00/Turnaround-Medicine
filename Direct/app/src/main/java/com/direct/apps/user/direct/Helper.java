package com.direct.apps.user.direct;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Helper {

    private Helper(){}

    public static Bitmap createIconForUser(char userSurname, Context context){
        Bitmap bitmap = new DrawView(context, userSurname).bitmap;
      /*  ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);*/
        return bitmap;
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
