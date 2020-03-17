package com.blueberryinc.foreignlearner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.r0adkll.slidr.Slidr;

public class LessonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Intent intent = getIntent();
        Slidr.attach(this);
        int LESSON_TYPE=intent.getIntExtra("LESSON_TYPE",0);
        switch (LESSON_TYPE){
            case 0:
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right,R.anim.exit_left_to_right).
                        replace(R.id.fragment_container,
                                new FragmentFindForeignToWord()).commit();
                break;
            case 1:
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right,R.anim.exit_left_to_right).
                        replace(R.id.fragment_container,
                                new FragmentFindWordToForeign()).commit();
                break;
            case 2:
                getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right,R.anim.exit_left_to_right).
                        replace(R.id.fragment_container,
                                new FragmentMatchWords()).commit();
                break;
        }
    }
}
