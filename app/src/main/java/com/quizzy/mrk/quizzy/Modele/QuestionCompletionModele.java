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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.PartCompletion;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.QuestionCompletion;
import com.quizzy.mrk.quizzy.Entities.QuizCompletion;
import com.quizzy.mrk.quizzy.Technique.Application;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QuestionCompletionModele {

    private Context context;
    private RequestQueue queue;

    public QuestionCompletionModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void newQuestionCompletion(final PartCompletion pc, final Question question, final int score, final QuestionCompletionModele.QuestionCompletionCallBack callBack) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "partCompletion/" + pc.getId() + "/question/" + question.getId() + "/score/" + score + "/questionCompletion/new",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject qcGiven = response.getJSONObject("qc");

                            int id = qcGiven.getInt("id");
                            int score = qcGiven.getInt("score");
                            int pcId = qcGiven.getInt("pc");
                            int questionId = qcGiven.getInt("question");

                            PartCompletion partForQC = new PartCompletion(pcId);
                            Question questionForQC = new Question(questionId);
                            QuestionCompletion qc = new QuestionCompletion(id, score, partForQC, questionForQC);
                            callBack.onSuccess(qc);


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

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void removeQuestionCompletion(final PartCompletion pc, final Question question, final QuestionCompletionModele.QuestionCompletionCallBack callBack) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "partCompletion/" + pc.getId() + "/question/" + question.getId() + "/questionCompletion/remove",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public interface QuestionCompletionCallBack {
        void onSuccess(QuestionCompletion questionCompletionCreate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

}
