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
import com.android.volley.toolbox.StringRequest;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Technique.Application;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QuestionModele {

    private Context context;
    private RequestQueue queue;

    public QuestionModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void newQuestion(final Question question, final String media, final QuestionCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "part/" + question.getPart().getId() + "/question/new",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            question.setId(json.getInt("id"));
                            question.setMedia(Application.getUrlServeur() + json.getString("media"));
                            callBack.onSuccess(question);
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
                params.put("type", question.getType());
                params.put("grade", String.valueOf(question.getGrade()));
                params.put("name", question.getName());
                if (media != null) {
                    params.put("media", media);
                }
                params.put("nbAnswers", String.valueOf(question.getAnswers().size()));
                for (int i = 0; i < question.getAnswers().size(); i++) {
                    params.put("name_answer_"+i, question.getAnswers().get(i).getName());
                    params.put("is_correct_answer_"+i, String.valueOf(question.getAnswers().get(i).isCorrect()));
                }
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void setQuestion(final Question question, final String media, final QuestionCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "question/edit/" + question.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            question.setMedia(Application.getUrlServeur() + json.getString("media"));
                            callBack.onSuccess(question);
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
                params.put("type", question.getType());
                params.put("grade", String.valueOf(question.getGrade()));
                params.put("name", question.getName());
                if (media != null) {
                    params.put("media", media);
                }
                params.put("nbAnswers", String.valueOf(question.getAnswers().size()));
                for (int i = 0; i < question.getAnswers().size(); i++) {
                    params.put("name_answer_"+i, question.getAnswers().get(i).getName());
                    params.put("is_correct_answer_"+i, String.valueOf(question.getAnswers().get(i).isCorrect()));
                }
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }


    public interface QuestionCallBack {
        void onSuccess(Question questionCreate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}