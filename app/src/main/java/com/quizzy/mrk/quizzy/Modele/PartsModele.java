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
import com.quizzy.mrk.quizzy.Entities.Answer;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Technique.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PartsModele {

    private Context context;
    private RequestQueue queue;

    public PartsModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void newPart(final Quiz quiz, final String name, final String desc, final String media, final NewPartCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "quiz/" + quiz.getId() + "/part/new",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            callBack.onSuccess(json.getInt("id"), Application.getUrlServeur() + json.getString("media"));
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
                params.put("desc", desc);
                if (media != null) {
                    params.put("media", media);
                }

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void setPart(final Part part, final String media, final SetPartCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "part/edit/" + part.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.isNull("media")) {
                                part.setMedia(null);
                            } else {
                                part.setMedia(Application.getUrlServeur() + json.getString("media"));
                            }
                            callBack.onSuccess(part);
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
                params.put("name", part.getName());
                params.put("desc", part.getDesc());
                if (media != null) {
                    params.put("media", media);
                }

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void getQuestions(final Part part, final PartsModele.getQuestionsCallBack callBack) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "part/" + part.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("APP", "On recup les questions ==> " + response);
                        ArrayList<Question> questions = new ArrayList<Question>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject q = response.getJSONObject(i);

                                ArrayList<Answer> answers = new ArrayList<Answer>();
                                JSONArray allAnswers = q.getJSONArray("answers");
                                for (int j = 0; j < allAnswers.length(); j++) {
                                    JSONObject a = allAnswers.getJSONObject(j);
                                    answers.add(
                                            new Answer(
                                                    a.getInt("id"),
                                                    a.getString("name"),
                                                    a.getBoolean("isCorrect")
                                            )
                                    );
                                }

                                String media;
                                if (q.isNull("media")) {
                                    media = null;
                                } else {
                                    media = Application.getUrlServeur() + q.getString("media");
                                }

                                questions.add(
                                        new Question(
                                                q.getInt("id"),
                                                q.getString("type"),
                                                q.getInt("grade"),
                                                q.getString("name"),
                                                media,
                                                part,
                                                answers
                                        )
                                );
                                //Log.d("APP", "QUESTIONMODELE ==> " + questions);
                            }
                            callBack.onSuccess(questions);
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
        Log.d("APP", "REQUEST ==> " + request);
        queue.add(request);
    }

    public void deleteQuestion(final Question question, final deleteQuestionCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                Application.getUrlServeur() + "question/delete/" + question.getId(),
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

    public interface NewPartCallBack {
        void onSuccess(int part_id, String media); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

    public interface SetPartCallBack {
        void onSuccess(Part partUpdate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

    public interface getQuestionsCallBack {
        void onSuccess(ArrayList<Question> questions); // recuperation des parties d'une quiz

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

    public interface deleteQuestionCallBack {
        void onSuccess();

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}