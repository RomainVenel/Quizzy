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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Modele.MesAmisModele;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private MesAmisModele mesAmisModele;
    private ArrayList<User> friendList;
    private ItemAddFriendsAdapteur adapter;

    private EditText etSearch;
    private TextView tvFriendsFound;
    private ListView lvFriend;

    private Handler handler;
    private Boolean canSearch;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_add_friends));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.mesAmisModele = new MesAmisModele(this, this.requestQueue);

        this.etSearch = findViewById(R.id.et_search);
        this.tvFriendsFound = findViewById(R.id.tv_friends_list);
        this.lvFriend = findViewById(R.id.lv_friends_list);

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
    }


    private void showFriendList() {
        this.mesAmisModele.getFriendsCanBeAdd(Session.getSession().getUser(), etSearch.getText().toString().trim(), new MesAmisModele.FriendListCallBack() {
            @Override
            public void onSuccess(ArrayList<User> friends) {
                friendList = friends;
                adapter = new ItemAddFriendsAdapteur(AddFriendActivity.this);
                lvFriend.setAdapter(adapter);
                updateFriendsCounter();
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_add_friend), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_add_friend), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    public void updateFriendsCounter() {
        String str = this.friendList.size() + " " + getResources().getString(R.string.counter_add_friends);
        tvFriendsFound.setText(str);
    }

    public void addFriendDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_dialog_accept_add_friend);
        alertDialogBuilder.setPositiveButton(
                R.string.dialog_btn_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addFriend(friendList.get(position));
                        friendList.remove(position);
                        ((BaseAdapter) lvFriend.getAdapter()).notifyDataSetChanged();
                        ((BaseAdapter) lvFriend.getAdapter()).notifyDataSetInvalidated();
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
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addFriend(User user) {
        this.mesAmisModele.addFriend(Session.getSession().getUser(), user, new MesAmisModele.choiceFriendCallBack() {
            @Override
            public void onSuccess() {
                updateFriendsCounter();
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_add_friend), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_add_friend), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AddFriendActivity.this, ListeAmisActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ItemAddFriendsAdapteur extends ArrayAdapter<User> {
        LayoutInflater inflater;

        public ItemAddFriendsAdapteur(Activity context) {
            super(context, R.layout.adapter_add_friend, friendList);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vItem = convertView;
            if (vItem == null) {
                vItem = getLayoutInflater().inflate(R.layout.adapter_add_friend, parent, false);
            }

            ImageView ivList = vItem.findViewById(R.id.iv_list);
            if (friendList.get(position).getMedia() != null) {
                Picasso.with(getContext()).load(friendList.get(position).getMedia()).into(ivList);
            }

            TextView tvNameFriend = vItem.findViewById(R.id.tv_name_user);
            tvNameFriend.setText(friendList.get(position).getUsername());

            TextView tvEmail = vItem.findViewById(R.id.tv_email_user);
            tvEmail.setText(friendList.get(position).getEmail());

            ImageButton btnAdd = vItem.findViewById(R.id.btn_add_list);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriendDialog(position);
                }
            });
            return vItem;
        }
    }
}
