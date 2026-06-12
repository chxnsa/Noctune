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
            "united kingdom", "japan", "south korea",
            "australia", "germany"
    };

    // Label display
    private final String[] countryLabels = {
            "🌍 GLOBAL", "🇮🇩 INDONESIA", "🇺🇸 USA",
            "🇬🇧 UK", "🇯🇵 JAPAN", "🇰🇷 KOREA",
            "🇦🇺 AUSTRALIA", "🇩🇪 GERMANY"
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
        for (int i = 0; i < countries.length; i++) {
            final String country = countries[i];
            final String label = countryLabels[i];
            final String color = chipColors[i];

            TextView chip = new TextView(getActivity());
            chip.setText(label);
            chip.setTextColor(Color.parseColor(
                    color.equals("#FFD600") ? "#000000" : "#FFFFFF"));
            chip.setBackgroundColor(Color.parseColor(color));
            chip.setTextSize(11f);
            chip.setPadding(20, 10, 20, 10);
            chip.setTypeface(android.graphics.Typeface.MONOSPACE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 8, 0);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                selectedCountry = country;
                tvLabel.setText("🏆 TOP TRACKS — " + label);
                loadCharts(country);
            });

            llCountryChips.addView(chip);
        }
    }

    private void loadCharts(String country) {
        if (!NetworkUtils.isConnected(getActivity())) {
            btnRetry.setVisibility(View.VISIBLE);
            ToastHelper.showError(getActivity(), "[ERROR: NO_INTERNET_CONNECTION]");
            return;
        }

        btnRetry.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<TopTracksResponse> call;

            // Global pakai chart.gettoptracks
            // Negara spesifik pakai geo.gettoptracks
            if (country.equals("global")) {
                call = apiService.getTopTracks(Constants.API_KEY, 1, 50);
            } else {
                call = apiService.getTracksByCountry(
                        country, Constants.API_KEY, 50);
            }

            call.enqueue(new Callback<TopTracksResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopTracksResponse> call,
                                       @NonNull Response<TopTracksResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getTracks() != null) {
                            chartAdapter.setTracks(
                                    response.body().getTracks().getTrack());
                        } else {
                            // SEBELUMNYA: "No chart data available"
                            ToastHelper.showSuccess(getActivity(), "[SYSTEM_LOG: NO_CHART_DATA_AVAILABLE]");
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopTracksResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        // SEBELUMNYA: "Error: " + t.getMessage()
                        ToastHelper.showError(getActivity(), "[CORE_ERROR: " + t.getMessage().toUpperCase() + "]");
                        btnRetry.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
    }
}