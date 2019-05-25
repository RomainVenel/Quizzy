package com.quizzy.mrk.quizzy;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Modele.MesAmisModele;
import com.quizzy.mrk.quizzy.Modele.UserModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private UserModele userModele;

    private ImageView ivUserImg;
    private EditText etLastName;
    private EditText etFirstName;
    private EditText etUsername;
    private EditText etEmail;
    private Button bReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_profil));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.userModele = new UserModele(this, this.requestQueue);

        this.ivUserImg = this.findViewById(R.id.profil_img);
        this.etLastName = findViewById(R.id.et_profil_lastName);
        this.etFirstName = findViewById(R.id.et_profil_firstName);
        this.etUsername = findViewById(R.id.et_profil_username);
        this.etEmail = findViewById(R.id.et_profil_email);
        this.bReturn = findViewById(R.id.b_profil_return);

        Picasso.with(this).load(Session.getSession().getUser().getMedia()).into(this.ivUserImg);
        etLastName.setText(Session.getSession().getUser().getLastName());
        etFirstName.setText(Session.getSession().getUser().getFirstName());
        etUsername.setText(Session.getSession().getUser().getUsername());
        etEmail.setText(Session.getSession().getUser().getEmail());

        this.bReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void updateProfile()
    {
        if (checkFieldsNotEmpty()) {
            this.userModele.updateProfile(
                    this.etLastName.getText().toString().trim(),
                    this.etFirstName.getText().toString().trim(),
                    this.etUsername.getText().toString().trim(),
                    this.etEmail.getText().toString().trim(),
                    Session.getSession().getUser(),
                    new UserModele.UserCallBack() {
                        @Override
                        public void onSuccess() {
                            Session.getSession().getUser().setLastName(etLastName.getText().toString().trim());
                            Session.getSession().getUser().setFirstName(etFirstName.getText().toString().trim());
                            Session.getSession().getUser().setUsername(etUsername.getText().toString().trim());
                            Session.getSession().getUser().setEmail(etEmail.getText().toString().trim());

                            Intent intent = new Intent(ProfilActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onErrorData(String error) {
                            if (error.equals("username")) {
                                etUsername.setError(getString(R.string.error_username_exist));
                                Log.d("APP", "username empty");
                            } else if(error.equals("email")) {
                                etEmail.setError(getString(R.string.error_email_exist));
                                Log.d("APP", "email empty");
                            }
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

        if (this.etUsername.getText().toString().matches("")) {
            this.etUsername.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etUsername.setError(null);
        }

        if (this.etEmail.getText().toString().matches("")) {
            this.etEmail.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etEmail.setError(null);
        }
        return check;
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
