package com.noctune.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteTrack implements Parcelable {
    private int id;
    private String trackName;
    private String artistName;
    private String playcount;
    private String listeners;
    private String duration;
    private String imageUrl;

    public FavoriteTrack() {}

    public FavoriteTrack(int id, String trackName, String artistName,
                         String playcount, String listeners,
                         String duration, String imageUrl) {
        this.id = id;
        this.trackName = trackName;
        this.artistName = artistName;
        this.playcount = playcount;
        this.listeners = listeners;
        this.duration = duration;
        this.imageUrl = imageUrl;
    }

    protected FavoriteTrack(Parcel in) {
        id = in.readInt();
        trackName = in.readString();
        artistName = in.readString();
        playcount = in.readString();
        listeners = in.readString();
        duration = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<FavoriteTrack> CREATOR = new Creator<FavoriteTrack>() {
        @Override
        public FavoriteTrack createFromParcel(Parcel in) { return new FavoriteTrack(in); }
        @Override
        public FavoriteTrack[] newArray(int size) { return new FavoriteTrack[size]; }
    };

    // Getters
    public int getId() { return id; }
    public String getTrackName() { return trackName; }
    public String getArtistName() { return artistName; }
    public String getPlaycount() { return playcount; }
    public String getListeners() { return listeners; }
    public String getDuration() { return duration; }
    public String getImageUrl() { return imageUrl; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(trackName);
        parcel.writeString(artistName);
        parcel.writeString(playcount);
        parcel.writeString(listeners);
        parcel.writeString(duration);
        parcel.writeString(imageUrl);
    }
}