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
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.QuizCompletion;
import com.quizzy.mrk.quizzy.Entities.User;
import com.quizzy.mrk.quizzy.Technique.Application;
import com.quizzy.mrk.quizzy.Technique.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PartCompletionModele {

    private Context context;
    private RequestQueue queue;

    public PartCompletionModele(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void newPartCompletion(final Part part, final QuizCompletion qc, final PartCompletionModele.PartCompletionCallBack callBack) {
        Log.d("APP", "MODELE PART ON RENTRE LA MON FRERE Y!");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Application.getUrlServeur() + part.getId() + "/" + qc.getId() + "/partCompletion/new",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject pcGiven = response.getJSONObject("qc");

                            int id = pcGiven.getInt("id");
                            int partId = pcGiven.getInt("part");
                            int qcId = pcGiven.getInt("qc");

                            Part partForPC = new Part(partId);
                            QuizCompletion quizForQC = new QuizCompletion(qcId);
                            PartCompletion pc = new PartCompletion(id, partForPC, quizForQC);
                            callBack.onSuccess(pc);


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

    public void getPartCompletion(final Part part, final QuizCompletion qc, final PartCompletionModele.PartCompletionCallBack callBack) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Application.getUrlServeur() + part.getId() + "/" + qc.getId() + "/partCompletion/get",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject pcGiven = response.getJSONObject("pc");

                            int id = pcGiven.getInt("id");
                            int partId = pcGiven.getInt("part");
                            int qcId = pcGiven.getInt("qc");

                            Part partForQC = new Part(partId);
                            QuizCompletion qcForQC = new QuizCompletion(qcId);
                            PartCompletion pc = new PartCompletion(id, partForQC, qcForQC);
                            callBack.onSuccess(pc);


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

    public interface PartCompletionCallBack {
        void onSuccess(PartCompletion partCompletionCreate); // quiz insere en bdd

        void onErrorNetwork(); // Pas de connexion

        void onErrorVollet(); // Erreur de volley
    }

}
