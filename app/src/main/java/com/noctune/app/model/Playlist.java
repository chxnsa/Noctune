package com.noctune.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {
    private int id;
    private String name;
    private String createdAt;
    private int trackCount;
    private String cover; // <--- 1. TAMBAHKAN VARIABEL BARU INI

    // Constructor Kosong (Jika ada)
    public Playlist() {}

    // 2. PASTI KAN CONSTRUCTOR UTAMAMU MENERIMA COVER (Atau biarkan kosongan lalu pakai setter)
    public Playlist(int id, String name, String createdAt, String cover) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.cover = cover;
    }

    // --- GETTER & SETTER BARU ---
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    // --- SISA GETTER & SETTER BAWAANMU (ID, Name, CreatedAt, TrackCount) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public int getTrackCount() { return trackCount; }
    public void setTrackCount(int trackCount) { this.trackCount = trackCount; }

    // --- IMPLEMENTASI PARCELABLE (Update jika kamu menggunakannya untuk Intent) ---
    protected Playlist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        createdAt = in.readString();
        trackCount = in.readInt();
        cover = in.readString(); // <-- Baca cover dari parcel
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(createdAt);
        dest.writeInt(trackCount);
        dest.writeString(cover); // <-- Tulis cover ke parcel
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) { return new Playlist(in); }
        @Override
        public Playlist[] newArray(int size) { return new Playlist[size]; }
    };
}