package com.quizzy.mrk.quizzy.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.GregorianCalendar;

public class Quiz implements Parcelable {

    private int id;
    private String name;
    private String media;
    private User user;
    private GregorianCalendar isValidated;
    private double popularity;

    public Quiz(int id, String name, String media, User user, GregorianCalendar isValidated, double popularity) {
        super();
        this.id = id;
        this.name = name;
        this.media = media;
        this.user = user;
        this.isValidated = isValidated;
        this.popularity = popularity;
    }

    public Quiz(int id, String name, String media, User user, GregorianCalendar isValidated) {
        super();
        this.id = id;
        this.name = name;
        this.media = media;
        this.user = user;
        this.isValidated = isValidated;
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

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GregorianCalendar getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(GregorianCalendar isValidated) {
        this.isValidated = isValidated;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.media);
        dest.writeValue(this.user);
        dest.writeValue(this.isValidated);
        dest.writeDouble(this.popularity);
    }

    public Quiz( Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.media = in.readString();
        this.user = (User) in.readValue(User.class.getClassLoader());
        this.isValidated = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
        this.popularity = in.readDouble();
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {

        public Quiz createFromParcel(Parcel source){
            return new Quiz(source);
        }

        public Quiz[] newArray(int size){
            return new Quiz[size];
        }
    };
}
