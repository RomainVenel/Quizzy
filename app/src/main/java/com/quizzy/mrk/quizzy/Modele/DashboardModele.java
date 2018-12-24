package com.quizzy.mrk.quizzy.Modele;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Technique.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DashboardModele {

    private Context context;
    private RequestQueue queue;

    public DashboardModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void getQuizNotFinished(final User user, final DashboardCallBack callBack) {
        JsonArrayRequest  request = new JsonArrayRequest(
                Request.Method.GET,
                Application.getUrlServeur() + user.getId() + "/quiz/status/" + 0,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("APP", "on recup les quiz non fini ==> " + response);
                        ArrayList<Quiz> listQuiz = new ArrayList<Quiz>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject quiz = response.getJSONObject(i);

                                String media;
                                if (quiz.isNull("media")) {
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
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public interface DashboardCallBack {
        void onSuccess(ArrayList<Quiz> listQuizNotFinished); // utilisateur trouvé en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}