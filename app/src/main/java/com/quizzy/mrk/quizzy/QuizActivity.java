package com.quizzy.mrk.quizzy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private final int SELECT_IMG = 1;
    private boolean isNewQuiz;
    private Quiz quiz;
    private ArrayList<Part> listParts;
    private Part partSelected;

    private RequestQueue requestQueue;
    private QuizModele quizModele;

    private EditText etName;
    private TextView tvImg;
    private ImageView ivImg;
    private TextView tvAddPart;
    private ListView lvParts;
    private Button btnCreer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_new_quiz));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.quizModele = new QuizModele(this, this.requestQueue);

        this.etName = findViewById(R.id.et_quiz_name);
        this.tvImg = findViewById(R.id.tv_quiz_img);
        this.ivImg = findViewById(R.id.iv_quiz_img);
        this.tvAddPart = findViewById(R.id.tv_quiz_add_part);
        this.lvParts = findViewById(R.id.lv_quiz_part);
        this.btnCreer = findViewById(R.id.btn_quiz);

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
                        setQuiz(2);
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
    }

    private void createQuiz() {
        quizModele.newQuiz(etName.getText().toString().trim(), getBase64Img(), new QuizModele.NewQuizCallBack() {
            @Override
            public void onSuccess(int quiz_id, String media) {
                quiz = new Quiz(quiz_id, etName.getText().toString().trim(), media, Session.getSession().getUser(), new GregorianCalendar());
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

    private void setQuiz(final int key) {
        quiz.setName(etName.getText().toString().trim());
        quizModele.setQuiz(this.quiz, getBase64Img(), new QuizModele.SetQuizCallBack() {
            @Override
            public void onSuccess(Quiz quizUpdate) {
                quiz = quizUpdate;
                postSetQuiz(key);
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

    private void deletePart(Part part) {
        this.quizModele.deletePart(part, new QuizModele.deletePartQuizCallBack() {
            @Override
            public void onSuccess() {

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

    private void postSetQuiz(int key) {
        Intent intent;
        Bundle paquet;
        switch (key) {
            case 1: // redirection dashboard
                intent = new Intent(QuizActivity.this, DashboardActivity.class);
                startActivity(intent);
                break;
            case 2: // redirection addPart
                paquet = new Bundle();
                paquet.putBoolean("new_part", true);
                paquet.putParcelable("quiz", quiz);
                intent = new Intent(QuizActivity.this, PartsActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
                break;
            case 3: // redirection listView parts
                paquet = new Bundle();
                paquet.putBoolean("new_part", false);
                paquet.putParcelable("quiz", quiz);
                paquet.putParcelable("part", partSelected);
                intent = new Intent(QuizActivity.this, PartsActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
                break;
        }
    }

    private void updateDataActivity() {
        this.etName.setText(this.quiz.getName());
        if (this.quiz.getMedia() != null) {
            Picasso.with(this).load(this.quiz.getMedia()).into(ivImg);
            this.tvImg.setText(R.string.btn_quiz_delete_img);
            this.tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_24dp, 0, 0, 0);
        }

        this.quizModele.getParts(this.quiz, new QuizModele.getPartsQuizCallBack() {
            @Override
            public void onSuccess(ArrayList<Part> parts) {
                listParts = parts;
                ItemPartsAdapteur adaptateur = new ItemPartsAdapteur(QuizActivity.this);
                lvParts.setAdapter(adaptateur);
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

    private String getBase64Img() {
        if (this.ivImg.getDrawable() == null) { // si il n'y a pas d'image
            return null;
        } else {
            return Application.bitmapToBase64(((BitmapDrawable) this.ivImg.getDrawable()).getBitmap());
        }
    }

    public void deletePartDialog(final int position) {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_dialog_delete_part);
        alertDialogBuilder.setPositiveButton(
                R.string.dialog_btn_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePart(listParts.get(position));
                        listParts.remove(position);
                        ((BaseAdapter) lvParts.getAdapter()).notifyDataSetInvalidated();
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.setNegativeButton(
                R.string.dialog_btn_no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!isNewQuiz) {
                    setQuiz(1);
                } else {
                    Intent intent = new Intent(QuizActivity.this, DashboardActivity.class);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ItemPartsAdapteur extends ArrayAdapter<Part> {
        public ItemPartsAdapteur(Activity context) {
            super(context, R.layout.adapter_iv_tv_btn, listParts);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vItem = convertView;
            if (vItem == null) {
                vItem = getLayoutInflater().inflate(R.layout.adapter_iv_tv_btn, parent, false);
            }

            ImageView ivList = vItem.findViewById(R.id.iv_list);
            if (listParts.get(position).getMedia() != null) {
                Picasso.with(getContext()).load(listParts.get(position).getMedia()).into(ivList);
            }

            TextView tvList = vItem.findViewById(R.id.tv_list);
            String namePart = listParts.get(position).getName();
            if (namePart.length() > 60) {
                namePart = listParts.get(position).getName().substring(0, 60) + "...";
            }
            tvList.setText(namePart);

            ImageButton btnEdit = vItem.findViewById(R.id.btn_edit_list);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    partSelected = listParts.get(position);
                    setQuiz(3);
                }
            });

            ImageButton btnDelete = vItem.findViewById(R.id.btn_delete_list);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePartDialog(position);
                }
            });
            return vItem;
        }
    }
}
