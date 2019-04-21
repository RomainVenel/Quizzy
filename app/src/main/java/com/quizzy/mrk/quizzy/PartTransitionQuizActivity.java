package com.quizzy.mrk.quizzy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Fragments.PageAdapter;
import com.quizzy.mrk.quizzy.Fragments.QuestionPassageQuizFragment;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class PartTransitionQuizActivity extends AppCompatActivity {

    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_transition_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PARTIE 1 !");
        actionBar.setDisplayHomeAsUpEnabled(false);

        Bundle data = getIntent().getExtras();
        ArrayList<Part> parts  = data.getParcelableArrayList("listParts");

        for(Part part : parts) {
            Log.d("APP", "On recup les parties de test ==> " + part.getName());
        }

        Intent intent = new Intent(PartTransitionQuizActivity.this, QuestionPassageQuizFragment.class);

        intent.putExtra("listParts" ,parts);

        this.configureViewPager(parts);

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

    private void configureViewPager(ArrayList<Part> parts){
        // 1 - Get ViewPager from layout
        ViewPager pager = findViewById(R.id.viewpager);
        // 2 - Set Adapter PageAdapter and glue it together
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), getResources().getIntArray(R.array.colorPagesViewPager), parts) {
        });
    }
}
