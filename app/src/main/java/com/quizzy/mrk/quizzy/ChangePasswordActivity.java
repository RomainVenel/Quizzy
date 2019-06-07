package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Modele.UserModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

public class ChangePasswordActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private UserModele userModele;

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private Button bChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_change_password));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.userModele = new UserModele(this, this.requestQueue);

        this.etCurrentPassword = findViewById(R.id.et_current_password);
        this.etNewPassword = findViewById(R.id.et_new_password);
        this.etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        this.bChangePassword = findViewById(R.id.btn_change_password);

        this.bChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void changePassword() {
        if (this.checkField() && this.checkCurrentPassword()) {
            this.userModele.changePassword(Session.getSession().getUser(), Application.md5(this.etNewPassword.getText().toString().trim()), new UserModele.UserCallBack() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(ChangePasswordActivity.this, ProfilActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onErrorData(String error) {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_change_password), R.string.user_not_found, 2500);
                    snackbar.show();
                }

                @Override
                public void onErrorNetwork() {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_change_password), R.string.error_connexion_http, 2500);
                    snackbar.show();
                }

                @Override
                public void onErrorVollet() {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_change_password), R.string.error_vollet, 2500);
                    snackbar.show();
                }
            });
        }
    }

    private boolean checkField() {
        boolean check = true;
        if (this.etCurrentPassword.getText().toString().matches("")) {
            this.etCurrentPassword.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etCurrentPassword.setError(null);
        }

        if (this.etNewPassword.getText().toString().matches("")) {
            this.etNewPassword.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etNewPassword.setError(null);
        }

        if (this.etConfirmNewPassword.getText().toString().matches("")) {
            this.etConfirmNewPassword.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etConfirmNewPassword.setError(null);
        }

        return check;
    }

    private boolean checkCurrentPassword() {
        boolean check = true;
        if (!Session.getSession().getUser().getPassword().equals(Application.md5(this.etCurrentPassword.getText().toString().trim()))) {
            this.etCurrentPassword.setError(getString(R.string.wrong_password));
            check = false;
        } else {
            this.etCurrentPassword.setError(null);
        }

        if (!this.etNewPassword.getText().toString().trim().equals(this.etConfirmNewPassword.getText().toString().trim())) {
            this.etConfirmNewPassword.setError(getString(R.string.et_error_mdp_diff));
            check = false;
        } else {
            this.etConfirmNewPassword.setError(null);
        }

        return check;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ChangePasswordActivity.this, ProfilActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
