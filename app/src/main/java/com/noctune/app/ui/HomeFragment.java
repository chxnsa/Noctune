package com.noctune.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.TrackAdapter;
import com.noctune.app.model.TopTracksResponse;
import com.noctune.app.network.ApiService;
import com.noctune.app.network.RetrofitClient;
import com.noctune.app.utils.Constants;
import com.noctune.app.utils.NetworkUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvTracks;
    private TrackAdapter trackAdapter;
    private Button btnRetry;
    private ApiService apiService;

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

        // Init views — sesuai modul (view.findViewById di Fragment)
        rvTracks = view.findViewById(R.id.rv_tracks);
        btnRetry = view.findViewById(R.id.btn_retry);

        // Setup RecyclerView — sesuai modul
        rvTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        trackAdapter = new TrackAdapter(getActivity());
        rvTracks.setAdapter(trackAdapter);

        // Init Retrofit — sesuai modul
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Tombol retry — syarat tugas
        btnRetry.setOnClickListener(v -> loadTracks());

        // Load data pertama kali
        loadTracks();
    }

    private void loadTracks() {
        // Cek koneksi internet dulu
        if (!NetworkUtils.isConnected(getActivity())) {
            // Tidak ada internet — tampilkan tombol retry
            btnRetry.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),
                    "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ada internet — sembunyikan retry
        btnRetry.setVisibility(View.GONE);

        // Pakai Executor untuk background thread — sesuai syarat tugas
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            // Jalankan API call di background thread
            // Sesuai pola modul networking — call.enqueue
            Call<TopTracksResponse> call = apiService.getTopTracks(
                    Constants.API_KEY, 1, 50
            );

            call.enqueue(new Callback<TopTracksResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopTracksResponse> call,
                                       @NonNull Response<TopTracksResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            // Berhasil — update RecyclerView di main thread
                            trackAdapter.setTracks(
                                    response.body().getTracks().getTrack()
                            );
                        } else {
                            Toast.makeText(getActivity(),
                                    "Failed to load data",
                                    Toast.LENGTH_SHORT).show();
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopTracksResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        Toast.makeText(getActivity(),
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        btnRetry.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
    }
}