package com.quizzy.mrk.quizzy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Modele.ConnexionModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ConnexionActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private RequestQueue requestQueue;
    private ConnexionModele connexionModele;
    private EditText etLoginUsername;
    private EditText etLoginPassword;
    private Button bLogin;
    private Button bSignIn;
    private TextView tvForgotMdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.connexionModele = new ConnexionModele(this, this.requestQueue);

        this.etLoginUsername = findViewById(R.id.et_login_username);
        this.etLoginPassword = findViewById(R.id.et_login_password);
        this.bLogin = findViewById(R.id.btn_login_valider);
        this.bSignIn = findViewById(R.id.btn_login_register);
        this.tvForgotMdp = findViewById(R.id.tv_login_forgot_mdp);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        this.bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentionEnvoyer = new Intent(ConnexionActivity.this, InscriptionActivity.class);
                startActivity(intentionEnvoyer);
            }
        });

        this.bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Application.hideKeyboard(getApplicationContext(), v);
                if (checkLogin()) {
                    pDialog.setMessage(getString(R.string.dialog_login));
                    pDialog.show();
                    connexionModele.authentication(etLoginUsername.getText().toString().trim(), Application.md5(etLoginPassword.getText().toString().trim()), new ConnexionModele.ConnexionCallBack() {
                        @Override
                        public void onSuccess() {
                            pDialog.hide();
                            Intent intentionEnvoyer = new Intent(ConnexionActivity.this, DashboardActivity.class);
                            startActivity(intentionEnvoyer);
                        }

                        @Override
                        public void onErrorLogin() {
                            pDialog.hide();
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_connexion), R.string.error_user_not_found, 2500);
                            snackbar.show();
                        }

                        @Override
                        public void onErrorNetwork() {
                            pDialog.hide();
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_connexion), R.string.error_connexion_http, 2500);
                            snackbar.show();
                        }

                        @Override
                        public void onErrorVollet() {
                            pDialog.hide();
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.activity_connexion), R.string.error_vollet, 2500);
                            snackbar.show();
                        }
                    });
                }
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private boolean checkLogin() {
        boolean check = true;
        if (this.etLoginUsername.getText().toString().matches("")) {
            this.etLoginUsername.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etLoginUsername.setError(null);
        }

        if (this.etLoginPassword.getText().toString().matches("")) {
            this.etLoginPassword.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etLoginPassword.setError(null);
        }
        return check;
    }
}
