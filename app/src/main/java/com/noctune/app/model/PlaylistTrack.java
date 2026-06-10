package com.noctune.app.model;

public class PlaylistTrack {
    private int id;
    private int playlistId;
    private String trackName;
    private String artistName;
    private String duration;
    private String imageUrl;

    public PlaylistTrack(int id, int playlistId, String trackName,
                         String artistName, String duration, String imageUrl) {
        this.id = id;
        this.playlistId = playlistId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.duration = duration;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public int getPlaylistId() { return playlistId; }
    public String getTrackName() { return trackName; }
    public String getArtistName() { return artistName; }
    public String getDuration() { return duration; }
    public String getImageUrl() { return imageUrl; }
}