package com.quizzy.mrk.quizzy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Modele.ConnexionModele;
import com.quizzy.mrk.quizzy.Modele.NewQuizModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class NewQuizActivity extends AppCompatActivity {

    private final int SELECT_IMG = 1;

    private RequestQueue requestQueue;
    private NewQuizModele newQuizModele;

    private EditText etName;
    private TextView tvImg;
    private ImageView ivImg;
    private Button btnCreer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_new_quiz));

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.newQuizModele = new NewQuizModele(this, this.requestQueue);

        this.etName = findViewById(R.id.et_new_quiz_name);
        this.tvImg = findViewById(R.id.tv_new_quiz_img);
        this.ivImg = findViewById(R.id.iv_new_quiz_img);
        this.btnCreer = findViewById(R.id.btn_new_quiz);

        this.tvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivImg.getDrawable() == null) {
                    openGallery();
                } else {
                    ivImg.setImageDrawable(null);
                    tvImg.setText(R.string.tv_new_quiz_add_img);
                    tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_24dp, 0, 0, 0);
                }
            }
        });

        this.btnCreer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Application.hideKeyboard(getApplicationContext(), v);
                if (checkName()) {
                    newQuizModele.save(etName.getText().toString().trim(), getDataImg(), new NewQuizModele.NewQuizCallBack() {
                        @Override
                        public void onSuccess(int quiz_id) {

                            Log.d("APP" , Session.getSession().getUser().toString());

                            ArrayList<User> friends = new ArrayList<>();
                            friends.add(new User(1, "test", "test", "test", new GregorianCalendar(), "test", "test", "test", new ArrayList<User>()));
                            friends.add(new User(3, "test", "test", "test", new GregorianCalendar(), "test", "test", "test", new ArrayList<User>()));


                            User testUser = new User(1, "test", "test", "test", new GregorianCalendar(), "test", "test", "test", friends);

                            Quiz quiz = new Quiz(quiz_id, etName.getText().toString().trim(), getDataImg(), testUser, new GregorianCalendar(), 3);
                            Bundle paquet = new Bundle();
                            paquet.putParcelable("quiz", quiz);

                            Intent intent = new Intent(NewQuizActivity.this, PartsActivity.class);
                            intent.putExtras(paquet);

                            startActivity(intent);
                        }

                        @Override
                        public void onErrorNetwork() {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_new_quiz), R.string.error_connexion_http, 2500);
                            snackbar.show();
                        }

                        @Override
                        public void onErrorVollet() {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_new_quiz), R.string.error_vollet, 2500);
                            snackbar.show();
                        }
                    });
                }
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
                this.tvImg.setText(R.string.btn_new_quiz_delete_img);
                this.tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_24dp, 0, 0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getDataImg() {
        if (this.ivImg.getDrawable() == null) { // si il n'y a pas d'image
            return null;
        } else {
            return Application.ConvertImgBase64(this.ivImg);
        }
    }
}
