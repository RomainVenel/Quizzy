package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
                if (media != null) {
                    params.put("media", media);
                }

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
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
                            if (json.isNull("media")) {
                                quiz.setMedia(null);
                            } else {
                                quiz.setMedia(Application.getUrlServeur() + json.getString("media"));
                            }
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
                if (media != null) {
                    params.put("media", media);
                }

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void getParts(final Quiz quiz, final getPartsQuizCallBack callBack) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "quiz/" + quiz.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("APP", "On recup les parties ==> " + response);
                        ArrayList<Part> parts = new ArrayList<Part>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject part = response.getJSONObject(i);

                                String media;
                                if (part.isNull("media")) {
                                    media = null;
                                } else {
                                    media = Application.getUrlServeur() + part.getString("media");
                                }

                                String desc;
                                if (part.isNull("desc")) {
                                    desc = null;
                                } else {
                                    desc = part.getString("desc");
                                }

                                parts.add(
                                        new Part(
                                                part.getInt("id"),
                                                part.getString("name"),
                                                desc,
                                                media,
                                                quiz
                                        )
                                );
                            }
                            callBack.onSuccess(parts);
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

    public interface NewQuizCallBack {
        void onSuccess(int quiz_id, String media); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

    public interface SetQuizCallBack {
        void onSuccess(Quiz quizUpdate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

    public interface getPartsQuizCallBack {
        void onSuccess(ArrayList<Part> parts); // recuperation des parties d'une quiz

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}