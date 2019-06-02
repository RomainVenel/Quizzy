package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.quizzy.mrk.quizzy.Modele.QuizModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class MesQuizActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private MesQuizModele mesQuizModele;
    private QuizModele mQuizModele;
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
        this.mQuizModele = new QuizModele(this, this.requestQueue);

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
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(MesQuizActivity.this);
                                    alertDialogBuilder.setMessage(R.string.message_dialog_mes_quiz);
                                    alertDialogBuilder.setPositiveButton(
                                            R.string.dialog_btn_partage,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    Bundle paquet = new Bundle();
                                                    paquet.putParcelable("quiz", listQuiz.get(position) );
                                                    Intent intent = new Intent(MesQuizActivity.this, ShareQuizActivity.class);
                                                    intent.putExtras(paquet);
                                                    startActivity(intent);
                                                }
                                            });

                                    alertDialogBuilder.setNegativeButton(
                                            R.string.dialog_btn_delete,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mQuizModele.deleteQuiz(listQuiz.get(position), new QuizModele.deleteQuizCallBack() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Intent intent = new Intent(MesQuizActivity.this, MesQuizActivity.class);
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
                                    androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
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
