package com.newsvarta.dvimaniya.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.newsvarta.dvimaniya.lists.NewsList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 01-08-2016.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TAG="DBHelper";
    // Database Name
    private static final String DATABASE_NAME = "CityVarta";

    // Contacts table name
    private static final String TABLE_POSTS = "cv_posts";
    // Contacts Table Columns names
    private static final String KEY_ID = "id"; //0
    private static final String IMG_URL = "img_url"; //1
    private static final String SOURCE_URL = "source_URL"; //2
    private static final String P_TITLE = "post_title"; //3
    private static final String P_TEXT = "post_text"; //4
    private static final String SOURCE_NAME = "source_name"; //5
    private static final String TIMESTAMP = "TIMESTAMP"; //6
    private static final String LAST_UPDATED_TIME = "time"; //7

    private static DataBaseHandler instance;

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataBaseHandler getHelper(Context context){
        if (instance == null){
             instance = new DataBaseHandler(context);
            }
        return instance;
        }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_POSTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + IMG_URL + " TEXT," + SOURCE_URL + " TEXT,"
                + P_TITLE + " TEXT," + P_TEXT + " TEXT," + SOURCE_NAME + " TEXT," +
                TIMESTAMP + " TEXT," + LAST_UPDATED_TIME + " TEXT"+ ")";
        db.execSQL(CREATE_POSTS_TABLE);
    }

    public List<NewsList> getPosts(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<NewsList> newsList = new ArrayList<NewsList>();
        String newsListQuery = "SELECT * FROM " + TABLE_POSTS;
        Cursor cursor = db.rawQuery(newsListQuery,null);
        if(cursor.moveToFirst()){
            do {
                NewsList newsList1 = new NewsList();
                newsList1.setThumbnail(cursor.getString(1));
                newsList1.setSourceUrl(cursor.getString(2));
                newsList1.setTitle(cursor.getString(3));
                newsList1.setNews(cursor.getString(4));
                newsList1.setSource(cursor.getString(5));
                newsList1.setTimeStamp(cursor.getString(6));
                newsList1.setUpdated(cursor.getString(7));
                newsList.add(newsList1);
                //Log.e(TAG +"NEWS LIST", newsList.toString());

            }while (cursor.moveToNext());
        }
        return newsList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);

        // Create tables again
        onCreate(db);
    }

    public void addPosts(NewsList newsList){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMG_URL, newsList.getThumbnail());
        values.put(SOURCE_URL, newsList.getSourceUrl());
        values.put(P_TITLE, newsList.getTitle());
        values.put(P_TEXT, newsList.getNews());
        values.put(SOURCE_NAME, newsList.getSource());
        values.put(TIMESTAMP, newsList.getSourceUrl());
        values.put(LAST_UPDATED_TIME, newsList.getUpdated());

        db.insert(TABLE_POSTS,null,values);
        Log.e(TAG +"INSRTNG VALUES",values.toString());
        db.close();
    }

    public String getLastSync(){
        String sync="0";
        String syncQuery = "SELECT MAX("+ LAST_UPDATED_TIME +") FROM " + TABLE_POSTS;
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            Cursor cursor = db.rawQuery(syncQuery, null);
            if(cursor.moveToFirst()){
                do {
                    sync = cursor.getString(0);
                }while (cursor.moveToNext());
            }
        }catch(SQLiteException e){
            String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
            Log.e(TAG, err);
        }


        if(sync!=null){
            Log.e(TAG +"LAST SYNC", sync);
            return sync;
        }else{
            return "0";
        }
    }


}
