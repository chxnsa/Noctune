package com.noctune.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Artist {
    private String name;
    private String url;

    @SerializedName("image")
    private List<TrackImage> images;

    public String getName() { return name; }
    public String getUrl() { return url; }

    public String getImageUrl() {
        if (images != null && images.size() > 2) {
            return images.get(2).getUrl();
        }
        return "";
    }
}