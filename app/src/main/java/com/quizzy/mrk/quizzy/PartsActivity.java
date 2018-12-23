package com.quizzy.mrk.quizzy;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Entities.Quiz;

import java.util.ArrayList;

public class PartsActivity extends AppCompatActivity {

    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_parts));

        //this.quiz = getIntent().getExtras().getParcelable("quiz");


    }
}
