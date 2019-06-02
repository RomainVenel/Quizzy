package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Modele.ConnexionModele;
import com.quizzy.mrk.quizzy.Modele.MesAmisModele;
import com.quizzy.mrk.quizzy.Modele.QuizModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ShareQuizActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private MesAmisModele mesAmisModele;
    private QuizModele sharesModele;
    private ArrayList<User> friendList;
    private ItemFriendAdapteur adapter;
    private EditText etSearch;
    private TextView tvFriendsFound;
    private ListView lvFriend;

    private Quiz quiz;

    private Handler handler;
    private Boolean canSearch;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_quiz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_my_friends));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.mesAmisModele = new MesAmisModele(this, this.requestQueue);
        this.sharesModele = new QuizModele(this, this.requestQueue);
        this.etSearch = findViewById(R.id.et_search);
        this.tvFriendsFound = findViewById(R.id.tv_friends_list);
        this.lvFriend = findViewById(R.id.lv_friends_list);
        this.quiz = getIntent().getExtras().getParcelable("quiz");

        this.handler = new android.os.Handler();
        this.canSearch = true;
        this.runnable = new Runnable() {
            public void run() {
                showFriendList();
                canSearch = true;
            }
        };

        this.showFriendList();

        TextWatcher searchWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if(canSearch == false) {
                    handler.removeCallbacks(runnable);
                }
                canSearch = false;
                handler.postDelayed(runnable, 600);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        this.etSearch.addTextChangedListener(searchWatcher);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showFriendList() {
        this.mesAmisModele.getFriendsList(Session.getSession().getUser(), etSearch.getText().toString().trim(), new MesAmisModele.FriendListCallBack() {
            @Override
            public void onSuccess(ArrayList<User> friends) {
                friendList = friends;
                adapter = new ItemFriendAdapteur(ShareQuizActivity.this);
                lvFriend.setAdapter(adapter);
                updateFriendsCounter();
                lvFriend.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(ShareQuizActivity.this);
                                alertDialogBuilder.setMessage(R.string.message_dialog_share_quiz);
                                alertDialogBuilder.setPositiveButton(
                                        R.string.dialog_btn_yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                sharesModele.shareQuiz(friendList.get(position), quiz, new QuizModele.ShareQuizCallBack() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Intent intent = new Intent(ShareQuizActivity.this, MesQuizActivity.class);
                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onErrorNetwork() {
                                                    }

                                                    @Override
                                                    public void onErrorVollet() {
                                                    }
                                                });
                                            }
                                        });


                                alertDialogBuilder.setNegativeButton(
                                        R.string.dialog_btn_no,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                Intent intent = new Intent(ShareQuizActivity.this, ShareQuizActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        }
                );
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_liste_amis), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_liste_amis), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    public void updateFriendsCounter() {
        String str = this.friendList.size() + " " + getResources().getString(R.string.friends_found);
        tvFriendsFound.setText(str);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ShareQuizActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ItemFriendAdapteur extends ArrayAdapter<User> {
        LayoutInflater inflater;

        public ItemFriendAdapteur(Activity context) {
            super(context, R.layout.adapter_delete_friend, friendList);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vItem = convertView;
            if (vItem == null) {
                vItem = getLayoutInflater().inflate(R.layout.adapter_friends_share, parent, false);
            }

            ImageView ivList = vItem.findViewById(R.id.iv_list);
            if (friendList.get(position).getMedia() != null) {
                Picasso.with(getContext()).load(friendList.get(position).getMedia()).into(ivList);
            }

            TextView tvUsernameFriend = vItem.findViewById(R.id.tv_name_user);
            tvUsernameFriend.setText(friendList.get(position).getUsername());

            TextView tvEmail = vItem.findViewById(R.id.tv_email_user);
            tvEmail.setText(friendList.get(position).getEmail());

            return vItem;
        }
    }
}