package com.example.dictionary.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.dictionary.adapter.BookMarkAdapter;
import com.example.dictionary.database.DatabaseHelper;
import com.example.dictionary.R;


public class BookMarkActivity extends AppCompatActivity {

    Toolbar toolbar;

    static DatabaseHelper databaseHelper;

    RecyclerView bookMarkRecyclerView;

    RelativeLayout emptyBookMark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_activity);
        initView();
        setToolbar();
        databaseHelper = new DatabaseHelper(this);
        try{
            databaseHelper.openDatabase();
        }catch (SQLException sQLE){
            throw sQLE;
        }
        setAdapter();
    }

    private void setAdapter() {
        BookMarkAdapter bookMarkAdapter = new BookMarkAdapter(databaseHelper.getAllBookMark("SELECT * FROM fav_group"), getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        bookMarkRecyclerView.setLayoutManager(layoutManager);
        bookMarkRecyclerView.setAdapter(bookMarkAdapter);

        if(bookMarkAdapter.getItemCount() != 0) {
            emptyBookMark.setVisibility(View.GONE);
        } else {
            emptyBookMark.setVisibility(View.VISIBLE);
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolBarBookMark);
        emptyBookMark = findViewById(R.id.emptyBookMark);
        bookMarkRecyclerView = findViewById(R.id.bookMarkRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
