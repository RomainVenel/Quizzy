package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class ConnexionModele {

    private Context context;
    private RequestQueue queue;
    private String URL = Application.getUrlServeur() + "login";

    public ConnexionModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void authentication(final String username, final String password, final ConnexionCallBack callBack) {
        StringRequest request = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APP", "Response ==> " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean("status")) { // utilisateur trouvé en bdd
                        JSONObject birth = json.getJSONObject("birthDate");

                        User user = new User(
                                json.getInt("id"),
                                json.getString("firstName"),
                                json.getString("lastName"),
                                json.getString("username"),
                                new GregorianCalendar(birth.getInt("year"), birth.getInt("month"), birth.getInt("day")),
                                json.getString("password"),
                                json.getString("email"),
                                Application.getUrlServeur() + json.getString("media")
                        );
                        Session.getSession().ouvrir(user);
                        callBack.onSuccess();
                    } else {
                        callBack.onErrorLogin();
                    }
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
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
        queue.add(request);
    }

    public interface ConnexionCallBack {
        void onSuccess(); // utilisateur trouvé en bdd

        void onErrorLogin(); // utilisateur non trouvé en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}