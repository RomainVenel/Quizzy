package com.quizzy.mrk.quizzy;

import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Modele.InscriptionModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class InscriptionActivity extends AppCompatActivity {

    private boolean authorizationGallery = false;
    private final int SELECT_IMG = 1;
    private RequestQueue requestQueue;
    private InscriptionModele inscriptionModele;
    private DatePickerDialog.OnDateSetListener birthdayListener;
    private GregorianCalendar birthdaySelected = new GregorianCalendar();

    private ImageView ivSigninImg;
    private EditText etSigninNom;
    private EditText etSigninPrenom;
    private EditText etSigninUsername;
    private EditText etSigninBirthday;
    private EditText etSigninEmail;
    private EditText etSigninPassword;
    private EditText etSigninConfirmPassword;
    private Button bBack;
    private Button bSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.inscriptionModele = new InscriptionModele(this, this.requestQueue);

        this.ivSigninImg = findViewById(R.id.iv_sign_in_img);
        this.etSigninNom = findViewById(R.id.et_sign_in_nom);
        this.etSigninPrenom = findViewById(R.id.et_sign_in_prenom);
        this.etSigninUsername = findViewById(R.id.et_sign_in_username);
        this.etSigninBirthday = findViewById(R.id.et_sign_in_birthday);
        this.etSigninEmail = findViewById(R.id.et_sign_in_email);
        this.etSigninPassword = findViewById(R.id.et_sign_in_password);
        this.etSigninConfirmPassword = findViewById(R.id.et_sign_in_confirm_password);
        this.bBack = findViewById(R.id.btn_login_back);
        this.bSignin = findViewById(R.id.btn_login_inscription);

        this.etSigninBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.hideKeyboard(getApplicationContext(), v);
                DatePickerDialog dialog = new DatePickerDialog(
                        InscriptionActivity.this,
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
                etSigninBirthday.setText(day + "/" + (month + 1) + "/" + year);
                birthdaySelected = new GregorianCalendar(year, month, day);
                etSigninBirthday.setError(null);
            }
        };

        this.bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InscriptionActivity.this, ConnexionActivity.class);
                startActivity(intent);
            }
        });

        this.bSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.hideKeyboard(getApplicationContext(), v);
                if (checkSignin()) {
                    inscriptionModele.inscription(
                            etSigninNom.getText().toString().trim(),
                            etSigninPrenom.getText().toString().trim(),
                            etSigninUsername.getText().toString().trim(),
                            birthdaySelected,
                            etSigninEmail.getText().toString().trim(),
                            Application.md5(etSigninPassword.getText().toString().trim()),
                            Application.bitmapToBase64(((BitmapDrawable)ivSigninImg.getDrawable()).getBitmap()),
                            new InscriptionModele.InscriptionCallBack() {
                                @Override
                                public void onSuccess() {
                                    Intent intentionEnvoyer = new Intent(InscriptionActivity.this, DashboardActivity.class);
                                    startActivity(intentionEnvoyer);
                                }

                                @Override
                                public void onErrorData(String error) {
                                    if (error == "username") {
                                        Snackbar snackbar = Snackbar
                                                .make(findViewById(R.id.activity_inscription), R.string.error_username_exist, 2500);
                                        snackbar.show();
                                    } else if (error == "email") {
                                        Snackbar snackbar = Snackbar
                                                .make(findViewById(R.id.activity_inscription), R.string.error_email_exist, 2500);
                                        snackbar.show();
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
                } else {
                    Log.d("APP", "Inscription not full");
                }
            }
        });

        this.ivSigninImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImgProfil();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private boolean checkSignin() {
        boolean check = true;
        if (this.etSigninNom.getText().toString().matches("")) {
            this.etSigninNom.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etSigninNom.setError(null);
        }

        if (this.etSigninPrenom.getText().toString().matches("")) {
            this.etSigninPrenom.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etSigninPrenom.setError(null);
        }

        if (this.etSigninUsername.getText().toString().matches("")) {
            this.etSigninUsername.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etSigninUsername.setError(null);
        }

        if (this.etSigninBirthday.getText().toString().matches("")) {
            this.etSigninBirthday.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etSigninBirthday.setError(null);
        }

        if (this.etSigninEmail.getText().toString().matches("")) {
            this.etSigninEmail.setError(getString(R.string.et_error_empty));
            check = false;
        } else if (!this.emailValid(this.etSigninEmail.getText().toString())) {
            this.etSigninEmail.setError(getString(R.string.et_error_email));
            check = false;
        } else {
            this.etSigninEmail.setError(null);
        }

        if (this.etSigninPassword.getText().toString().matches("")) {
            this.etSigninPassword.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etSigninPassword.setError(null);
        }

        if (this.etSigninConfirmPassword.getText().toString().matches("")) {
            this.etSigninConfirmPassword.setError(getString(R.string.et_error_empty));
            check = false;
        } else if (!this.etSigninConfirmPassword.getText().toString().equals(this.etSigninPassword.getText().toString())) {
            this.etSigninConfirmPassword.setError(getString(R.string.et_error_mdp_diff));
            check = false;
        } else {
            this.etSigninConfirmPassword.setError(null);
        }
        return check;
    }

    private boolean emailValid(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void changeImgProfil() {
        if (!this.authorizationGallery) { // on demande l'autorisation une seule fois
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle(getString(R.string.dialog_sign_in_title));
            String[] pictureDialogItems = {
                    getString(R.string.dialog_sign_in_autorise),
                    getString(R.string.dialog_sign_in_refuse)};
            pictureDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    authorizationGallery = true;
                                    openGallery();
                                    break;
                                case 1:
                                    break;
                            }
                        }
                    });
            pictureDialog.show();
        } else {
            this.openGallery();
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
                this.ivSigninImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
