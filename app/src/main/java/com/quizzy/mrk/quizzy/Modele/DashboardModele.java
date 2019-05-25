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
import com.android.volley.toolbox.StringRequest;
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
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Application.getUrlServeur() + user.getId() + "/quiz",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("APP", "on recup les quiz ==> " + response);
                        ArrayList<Quiz> quizNotFinished = new ArrayList<>();
                        ArrayList<Quiz> quizShared = new ArrayList<>();

                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray jQuizNotFinished = json.getJSONArray("quiz_not_finished");
                            JSONArray jQuizShared = json.getJSONArray("quiz_shared");

                            for (int i = 0; i < jQuizNotFinished.length(); i++) {
                                JSONObject quiz = jQuizNotFinished.getJSONObject(i);
                                String media = quiz.isNull("media") ? null : Application.getUrlServeur() + quiz.getString("media");
                                quizNotFinished.add(
                                    new Quiz(
                                        quiz.getInt("id"),
                                        quiz.getString("name"),
                                        media,
                                        user,
                                        null,
                                        0
                                    )
                                );
                            }

                            for (int i = 0; i < jQuizShared.length(); i++) {
                                JSONObject quiz = jQuizShared.getJSONObject(i);
                                String media = quiz.isNull("media") ? null : Application.getUrlServeur() + quiz.getString("media");
                                quizShared.add(
                                    new Quiz(
                                        quiz.getInt("id"),
                                        quiz.getString("name"),
                                        media,
                                        user,
                                        null,
                                        0
                                    )
                                );
                            }

                            callBack.onSuccess(quizNotFinished, quizShared, json.getInt("friends_request_counter"));
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
        void onSuccess(ArrayList<Quiz> quizNotFinished, ArrayList<Quiz> quizShared, int friendsRequestCounter); // utilisateur trouvé en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }
}