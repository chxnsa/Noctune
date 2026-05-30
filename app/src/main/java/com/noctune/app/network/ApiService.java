package com.noctune.app.network;

import com.noctune.app.model.TopTracksResponse;
import com.noctune.app.model.TopArtistsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // Top tracks — untuk HomeFragment
    @GET("?method=chart.gettoptracks&format=json")
    Call<TopTracksResponse> getTopTracks(
            @Query("api_key") String apiKey,
            @Query("page") int page,
            @Query("limit") int limit
    );

    // Top artists by tag/genre — untuk ExploreFragment
    @GET("?method=tag.gettopartists&format=json")
    Call<TopArtistsResponse> getArtistsByTag(
            @Query("tag") String tag,
            @Query("api_key") String apiKey,
            @Query("limit") int limit
    );
}