package com.quizzy.mrk.quizzy.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.GregorianCalendar;

public class Part implements Parcelable{

    private int id;
    private String name;
    private String desc;
    private String media;
    private Quiz quiz;

    public Part(int id) {
        super();
        this.id = id;
    }

    public Part(int id, String name, String desc, String media, Quiz quiz) {
        super();
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.media = media;
        this.quiz = quiz;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @Override
    public String toString() {
        return "Part{" +
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
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeString(this.media);
        dest.writeValue(this.quiz);
    }

    public Part(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.desc = in.readString();
        this.media = in.readString();
        this.quiz = (Quiz) in.readValue(Quiz.class.getClassLoader());
    }

    public static final Creator<Part> CREATOR = new Creator<Part>() {

        public Part createFromParcel(Parcel source){
            return new Part(source);
        }

        public Part[] newArray(int size){
            return new Part[size];
        }
    };
}
