package com.example.dictionary.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dictionary.database.DatabaseHelper;
import com.example.dictionary.Fragment.FragmentDef;
import com.example.dictionary.MainActivity;
import com.example.dictionary.R;
import com.example.dictionary.model.BookMark;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordMeaningAcitvity extends AppCompatActivity {

    String enWord;
    DatabaseHelper databaseHelper;
    Cursor cursor = null;

    public String vieDefinition;

    TextToSpeech textToSpeech;

    boolean startedFromShare = false;

    private ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbarMeaning;
    ImageButton imageButton;
    ImageButton btnBookMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning_acitvity);

        //Received values
        final Bundle bundle = getIntent().getExtras();
        enWord = bundle.getString("en_word");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null)
        {
            if("text/plain".equals(type))
            {
                String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
                startedFromShare = true;

                if(shareText != null)
                {
                    Pattern p = Pattern.compile("[A-Za-z \\-.]{1,25}");
                    Matcher m = p.matcher(shareText);

                    if(m.matches())
                    {
                        enWord = shareText;
                    }
                    else
                    {
                        enWord="Word Not Available :(((";
                    }
                }
            }
        }

        databaseHelper = new DatabaseHelper(this);
        try{
            databaseHelper.openDatabase();
        }catch (SQLException sQLE){
            throw sQLE;
        }

        cursor = databaseHelper.getMeaning(enWord);


        if(cursor.moveToFirst()){
            vieDefinition = cursor.getString(cursor.getColumnIndex("vie_definition"));
            databaseHelper.insertHistory(enWord);
        }
        else {
            enWord="Word Not Available :(((";
        }

        btnBookMark = findViewById(R.id.btnBookMark);

        btnBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBookMark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                addBookmark();
            }
        });


        imageButton = findViewById(R.id.btnSpeak);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(WordMeaningAcitvity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if ( status == TextToSpeech.SUCCESS){
                            int result = textToSpeech.setLanguage(Locale.getDefault());
                            if(result==TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("error","This Language is not supported");
                            }
                            else {
                                textToSpeech.speak(enWord, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else {
                            Log.e("error","Initialization Failed !!!");
                        }

                    }
                });
            }
        });

        // ToolBar

        toolbarMeaning = findViewById(R.id.toolBarWordMeaning);
        setSupportActionBar(toolbarMeaning);
        getSupportActionBar().setTitle(enWord);
        toolbarMeaning.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        viewPager = findViewById(R.id.tab_viewPager);

        //ViewPager and Tablayout

        if(viewPager != null){
            setupViewPager(viewPager);
        }

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void addBookmark() {
        try {
            List<BookMark> bookMarkList = databaseHelper.getAllBookMark("SELECT * FROM fav_group WHERE fav_word = '"+enWord+"'");
            if(enWord.equalsIgnoreCase(bookMarkList.get(0).getBookMark_word())) {
                Toast.makeText(this, "Your bookmark already exits", Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.insertBookMark(new BookMark(enWord));
                Toast.makeText(WordMeaningAcitvity.this, "Add to your Bookmark", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            databaseHelper.insertBookMark(new BookMark(enWord));
            Toast.makeText(WordMeaningAcitvity.this, "Add to your Bookmark", Toast.LENGTH_SHORT).show();
            Log.d(")))))))))))", e.toString());
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTittleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        void addFrag(Fragment fragment,String title){
            fragmentList.add(fragment);
            fragmentTittleList.add(title);
        }

        @Override
        public Fragment getItem(int position){
            return fragmentList.get(position);
        }

        @Override
        public int getCount(){
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            return fragmentTittleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentDef(),"Definition");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            if(startedFromShare){
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            else
            {
                onBackPressed();
            }
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
