package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class InscriptionModele {

    private Context context;
    private RequestQueue queue;
    private String URL = Application.getUrlServeur() + "inscription";

    public InscriptionModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void inscription(final String nom, final String prenom, final String username, final GregorianCalendar birthday, final String email, final String password, final String media, final InscriptionCallBack callBack) {
        StringRequest request = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APP", "Response ==> " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if(json.getBoolean("status")){
                        User user = new User(
                                json.getInt("id"),
                                prenom,
                                nom,
                                username,
                                birthday,
                                password,
                                email,
                                Application.getUrlServeur() + json.getString("media")
                        );
                        Session.getSession().ouvrir(user);
                        callBack.onSuccess();
                    }
                    else{
                        callBack.onErrorData(json.getString("error"));
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
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("username", username);
                params.put("birthday", birthday.get(Calendar.YEAR) + "-" + birthday.get(Calendar.MONTH) + "-" + birthday.get(Calendar.DAY_OF_MONTH));
                params.put("email", email);
                params.put("password", password);
                params.put("media", media);

                return params;
            }
        };
        queue.add(request);
    }

    public interface InscriptionCallBack {
        void onSuccess(); // utilisateur insere en bdd

        void onErrorData(String error); // utilisateur non trouvé en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }


}