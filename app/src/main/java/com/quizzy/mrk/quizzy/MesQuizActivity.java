package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.MesQuizModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class MesQuizActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private MesQuizModele mesQuizModele;
    private ArrayList<Quiz> listQuiz;

    private TextView tvNoQuiz;
    private ListView lvQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_mes_quiz));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.mesQuizModele = new MesQuizModele(this, this.requestQueue);

        this.tvNoQuiz = findViewById(R.id.tv_mes_quiz);
        this.lvQuiz = findViewById(R.id.lv_mes_quiz);

        updateActivity();
    }

    private void updateActivity() {
        this.mesQuizModele.getMyQuizCreated(Session.getSession().getUser(), new MesQuizModele.MesQuizCallBack() {
            @Override
            public void onSuccess(ArrayList<Quiz> listQuizCreated) {
                listQuiz = listQuizCreated;
                if (listQuiz.size() == 0) {
                    tvNoQuiz.setText(getString(R.string.tv_mes_quiz_empty));
                } else {
                    ItemMesQuizAdapteur adapteur = new ItemMesQuizAdapteur(MesQuizActivity.this);
                    lvQuiz.setAdapter(adapteur);
                    lvQuiz.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Bundle paquet = new Bundle();
                                    paquet.putParcelable("quiz", listQuiz.get(position) );
                                    Intent intent = new Intent(MesQuizActivity.this, MonQuizOptionsActivity.class);
                                    intent.putExtras(paquet);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                            }
                    );
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MesQuizActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ItemMesQuizAdapteur extends ArrayAdapter<Quiz> {
        public ItemMesQuizAdapteur(Activity context) {
            super(context, R.layout.adapteur_mes_quiz, listQuiz);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vItem = convertView;
            if (vItem == null) {
                vItem = getLayoutInflater().inflate(R.layout.adapteur_mes_quiz, parent, false);
            }

            ImageView ivList = vItem.findViewById(R.id.iv_quiz);
            if (listQuiz.get(position).getMedia() != null) {
                Picasso.with(getContext()).load(listQuiz.get(position).getMedia()).into(ivList);
            }

            TextView tvName = vItem.findViewById(R.id.tv_name_quiz);
            tvName.setText(listQuiz.get(position).getName());

            TextView tvDate = vItem.findViewById(R.id.tv_date_quiz);
            int day = listQuiz.get(position).getIsValidated().get(Calendar.DAY_OF_MONTH);
            int month = listQuiz.get(position).getIsValidated().get(Calendar.MONTH);
            int year = listQuiz.get(position).getIsValidated().get(Calendar.YEAR);
            tvDate.setText(getString(R.string.tv_mes_quiz_adapteur_date) + " " + day + "/" + month + "/" + year);

            return vItem;
        }
    }
}
