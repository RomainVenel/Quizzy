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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.quizzy.mrk.quizzy.Entities.Answer;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Modele.QuestionModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private final int SELECT_IMG = 1;
    private boolean isNewQuestion;
    private Part part;
    private Question question;
    private LayoutInflater layoutInflater;

    private RequestQueue requestQueue;
    private QuestionModele questionModele;

    private LinearLayout llAllAnswer;
    private Spinner spType;
    private Spinner spGrade;
    private EditText etName;
    private TextView tvImg;
    private ImageView ivImg;
    private TextView tvAddAnswer;
    private TextView tvErrorAnswer;
    private Button btnSave;

    private List<String> listType;
    private List<String> listGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_question));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.questionModele = new QuestionModele(this, this.requestQueue);

        this.llAllAnswer = findViewById(R.id.all_answer);
        this.spType = findViewById(R.id.sp_question_type);
        this.spGrade = findViewById(R.id.sp_question_grade);
        this.etName = findViewById(R.id.et_question_name);
        this.tvImg = findViewById(R.id.tv_question_img);
        this.ivImg = findViewById(R.id.iv_question_img);
        this.tvAddAnswer = findViewById(R.id.tv_question_add_answer);
        this.tvErrorAnswer = findViewById(R.id.tv_question_error_answer);
        this.btnSave = findViewById(R.id.btn_question_save);

        this.hydrateListType();
        this.hydrateListGrade();

        ArrayAdapter<String> aaType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.listType);
        aaType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spType.setAdapter(aaType);
        this.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (llAllAnswer.getChildCount() > 0) { // si il y a des reponses, on delete toutes les reponses
                    llAllAnswer.removeAllViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> aaGrade = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.listGrade);
        aaGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spGrade.setAdapter(aaGrade);

        this.layoutInflater = getLayoutInflater();

        this.part = getIntent().getExtras().getParcelable("part");
        this.isNewQuestion = getIntent().getExtras().getBoolean("new_question");
        if (this.isNewQuestion == false) {
            this.question = getIntent().getExtras().getParcelable("question");
            this.updateDataActivity();
        }

        this.tvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivImg.getDrawable() == null) {
                    openGallery();
                } else {
                    ivImg.setImageDrawable(null);
                    tvImg.setText(R.string.btn_question_add_img);
                    tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_24dp, 0, 0, 0);
                }
            }
        });

        this.tvAddAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rowView;
                if (spType.getSelectedItem().toString().equals("QCM")) {
                    rowView = layoutInflater.inflate(R.layout.question_qcm_row, llAllAnswer, false);
                } else {
                    rowView = layoutInflater.inflate(R.layout.question_qcu_row, llAllAnswer, false);
                }
                llAllAnswer.addView(rowView);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.hideKeyboard(getApplicationContext(), v);
                if (checkQuestion()) {
                    updateQuestion();
                    if (isNewQuestion) {
                        createQuestion();
                    } else {
                        setQuestion();
                    }
                }
            }
        });
    }

    private void updateQuestion() {
        if (isNewQuestion) {
            this.question = new Question();
        }
        this.question.setType(spType.getSelectedItem().toString());
        this.question.setGrade(Integer.parseInt(spGrade.getSelectedItem().toString()));
        this.question.setName(this.etName.getText().toString().trim());
        this.question.setPart(this.part);
        this.question.setAnswers(new ArrayList<Answer>());

        for (int i = 0; i < this.llAllAnswer.getChildCount(); i++) {
            View llAnswer = llAllAnswer.getChildAt(i);
            Answer answer = new Answer();

            if (spType.getSelectedItem().toString().equals("QCM")) {
                CheckBox cbAnswer = llAnswer.findViewById(R.id.ck_answer);
                answer.setCorrect(cbAnswer.isChecked());
            } else {
                RadioButton rbAnswer = llAnswer.findViewById(R.id.rb_answer);
                answer.setCorrect(rbAnswer.isChecked());
            }
            EditText etAnswer = llAnswer.findViewById(R.id.et_answer);
            answer.setName(etAnswer.getText().toString().trim());

            this.question.addAnswer(answer);
        }
    }

    private boolean checkQuestion() {
        boolean check = true;
        if (this.etName.getText().toString().matches("")) {
            this.etName.setError(getString(R.string.et_error_empty));
            check = false;
        } else {
            this.etName.setError(null);
        }

        int goodAnswer = 0;
        int nameAnswerEmpty = 0;
        for (int i = 0; i < this.llAllAnswer.getChildCount(); i++) {
            View llAnswer = llAllAnswer.getChildAt(i);
            if (spType.getSelectedItem().toString().equals("QCM")) {
                CheckBox cbAnswer = llAnswer.findViewById(R.id.ck_answer);
                if (cbAnswer.isChecked()) {
                    goodAnswer++;
                }
            } else {
                RadioButton rbAnswer = llAnswer.findViewById(R.id.rb_answer);
                if (rbAnswer.isChecked()) {
                    goodAnswer++;
                }
            }
            EditText etAnswer = llAnswer.findViewById(R.id.et_answer);
            if (etAnswer.getText().toString().trim().matches("")) {
                nameAnswerEmpty++;
            }
        }

        if (this.llAllAnswer.getChildCount() == 0) { // si il n'y a pas de réponsea la question
            this.tvErrorAnswer.setText(getString(R.string.et_question_empty_answer));
            check = false;
        } else if (goodAnswer == 0) { // si il n'y a aucune bonne réponse
            this.tvErrorAnswer.setText(getString(R.string.et_question_empty_good_answer));
            check = false;
        } else if (nameAnswerEmpty > 0) {
            this.tvErrorAnswer.setText(getString(R.string.et_question_empty_name_answer));
            check = false;
        } else {
            this.tvErrorAnswer.setText(null);
        }
        return check;
    }

    public void deleteAnswer(View v) {
        llAllAnswer.removeView((View) v.getParent());
    }

    public void uncheckQcuAnswer(View v) {
        for (int i = 0; i < llAllAnswer.getChildCount(); i++) {
            View llAnswer = llAllAnswer.getChildAt(i);
            RadioButton rbAnswer = llAnswer.findViewById(R.id.rb_answer);
            rbAnswer.setChecked(false);
        }
        RadioButton rbAnswerClicked = v.findViewById(R.id.rb_answer);
        rbAnswerClicked.setChecked(true);
    }

    private void createQuestion() {
        questionModele.newQuestion(question, getBase64Img(), new QuestionModele.QuestionCallBack() {
            @Override
            public void onSuccess(Question questionCreate) {
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_part", false);
                paquet.putParcelable("quiz", part.getQuiz());
                paquet.putParcelable("part", part);
                Intent intent = new Intent(QuestionActivity.this, PartsActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_question), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_question), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    private void setQuestion() {
        this.questionModele.setQuestion(question, getBase64Img(), new QuestionModele.QuestionCallBack() {
            @Override
            public void onSuccess(Question questionCreate) {
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_part", false);
                paquet.putParcelable("quiz", part.getQuiz());
                paquet.putParcelable("part", part);
                Intent intent = new Intent(QuestionActivity.this, PartsActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_question), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_question), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    private void updateDataActivity() {
        int posType = 0;
        if (this.question.getType().equals("QCM")) {
            posType = 1;
        }
        this.spType.setSelection(posType);
        this.spGrade.setSelection(this.question.getGrade() - 1);
        this.etName.setText(this.question.getName());
        if (this.question.getMedia() != null) {
            Picasso.with(this).load(this.question.getMedia()).into(ivImg);
            this.tvImg.setText(R.string.btn_question_delete_img);
            this.tvImg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross_24dp, 0, 0, 0);
        }

        for (Answer answer : this.question.getAnswers()) {
            Log.d("APP", "on rajoute une reponse ============");

            View rowView;
            if (this.question.getType().equals("QCM")) {
                rowView = this.layoutInflater.inflate(R.layout.question_qcm_row, this.llAllAnswer, false);
                CheckBox cbAnswer = rowView.findViewById(R.id.ck_answer);
                cbAnswer.setChecked(answer.isCorrect());
            } else {
                rowView = this.layoutInflater.inflate(R.layout.question_qcu_row, this.llAllAnswer, false);
                RadioButton rbAnswer = rowView.findViewById(R.id.rb_answer);
                rbAnswer.setChecked(answer.isCorrect());
            }
            EditText etAnswer = rowView.findViewById(R.id.et_answer);
            etAnswer.setText(answer.getName());
            rowView.setId(answer.getId());
            this.llAllAnswer.addView(rowView);
        }
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

    private void hydrateListType() {
        this.listType = new ArrayList<>();
        this.listType.add("QCU");
        this.listType.add("QCM");
    }

    private void hydrateListGrade() {
        this.listGrade = new ArrayList<>();
        this.listGrade.add("1");
        this.listGrade.add("2");
        this.listGrade.add("3");
        this.listGrade.add("4");
        this.listGrade.add("5");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMG && resultCode == RESULT_OK) {
            Uri pathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathUri);
                this.ivImg.setImageBitmap(bitmap);
                this.tvImg.setText(R.string.btn_question_delete_img);
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
                if (isNewQuestion) {
                    Bundle paquet = new Bundle();
                    paquet.putBoolean("new_part", false);
                    paquet.putParcelable("quiz", part.getQuiz());
                    paquet.putParcelable("part", part);
                    Intent intent = new Intent(QuestionActivity.this, PartsActivity.class);
                    intent.putExtras(paquet);
                    startActivity(intent);
                } else {
                    updateQuestion();
                    setQuestion();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
