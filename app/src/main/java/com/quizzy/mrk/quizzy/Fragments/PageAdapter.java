package com.quizzy.mrk.quizzy.Fragments;

import com.quizzy.mrk.quizzy.Entities.Part;
import com.quizzy.mrk.quizzy.Entities.Question;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    // 1 - Array of colors that will be passed to PageFragment
    private int[] colors;
    private ArrayList<Part> parts;
    private ArrayList<Question> questions;

    // 2 - Default Constructor
    public PageAdapter(FragmentManager mgr, int[] colors, ArrayList<Part> parts, ArrayList<Question> questions) {
        super(mgr);
        this.colors = colors;
        this.parts = parts;
        this.questions = questions;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Fragment getItem(int position) {
        // 4 - Page to return
        return(QuestionPassageQuizFragment.newInstance(position, this.colors[position], parts, questions));
    }
}
