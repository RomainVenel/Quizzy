package com.quizzy.mrk.quizzy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Fragments.PageAdapter;
import com.quizzy.mrk.quizzy.Fragments.QuestionPassageQuizFragment;
import com.quizzy.mrk.quizzy.Modele.PartsModele;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class PartTransitionQuizActivity extends AppCompatActivity {

    private Quiz quiz;
    private RequestQueue requestQueue;
    private PartsModele partsModele;
    private ArrayList<Part> listParts;
    private ArrayList<Question> listQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_transition_quiz);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.partsModele = new PartsModele(this, this.requestQueue);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PARTIE 1 !");
        actionBar.setDisplayHomeAsUpEnabled(false);

        Bundle data = getIntent().getExtras();
        ArrayList<Part> parts  = data.getParcelableArrayList("listParts");
        listParts = parts;
        listQuestions = new ArrayList<>();

        for (Part part : parts) {

            partsModele.getQuestions(part, new PartsModele.getQuestionsCallBack() {
                @Override
                public void onSuccess(ArrayList<Question> questions) {
                    for (Question question : questions) {

                        listQuestions.add(question);
                    }

                    Collections.sort(listQuestions, new QuestionIdComparator());
                }

                @Override
                public void onErrorNetwork() {

                }

                @Override
                public void onErrorVollet() {

                }
            });

        }

        Intent intent = new Intent(PartTransitionQuizActivity.this, QuestionPassageQuizFragment.class);

        intent.putExtra("listParts" ,listParts);

        Runnable r = new Runnable() {
            @Override
            public void run(){
                configureViewPager(listParts, listQuestions); //<-- put your code in here.
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 1000); // <-- the "1000" is the delay time in miliseconds.

    }

    public class QuestionIdComparator implements Comparator<Question>
    {
        @Override
        public int compare(Question question1, Question question2) {
            return question1.getId() - question2.getId();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Voulez-vous vraiment quitter?")
                .setMessage("Vous ne pourrez plus repasser le quiz?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(PartTransitionQuizActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }
                }).create().show();
    }

    private void configureViewPager(ArrayList<Part> parts, ArrayList<Question> questions){
        // 1 - Get ViewPager from layout
        ViewPager pager = findViewById(R.id.viewpager);
        // 2 - Set Adapter PageAdapter and glue it together
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), getResources().getIntArray(R.array.colorPagesViewPager), parts, questions) {
        });
    }
}
