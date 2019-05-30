package com.quizzy.mrk.quizzy.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Answer implements Parcelable {

    private int id;
    private String name;
    private boolean correct;

    public Answer(int id, String name, boolean correct) {
        this.id = id;
        this.name = name;
        this.correct = correct;
    }

    public Answer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.correct ? 1 : 0);
    }

    public Answer(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.correct = in.readInt() == 1;
    }

    public Answer(int id) {
        this.id = id;
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {

        public Answer createFromParcel(Parcel source){
            return new Answer(source);
        }

        public Answer[] newArray(int size){
            return new Answer[size];
        }
    };
}
