package com.example.dictionary.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.dictionary.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbarAbout;

        toolbarAbout = findViewById(R.id.toolBar_about);
        setSupportActionBar(toolbarAbout);
        getSupportActionBar().setTitle("About");

        toolbarAbout.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
