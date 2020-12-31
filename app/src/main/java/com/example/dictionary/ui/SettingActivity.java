package com.example.dictionary.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dictionary.database.DatabaseHelper;
import com.example.dictionary.R;

public class SettingActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    TextView clearHistory;
    TextView About;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = findViewById(R.id.toolBar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        About = findViewById(R.id.about);

        clearHistory = findViewById(R.id.clearHistory);

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper = new DatabaseHelper(SettingActivity.this);
                try {
                    databaseHelper.openDatabase();
                }catch (SQLException e){
                    e.printStackTrace();
                }

                showAlertDialog();
            }
        });

        About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this,R.style.MyDialogTheme);
        builder.setTitle("Are you sure ???");
        builder.setMessage("All the history will be delete !!!");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteAllHistory();
                    }
                });
        String negativeText = "No";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
