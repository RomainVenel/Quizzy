package com.quizzy.mrk.quizzy.Fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionPassageQuizFragment extends Fragment {

    // 1 - Create keys for our Bundle
    private static final String KEY_POSITION="position";
    private static final String KEY_COLOR="color";
    private static final String KEY_PARTS="parts";


    public QuestionPassageQuizFragment() { }


    // 2 - Method that will create a new instance of PageFragment, and add data to its bundle.
    public static QuestionPassageQuizFragment newInstance(int position, int color, ArrayList<Part> parts) {

        // 2.1 Create new fragment
        QuestionPassageQuizFragment frag = new QuestionPassageQuizFragment();

        // 2.2 Create bundle and add it some data
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_COLOR, color);
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
        LinearLayout rootView= (LinearLayout) result.findViewById(R.id.fragment_page_rootview);
        TextView textView= (TextView) result.findViewById(R.id.fragment_page_title);

        // 5 - Get data from Bundle (created in method newInstance)
        int position = getArguments().getInt(KEY_POSITION, -1);
        int color = getArguments().getInt(KEY_COLOR, -1);
        ArrayList<Part> parts = getArguments().getParcelableArrayList(KEY_PARTS);

        // 6 - Update widgets with it
        rootView.setBackgroundColor(color);
        textView.setText(parts.get(position).getName());

        Log.e(getClass().getSimpleName(), "onCreateView called for fragment number "+position);

        return result;
    }

}