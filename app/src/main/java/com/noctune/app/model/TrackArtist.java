package com.noctune.app.model;

public class TrackArtist {
    private String name;
    private String url;

    // Constructor baru — dipakai saat rebuild dari Parcel
    public TrackArtist(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public String getUrl() { return url; }
}