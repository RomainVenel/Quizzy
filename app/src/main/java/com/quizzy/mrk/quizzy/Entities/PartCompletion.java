package com.quizzy.mrk.quizzy.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class PartCompletion implements Parcelable {

    private int id;
    private QuizCompletion qc;
    private Part part;

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

    public PartCompletion(Parcel in){
        this.id = in.readInt();
        this.qc = (QuizCompletion) in.readValue(QuizCompletion.class.getClassLoader());
        this.part = (Part) in.readValue(Part.class.getClassLoader());
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
}
