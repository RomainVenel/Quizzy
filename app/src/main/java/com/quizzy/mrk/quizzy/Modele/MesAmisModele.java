package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Technique.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class MesAmisModele {

    private Context context;
    private RequestQueue queue;

    public MesAmisModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void getFriendsList(final User user, final FriendListCallBack callBack) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                Application.getUrlServeur() + "friends/" + user.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("APP", "mes quiz ==> " + response);
                        ArrayList<User> friendsList = new ArrayList<User>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friend = response.getJSONObject(i);
                                JSONObject birth = friend.getJSONObject("birthDate");

                                friendsList.add(new User(
                                        friend.getInt("id"),
                                        friend.getString("firstName"),
                                        friend.getString("lastName"),
                                        friend.getString("username"),
                                        new GregorianCalendar(birth.getInt("year"), birth.getInt("month"), birth.getInt("day")),
                                        friend.getString("password"),
                                        friend.getString("email"),
                                        friend.isNull("media") ? null : Application.getUrlServeur() + friend.getString("media")
                                ));
                            }
                            callBack.onSuccess(friendsList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    callBack.onErrorNetwork();
                } else if (error instanceof VolleyError) {
                    Log.d("APP", "bug => " + error.getMessage());
                    callBack.onErrorVollet();
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public interface FriendListCallBack {
        void onSuccess(ArrayList<User> friends);

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}
