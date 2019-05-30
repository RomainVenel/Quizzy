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
import com.quizzy.mrk.quizzy.Entities.Answer;
import com.quizzy.mrk.quizzy.Entities.AnswerCompletion;
import com.quizzy.mrk.quizzy.Entities.PartCompletion;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.QuestionCompletion;
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

    /*public void newAnswerCompletion(final QuestionCompletion qc, final Answer answer, final AnswerCompletionModele.AnswerCompletionCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + "/questionCompletion/" + qc + "/answer/" + answer + "/answerCompletion/new",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            qc.setId(json.getInt("id"));
                            qc.setScore(json.getInt("score"));
                            qc.setQuestion(question);
                            callBack.onSuccess(response);
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
                params.put("score", String.valueOf(qc.getScore()));

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }*/

    public interface AnswerCompletionCallBack {
        void onSuccess(AnswerCompletion answerCompletionCreate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

}
