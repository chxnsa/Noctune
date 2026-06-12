package com.noctune.app.model;

import com.google.gson.annotations.SerializedName;

public class ArtistTopTracksResponse {
    @SerializedName("toptracks")
    private TrackList topTracks;

    public TrackList getTopTracks() {
        return topTracks;
    }
}
