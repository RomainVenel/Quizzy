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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.quizzy.mrk.quizzy.Entities.Answer;
import com.quizzy.mrk.quizzy.Entities.AnswerCompletion;
import com.quizzy.mrk.quizzy.Entities.PartCompletion;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.QuestionCompletion;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.QuizCompletion;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizCompletionModele {

    private Context context;
    private RequestQueue queue;

    public QuizCompletionModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void newQuizCompletion(final Quiz quiz, final QuizCompletionModele.QuizCompletionCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Application.getUrlServeur() + Session.getSession().getUser().getId() + "/" + quiz.getId() + "/quizCompletion/new",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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

    public void getQuizCompletion(final Quiz quiz, final QuizCompletionModele.QuizCompletionCallBack callBack) {
        Log.d("APP", "JE RENTRE?" + Application.getUrlServeur() + Session.getSession().getUser().getId() + "/" + quiz.getId() + "/quizCompletion/get");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Application.getUrlServeur() + Session.getSession().getUser().getId() + "/" + quiz.getId() + "/quizCompletion/get",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject qcGiven = response.getJSONObject("qc");

                            int id = qcGiven.getInt("id");
                            int userId = qcGiven.getInt("user");
                            int quizId = qcGiven.getInt("quiz");

                            User userForQC = new User(userId);
                            Quiz quizForQC = new Quiz(quizId);
                            QuizCompletion qc = new QuizCompletion(id, userForQC, quizForQC);
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

    public void setQuizCompletionScore(final QuizCompletion qc, final QuizCompletionModele.QuizCompletionCallBack callBack) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Application.getUrlServeur() + qc.getId() + "/quizCompletion/setScore",
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

    public interface QuizCompletionCallBack {
        void onSuccess(QuizCompletion quizCompletionCreate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

}
