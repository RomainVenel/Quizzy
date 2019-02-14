package com.quizzy.mrk.quizzy;

import android.os.Bundle;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Entities.Quiz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PassageQuizActivity extends AppCompatActivity {

    private Quiz quiz;
    private TextView tv_passageTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passage_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Allez les Bleus !");
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.quiz = getIntent().getExtras().getParcelable("quiz");
        this.tv_passageTest = findViewById(R.id.tv_passage_test);

        tv_passageTest.setText(quiz.getName());

    }

}
