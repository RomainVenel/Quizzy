package com.quizzy.mrk.quizzy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.QuizModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class QuizActivity extends AppCompatActivity {

    private final int SELECT_IMG = 1;
    private boolean isNewQuiz;
    private Quiz quiz;
    private ArrayList<Part> listParts;

    private RequestQueue requestQueue;
    private QuizModele quizModele;

    private EditText etName;
    private TextView tvImg;
    private ImageView ivImg;
    private TextView tvAddPart;
    private ListView lvParts;
    private Button btnCreer;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_new_quiz));

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.quizModele = new QuizModele(this, this.requestQueue);

        this.etName = findViewById(R.id.et_quiz_name);
        this.tvImg = findViewById(R.id.tv_quiz_img);
        this.ivImg = findViewById(R.id.iv_quiz_img);
        this.tvAddPart = findViewById(R.id.tv_quiz_add_part);
        this.lvParts = findViewById(R.id.lv_quiz_part);
        this.btnCreer = findViewById(R.id.btn_quiz);
        this.btnBack = findViewById(R.id.btn_quiz_back);

        this.isNewQuiz = getIntent().getExtras().getBoolean("new_quiz");
        if (this.isNewQuiz == false) {
            this.quiz = getIntent().getExtras().getParcelable("quiz");
            this.updateDataActivity();
        }

        this.tvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivImg.getDrawable() == null) {
                    openGallery();
                } else {
                    ivImg.setImageDrawable(null);
                    tvImg.setText(R.string.tv_quiz_add_img);
                    tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_24dp, 0, 0, 0);
                }
            }
        });

        this.tvAddPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.hideKeyboard(getApplicationContext(), v);
                if (checkName()) {
                    if (isNewQuiz) { // Si c'est une nouveau quiz, on sauvegarde
                        createQuiz();
                    } else { // on edit
                        setQuiz();
                    }
                }
            }
        });

        this.btnCreer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.hideKeyboard(getApplicationContext(), v);
                if (checkName()) { // on valide le quiz

                }
            }
        });

        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createQuiz() {
        quizModele.newQuiz(etName.getText().toString().trim(), getBase64Img(), new QuizModele.NewQuizCallBack() {
            @Override
            public void onSuccess(int quiz_id, String media) {
                Quiz quiz = new Quiz(quiz_id, etName.getText().toString().trim(), media, Session.getSession().getUser(), new GregorianCalendar());
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_part", true);
                paquet.putParcelable("quiz", quiz);
                Intent intent = new Intent(QuizActivity.this, PartsActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_quiz), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_quiz), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    private void setQuiz() {
        quiz.setName(etName.getText().toString().trim());
        quizModele.setQuiz(this.quiz, getBase64Img(), new QuizModele.SetQuizCallBack() {
            @Override
            public void onSuccess(Quiz quiz) {
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_part", true);
                paquet.putParcelable("quiz", quiz);
                Intent intent = new Intent(QuizActivity.this, PartsActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_quiz), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_quiz), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    private void updateDataActivity() {
        this.etName.setText(this.quiz.getName());
        if (this.quiz.getMedia() != null) {
            Picasso.with(this).load(this.quiz.getMedia()).into(ivImg);
            this.tvImg.setText(R.string.btn_quiz_delete_img);
            this.tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_24dp, 0, 0, 0);
        }

        Log.d("APP", "on met a jour la liste des parties");

        this.quizModele.getParts(this.quiz, new QuizModele.getPartsQuizCallBack() {
            @Override
            public void onSuccess(ArrayList<Part> parts) {
                listParts = parts;
                ArrayAdapter<Part> adaptateur = new ArrayAdapter<Part>(QuizActivity.this, android.R.layout.simple_list_item_1, parts);
                lvParts.setAdapter(adaptateur);

                lvParts.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle paquet = new Bundle();
                                paquet.putBoolean("new_part", false);
                                paquet.putParcelable("quiz", quiz);
                                paquet.putParcelable("part", listParts.get(position));

                                Intent intent = new Intent(QuizActivity.this, PartsActivity.class);
                                intent.putExtras(paquet);
                                startActivity(intent);
                            }
                        }
                );

            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_quiz), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_quiz), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });

    }

    private boolean checkName() {
        boolean check = true;
        if (this.etName.getText().toString().matches("")) {
            this.etName.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etName.setError(null);
        }

        return check;
    }

    private void openGallery() {
        Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
        picker.setType("image/*");
        picker.putExtra(Intent.EXTRA_LOCAL_ONLY, true); // seulement image en memoire interne
        startActivityForResult(Intent.createChooser(picker, getString(R.string.choose_img)), this.SELECT_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMG && resultCode == RESULT_OK) {
            Uri pathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathUri);
                this.ivImg.setImageBitmap(bitmap);
                this.tvImg.setText(R.string.btn_quiz_delete_img);
                this.tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_24dp, 0, 0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getBase64Img() {
        if (this.ivImg.getDrawable() == null) { // si il n'y a pas d'image
            return null;
        } else {
            return Application.bitmapToBase64(((BitmapDrawable) this.ivImg.getDrawable()).getBitmap());
        }
    }
}
