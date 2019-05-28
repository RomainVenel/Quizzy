package com.quizzy.mrk.quizzy.Entities;


import android.os.Parcel;
import android.os.Parcelable;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class User implements Parcelable {

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private GregorianCalendar birth_date;
    private String password;
    private String email;
    private String media;

    public User(int id, String firstName, String lastName, String username, GregorianCalendar birth_date, String password, String email, String media) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.birth_date = birth_date;
        this.password = password;
        this.email = email;
        this.media = media;
    }

    public User(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GregorianCalendar getBirthDate() {
        return birth_date;
    }

    public void setBirthDate(GregorianCalendar birth_date) {
        this.birth_date = birth_date;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", birth_date=" + birth_date.get(Calendar.DAY_OF_MONTH) + "/" + birth_date.get(Calendar.MONTH) + "/" + birth_date.get(Calendar.YEAR) +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", media='" + media + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.username);
        dest.writeValue(this.birth_date);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.media);
    }

    public User( Parcel in){
        this.id = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.username = in.readString();
        this.birth_date = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
        this.email = in.readString();
        this.password = in.readString();
        this.media = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {

        public User createFromParcel(Parcel source){
            return new User(source);
        }

        public User[] newArray(int size){
            return new User[size];
        }
    };
}
