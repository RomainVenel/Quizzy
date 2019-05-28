package com.quizzy.mrk.quizzy.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class QuizCompletion implements Parcelable {

    private int id;
    private User user;
    private Quiz quiz;

    public static final Creator<QuizCompletion> CREATOR = new Creator<QuizCompletion>() {
        @Override
        public QuizCompletion createFromParcel(Parcel in) {
            return new QuizCompletion(in);
        }

        @Override
        public QuizCompletion[] newArray(int size) {
            return new QuizCompletion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeValue(this.user);
        parcel.writeValue(this.quiz);
    }

    public QuizCompletion(int id, User user, Quiz quiz) {
        super();
        this.id = id;
        this.user = user;
        this.quiz = quiz;
    }

    public QuizCompletion(Parcel in){
        this.id = in.readInt();
        this.user = (User) in.readValue(User.class.getClassLoader());
        this.quiz = (Quiz) in.readValue(Quiz.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
