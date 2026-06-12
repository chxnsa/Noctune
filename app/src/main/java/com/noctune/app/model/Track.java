package com.noctune.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Track implements Parcelable {
    private String name;
    private String duration;
    private String playcount;
    private String listeners;
    private String url;
    private String cachedImageUrl;

    @SerializedName("artist")
    private TrackArtist artist;

    @SerializedName("image")
    private List<TrackImage> images;

    public Track(String name, String duration, String playcount,
                 String listeners, String url, TrackArtist artist,
                 String imageUrl) {
        this.name = name;
        this.duration = duration;
        this.playcount = playcount;
        this.listeners = listeners;
        this.url = url;
        this.artist = artist;
        this.cachedImageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getPlaycount() { return playcount; }
    public String getListeners() { return listeners; }
    public String getUrl() { return url; }
    public TrackArtist getArtist() { return artist; }

    public String getImageUrl() {
        // Kalau sudah di-cache (dari Parcel), langsung return
        if (cachedImageUrl != null && !cachedImageUrl.isEmpty()) {
            return cachedImageUrl;
        }

        // Coba ambil dari list images — dari yang terbesar dulu
        if (images != null) {
            // Coba extralarge (index 3) dulu
            for (int i = images.size() - 1; i >= 0; i--) {
                String imgUrl = images.get(i).getUrl();
                if (imgUrl != null
                        && !imgUrl.isEmpty()
                        && !imgUrl.contains("2a96cbd8b46e442fc41c2b86b821562f")) {
                    // Filter URL placeholder Last.fm
                    return imgUrl;
                }
            }
        }
        return "";
    }

    // Parcelable — simpan imageUrl saat dikirim via Intent
    protected Track(Parcel in) {
        name = in.readString();
        duration = in.readString();
        playcount = in.readString();
        listeners = in.readString();
        url = in.readString();

        String artistName = in.readString();
        if (artistName != null) {
            artist = new TrackArtist(artistName);
        }

        cachedImageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(duration);
        parcel.writeString(playcount);
        parcel.writeString(listeners);
        parcel.writeString(url);
        parcel.writeString(artist != null ? artist.getName() : null);
        // Cache imageUrl sebelum dikirim via Parcel
        parcel.writeString(getImageUrl());
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) { return new Track(in); }
        @Override
        public Track[] newArray(int size) { return new Track[size]; }
    };
}