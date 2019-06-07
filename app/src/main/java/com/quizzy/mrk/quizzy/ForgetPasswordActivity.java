package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Modele.UserModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ForgetPasswordActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private UserModele userModele;
    private DatePickerDialog.OnDateSetListener birthdayListener;
    private GregorianCalendar birthdaySelected = new GregorianCalendar();

    private EditText etUsername;
    private EditText etBirthday;
    private Button bValidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_forget_password));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.userModele = new UserModele(this, this.requestQueue);

        this.etUsername = findViewById(R.id.username);
        this.etBirthday = findViewById(R.id.birthday);
        this.bValidate = findViewById(R.id.btn_validate);

        this.etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.hideKeyboard(getApplicationContext(), v);
                DatePickerDialog dialog = new DatePickerDialog(
                        ForgetPasswordActivity.this,
                        R.style.MyDatePickerDialogTheme,
                        birthdayListener,
                        birthdaySelected.get(Calendar.YEAR),
                        birthdaySelected.get(Calendar.MONTH),
                        birthdaySelected.get(Calendar.DAY_OF_MONTH)
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                GregorianCalendar dateLimit = new GregorianCalendar();
                dateLimit.add(GregorianCalendar.YEAR, -5); // On retranche 5 années
                dialog.getDatePicker().setMaxDate(dateLimit.getTimeInMillis());
                dialog.show();
            }
        });

        this.birthdayListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                etBirthday.setText(day + "/" + (month + 1) + "/" + year);
                birthdaySelected = new GregorianCalendar(year, month, day);
                etBirthday.setError(null);
            }
        };

        this.bValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void changePassword() {
        if (this.checkFields()) {
            this.userModele.forgetPassword(this.etUsername.getText().toString().trim(), this.birthdaySelected, new UserModele.UserCallBack() {
                @Override
                public void onSuccess() {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_forget_password), R.string.check_mail_new_password, 3000);
                    snackbar.show();
                }

                @Override
                public void onErrorData(String error) {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_forget_password), R.string.user_not_found, 3000);
                    snackbar.show();
                }

                @Override
                public void onErrorNetwork() {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_forget_password), R.string.error_connexion_http, 2500);
                    snackbar.show();
                }

                @Override
                public void onErrorVollet() {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_forget_password), R.string.error_vollet, 2500);
                    snackbar.show();
                }
            });
        }
    }

    private boolean checkFields() {
        boolean check = true;

        if (this.etUsername.getText().toString().matches("")) {
            this.etUsername.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etUsername.setError(null);
        }

        if (this.etBirthday.getText().toString().matches("")) {
            this.etBirthday.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etBirthday.setError(null);
        }

        return check;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ForgetPasswordActivity.this, ConnexionActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
