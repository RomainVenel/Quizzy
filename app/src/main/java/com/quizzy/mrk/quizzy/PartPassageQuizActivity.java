package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Fragments.PageAdapter;

import java.util.ArrayList;

public class PartPassageQuizActivity extends AppCompatActivity {

    private TextView tvPartTitle;
    private static int TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_passage_quiz);

        Bundle data = getIntent().getExtras();
        final ArrayList<Part> parts  = data.getParcelableArrayList("listParts");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        this.tvPartTitle = findViewById(R.id.tv_part_activity_title);
        this.tvPartTitle.setText(parts.get(0).getName());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(PartPassageQuizActivity.this, PartTransitionQuizActivity.class);

                intent.putExtra("listParts" ,parts);
                startActivity(intent);

                PartPassageQuizActivity.this.finish();

            }
        }, TIME_OUT);
    }
}
