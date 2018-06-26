package com.example.user.dipl1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class CustomFaceDetector {

    private FaceDetector faceDetector;
    private Context context;
    private Bitmap bitmap;
    private Bitmap resultBitmap;
    private Paint paint;
    private Canvas canvas;

    public CustomFaceDetector(Context context, Bitmap drawable){

        this.context = context;
        this.faceDetector = new FaceDetector.Builder(this.context)
                .setProminentFaceOnly(true)
                .setTrackingEnabled(false)
                .build();

        this.bitmap = drawable;
    }

    private void paintInit(){
        this.paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void canvasInit(){

        resultBitmap = Bitmap.createBitmap(
                bitmap.getWidth(),
                bitmap.getHeight(),
                Bitmap.Config.RGB_565
        );

        this.canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(this.bitmap, 0, 0, null);
    }

    public Bitmap hideFace(){

        paintInit();
        canvasInit();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        for(int i = 0; i < faces.size(); i++) {

            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            canvas.drawRect(new RectF(x1, y1, x2, y2),  paint);
        }

        return resultBitmap;
    }
}
