package com.quizzy.mrk.quizzy;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.quizzy.mrk.quizzy.Adapter.CustomAdapter;
import com.quizzy.mrk.quizzy.Adapter.DataItem;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Modele.DashboardModele;
import com.quizzy.mrk.quizzy.Modele.QuizModele;
import com.quizzy.mrk.quizzy.Technique.Session;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RequestQueue requestQueue;
    private DashboardModele dashboardModele;
    private QuizModele quizModele;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToolbar;

    private ListView lvQuizNotFinish;
    private ArrayList<Quiz> quizNotFinished;
    private Button bNewQuiz;
    private Button bCreatedQuiz;
    private Button bSharedQuiz;
    private Button bNotFinishedQuiz;

    private boolean isBig = false;

    private LinearLayout lNotFinishedQuiz;
    private LinearLayout lSharedQuiz;
    private LinearLayout lFinishedQuiz;

    private ListView lvQuizShared;
    private ArrayList<Quiz> quizShared;

    private ListView lvQuizCompleted;
    private ArrayList<Quiz> quizCompleted;

    private TextView tvBadgeFriendsRequest;

    private PieChart pieChart;

    private String[] xData = {"Quiz en cours", "Quiz partagés", "Quiz finis"};

    private ArrayList<DataItem> data = new ArrayList<DataItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.pieChart = findViewById(R.id.chart);

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleAlpha(0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_activity_dashboard));

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        this.dashboardModele = new DashboardModele(this, this.requestQueue);
        this.quizModele = new QuizModele(this, this.requestQueue);

        this.mDrawerLayout = findViewById(R.id.dashboard_drawer);
        this.mToolbar = new ActionBarDrawerToggle(this, this.mDrawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);
        this.mDrawerLayout.addDrawerListener(this.mToolbar);
        this.mToolbar.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.lvQuizNotFinish = findViewById(R.id.list_dashboard_quiz_created);
        this.lvQuizShared = findViewById(R.id.list_dashboard_quiz_shared);
        this.lvQuizCompleted = findViewById(R.id.list_dashboard_quiz_completed);
        this.manageLists();

        // Create quiz
        this.bNewQuiz = findViewById(R.id.btn_dashboard_new_quiz);
        this.bNewQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle paquet = new Bundle();
                paquet.putBoolean("new_quiz", true);
                Intent intent = new Intent(DashboardActivity.this, QuizActivity.class);
                intent.putExtras(paquet);
                startActivity(intent);
            }
        });

        // Edit quiz
        this.bCreatedQuiz = findViewById(R.id.btn_dashboard_created_quiz);
        this.lNotFinishedQuiz = findViewById(R.id.layout_created_quiz);
        this.bCreatedQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCloseList(lNotFinishedQuiz, lvQuizNotFinish, v);

            }
        });

        // Shared quiz
        this.bSharedQuiz = findViewById(R.id.btn_dashboard_quiz_shared);
        this.lSharedQuiz = findViewById(R.id.layout_shared_quiz);
        this.bSharedQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCloseList(lSharedQuiz, lvQuizShared, v);
            }
        });

        // Finished quiz
        this.bNotFinishedQuiz = findViewById(R.id.btn_dashboard_finished_quiz);
        this.lFinishedQuiz = findViewById(R.id.layout_finished_quiz);
        this.bNotFinishedQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCloseList(lFinishedQuiz, lvQuizCompleted, v);
            }
        });
    }

    private void addDataSet(PieChart pieChart, ListView lvNotFinish, ListView lvShared, ListView lvFinished) {

        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        if (lvFinished.getAdapter() == null) {
            final Integer yData[] = {lvNotFinish.getAdapter().getCount(), lvShared.getAdapter().getCount(), 0};

            for (int i = 0; i < yData.length; i++) {
                yEntrys.add(new PieEntry(yData[i] , i));
            }
        } else {
            final Integer yData[] = {lvNotFinish.getAdapter().getCount(), lvShared.getAdapter().getCount(), lvFinished.getAdapter().getCount()};

            for (int i = 0; i < yData.length; i++) {
                yEntrys.add(new PieEntry(yData[i] , i));
            }
        }

        for (int i = 1; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        // create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "pourcentage quiz");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(14);

        // add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(218, 95, 106));
        colors.add(Color.rgb(146, 180, 34));
        colors.add(Color.rgb(173, 206, 183));

        pieDataSet.setColors(colors);

        // add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setTextColor(Color.WHITE);
        legend.setEnabled(true);

        // remove description
        Description desc = pieChart.getDescription();
        desc.setEnabled(false);

        // create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    private void updateDataUserInNavigation(int friendsRequestCounter) {
        NavigationView navigationView = findViewById(R.id.dashboard_nav);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ImageView ivUserImg = headerView.findViewById(R.id.header_nav_img);
        Picasso.with(this).load(Session.getSession().getUser().getMedia()).into(ivUserImg);
        TextView tvNameUser = headerView.findViewById(R.id.header_nav_name);
        tvNameUser.setText(Session.getSession().getUser().getFirstName() + " " + Session.getSession().getUser().getLastName());
        TextView tvEmailUser = headerView.findViewById(R.id.header_nav_email);
        tvEmailUser.setText(Session.getSession().getUser().getEmail());

        tvBadgeFriendsRequest = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.menu_drawer_friends_request));
        tvBadgeFriendsRequest.setGravity(Gravity.CENTER_VERTICAL);
        tvBadgeFriendsRequest.setTypeface(null,Typeface.BOLD);
        tvBadgeFriendsRequest.setTextColor(getResources().getColor(R.color.red));
        if (friendsRequestCounter > 0) {
            tvBadgeFriendsRequest.setText("" + friendsRequestCounter);
        } else {
            tvBadgeFriendsRequest.setText("");
        }
    }

    private void manageLists(){
        this.dashboardModele.getQuizNotFinished(Session.getSession().getUser(), new DashboardModele.DashboardCallBack() {
            @Override
            public void onSuccess(ArrayList<Quiz> listQuizNotFinished, ArrayList<Quiz> listQuizShared, ArrayList<Quiz> listQuizCompleted, int friendsRequestCounter) {
                // Liste contenant les noms des quiz
                ArrayList<String> itemQuizNotFinished = new ArrayList<String>();
                ArrayList<String> itemQuizShared = new ArrayList<String>();
                quizNotFinished = listQuizNotFinished;
                quizShared = listQuizShared;
                quizCompleted = listQuizCompleted;

                // Boucle pour afficher seulement le nom des quiz dans le dashboard
                for(Quiz quiz : quizNotFinished) {
                    String quizName = quiz.getName();
                    itemQuizNotFinished.add(quizName);
                }
                for(Quiz quiz : quizShared) {
                    String quizName = quiz.getName();
                    itemQuizShared.add(quizName);
                }
                for(Quiz quiz : quizCompleted) {
                    getScoreQuiz(quiz);
                }

                ArrayAdapter<String> adaptateurQuizNotFinished = new ArrayAdapter<String>(DashboardActivity.this, R.layout.dahsboard_custom_white_text, itemQuizNotFinished) ;
                ArrayAdapter<String> adaptateurQuizShared = new ArrayAdapter<String>(DashboardActivity.this ,R.layout.dahsboard_custom_white_text, itemQuizShared);
                lvQuizNotFinish.setAdapter(adaptateurQuizNotFinished);
                lvQuizShared.setAdapter(adaptateurQuizShared);

                lvQuizNotFinish.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle paquet = new Bundle();
                                paquet.putBoolean("new_quiz", false);
                                paquet.putParcelable("quiz", quizNotFinished.get(position) );
                                Intent intent = new Intent(DashboardActivity.this, QuizActivity.class);
                                intent.putExtras(paquet);
                                startActivity(intent);
                            }
                        }
                );

                lvQuizShared.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle paquet = new Bundle();
                                paquet.putBoolean("new_quiz", false);
                                paquet.putParcelable("quiz", quizShared.get(position) );
                                Intent intent = new Intent(DashboardActivity.this, ResumQuizActivity.class);
                                intent.putExtras(paquet);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }
                );

                lvQuizCompleted.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle paquet = new Bundle();
                                paquet.putBoolean("new_quiz", false);
                                paquet.putParcelable("quiz", quizCompleted.get(position) );
                                /*Intent intent = new Intent(DashboardActivity.this, ResumQuizActivity.class);
                                intent.putExtras(paquet);
                                startActivity(intent);*/
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }
                );

                updateDataUserInNavigation(friendsRequestCounter);
            }

            @Override
            public void onErrorNetwork() {
            }

            @Override
            public void onErrorVollet() {

            }
        });
    }

    public void openDialogApropos() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_a_propos);
        Button closeDialog = dialog.findViewById(R.id.btn_close_dialog_a_propos);
        dialog.show();

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToolbar.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        if (menuItem.getItemId() == R.id.menu_drawer_profil) { // item profil
            intent = new Intent(DashboardActivity.this, ProfilActivity.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_drawer_friend) { // item mes amis
            intent = new Intent(DashboardActivity.this, ListeAmisActivity.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_drawer_quiz) { // item mes quiz
            intent = new Intent(DashboardActivity.this, MesQuizActivity.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_drawer_friends_request) { // item mes quiz
            intent = new Intent(DashboardActivity.this, FriendsRequestActivity.class);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.menu_drawer_a_propos) { // item à propos
            mDrawerLayout.closeDrawers();
            this.openDialogApropos();
        } else if (menuItem.getItemId() == R.id.menu_drawer_logout) {  // item deconnexion
            Session.getSession().fermer();
            intent = new Intent(DashboardActivity.this, ConnexionActivity.class);
            startActivity(intent);
        }
        return false;
    }

    private void getScoreQuiz(final Quiz quiz) {

        quizModele.getScoreQuiz(quiz.getUser(), quiz, new QuizModele.getScoreQuizCallBack() {
            @Override
            public void onSuccess(String score, String maxScore) {
                final String quizName = quiz.getName();
                final String quizScore = score;
                final String quizMaxScore = maxScore;

                final String resultScores = quizScore + "/" + quizMaxScore;

                DataItem dataItem = new DataItem(quizName, resultScores);
                data.add(dataItem);

                CustomAdapter adapter = new CustomAdapter(DashboardActivity.this, R.layout.row_list_completed, data);

                lvQuizCompleted.setAdapter(adapter);

                addDataSet(pieChart, lvQuizNotFinish, lvQuizShared, lvQuizCompleted);
            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });
    }

    private void openCloseList(final LinearLayout layout, ListView list, View view) {

        if (list.getAdapter() != null) {
            if (list.getAdapter().getCount() != 0) {
                if (!isBig) {
                    ValueAnimator va = ValueAnimator.ofInt(0, 400);
                    va.setDuration(900);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Integer value = (Integer) animation.getAnimatedValue();
                            layout.getLayoutParams().height = value.intValue();
                            layout.requestLayout();
                        }
                    });
                    va.start();
                    isBig = true;
                } else {
                    ValueAnimator va = ValueAnimator.ofInt(400, 0);
                    va.setDuration(900);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Integer value = (Integer) animation.getAnimatedValue();
                            layout.getLayoutParams().height = value.intValue();
                            layout.requestLayout();
                        }
                    });
                    va.start();
                    isBig = false;
                }
                list.setVisibility(View.VISIBLE);
            } else {
                Animation shake = AnimationUtils.loadAnimation(DashboardActivity.this, R.anim.shake);
                view.startAnimation(shake);
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_dashboard),  "Aucun quiz disponible", 2500);
                snackbar.show();
            }
        } else {
            Animation shake = AnimationUtils.loadAnimation(DashboardActivity.this, R.anim.shake);
            view.startAnimation(shake);
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.activity_dashboard),  "Aucun quiz disponible", 2500);
            snackbar.show();
        }
    }
}
