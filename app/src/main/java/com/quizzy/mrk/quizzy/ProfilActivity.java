package com.quizzy.mrk.quizzy;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Technique.Session;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    private TextView tvLastName;
    private TextView tvFirstName;
    private TextView tvEmail;
    private EditText etLastName;
    private EditText etFirstName;
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ImageView ivUserImg = this.findViewById(R.id.profil_img);
        Picasso.with(this).load(Session.getSession().getUser().getMedia()).into(ivUserImg);

        this.tvLastName = findViewById(R.id.tv_profil_lastName);
        this.tvFirstName = findViewById(R.id.tv_profil_firstName);
        this.tvEmail = findViewById(R.id.tv_profil_email);
        this.etLastName = findViewById(R.id.et_profil_lastName);
        this.etFirstName = findViewById(R.id.et_profil_firstName);
        this.etEmail = findViewById(R.id.et_profil_email);

        etLastName.setText(Session.getSession().getUser().getLastName());
        etFirstName.setText(Session.getSession().getUser().getFirstName());
        etEmail.setText(Session.getSession().getUser().getEmail());
    }

}
