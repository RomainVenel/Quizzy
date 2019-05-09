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

    public void getFriendsRequest(final User user, final friendsRequestCallBack callBack) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "friend/" + user.getId() + "/request",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("APP", "On recup les demandes d'amis ==> " + response);
                        ArrayList<User> friendsRequest = new ArrayList<User>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friend = response.getJSONObject(i);
                                JSONObject birth = friend.getJSONObject("birthDate");

                                friendsRequest.add(new User(
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
                            callBack.onSuccess(friendsRequest);
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
                    Log.d("APP", "bug => " + error.toString());
                    callBack.onErrorVollet();
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void choiceFriendRequest(final User currentUser, final User userSender, final boolean choice, final choiceFriendCallBack callBack) {
        int choiceUser = choice == true ? 1 : 0;
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "friend/request/" + currentUser.getId() + "/" + userSender.getId() + "/" + choiceUser,
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

    public void getFriendsCanBeAdd(final User user, final String search, final FriendListCallBack callBack) {

        StringRequest request = new StringRequest(Request.Method.POST, Application.getUrlServeur() + "friend/add/possible/" + user.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APP", "on recup les amis qui peuvent etre ajouté ==> " + response);
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

    public void addFriend(final User userSender, final User user, final choiceFriendCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "add/friend/" + userSender.getId() + "/" + user.getId(),
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

    public  interface friendsRequestCallBack {
        void onSuccess(ArrayList<User> friends);
        void onErrorNetwork(); // Pas de connexion
        void onErrorVollet(); // Erreur de volley
    }

    public interface deleteFriendCallBack {
        void onSuccess();
        void onErrorNetwork(); // Pas de connexion
        void onErrorVollet(); // Erreur de volley
    }

    public interface choiceFriendCallBack {
        void onSuccess();
        void onErrorNetwork(); // Pas de connexion
        void onErrorVollet(); // Erreur de volley
    }
}
