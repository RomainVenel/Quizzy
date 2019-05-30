package com.quizzy.mrk.quizzy.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.tabs.TabLayout;
import com.quizzy.mrk.quizzy.DashboardActivity;
import com.quizzy.mrk.quizzy.Entities.Answer;
import com.quizzy.mrk.quizzy.Entities.AnswerCompletion;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.PartCompletion;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.Entities.QuestionCompletion;
import com.quizzy.mrk.quizzy.Entities.Quiz;
import com.quizzy.mrk.quizzy.Entities.QuizCompletion;
import com.quizzy.mrk.quizzy.Enum.SwipeDirection;
import com.quizzy.mrk.quizzy.Modele.AnswerCompletionModele;
import com.quizzy.mrk.quizzy.Modele.PartCompletionModele;
import com.quizzy.mrk.quizzy.Modele.QuestionCompletionModele;
import com.quizzy.mrk.quizzy.Modele.QuizCompletionModele;
import com.quizzy.mrk.quizzy.PartPassageQuizActivity;
import com.quizzy.mrk.quizzy.R;
import com.quizzy.mrk.quizzy.Technique.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionPassageQuizFragment extends Fragment {

    // 1 - Create keys for our Bundle
    private static final String KEY_POSITION="position";
    private static final String KEY_COLOR="color";
    private static final String KEY_PARTS="parts";
    private static final String KEY_QUESTIONS="questions";
    private static final String KEY_QC="qc";
    private QuizCompletionModele quizCompletionModele;
    private PartCompletionModele partCompletionModele;
    private QuestionCompletionModele questionCompletionModele;
    private AnswerCompletionModele answerCompletionModele;
    private int score;
    private int cumul;
    private RequestQueue requestQueue;

    public QuestionPassageQuizFragment() {
    }



    // 2 - Method that will create a new instance of PageFragment, and add data to its bundle.
    public static QuestionPassageQuizFragment newInstance(int position, int color, ArrayList<Part> parts, ArrayList<Question> questions, QuizCompletion qc) {

        // 2.1 Create new fragment
        QuestionPassageQuizFragment frag = new QuestionPassageQuizFragment();

        // 2.2 Create bundle and add it some data
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_COLOR, color);
        args.putParcelableArrayList(KEY_QUESTIONS, questions);
        args.putParcelableArrayList(KEY_PARTS, parts);
        args.putParcelableArrayList(KEY_QUESTIONS, questions);
        args.putParcelableArrayList(KEY_PARTS, parts);
        args.putParcelable(KEY_QC, qc);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 3 - Get layout of PageFragment
        View result = inflater.inflate(R.layout.fragment_question_passage_quiz, container, false);

        // 4 - Get widgets from layout and serialise it
        FrameLayout rootView= (FrameLayout) result.findViewById(R.id.fragment_page_rootview);
        final TableLayout answersView= (TableLayout) result.findViewById(R.id.answers_view);
        ImageView ivQuestion = result.findViewById(R.id.iv_question_quiz);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TabLayout.LayoutParams.WRAP_CONTENT,
                TabLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 30, 50, 10);
        TextView textView= (TextView) result.findViewById(R.id.fragment_page_title);
        Button btnNextPart = (Button) result.findViewById(R.id.btn_next_part);

        // 5 - Get data from Bundle (created in method newInstance)
        final int position = getArguments().getInt(KEY_POSITION, -1);
        final ArrayList<Question> questions = getArguments().getParcelableArrayList(KEY_QUESTIONS);
        final ArrayList<Part> parts = getArguments().getParcelableArrayList(KEY_PARTS);
        final QuizCompletion qc = getArguments().getParcelable(KEY_QC);

        final ArrayList<Answer> listAnswersChecked = new ArrayList<>();
        final ArrayList<Answer> listAnswersIncorrect = new ArrayList<>();

        this.requestQueue = VolleySingleton.getInstance(this.getActivity()).getRequestQueue();
        this.quizCompletionModele = new QuizCompletionModele(this.getActivity(), this.requestQueue);
        this.partCompletionModele = new PartCompletionModele(this.getActivity(), this.requestQueue);
        this.questionCompletionModele = new QuestionCompletionModele(this.getActivity(), this.requestQueue);
        this.answerCompletionModele = new AnswerCompletionModele(this.getActivity(), this.requestQueue);

        for (final Answer answer : questions.get(position).getAnswers()) {
            listAnswersChecked.clear();
            listAnswersIncorrect.clear();
            View rowView;
            if (questions.get(position).getType().equals("QCM")) {

                rowView = inflater.inflate(R.layout.answer_qcm_row, answersView, false);
                TextView answerText = rowView.findViewById(R.id.tv_qcm_answer);
                final CheckBox checkAnswer = rowView.findViewById(R.id.ck_qcm_answer);
                answerText.setText(answer.getName());
                answersView.addView(rowView);

                checkAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkAnswer.isChecked()) {
                            listAnswersChecked.add(answer);
                            if (!answer.isCorrect()) {
                                listAnswersIncorrect.add(answer);
                            }
                            continueIfAnswers(parts.get(0), qc, questions.get(position), listAnswersChecked, listAnswersIncorrect, answer);
                        }else{
                            listAnswersIncorrect.remove(answer);
                            listAnswersChecked.remove(answer);
                            continueIfAnswers(parts.get(0), qc, questions.get(position), listAnswersChecked, listAnswersIncorrect, answer);
                        }
                    }
                });

            }else{

                rowView = inflater.inflate(R.layout.answer_qcu_row, answersView, false);
                TextView answerText = rowView.findViewById(R.id.tv_qcu_answer);
                final RadioButton radioAnswer = rowView.findViewById(R.id.rb_qcu_answer);
                answerText.setText(answer.getName());
                answersView.addView(rowView);

                radioAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < answersView.getChildCount(); i++) {
                            View llAnswer = answersView.getChildAt(i);
                            RadioButton rbAnswer = llAnswer.findViewById(R.id.rb_qcu_answer);
                            rbAnswer.setChecked(false);
                        }
                        RadioButton rbAnswerClicked = view.findViewById(R.id.rb_qcu_answer);
                        rbAnswerClicked.setChecked(true);

                        if (radioAnswer.isChecked()) {
                            listAnswersChecked.clear();
                            listAnswersIncorrect.clear();
                            listAnswersChecked.add(answer);
                            if (!answer.isCorrect()) {
                                listAnswersIncorrect.add(answer);
                            }
                            continueIfAnswers(parts.get(0), qc, questions.get(position), listAnswersChecked, listAnswersIncorrect, answer);
                        }

                    }
                });
            }

        }

        // 6 - Update widgets with it
        //rootView.setBackgroundColor(color);FrameLayout rootView= (FrameLayout) result.findViewById(R.id.fragment_page_rootview);
        //textView.setText(parts.get(position).getName());
        textView.setText(questions.get(position).getName());

        if (questions.get(position).getMedia() != null) {
            Picasso.with(getActivity()).load(questions.get(position).getMedia()).into(ivQuestion);
        }else {
            ivQuestion.setVisibility(View.INVISIBLE);
        }

        if ((position + 1) == questions.size()) {
            if (parts.size() > 1) {
                btnNextPart.setVisibility(View.VISIBLE);
                btnNextPart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        parts.remove(0);
                        Intent intent = new Intent(getActivity(), PartPassageQuizActivity.class);
                        intent.putExtra("listParts", parts);

                        createPartCompletion(parts.get(0), qc);

                        startActivity(intent);
                    }
                });
            }else {
                btnNextPart.setVisibility(View.VISIBLE);
                btnNextPart.setText("Finir le Quiz !");
                btnNextPart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DashboardActivity.class);
                        intent.putExtra("listParts", parts);
                        startActivity(intent);
                    }
                });
            }
        }else {
            btnNextPart.setVisibility(View.INVISIBLE);
        }

        return result;
    }

    private void continueIfAnswers(Part part, final QuizCompletion qc, final Question question , ArrayList<Answer> answers, ArrayList<Answer> answersIncorrect, final Answer answer) {
        Log.d("APP", "Question => " + question.getGrade() + " --- Answers" + answers + " -------- Answers incorrect " + answersIncorrect);
        if (answersIncorrect.isEmpty()) {
            score = question.getGrade();
        }else {
            score = 0;
        }

        this.partCompletionModele.getPartCompletion(part, qc, new PartCompletionModele.PartCompletionCallBack() {
            @Override
            public void onSuccess(final PartCompletion pc) {
                createQuestionCompletion(qc, score, pc, question, answer);
            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });
    }

    private void setScoreForAnswerCompletion(final QuestionCompletion qc, final int score, final Answer answer) {

        answerCompletionModele.setScoreForAnswerCompletion(qc, score, answer,  new AnswerCompletionModele.AnswerCompletionCallBack() {
            @Override
            public void onSuccess(AnswerCompletion answerCompletionCreate) {

            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });

    }

    private void createPartCompletion(Part part, QuizCompletion quizCompletion) {

        partCompletionModele.newPartCompletion(part, quizCompletion,  new PartCompletionModele.PartCompletionCallBack() {
            @Override
            public void onSuccess(PartCompletion PartCompletionCreate) {

            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });

    }

    private void createQuestionCompletion(final QuizCompletion qc, final int score, PartCompletion pc, final Question question, final Answer answer) {

        Log.d("APP", "NEGATIF " + score);

        questionCompletionModele.newQuestionCompletion(pc, question,  new QuestionCompletionModele.QuestionCompletionCallBack() {
            @Override
            public void onSuccess(QuestionCompletion questionCompletion) {
                Log.d("APP", "QuestionCompletion " + questionCompletion.getId() + "Answer " + answer.getId());
                createAnswerCompletion(questionCompletion, answer, score);
            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });

    }

    private void createAnswerCompletion(final QuestionCompletion qc, final Answer answer, final int score) {

        answerCompletionModele.newAnswerCompletion(qc, answer, new AnswerCompletionModele.AnswerCompletionCallBack() {
            @Override
            public void onSuccess(AnswerCompletion answerCompletionCreate) {
                Log.d("APP", "LE SCORE EST NULL???? " + score);
                setScoreForAnswerCompletion(qc, score, answer);
            }

            @Override
            public void onErrorNetwork() {

            }

            @Override
            public void onErrorVollet() {

            }
        });

    }
}