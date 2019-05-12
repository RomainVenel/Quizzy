package com.quizzy.mrk.quizzy.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.quizzy.mrk.quizzy.DashboardActivity;
import com.quizzy.mrk.quizzy.Entities.Answer;
import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;
import com.quizzy.mrk.quizzy.PartPassageQuizActivity;
import com.quizzy.mrk.quizzy.PartTransitionQuizActivity;
import com.quizzy.mrk.quizzy.R;

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

    public QuestionPassageQuizFragment() {
    }



    // 2 - Method that will create a new instance of PageFragment, and add data to its bundle.
    public static QuestionPassageQuizFragment newInstance(int position, int color, ArrayList<Part> parts, ArrayList<Question> questions) {

        // 2.1 Create new fragment
        QuestionPassageQuizFragment frag = new QuestionPassageQuizFragment();

        // 2.2 Create bundle and add it some data
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_COLOR, color);
        args.putParcelableArrayList(KEY_QUESTIONS, questions);
        args.putParcelableArrayList(KEY_PARTS, parts);
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
        TableLayout answersView= (TableLayout) result.findViewById(R.id.answers_view);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TabLayout.LayoutParams.WRAP_CONTENT,
                TabLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 30, 50, 10);
        TextView textView= (TextView) result.findViewById(R.id.fragment_page_title);
        Button btnNextPart = (Button) result.findViewById(R.id.btn_next_part);

        // 5 - Get data from Bundle (created in method newInstance)
        int position = getArguments().getInt(KEY_POSITION, -1);
        int color = getArguments().getInt(KEY_COLOR, -1);
        ArrayList<Question> questions = getArguments().getParcelableArrayList(KEY_QUESTIONS);
        final ArrayList<Part> parts = getArguments().getParcelableArrayList(KEY_PARTS);



        for (Answer answer : questions.get(position).getAnswers()) {
            Button buttonAnswer = new Button(getActivity());
            TextView answerText = new TextView(getActivity());
            buttonAnswer.setText(answer.getName());
            buttonAnswer.setBackgroundColor(getResources().getColor(R.color.white));
            buttonAnswer.setBackground(getResources().getDrawable(R.drawable.rounded_shape));
            buttonAnswer.setLayoutParams(params);
            answersView.addView(buttonAnswer);
        }

        // 6 - Update widgets with it
        //rootView.setBackgroundColor(color);FrameLayout rootView= (FrameLayout) result.findViewById(R.id.fragment_page_rootview);
        //textView.setText(parts.get(position).getName());
        textView.setText(questions.get(position).getName());

        Log.d("APP", "POSITION ==> " + position);
        Log.d("APP", "SIZE ==> " + questions.size());

        if ((position + 1) == questions.size()) {
            if (parts.size() > 1) {
                btnNextPart.setVisibility(View.VISIBLE);
                btnNextPart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        parts.remove(0);
                        Intent intent = new Intent(getActivity(), PartPassageQuizActivity.class);
                        intent.putExtra("listParts", parts);
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

}