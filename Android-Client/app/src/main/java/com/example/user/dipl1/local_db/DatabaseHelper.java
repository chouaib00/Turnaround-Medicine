package com.example.user.dipl1.local_db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Параметры БД
    private static String DB_PATH; // полный путь к базе данных
    private static String DB_NAME = "medical_local_db.db";
    private static final int SCHEMA = 1; // версия базы данных
    public static final String TABLE = "photos"; // название таблицы в бд
    public static final String TABLE_FAVORITES = "favorites";

    // Названия столбцов
    public static final String COLUMN_ID = "photo_id";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_DATE = "photo_date";

    public static final String COLUMN_FID = "_id";
    public static final String COLUMN_FAVORITE_POST = "favorite_post";

    private Context myContext;

    //Конструктор
    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext=context;
        DB_PATH =context.getFilesDir().getPath() + DB_NAME;
    }

    // Унаследованные методы (пустые, т.к. исп. существующую версию БД)
    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    void create_db(){
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                String outFileName = DB_PATH;

                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        }
        catch(IOException ex){
            Log.d("DatabaseHelper", ex.getMessage());
        }
    }

    public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
