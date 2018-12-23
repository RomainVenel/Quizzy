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
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QuizModele {

    private Context context;
    private RequestQueue queue;

    public QuizModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void newQuiz(final String name, final String media, final NewQuizCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + Session.getSession().getUser().getId() + "/quiz/new",
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APP", "Response ==> " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    callBack.onSuccess(json.getInt("id"), json.getString("media"));
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
                params.put("name", name);
                if(media != null){
                    params.put("media", media);
                }

                return params;
            }
        };
        queue.add(request);
    }

    public void setQuiz(final Quiz quiz, final String media, final SetQuizCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "quiz/edit/" + quiz.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            quiz.setMedia(json.getString("media"));
                            callBack.onSuccess(quiz);
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
                params.put("name", quiz.getName());
                if(media != null){
                    params.put("media", media);
                }

                return params;
            }
        };
        queue.add(request);
    }

    public interface NewQuizCallBack {
        void onSuccess(int quiz_id, String media); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

    public interface SetQuizCallBack {
        void onSuccess(Quiz quiz); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}