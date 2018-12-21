package com.quizzy.mrk.quizzy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Entities.Quiz;

public class PartsActivity extends AppCompatActivity {

    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts);

        this.quiz = getIntent().getExtras().getParcelable("quiz");

        TextView tvTest = findViewById(R.id.test);
        tvTest.setText(String.valueOf(quiz.getUser().getFirstName()));

    }
}
