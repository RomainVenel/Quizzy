package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class DashboardModele {

    private Context context;
    private RequestQueue queue;

    public DashboardModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void getQuizNotFinished(final User user, final DashboardCallBack callBack) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Application.getUrlServeur() + user.getId() + "/quiz/0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "Response ==> " + response);
                        ArrayList<Quiz> listQuiz = new ArrayList<Quiz>();
                        try {
                            JSONArray json = new JSONArray(response);
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject quiz = json.getJSONObject(i);

                                String media;
                                if (quiz.getString("media") == null) {
                                    media = null;
                                } else {
                                    media = Application.getUrlServeur() + quiz.getString("media");
                                }

                                listQuiz.add(new Quiz(
                                                quiz.getInt("id"),
                                                quiz.getString("name"),
                                                media,
                                                user,
                                                null,
                                                0
                                        )
                                );
                            }
                            callBack.onSuccess(listQuiz);
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
        });
        queue.add(request);
    }

    public interface DashboardCallBack {
        void onSuccess(ArrayList<Quiz> listQuizNotFinished); // utilisateur trouvé en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}