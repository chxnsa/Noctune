package com.noctune.app.network;

import com.noctune.app.model.ITunesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITunesService {

    @GET("search")
    Call<ITunesResponse> searchTrack(
            @Query("term") String term,
            @Query("entity") String entity,
            @Query("limit") int limit
    );
}