package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.quizzy.mrk.quizzy.Technique.Session;

import java.util.Timer;
import java.util.TimerTask;

public class LoadActivity extends AppCompatActivity {

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent;
                if (Session.getSession() == null) {
                    intent = new Intent(LoadActivity.this, ConnexionActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(LoadActivity.this, DashboardActivity.class);
                    startActivity(intent);
                }
            }
        }, 2500);
    }
}
