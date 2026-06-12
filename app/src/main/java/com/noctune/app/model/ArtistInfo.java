package com.noctune.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ArtistInfo {
    private String name;

    @SerializedName("image")
    private List<TrackImage> images;

    private ArtistStats stats;
    private ArtistBio bio;

    public String getName() { return name; }
    public ArtistStats getStats() { return stats; }
    public ArtistBio getBio() { return bio; }

    public String getImageUrl() {
        if (images != null && images.size() > 3) {
            return images.get(3).getUrl();
        }
        return "";
    }

    public static class ArtistStats {
        private String listeners;
        private String playcount;
        public String getListeners() { return listeners; }
        public String getPlaycount() { return playcount; }
    }

    public static class ArtistBio {
        private String summary;
        public String getSummary() { return summary; }
    }
}