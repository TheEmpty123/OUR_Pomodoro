package com.mobile.pomodoro.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DailyTask implements Parcelable {
    private String id;
    private String title;
    private boolean isDone;

    public DailyTask(String id, String title, boolean isDone) {
        this.id = id;
        this.title = title;
        this.isDone = isDone;
    }

    protected DailyTask(Parcel in) {
        id = in.readString();
        title = in.readString();
        isDone = in.readByte() != 0;
    }

    public static final Creator<DailyTask> CREATOR = new Creator<DailyTask>() {
        @Override
        public DailyTask createFromParcel(Parcel in) {
            return new DailyTask(in);
        }

        @Override
        public DailyTask[] newArray(int size) {
            return new DailyTask[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeByte((byte) (isDone ? 1 : 0));
    }
}
