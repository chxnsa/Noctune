package com.noctune.app.model;

import com.google.gson.annotations.SerializedName;

public class TrackImage {
    @SerializedName("#text")
    private String url;

    private String size;

    public String getUrl() { return url; }
    public String getSize() { return size; }
}