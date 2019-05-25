package com.quizzy.mrk.quizzy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Modele.MesAmisModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsRequestActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private MesAmisModele mesAmisModele;
    private ArrayList<User> friendsRequest;

    private TextView tvFriendsRequestFoundCounter;
    private ListView lvFriendsResquest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_request);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_friends_request));
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.mesAmisModele = new MesAmisModele(this, this.requestQueue);

        this.tvFriendsRequestFoundCounter = findViewById(R.id.tv_counter_friends_request);
        this.lvFriendsResquest = findViewById(R.id.lv_friends_request);

        this.getFriendsRequest();
    }

    private void getFriendsRequest() {
        this.mesAmisModele.getFriendsRequest(Session.getSession().getUser(), new MesAmisModele.friendsRequestCallBack() {
            @Override
            public void onSuccess(ArrayList<User> friends) {
                friendsRequest = friends;
                ItemChoiceFriendRequestAdapteur adapter = new ItemChoiceFriendRequestAdapteur(FriendsRequestActivity.this);
                lvFriendsResquest.setAdapter(adapter);
                updateFriendsRequestCounter();
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_friends_request), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_friends_request), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    public void acceptFriendRequestDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_dialog_accept_friend_request);
        alertDialogBuilder.setPositiveButton(
                R.string.dialog_btn_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiceRequest(position, true);
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

    public void refuseFriendRequestDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_dialog_refuse_friend_request);
        alertDialogBuilder.setPositiveButton(
                R.string.dialog_btn_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiceRequest(position, false);
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

    public void updateFriendsRequestCounter() {
        this.tvFriendsRequestFoundCounter.setText(this.friendsRequest.size() + " " + getResources().getString(R.string.counter_friend_requests));
    }

    public void choiceRequest(final int position, boolean choice) {
        this.mesAmisModele.choiceFriendRequest(Session.getSession().getUser(), this.friendsRequest.get(position), choice, new MesAmisModele.choiceFriendCallBack() {
            @Override
            public void onSuccess() {
                friendsRequest.remove(position);
                ((BaseAdapter) lvFriendsResquest.getAdapter()).notifyDataSetChanged();
                ((BaseAdapter) lvFriendsResquest.getAdapter()).notifyDataSetInvalidated();
                updateFriendsRequestCounter();
            }

            @Override
            public void onErrorNetwork() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_friends_request), R.string.error_connexion_http, 2500);
                snackbar.show();
            }

            @Override
            public void onErrorVollet() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_friends_request), R.string.error_vollet, 2500);
                snackbar.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FriendsRequestActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ItemChoiceFriendRequestAdapteur extends ArrayAdapter<User> {
        LayoutInflater inflater;

        public ItemChoiceFriendRequestAdapteur(Activity context) {
            super(context, R.layout.adapter_friends_request, friendsRequest);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vItem = convertView;
            if (vItem == null) {
                vItem = getLayoutInflater().inflate(R.layout.adapter_friends_request, parent, false);
            }

            if (friendsRequest.get(position).getMedia() != null) {
                ImageView ivFriend = vItem.findViewById(R.id.iv_friend);
                Picasso.with(getContext()).load(friendsRequest.get(position).getMedia()).into(ivFriend);
            }

            TextView tvUsernameFriend = vItem.findViewById(R.id.tv_name_friend);
            tvUsernameFriend.setText(friendsRequest.get(position).getUsername());

            TextView tvEmailFriend = vItem.findViewById(R.id.tv_email_friend);
            tvEmailFriend.setText(friendsRequest.get(position).getEmail());

            ImageButton btnAcceptRequest = vItem.findViewById(R.id.btn_accept_friends);
            btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptFriendRequestDialog(position);
                }
            });

            ImageButton btnRefuseRequest = vItem.findViewById(R.id.btn_refuse_friends);
            btnRefuseRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refuseFriendRequestDialog(position);
                }
            });
            return vItem;
        }
    }
}
