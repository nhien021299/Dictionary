package com.example.dictionary.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import com.example.dictionary.MainActivity;
import com.example.dictionary.R;
import com.example.dictionary.database.DatabaseHelper;

import java.io.IOException;

public class LoadDatabaseAsync extends AsyncTask<Void,Void,Boolean> {

    private Context context;
    private AlertDialog alertDialog;
    private DatabaseHelper mDbHelper;

    public LoadDatabaseAsync(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AlertDialog.Builder d = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.alert_dialog_database, null);
        d.setTitle("Loading Database...");
        d.setView(dialogView);
        alertDialog = d.create();

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected  Boolean doInBackground(Void... params){
        mDbHelper = new DatabaseHelper(context);

        try {
            mDbHelper.createDatabase();
        }catch (IOException e){
            throw  new Error ("Database was not created");
        }
        mDbHelper.close();
        return null;
    }

    protected void onProgressUpdate(Void... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);
        alertDialog.dismiss();
        MainActivity.openDatabase();
    }
}
