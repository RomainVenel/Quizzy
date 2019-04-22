package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.PartsModele;
import com.quizzy.mrk.quizzy.Modele.QuizModele;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

import java.util.ArrayList;

public class ResumQuizActivity extends AppCompatActivity {

    private Quiz quiz;
    private QuizModele quizModele;
    private PartsModele partsModele;
    private int cptParts;
    private int cptQuestions;
    private int timeQuiz;
    private RequestQueue requestQueue;
    private TextView tvResumTitle;
    private TextView tvResumNbParts;
    private TextView tvResumNbQuestions;
    private TextView tvResumTimer;
    private Button btnResumRunQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resum_quiz);

        this.quiz = getIntent().getExtras().getParcelable("quiz");
        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.quizModele = new QuizModele(this, this.requestQueue);
        this.partsModele = new PartsModele(this, this.requestQueue);

        this.tvResumNbParts = findViewById(R.id.tv_resum_nb_parts);
        this.tvResumNbQuestions = findViewById(R.id.tv_resum_nb_questions);
        this.tvResumTitle = findViewById(R.id.tv_resum_quiz_title);
        this.tvResumTimer = findViewById(R.id.tv_resum_timer);
        this.btnResumRunQuiz = findViewById(R.id.btn_resum_run_quiz);

        tvResumTitle.setText(quiz.getName());

        this.btnResumRunQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_quiz", false);
                paquet.putParcelable("quiz", quiz );
                Intent intent = new Intent(ResumQuizActivity.this, TransitionPassageQuizActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        quizModele.getParts(quiz, new QuizModele.getPartsQuizCallBack() {
            @Override
            public void onSuccess(ArrayList<Part> parts) {
                getQuestions(parts);
            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });

    }

    private void getQuestions(ArrayList<Part> parts) {

        cptParts = 0;
        cptQuestions = 0;
        timeQuiz = 0;

        for (Part part : parts) {
            this.partsModele.getQuestions(part, new PartsModele.getQuestionsCallBack() {
                @Override
                public void onSuccess(ArrayList<Question> questions) {
                    ArrayList<Question> listQuestions = new ArrayList<>();

                    for (Question question: questions) {
                        listQuestions.add(question);
                        cptQuestions++;
                        timeQuiz = timeQuiz + 8;
                        tvResumNbQuestions.setText(Integer.toString(cptQuestions));
                        tvResumTimer.setText(Integer.toString(timeQuiz));
                    }
                }

                @Override
                public void onErrorNetwork() {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_parts), R.string.error_connexion_http, 2500);
                    snackbar.show();
                }

                @Override
                public void onErrorVollet() {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_parts), R.string.error_vollet, 2500);
                    snackbar.show();
                }
            });

            cptParts++;
            tvResumNbParts.setText(Integer.toString(cptParts));

        }

    }

}
