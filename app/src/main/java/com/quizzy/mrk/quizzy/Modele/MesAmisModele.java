package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MesAmisModele {

    private Context context;
    private RequestQueue queue;

    public MesAmisModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void getFriendsList(final User user, final String search, final FriendListCallBack callBack) {

        StringRequest request = new StringRequest(Request.Method.POST, Application.getUrlServeur() + "friends/" + user.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APP", "Response ==> " + response);
                ArrayList<User> friendsList = new ArrayList<User>();
                try {
                    JSONArray json = new JSONArray(response);
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject friend = json.getJSONObject(i);
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search", search);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void deleteFriend(final User currentUser, User user, final deleteFriendCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                Application.getUrlServeur() + "friend/delete/" + currentUser.getId() + "/" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        callBack.onSuccess();
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

    public interface deleteFriendCallBack {
        void onSuccess();

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}