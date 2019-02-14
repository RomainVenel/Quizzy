package com.quizzy.mrk.quizzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Entities.Quiz;

import androidx.appcompat.app.AppCompatActivity;

public class TransitionPassageQuizActivity extends AppCompatActivity {
    private TextView tv_debut_timer;
    private Quiz quiz;
    private static int TIME_OUT = 4000; //Time to launch the another activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_passage_quiz);

        this.tv_debut_timer = findViewById(R.id.tv_debut_timer);
        this.quiz = getIntent().getExtras().getParcelable("quiz");

        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_debut_timer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {

            }
        };

        countDownTimer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Bundle paquet = new Bundle();
                paquet.putBoolean("new_quiz", false);
                paquet.putParcelable("quiz", quiz );
                Intent intent = new Intent(TransitionPassageQuizActivity.this, PassageQuizActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
                TransitionPassageQuizActivity.this.finish();
            }
        }, TIME_OUT);
    }
}
