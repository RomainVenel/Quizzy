package com.quizzy.mrk.quizzy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Modele.UserModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ProfilActivity extends AppCompatActivity {

    private final int SELECT_IMG = 1;
    private RequestQueue requestQueue;
    private UserModele userModele;

    private ImageView ivUserImg;
    private EditText etLastName;
    private EditText etFirstName;
    private EditText etEmail;
    private Button bValidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_profile));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.userModele = new UserModele(this, this.requestQueue);

        this.ivUserImg = this.findViewById(R.id.profil_img);
        this.etLastName = findViewById(R.id.et_profil_lastName);
        this.etFirstName = findViewById(R.id.et_profil_firstName);
        this.etEmail = findViewById(R.id.et_profil_email);
        this.bValidate = findViewById(R.id.btn_profil_validate);

        Picasso.with(this).load(Session.getSession().getUser().getMedia()).into(this.ivUserImg);
        etLastName.setText(Session.getSession().getUser().getLastName());
        etFirstName.setText(Session.getSession().getUser().getFirstName());
        etEmail.setText(Session.getSession().getUser().getEmail());

        this.ivUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        this.bValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void updateProfile()
    {
        if (checkFieldsNotEmpty()) {
            this.userModele.updateProfile(
                    this.etLastName.getText().toString().trim(),
                    this.etFirstName.getText().toString().trim(),
                    this.etEmail.getText().toString().trim(),
                    this.getBase64Img(),
                    Session.getSession().getUser(),
                    new UserModele.UserCallBack() {
                        @Override
                        public void onSuccess() {
                            Session.getSession().getUser().setLastName(etLastName.getText().toString().trim());
                            Session.getSession().getUser().setFirstName(etFirstName.getText().toString().trim());
                            Session.getSession().getUser().setEmail(etEmail.getText().toString().trim());

                            Intent intent = new Intent(ProfilActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onErrorData(String error) {
                            etEmail.setError(getString(R.string.error_email_exist));
                            Log.d("APP", "email empty");
                        }

                        @Override
                        public void onErrorNetwork() {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_inscription), R.string.error_connexion_http, 2500);
                            snackbar.show();
                        }

                        @Override
                        public void onErrorVollet() {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_inscription), R.string.error_vollet, 2500);
                            snackbar.show();
                        }
                    }
            );
        }
    }

    private boolean checkFieldsNotEmpty() {
        boolean check = true;
        if (this.etLastName.getText().toString().matches("")) {
            this.etLastName.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etLastName.setError(null);
        }

        if (this.etFirstName.getText().toString().matches("")) {
            this.etFirstName.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etFirstName.setError(null);
        }

        if (this.etEmail.getText().toString().matches("")) {
            this.etEmail.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etEmail.setError(null);
        }
        return check;
    }

    private String getBase64Img() {
        if (this.ivUserImg.getDrawable() == null) { // si il n'y a pas d'image
            return null;
        } else {
            return Application.bitmapToBase64(((BitmapDrawable) this.ivUserImg.getDrawable()).getBitmap());
        }
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
                this.ivUserImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ProfilActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
