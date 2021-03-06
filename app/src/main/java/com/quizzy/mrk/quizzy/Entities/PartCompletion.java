package com.quizzy.mrk.quizzy.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PartCompletion implements Parcelable {

    private int id;
    private QuizCompletion qc;
    private Part part;
    private ArrayList<QuestionCompletion> questionsCompletion;

    public static final Creator<PartCompletion> CREATOR = new Creator<PartCompletion>() {
        @Override
        public PartCompletion createFromParcel(Parcel in) {
            return new PartCompletion(in);
        }

        @Override
        public PartCompletion[] newArray(int size) {
            return new PartCompletion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeValue(this.qc);
        parcel.writeValue(this.part);
    }

    public PartCompletion(int id) {
        super();
        this.id = id;
    }

    public PartCompletion(int id, Part part, QuizCompletion qc) {
        super();
        this.id = id;
        this.part = part;
        this.qc = qc;
    }

    public PartCompletion(Parcel in){
        this.id = in.readInt();
        this.qc = (QuizCompletion) in.readValue(QuizCompletion.class.getClassLoader());
        this.part = (Part) in.readValue(Part.class.getClassLoader());
        this.questionsCompletion = new ArrayList<>();
        in.readList(this.questionsCompletion, getClass().getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuizCompletion getQc() {
        return qc;
    }

    public void setQc(QuizCompletion qc) {
        this.qc = qc;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public ArrayList<QuestionCompletion> getQuestionsCompletion() {
        return questionsCompletion;
    }

    public void setQuestionsCompletion(ArrayList<QuestionCompletion> questionsCompletion) {
        this.questionsCompletion = questionsCompletion;
    }
}
