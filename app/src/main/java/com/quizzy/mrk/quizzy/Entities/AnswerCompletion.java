package com.quizzy.mrk.quizzy.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class AnswerCompletion implements Parcelable {

    private int id;
    private QuestionCompletion qc;
    private Answer answer;

    public static final Creator<AnswerCompletion> CREATOR = new Creator<AnswerCompletion>() {
        @Override
        public AnswerCompletion createFromParcel(Parcel in) {
            return new AnswerCompletion(in);
        }

        @Override
        public AnswerCompletion[] newArray(int size) {
            return new AnswerCompletion[size];
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
        parcel.writeValue(this.answer);
    }

    public AnswerCompletion(int id) {
        super();
        this.id = id;
    }

    public AnswerCompletion(int id, QuestionCompletion qc, Answer answer) {
        super();
        this.id = id;
        this.qc = qc;
        this.answer = answer;
    }

    public AnswerCompletion(Parcel in){
        this.id = in.readInt();
        this.qc = (QuestionCompletion) in.readValue(QuestionCompletion.class.getClassLoader());
        this.answer = (Answer) in.readValue(Answer.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuestionCompletion getQc() {
        return qc;
    }

    public void setQc(QuestionCompletion qc) {
        this.qc = qc;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
