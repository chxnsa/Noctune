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

    @SerializedName("artist")
    private TrackArtist artist;

    @SerializedName("image")
    private List<TrackImage> images;

    // Getter
    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getPlaycount() { return playcount; }
    public String getListeners() { return listeners; }
    public String getUrl() { return url; }
    public TrackArtist getArtist() { return artist; }

    // Ambil URL gambar ukuran extralarge (index 3)
    public String getImageUrl() {
        if (images != null && images.size() > 3) {
            return images.get(3).getUrl();
        }
        return "";
    }

    // Parcelable — untuk kirim data via Intent ke DetailActivity
    protected Track(Parcel in) {
        name = in.readString();
        duration = in.readString();
        playcount = in.readString();
        listeners = in.readString();
        url = in.readString();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) { return new Track(in); }
        @Override
        public Track[] newArray(int size) { return new Track[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(duration);
        parcel.writeString(playcount);
        parcel.writeString(listeners);
        parcel.writeString(url);
    }
}