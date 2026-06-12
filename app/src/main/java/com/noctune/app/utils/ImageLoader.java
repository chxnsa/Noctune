package com.noctune.app.utils;

import android.widget.ImageView;
import com.noctune.app.R;
import com.noctune.app.model.ITunesResponse;
import com.noctune.app.network.ITunesClient;
import com.noctune.app.network.ITunesService;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageLoader {

    // Filter URL placeholder Last.fm
    private static boolean isLastFmPlaceholder(String url) {
        return url == null || url.isEmpty()
                || url.contains("2a96cbd8b46e442fc41c2b86b821562f");
    }

    // Load gambar track dengan fallback ke iTunes
    public static void loadTrackImage(ImageView imageView,
                                      String lastFmUrl,
                                      String trackName,
                                      String artistName) {
        if (!isLastFmPlaceholder(lastFmUrl)) {
            // Last.fm punya gambar valid
            Picasso.get()
                    .load(lastFmUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageView);
            return;
        }

        // Fallback ke iTunes
        loadFromITunes(imageView, trackName + " " + artistName);
    }

    // Load gambar artist dengan fallback ke iTunes
    public static void loadArtistImage(ImageView imageView,
                                       String lastFmUrl,
                                       String artistName) {
        if (!isLastFmPlaceholder(lastFmUrl)) {
            Picasso.get()
                    .load(lastFmUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageView);
            return;
        }

        loadFromITunes(imageView, artistName);
    }

    private static void loadFromITunes(ImageView imageView, String query) {
        ITunesService service = ITunesClient.getClient().create(ITunesService.class);

        // Tampilkan placeholder sambil loading
        imageView.setImageResource(R.drawable.ic_launcher_background);

        Call<ITunesResponse> call = service.searchTrack(query, "song", 1);
        call.enqueue(new Callback<ITunesResponse>() {
            @Override
            public void onResponse(Call<ITunesResponse> call,
                                   Response<ITunesResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getResults() != null
                        && !response.body().getResults().isEmpty()) {

                    String artwork = response.body().getResults()
                            .get(0).getHighResArtwork();

                    if (artwork != null && !artwork.isEmpty()) {
                        Picasso.get()
                                .load(artwork)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(imageView);
                        return;
                    }
                }
                // Tidak ketemu di iTunes juga — kasih warna solid
                setFallbackColor(imageView, query);
            }

            @Override
            public void onFailure(Call<ITunesResponse> call, Throwable t) {
                setFallbackColor(imageView, query);
            }
        });
    }

    private static void setFallbackColor(ImageView imageView, String seed) {
        int[] colors = {0xFFE63329, 0xFFFFD600, 0xFF2D6A4F,
                0xFF1565C0, 0xFF9C27B0, 0xFFFF6B35};
        int colorIndex = Math.abs(seed.hashCode()) % colors.length;
        imageView.setImageResource(R.drawable.ic_launcher_background);
        imageView.setBackgroundColor(colors[colorIndex]);
    }
}