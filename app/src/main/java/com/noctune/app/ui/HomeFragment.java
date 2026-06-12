package com.noctune.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.TrackAdapter;
import com.noctune.app.model.TopTracksResponse;
import com.noctune.app.model.Track;
import com.noctune.app.network.ApiService;
import com.noctune.app.network.RetrofitClient;
import com.noctune.app.utils.Constants;
import com.noctune.app.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvTracks;
    private TrackAdapter trackAdapter;
    private Button btnRetry;
    private EditText etSearch;
    private ApiService apiService;

    // Simpan semua track dari API — untuk filter search
    private List<Track> allTracks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTracks = view.findViewById(R.id.rv_tracks);
        btnRetry = view.findViewById(R.id.btn_retry);
        etSearch = view.findViewById(R.id.et_search);

        rvTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        trackAdapter = new TrackAdapter(getActivity());
        rvTracks.setAdapter(trackAdapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnRetry.setOnClickListener(v -> loadTracks());

        // Search realtime dengan TextWatcher
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                filterTracks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadTracks();
    }

    private void filterTracks(String query) {
        if (allTracks.isEmpty()) return;

        if (query.isEmpty()) {
            // Kosong → tampilkan semua
            trackAdapter.setTracks(allTracks);
            return;
        }

        // Filter berdasarkan nama track atau nama artis
        List<Track> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (Track track : allTracks) {
            boolean matchTrack = track.getName() != null &&
                    track.getName().toLowerCase().contains(lowerQuery);

            boolean matchArtist = track.getArtist() != null &&
                    track.getArtist().getName() != null &&
                    track.getArtist().getName().toLowerCase().contains(lowerQuery);

            if (matchTrack || matchArtist) {
                filtered.add(track);
            }
        }

        trackAdapter.setTracks(filtered);

        // Kasih feedback kalau tidak ada hasil
        if (filtered.isEmpty()) {
            // SEBELUMNYA: "No results for \"" + query + "\""
            ToastHelper.showSuccess(getActivity(), "[NO RESULTS FOR " + query.toUpperCase() + "]");
        }
    }

    private void loadTracks() {
        if (!NetworkUtils.isConnected(getActivity())) {
            btnRetry.setVisibility(View.VISIBLE);
            // SEBELUMNYA: "No internet connection"
            ToastHelper.showError(getActivity(), "[ERROR: NO INTERNET CONNECTION]");
            return;
        }

        btnRetry.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<TopTracksResponse> call = apiService.getTopTracks(
                    Constants.API_KEY, 1, 50
            );

            call.enqueue(new Callback<TopTracksResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopTracksResponse> call,
                                       @NonNull Response<TopTracksResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            // Simpan ke allTracks untuk keperluan filter
                            allTracks = response.body().getTracks().getTrack();
                            trackAdapter.setTracks(allTracks);

                            // Kalau ada teks search aktif, filter ulang
                            if (etSearch != null) {
                                String currentQuery = etSearch.getText()
                                        .toString().trim();
                                if (!currentQuery.isEmpty()) {
                                    filterTracks(currentQuery);
                                }
                            }
                        } else {
                            // SEBELUMNYA: "Failed to load data"
                            ToastHelper.showError(getActivity(), "[ERROR: FAILED TO LOAD DATA]");
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopTracksResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        // SEBELUMNYA: "Error: " + t.getMessage()
                        ToastHelper.showError(getActivity(), "[ERROR: " + t.getMessage().toUpperCase() + "]");
                        btnRetry.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
    }
}