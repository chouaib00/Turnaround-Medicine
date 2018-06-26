package com.example.user.dipl1.local_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public class DatabaseService {

    private DatabaseHelper sqlHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;
    private Context ctx;

    public SQLiteDatabase getDb() {
        return db;
    }

    public DatabaseService(Context ctx){
        sqlHelper = new DatabaseHelper(ctx);
        sqlHelper.create_db();
        db = sqlHelper.open();
    }

    public void savePhoto(List<PhotoTable> tableList){
        for (PhotoTable item:
             tableList) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_ID, item.getPhoto_id());
            cv.put(DatabaseHelper.COLUMN_PHOTO, item.getPhoto());
            cv.put(DatabaseHelper.COLUMN_DATE, item.getPhoto_date());
            db.insert(DatabaseHelper.TABLE, null, cv);
        }
    }

    public void addPostToFavorites(int postId){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_FAVORITE_POST, postId);
        db.insert(DatabaseHelper.TABLE_FAVORITES, null, cv);
    }
    public Cursor getPhotos(){
        userCursor = db.rawQuery("select *from " + DatabaseHelper.TABLE, null);
        return userCursor;
    }

    public void onDestroy(){
        userCursor.close();
    }

    public void closeDB(){
        db.close();
    }

    public boolean isContains(){
        Cursor cursor = db.rawQuery("select *from " + DatabaseHelper.TABLE, null);
        int cnt = cursor.getCount();
        cursor.close();

        if(cnt > 0)
            return true;
        else
            return false;
    }
}
