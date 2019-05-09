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
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Modele.ConnexionModele;
import com.quizzy.mrk.quizzy.Modele.MesAmisModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ListeAmisActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private MesAmisModele mesAmisModele;
    private ArrayList<User> friendList;
    private ItemDeleteFriendAdapteur adapter;

    private EditText etSearch;
    private TextView tvMessage;
    private ListView lvFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_amis);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_mes_amis));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.mesAmisModele = new MesAmisModele(this, this.requestQueue);

        this.etSearch = findViewById(R.id.et_search);
        this.tvMessage = findViewById(R.id.tv_friends_list);
        this.lvFriend = findViewById(R.id.lv_friends_list);
         

        this.showFriendList();

        TextWatcher searchWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ListeAmisActivity.this.adapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        this.etSearch.addTextChangedListener(searchWatcher);
    }

    private void showFriendList() {
        this.mesAmisModele.getFriendsList(Session.getSession().getUser(), new MesAmisModele.FriendListCallBack() {
            @Override
            public void onSuccess(ArrayList<User> friends) {
                friendList = friends;
                adapter = new ItemDeleteFriendAdapteur(ListeAmisActivity.this);
                lvFriend.setAdapter(adapter);
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


    public void deleteFriendDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_dialog_delete_friend);
        alertDialogBuilder.setPositiveButton(
                R.string.dialog_btn_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        deleteQuestion(listQuestions.get(position));
//                        listQuestions.remove(position);
//                        ((BaseAdapter) lvQuestions.getAdapter()).notifyDataSetChanged();
//                        ((BaseAdapter) lvQuestions.getAdapter()).notifyDataSetInvalidated();
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

    private void deleteFriend(User user) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ListeAmisActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ItemDeleteFriendAdapteur extends ArrayAdapter<User> {

        private ArrayList<User> mOriginalValues; // Original Values
        private ArrayList<User> mDisplayedValues;
        LayoutInflater inflater;

        public ItemDeleteFriendAdapteur(Activity context) {
            super(context, R.layout.adapter_delete_friend, friendList);
            this.mOriginalValues = friendList;
            this.mDisplayedValues = friendList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vItem = convertView;
            if (vItem == null) {
                vItem = getLayoutInflater().inflate(R.layout.adapter_delete_friend, parent, false);
            }

            ImageView ivList = vItem.findViewById(R.id.iv_list);
            if (friendList.get(position).getMedia() != null) {
                Picasso.with(getContext()).load(friendList.get(position).getMedia()).into(ivList);
            }

            TextView tvNameFriend = vItem.findViewById(R.id.tv_name_user);
            tvNameFriend.setText(friendList.get(position).getLastName() + " " + friendList.get(position).getFirstName());

            TextView tvDate = vItem.findViewById(R.id.tv_email_user);
            tvDate.setText(friendList.get(position).getEmail());

            ImageButton btnDelete = vItem.findViewById(R.id.btn_delete_list);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriendDialog(position);
                }
            });
            return vItem;
        }
    }
}
