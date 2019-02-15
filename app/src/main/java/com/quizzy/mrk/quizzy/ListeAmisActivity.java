package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListeAmisActivity extends AppCompatActivity {

    private EditText etSearch;
    private TextView tvMessage;
    private ListView lvFriend;
    private Boolean searchAccess = true;
    private Handler handler = new android.os.Handler();


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

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            tvMessage.setText(etSearch.getText().toString().trim());
                            searchAccess = true;
                        }
                    };

                    if (searchAccess) {
                        searchAccess = false;
                        handler.postDelayed(runnable, 2000);
                    } else {
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 2000);
                    }

                }
            }

            private boolean filterLongEnough() {
                return etSearch.getText().toString().trim().length() > 3;
            }
        };
        this.etSearch.addTextChangedListener(fieldValidatorTextWatcher);
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
