package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class UserModele {

    private Context context;
    private RequestQueue queue;

    public UserModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void updateProfile(final String lastName, final String firstName, final String username, final String email, final User user, final UserCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "update/profil/" + user.getId(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APP", "Response ==> " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean("status")) {
                        callBack.onSuccess();
                    } else {
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
                params.put("last_name", lastName);
                params.put("first_name", firstName);
                params.put("username", username);
                params.put("email", email);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public interface UserCallBack {
        void onSuccess(); // updated in bdd

        void onErrorData(String error); // utilisateur non trouvé en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }


}