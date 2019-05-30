package com.quizzy.mrk.quizzy.Entities;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Question implements Parcelable{

    private int id;
    private String type;
    private int grade;
    private String name;
    private String media;
    private Part part;
    private ArrayList<Answer> answers;

    public Question(int id) {
        this.id = id;
    }

    public Question(int id, String type, int grade, String name, String media, Part part, ArrayList<Answer> answers) {
        this.id = id;
        this.type = type;
        this.grade = grade;
        this.name = name;
        this.media = media;
        this.part = part;
        this.answers = answers;
    }

    public Question() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.type);
        dest.writeInt(this.grade);
        dest.writeString(this.name);
        dest.writeString(this.media);
        dest.writeValue(this.part);
        dest.writeList(this.answers);
    }

    public Question(Parcel in){
        this.id = in.readInt();
        this.type = in.readString();
        this.grade = in.readInt();
        this.name = in.readString();
        this.media = in.readString();
        this.part = (Part) in.readValue(Part.class.getClassLoader());
        this.answers = new ArrayList<Answer>();
        in.readList(this.answers, getClass().getClassLoader());
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {

        public Question createFromParcel(Parcel source){
            return new Question(source);
        }

        public Question[] newArray(int size){
            return new Question[size];
        }
    };
}
