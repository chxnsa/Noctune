package com.noctune.app.model;

public class PlaylistTrack {
    private int id;
    private int playlistId;
    private String trackName;
    private String artistName;
    private String duration;
    private String imageUrl;

    private String playcount;
    private String listeners;

    // Update constructor utamanya agar menerima playcount dan listeners
    public PlaylistTrack(int id, int playlistId, String trackName, String artistName,
                         String duration, String imageUrl, String playcount, String listeners) {
        this.id = id;
        this.playlistId = playlistId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.playcount = playcount;
        this.listeners = listeners;
    }

    // Tambahkan Getter-nya di bawah
    public String getPlaycount() { return playcount; }
    public String getListeners() { return listeners; }

    public int getId() { return id; }
    public int getPlaylistId() { return playlistId; }
    public String getTrackName() { return trackName; }
    public String getArtistName() { return artistName; }
    public String getDuration() { return duration; }
    public String getImageUrl() { return imageUrl; }
}