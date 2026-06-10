package com.noctune.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {
    private int id;
    private String name;
    private String createdAt;
    private int trackCount;

    public Playlist() {}

    public Playlist(int id, String name, String createdAt, int trackCount) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.trackCount = trackCount;
    }

    protected Playlist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        createdAt = in.readString();
        trackCount = in.readInt();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) { return new Playlist(in); }
        @Override
        public Playlist[] newArray(int size) { return new Playlist[size]; }
    };

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCreatedAt() { return createdAt; }
    public int getTrackCount() { return trackCount; }
    public void setTrackCount(int trackCount) { this.trackCount = trackCount; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(createdAt);
        parcel.writeInt(trackCount);
    }
}