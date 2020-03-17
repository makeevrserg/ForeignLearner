package com.blueberryinc.foreignlearner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.blueberryinc.foreignlearner.Adapters.Options;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mobi.gspd.segmentedbarview.Segment;
import mobi.gspd.segmentedbarview.SegmentedBarView;
import mobi.gspd.segmentedbarview.SegmentedBarViewSideStyle;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    Toolbar toolbar;

    private int STORAGE_PERMISSION_CODE = 1;
    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Тип занятия");

        Log.d(TAG, "onResume: "+mOptions.SELECTED_LANGUAGE);

        SetLanguageIcon();


    }
    public void SetLanguageIcon(){
        mOptions.Load_SELECTED_LANGUAGE();
        if (mOptions.SELECTED_LANGUAGE.equals(Options.JAPANESE))
            toolbar.setLogo(R.drawable.ic_japan);
        else
            toolbar.setLogo(R.drawable.ic_english);
    }
    BottomNavigationView bottomNav;
    Options mOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        mOptions = new Options(MainActivity.this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentWordList()).commit();
        }
        SetLanguageIcon();
//        SegmentedBarView barView=findViewById(R.id.segment_bar);
//
//        List<Segment> segments = new ArrayList<>();
//        Segment segment = new Segment(0, 25f, "Low", Color.parseColor("#9B1731"));
//        segments.add(segment);
//        Segment segment2 = new Segment(25f, 50f, "Optimal", Color.parseColor("#CE4B23"));
//        segments.add(segment2);
//        Segment segment3 = new Segment(50f, 100f, "High", Color.parseColor("#3A8B34"));
//        segments.add(segment3);
//
//
//        barView.setSegments(segments);
//        barView.setValue(50f);
//        barView.setShowDescriptionText(false);
//        barView.setShowSegmentText(false);
        CheckPermissionStorage();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    mOptions.Load_CURRNET_MENU();
                    String CURRENT_MENU="DICTIONARY";
                    switch (item.getItemId()) {
                        case R.id.nav_word_list:
                            //toolbar.setTitle("Список слов");
                            CURRENT_MENU = "DICTIONARY";
                            selectedFragment = new FragmentWordList();
                            break;
                        case R.id.nav_settings:
                            //toolbar.setTitle("Настройки");
                            CURRENT_MENU = "OPTIONS";
                            selectedFragment = new FragmentOptions();
                            break;
                    }
                    if (CURRENT_MENU.equals(mOptions.PREVIOUS_MENU))
                        return false;
                    mOptions.PREVIOUS_MENU=CURRENT_MENU;
                    mOptions.Save_CURRENT_MENU();
                    getSupportFragmentManager().
                            beginTransaction().
                            setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right,R.anim.exit_left_to_right).
                            replace(R.id.fragment_container,
                                    selectedFragment).addToBackStack(null).commit();
                    return true;
                }
            };


    private void CheckPermissionStorage(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Objects.requireNonNull(this),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
        }
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Необходимо разрешение ")
                    .setMessage("Необходимо разрешение на хранение и запись информации")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestStoragePermission();
                        }
                    })
                    .create().show();
        }
    }
    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale((this),
                Manifest.permission.READ_EXTERNAL_STORAGE)){

            ActivityCompat.requestPermissions((this),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE){
            Log.d(TAG, "onRequestPermissionsResult: RequestCode==");
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "grantResults.length>0");
            }
            else {
                finish();
                System.exit(0);
                Log.d(TAG, "grantResults.length<0");
            }
        }

    }
}
