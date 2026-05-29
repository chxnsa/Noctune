package com.noctune.app.network;

import com.noctune.app.model.TopTracksResponse;
import com.noctune.app.utils.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint: chart.gettoptracks
    @GET("?method=chart.gettoptracks&format=json")
    Call<TopTracksResponse> getTopTracks(
            @Query("api_key") String apiKey,
            @Query("page") int page,
            @Query("limit") int limit
    );

    // Endpoint: artist.search
    @GET("?method=artist.search&format=json")
    Call<Object> searchArtist(
            @Query("artist") String artistName,
            @Query("api_key") String apiKey
    );
}