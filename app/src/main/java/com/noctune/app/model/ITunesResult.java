package com.noctune.app.model;

public class ITunesResult {
    private String artworkUrl100;
    private String artistName;
    private String trackName;

    public String getArtworkUrl100() { return artworkUrl100; }
    public String getArtistName() { return artistName; }
    public String getTrackName() { return trackName; }

    // iTunes return 100x100, kita upscale ke 300x300 untuk kualitas lebih baik
    public String getHighResArtwork() {
        if (artworkUrl100 == null) return null;
        return artworkUrl100.replace("100x100", "300x300");
    }
}