package com.noctune.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.FavoriteTrack;
import com.noctune.app.model.Track;
import com.noctune.app.model.TrackArtist;
import com.noctune.app.ui.DetailActivity;
import com.noctune.app.ui.ToastHelper;
import com.noctune.app.utils.ImageLoader;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class FavesAdapter extends RecyclerView.Adapter<FavesAdapter.ViewHolder> {

    private ArrayList<FavoriteTrack> favorites = new ArrayList<>();
    private final Activity activity;
    private MusicHelper musicHelper;

    public FavesAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setFavorites(ArrayList<FavoriteTrack> favorites) {
        this.favorites.clear();
        if (favorites.size() > 0) {
            this.favorites.addAll(favorites);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faves, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(favorites.get(position));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvTrackName, tvArtistName, tvDelete;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_fave_image);
            tvTrackName = itemView.findViewById(R.id.tv_fave_track_name);
            tvArtistName = itemView.findViewById(R.id.tv_fave_artist_name);
            tvDelete = itemView.findViewById(R.id.tv_delete);
        }

        void bind(FavoriteTrack fave) {
            // Amankan nilai null agar string tidak kosong/null pointer
            String trackName = fave.getTrackName() != null ? fave.getTrackName() : "Unknown Track";
            String artistName = fave.getArtistName() != null ? fave.getArtistName() : "Unknown Artist";
            String imageUrl = fave.getImageUrl() != null ? fave.getImageUrl() : "";
            String duration = fave.getDuration() != null ? fave.getDuration() : "0";
            String playcount = fave.getPlaycount() != null ? fave.getPlaycount() : "0";
            String listeners = fave.getListeners() != null ? fave.getListeners() : "0";

            tvTrackName.setText(trackName);
            tvArtistName.setText(artistName);

            // AMAN DARI NULL: Masukkan variabel yang sudah divalidasi ke ImageLoader
            com.noctune.app.utils.ImageLoader.loadTrackImage(
                    ivImage,
                    imageUrl,
                    trackName,
                    artistName
            );

            // Klik Item ke Detail Activity (Gunakan variabel yang sudah aman)
            itemView.setOnClickListener(v -> {
                Track detailTrack = new Track(
                        trackName,
                        duration,
                        playcount,
                        listeners,
                        "",
                        new TrackArtist(artistName),
                        imageUrl
                );

                Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                intent.putExtra("track", detailTrack);
                itemView.getContext().startActivity(intent);
            });

            // Tombol hapus dari favorites dengan dialog konfirmasi kustom
            tvDelete.setOnClickListener(v -> {
                // Gunakan properti 'activity' dari adapter yang sudah pasti valid Activity Context
                View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_delete, null);
                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
                        .setView(dialogView).create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                TextView tvMessage = dialogView.findViewById(R.id.tv_confirm_message);
                android.widget.Button btnCancel = dialogView.findViewById(R.id.btn_cancel_delete);
                android.widget.Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_delete);

                // Menggunakan trackName yang sudah di-convert to UpperCase dengan aman
                tvMessage.setText("ARE YOU SURE YOU WANT TO REMOVE '" + trackName.toUpperCase() + "' FROM FAVORITES?");

                // Jika CANCEL
                btnCancel.setOnClickListener(vCancel -> dialog.dismiss());

                // Jika REMOVE (Konfirmasi)
                btnConfirm.setOnClickListener(vConfirm -> {
                    musicHelper = MusicHelper.getInstance(activity.getApplicationContext());
                    musicHelper.open();
                    int result = musicHelper.deleteById(String.valueOf(fave.getId()));
                    if (result > 0) {
                        int currentPosition = getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            favorites.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            ToastHelper.showError(activity, "[SYSTEM_LOG: TRACK_REMOVED_FROM_FAVORITES]");
                        }
                    }
                    musicHelper.close();
                    dialog.dismiss();
                });

                dialog.show();
            });
        }
    }
}