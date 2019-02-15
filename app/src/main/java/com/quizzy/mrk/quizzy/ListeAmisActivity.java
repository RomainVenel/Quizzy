package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ListeAmisActivity extends AppCompatActivity {

    private EditText etSearch;
    private TextView tvMessage;
    private ListView lvFriend;
    private Timer timerSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_amis);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_mes_amis));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.etSearch = findViewById(R.id.et_search);
        this.tvMessage = findViewById(R.id.tv_friends_list);
        this.lvFriend = findViewById(R.id.lv_friends_list);

        TextWatcher searchWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                timerSearch = new Timer();
                timerSearch.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //tvMessage.setText(etSearch.getText().toString().trim());
                    }
                }, 600);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timerSearch != null) {
                    timerSearch.cancel();
                }
            }
        };
        this.etSearch.addTextChangedListener(searchWatcher);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ListeAmisActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
