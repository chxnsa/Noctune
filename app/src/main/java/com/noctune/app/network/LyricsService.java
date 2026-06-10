package com.noctune.app.network;

import com.noctune.app.model.LyricsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LyricsService {

    @GET("v1/{artist}/{title}")
    Call<LyricsResponse> getLyrics(
            @Path("artist") String artist,
            @Path("title") String title
    );
}