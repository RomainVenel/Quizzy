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
import android.view.MenuItem;
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
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.PartsModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class PartsActivity extends AppCompatActivity {

    private final int SELECT_IMG = 1;
    private boolean isNewPart;
    private Part part;
    private Quiz quiz;
    private ArrayList<Question> listQuestions;
    private Question questionSelected;

    private RequestQueue requestQueue;
    private PartsModele partsModele;

    private EditText etName;
    private EditText etDesc;
    private TextView tvImg;
    private ImageView ivImg;
    private ListView lvQuestions;
    private TextView tvAddQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_parts));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.partsModele = new PartsModele(this, this.requestQueue);

        this.etName = findViewById(R.id.et_part_name);
        this.etDesc = findViewById(R.id.et_part_desc);
        this.tvImg = findViewById(R.id.tv_quiz_img);
        this.ivImg = findViewById(R.id.iv_part_img);
        this.tvAddQuestion = findViewById(R.id.tv_part_add_question);
        this.lvQuestions = findViewById(R.id.lv_part_question);

        this.quiz = getIntent().getExtras().getParcelable("quiz");
        this.isNewPart = getIntent().getExtras().getBoolean("new_part");
        if (this.isNewPart == false) {
            this.part = getIntent().getExtras().getParcelable("part");
            this.updateDataActivity();
        }

        this.tvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivImg.getDrawable() == null) {
                    openGallery();
                } else {
                    ivImg.setImageDrawable(null);
                    tvImg.setText(R.string.btn_part_add_img);
                    tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_24dp, 0, 0, 0);
                }
            }
        });

        this.tvAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.hideKeyboard(getApplicationContext(), v);
                if (checkName()) {
                    if (isNewPart) { // Si c'est une nouvelle partie, on sauvegarde
                        createPart();
                    } else { // on edit
                        setPart(2);
                    }
                }
            }
        });
    }


    private void createPart() {
        partsModele.newPart(this.quiz, this.etName.getText().toString().trim(), this.etDesc.getText().toString().trim(), getBase64Img(), new PartsModele.NewPartCallBack() {
            @Override
            public void onSuccess(int part_id, String media) {
                Part part = new Part(part_id, etName.getText().toString().trim(), etDesc.getText().toString().trim(), media, quiz);
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_question", true);
                paquet.putParcelable("part", part);
                Intent intent = new Intent(PartsActivity.this, QuestionActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
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
    }

    private void setPart(final int key) {
        this.part.setName(etName.getText().toString().trim());
        this.part.setDesc(etDesc.getText().toString().trim());
        partsModele.setPart(this.part, getBase64Img(), new PartsModele.SetPartCallBack() {
            @Override
            public void onSuccess(Part partUpdate) {
                part = partUpdate;
                part.setQuiz(quiz);
                postSetPart(key);
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

    }

    private void postSetPart(int key) {
        Intent intent;
        Bundle paquet;
        switch (key) {
            case 1: // redirection quiz
                paquet = new Bundle();
                paquet.putBoolean("new_quiz", false);
                paquet.putParcelable("quiz", quiz);
                intent = new Intent(PartsActivity.this, QuizActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
                break;
            case 2: // redirection addQuestion
                paquet = new Bundle();
                paquet.putBoolean("new_question", true);
                paquet.putParcelable("part", part);
                intent = new Intent(PartsActivity.this, QuestionActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
                break;
            case 3: // redirection listView question
                paquet = new Bundle();
                paquet.putBoolean("new_question", false);
                paquet.putParcelable("part", part);
                paquet.putParcelable("question", questionSelected);
                intent = new Intent(PartsActivity.this, QuestionActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
                break;
        }
    }

    private void updateDataActivity() {
        this.etName.setText(this.part.getName());
        this.etDesc.setText(this.part.getDesc());
        if (this.part.getMedia() != null) {
            Picasso.with(this).load(this.part.getMedia()).into(ivImg);
            this.tvImg.setText(R.string.btn_part_delete_img);
            this.tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_24dp, 0, 0, 0);
        }

        this.partsModele.getQuestions(this.part, new PartsModele.getQuestionsCallBack() {
            @Override
            public void onSuccess(ArrayList<Question> questions) {
                listQuestions = questions;
                ArrayAdapter<Question> adaptateur = new ArrayAdapter<Question>(PartsActivity.this, android.R.layout.simple_list_item_1, questions);
                lvQuestions.setAdapter(adaptateur);

                lvQuestions.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                questionSelected = listQuestions.get(position);
                                setPart(3);
                            }
                        }
                );
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

    private String getBase64Img() {
        if (this.ivImg.getDrawable() == null) { // si il n'y a pas d'image
            return null;
        } else {
            return Application.bitmapToBase64(((BitmapDrawable) this.ivImg.getDrawable()).getBitmap());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMG && resultCode == RESULT_OK) {
            Uri pathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathUri);
                this.ivImg.setImageBitmap(bitmap);
                this.tvImg.setText(R.string.btn_part_delete_img);
                this.tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_24dp, 0, 0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!isNewPart) {
                    setPart(1);
                } else {
                    Bundle paquet = new Bundle();
                    paquet.putBoolean("new_quiz", false);
                    paquet.putParcelable("quiz", quiz);
                    Intent intent = new Intent(PartsActivity.this, QuizActivity.class);
                    intent.putExtras(paquet);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
