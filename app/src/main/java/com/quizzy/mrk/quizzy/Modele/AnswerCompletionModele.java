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
import com.quizzy.mrk.quizzy.Entities.Answer;
import com.quizzy.mrk.quizzy.Entities.AnswerCompletion;
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

public class AnswerCompletionModele {

    private Context context;
    private RequestQueue queue;

    public AnswerCompletionModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void newAnswerCompletion(final QuestionCompletion qc, final Answer answer, final AnswerCompletionModele.AnswerCompletionCallBack callBack) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "questionCompletion/" + qc.getId() + "/answer/" + answer.getId() + "/answerCompletion/new",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("APP", "Response ==> " + response);
                        try {
                            JSONObject pcGiven = response.getJSONObject("ac");

                            int id = pcGiven.getInt("id");
                            int qcId = pcGiven.getInt("qc");
                            int answerId = pcGiven.getInt("answer");

                            QuestionCompletion qcForAc = new QuestionCompletion(qcId);
                            Answer answerForAC = new Answer(answerId);
                            AnswerCompletion ac = new AnswerCompletion(id, qcForAc, answerForAC);
                            callBack.onSuccess(ac);
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

    public void setScoreForAnswerCompletion(final QuestionCompletion qc, int score, final Answer answer, final AnswerCompletionModele.AnswerCompletionCallBack callBack) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "questionCompletion/" + qc.getId() + "/answer/" + answer.getId() + "/score/" + score + "/answerCompletion/setScore",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("APP","ACGIVEN" +  response);
                        try {

                            JSONObject acGiven = response.getJSONObject("ac");



                            int id = acGiven.getInt("id");
                            int qcId = acGiven.getInt("qc");
                            int answerId = acGiven.getInt("answer");
                            int score = acGiven.getInt("score");

                            QuestionCompletion qcForQC = new QuestionCompletion(qcId);
                            Answer answerForQC = new Answer(answerId);
                            AnswerCompletion ac = new AnswerCompletion(id, qcForQC, answerForQC, score);
                            callBack.onSuccess(ac);


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

    public interface AnswerCompletionCallBack {
        void onSuccess(AnswerCompletion answerCompletionCreate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

}
