package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.QuizModele;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

public class MonQuizOptionsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Quiz quiz;
    private Button bDeleteQuiz;
    private QuizModele mQuizModele;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_quiz_options);

        this.quiz = getIntent().getExtras().getParcelable("quiz");
        this.bDeleteQuiz = findViewById(R.id.btn_mon_quiz_delete);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.mQuizModele = new QuizModele(this, this.requestQueue);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(quiz.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.bDeleteQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuizModele.deleteQuiz(quiz, new QuizModele.deleteQuizCallBack() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(MonQuizOptionsActivity.this, MesQuizActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onErrorNetwork() {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.activity_mes_quiz), R.string.error_connexion_http, 2500);
                        snackbar.show();
                    }

                    @Override
                    public void onErrorVollet() {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.activity_mes_quiz), R.string.error_vollet, 2500);
                        snackbar.show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MonQuizOptionsActivity.this, MesQuizActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
