package com.example.dictionary.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dictionary.model.BookMark;
import com.example.dictionary.model.History;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private String DB_PATH = null;
    private static String DB_NAME = "DicAnhViet.db";
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,1);
        this.mContext = context;
        this.DB_PATH = "/data/data/" + context.getOpPackageName() + "/" + "databases/";
        Log.e("Path 1 ", DB_PATH);
    }

    public void createDatabase() throws IOException{
        boolean dbExist = checkDatabase();
        if(!dbExist){
            this.getReadableDatabase();
            try {
                copyDatabase();
            }catch (IOException e){
                throw new Error("Error copying database");
            }
        }
    }
    public boolean checkDatabase(){
        SQLiteDatabase checkDatabase = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLException e ){

        }
        if ( checkDatabase != null)
        {
            checkDatabase.close();
        }
        return checkDatabase != null ? true : false;
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream outputStream = new FileOutputStream(outFileName);
        byte[] buffer = new byte[64];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer,0 , length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void openDatabase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath , null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close(){
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i , int i1){
        try{
            this.getReadableDatabase();
            mContext.deleteDatabase(DB_NAME);
            copyDatabase();
        }catch (IOException e ){
            e.printStackTrace();
        }
    }

    public Cursor getMeaning(String text)
    {
//        Cursor cursor = mDataBase.rawQuery("SELECT vie_definition FROM AnhVietDic WHERE en_word==UPPER('"+text+"')",null);
        Cursor cursor = mDataBase.rawQuery("SELECT vie_definition FROM AnhVietDic WHERE en_word LIKE '"+text+"'",null);
        return cursor;
    }

    public Cursor getSuggestion(String text)
    {
        Cursor cursor = mDataBase.rawQuery("SELECT _id, en_word FROM AnhVietDic WHERE en_word LIKE '"+text+"%' LIMIT 40",null);
        return cursor;
    }
    public void insertHistory(String text)
    {
        mDataBase.execSQL("INSERT INTO history(word) VALUES('"+text+"')");
    }

    public Cursor getHistory()
    {
        Cursor cursor = mDataBase.rawQuery("SELECT DISTINCT word FROM history ORDER BY _id DESC", null);
        return  cursor;
    }

    public void deleteAllHistory()
    {
        mDataBase.execSQL("DELETE FROM history");
    }



    public int insertBookMark(BookMark bookMark) {
        mDataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fav_word", bookMark.getBookMark_word());
        try {
            if(mDataBase.insert("fav_group", null, values) < 0) {
                return -1;
            }
        } catch (Exception e) {
            Log.d("err", e.toString());
        }
        return 1;
    }

    public List<History> getAllHistory(String sql) {
        mDataBase = this.getWritableDatabase();
        List<History> bookMarks = new ArrayList<>();
        Cursor cursor = mDataBase.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            History history = new History();
            history.setId(cursor.getInt(0));
            history.setEnglish_word(cursor.getString(1));
            bookMarks.add(history);
            cursor.moveToNext();
        }
        cursor.close();
        return bookMarks;
    }

    public Cursor getBookMark()
    {
        Cursor cursor = mDataBase.rawQuery("SELECT fav_word FROM fav_group", null);
        return  cursor;
    }

    public void deleteBookMark(BookMark bookMark) {
        String[] id = {String.valueOf(bookMark.getId())};
        mDataBase = this.getWritableDatabase();
        mDataBase.delete("fav_group", "_id = ?", id);
    }

    public List<BookMark> getAllBookMark(String sql) {
        mDataBase = this.getWritableDatabase();
        List<BookMark> bookMarks = new ArrayList<>();
        Cursor cursor = mDataBase.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BookMark bookMark = new BookMark();
            bookMark.setId(cursor.getInt(0));
            bookMark.setBookMark_word(cursor.getString(1));
            bookMarks.add(bookMark);
            cursor.moveToNext();
        }
        cursor.close();
        return bookMarks;
    }
}
