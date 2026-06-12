package com.noctune.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.ChartAdapter;
import com.noctune.app.model.TopTracksResponse;
import com.noctune.app.model.Track;
import com.noctune.app.network.ApiService;
import com.noctune.app.network.RetrofitClient;
import com.noctune.app.utils.Constants;
import com.noctune.app.utils.NetworkUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartsFragment extends Fragment {

    private RecyclerView rvCharts;
    private ChartAdapter chartAdapter;
    private Button btnRetry;
    private TextView tvLabel;
    private LinearLayout llCountryChips;
    private ApiService apiService;
    private String selectedCountry = "global";

    // Daftar negara
    private final String[] countries = {
            "global", "indonesia", "united states",
            "united kingdom", "japan",
            "australia", "germany"
    };

    // Label display
    private final String[] countryLabels = {
            "GLOBAL", "INDONESIA", "USA",
            "UK", "JAPAN",
            "AUSTRALIA", "GERMANY"
    };
    // Warna chip
    private final String[] chipColors = {
            "#FFD600", "#E63329", "#1565C0",
            "#2D6A4F", "#E63329", "#1565C0",
            "#FF6B35", "#424242"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCharts = view.findViewById(R.id.rv_charts);
        btnRetry = view.findViewById(R.id.btn_retry_charts);
        tvLabel = view.findViewById(R.id.tv_chart_label);
        llCountryChips = view.findViewById(R.id.ll_country_chips);

        rvCharts.setLayoutManager(new LinearLayoutManager(getActivity()));
        chartAdapter = new ChartAdapter(getActivity());
        rvCharts.setAdapter(chartAdapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        buildCountryChips();

        btnRetry.setOnClickListener(v -> loadCharts(selectedCountry));

        // Load global chart pertama kali
        loadCharts(selectedCountry);
    }

    private void buildCountryChips() {
        llCountryChips.removeAllViews(); // Bersihkan container terlebih dahulu
        for (int i = 0; i < countries.length; i++) {
            final String country = countries[i];
            final String label = countryLabels[i];

            TextView chip = new TextView(getActivity());
            chip.setText(label);
            chip.setTextSize(11f);
            chip.setPadding(28, 14, 28, 14); // padding disesuaikan agar proporsional kotak
            chip.setTypeface(android.graphics.Typeface.MONOSPACE);
            chip.setGravity(android.view.Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 12, 0); // Jarak antar kotak chip
            chip.setLayoutParams(params);

            // Tentukan style awal berdasarkan status aktif
            updateChipStyle(chip, country.equals(selectedCountry));

            chip.setOnClickListener(v -> {
                selectedCountry = country;
                tvLabel.setText("TOP TRACKS — " + label);
                refreshChipStyles(); // Perbarui status visual semua tombol chip
                loadCharts(country);
            });

            llCountryChips.addView(chip);
        }
    }

    private void loadCharts(String country) {
        if (!NetworkUtils.isConnected(getActivity())) {
            btnRetry.setVisibility(View.VISIBLE);
            ToastHelper.showError(getActivity(), "NO INTERNET CONNECTION]");
            return;
        }

        btnRetry.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<TopTracksResponse> call;

            if (country.equals("global")) {
                call = apiService.getTopTracks(Constants.API_KEY, 1, 50);
            } else {
                call = apiService.getTracksByCountry(country, Constants.API_KEY, 50);
            }

            call.enqueue(new Callback<TopTracksResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopTracksResponse> call,
                                       @NonNull Response<TopTracksResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getTracks() != null) {

                            java.util.List<Track> trackList = response.body().getTracks().getTrack();

                            // --- PERBAIKAN: EMULASI DATA UNTUK PETA DATA REGIONAL/GEO ---
                            if (!country.equals("global") && trackList != null) {
                                for (Track t : trackList) {
                                    // Geo API Last.fm meletakkan angka jumlah dengar di objek listeners,
                                    // sedangkan playcount-nya kosong. Kita pindahkan nilainya agar adapter tidak membaca null.
                                    if ((t.getPlaycount() == null || t.getPlaycount().isEmpty())
                                            && t.getListeners() != null) {
                                        t.setPlaycount(t.getListeners());
                                    }
                                }
                            }

                            chartAdapter.setTracks(trackList);
                        } else {
                            ToastHelper.showSuccess(getActivity(), "[NO CHART DATA AVAILABLE]");
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopTracksResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        ToastHelper.showError(getActivity(), "[ERROR: " + t.getMessage().toUpperCase() + "]");
                        btnRetry.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
    }

    private void refreshChipStyles() {
        for (int i = 0; i < llCountryChips.getChildCount(); i++) {
            TextView chip = (TextView) llCountryChips.getChildAt(i);
            String country = countries[i];
            updateChipStyle(chip, country.equals(selectedCountry));
        }
    }

    private void updateChipStyle(TextView chip, boolean isActive) {
        if (isActive) {
            // Active Style: Tetap Kuning Menyala Khas Noctune
            chip.setBackgroundColor(Color.parseColor("#FFD600"));
            chip.setTextColor(Color.BLACK);
        } else {
            // Inactive Style: Menggunakan latar abu-abu gelap semen, teks abu-abu redup
            chip.setBackgroundColor(Color.parseColor("#222222")); // Kotak abu-abu arang kaku
            chip.setTextColor(Color.parseColor("#888888")); // Teks abu-abu medium (tidak silau, tidak hilang)
        }
    }
}