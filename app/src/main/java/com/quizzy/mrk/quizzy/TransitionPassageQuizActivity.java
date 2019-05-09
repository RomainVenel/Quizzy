package com.quizzy.mrk.quizzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.QuizModele;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

import java.util.ArrayList;
import java.util.Comparator;

import androidx.appcompat.app.AppCompatActivity;

public class TransitionPassageQuizActivity extends AppCompatActivity {

    private TextView tv_debut_timer;
    private Quiz quiz;
    private static int TIME_OUT = 4000; //Time to launch the another activity
    private QuizModele quizModele;
    private ArrayList<Part> listParts;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_passage_quiz);

        this.tv_debut_timer = findViewById(R.id.tv_debut_timer);
        this.quiz = getIntent().getExtras().getParcelable("quiz");
        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.quizModele = new QuizModele(this, this.requestQueue);

        listParts = new ArrayList<>();

        quizModele.getParts(quiz, new QuizModele.getPartsQuizCallBack() {
            @Override
            public void onSuccess(ArrayList<Part> parts) {
                for (Part part : parts) {
                    listParts.add(part);
                }
            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });

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

                    Intent intent = new Intent(TransitionPassageQuizActivity.this, PartPassageQuizActivity.class);

                    intent.putExtra("listParts" ,listParts);
                    Log.d("APP", "REAL PART ==> " + listParts);
                    startActivity(intent);

                    TransitionPassageQuizActivity.this.finish();
            }
        }, TIME_OUT);
    }

    public class QuestionIdComparator implements Comparator<Question>
    {
        @Override
        public int compare(Question question1, Question question2) {
            return question1.getId() - question2.getId();
        }
    }
}
