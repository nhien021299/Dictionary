package com.example.dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.adapter.HistoryAdapter;
import com.example.dictionary.database.DatabaseHelper;
import com.example.dictionary.helper.LoadDatabaseAsync;
import com.example.dictionary.model.History;
import com.example.dictionary.ui.BookMarkActivity;
import com.example.dictionary.ui.SettingActivity;
import com.example.dictionary.ui.WordMeaningAcitvity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity{

//    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    Toolbar toolbar;
    SearchView searchView;
    ImageButton btnVoice ;

    static DatabaseHelper databaseHelper;
    static boolean databaseOpened = false;

    SimpleCursorAdapter suggestionAdapter;

    List<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;

    RelativeLayout emptyHistory;
    Cursor cursorHistory;

    public String click_word;

    boolean doubleBackToExitPressedOncee = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        btnVoice = findViewById(R.id.btnVoice);
        searchView = (SearchView) findViewById(R.id.searchView);

        //Speech to text
//        btnVoice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                speak();
//            }
//        });

        // Onclick in SearchView

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchView.setIconified(false);

            }
        });

        // Check and OpenDatabase

        databaseHelper = new DatabaseHelper(this);

        if(databaseHelper.checkDatabase()){
            openDatabase();
        }
        else {
            LoadDatabaseAsync loadDatabaseAsync = new LoadDatabaseAsync(MainActivity.this);
            loadDatabaseAsync.execute();
        }

        // setup SimpleCusorAdapter

        final String[] from = new String[] {"en_word"};
        final int[] to = new  int[] {R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.suggestion_row, null, from, to,0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };

        searchView.setSuggestionsAdapter(suggestionAdapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {


                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);
                click_word = cursor.getString(cursor.getColumnIndex("en_word"));
                searchView.setQuery(click_word,false);

                searchView.clearFocus();
                searchView.setFocusable(false);

                Intent intent = new Intent(MainActivity.this, WordMeaningAcitvity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",click_word);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = searchView.getQuery().toString();

                Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher m = p.matcher(text);

                if(m.matches()){
                    Cursor cursor = databaseHelper.getMeaning(text);
                    if(cursor.getCount()==0)
                    {
                        showAlertDialog();

                    }else {
                        searchView.clearFocus();
                        searchView.setFocusable(false);
                        Intent intent = new Intent(MainActivity.this, WordMeaningAcitvity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("en_word",text);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }

                }
                else
                {
                    showAlertDialog();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {

                searchView.setIconifiedByDefault(false);

                Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                Matcher m = p.matcher(s);

                if(m.matches()){
                    Cursor cursorSuggestion= databaseHelper.getSuggestion(s);
                    suggestionAdapter.changeCursor(cursorSuggestion);
                }

                return false;
            }
        });

        emptyHistory = findViewById(R.id.emptyHistory);

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        fetch_history();
    }

    public void fetch_history(){
//        historyList = databaseHelper.getAllHistory("SELECT * FROM history");
//        historyAdapter = new HistoryAdapter(historyList,this);
//        if(historyAdapter.getItemCount() != 0) {
//            emptyHistory.setVisibility(View.GONE);
//        } else {
//            emptyHistory.setVisibility(View.VISIBLE);
//        }
//
//        recyclerView.setAdapter(historyAdapter);
//        historyAdapter.notifyDataSetChanged();

        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList, this);
        recyclerView.setAdapter(historyAdapter);
        History history;

        if(databaseOpened){
            cursorHistory = databaseHelper.getHistory();
            if(cursorHistory.moveToFirst()){
                do {
                    history = new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")));
                    historyList.add(history);
                }while (cursorHistory.moveToNext());
            }

            historyAdapter.notifyDataSetChanged();

            if(historyAdapter.getItemCount()==0){
                emptyHistory.setVisibility(View.VISIBLE);
            }
            else {
                emptyHistory.setVisibility(View.GONE);
            }
        }

    }

    private void showAlertDialog()
    {
        searchView.setQuery("",false);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        builder.setTitle("Word Not Found :(((");
        builder.setMessage("Please search again !!!");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
//        String negativeText = getString(android.R.string.cancel);
//        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                searchView.clearFocus();
//            }
//        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void openDatabase()
    {
        try {
            databaseHelper.openDatabase();
            databaseOpened=true;
        }catch (SQLException e ){
            e.printStackTrace();
        }
    }

//    private void speak(){
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");
//
//
//        try{
//            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//        }
//        catch (ActivityNotFoundException a){
//            Toast.makeText(getApplicationContext(),
//                    "error",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode){
//            case REQUEST_CODE_SPEECH_INPUT:{
//                if(requestCode == RESULT_OK && data!= null){
//                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    searchView.setQuery(result.get(0),false);
//                }
//                break;
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }

    // Create Option Menuuuu.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.settings){
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.exit){
            System.exit(0);
            return true;
        }

        if(id == R.id.bookMark)
        {
            Intent intent = new Intent(MainActivity.this, BookMarkActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetch_history();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOncee){
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOncee = true;
        Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOncee=false;
            }
        }, 2000);
    }
}
