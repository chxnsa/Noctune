package com.noctune.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.PlaylistTrack;
import com.noctune.app.model.Track;
import com.noctune.app.model.TrackArtist;
import com.noctune.app.ui.DetailActivity;
import com.noctune.app.ui.ToastHelper;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class PlaylistTrackAdapter extends
        RecyclerView.Adapter<PlaylistTrackAdapter.ViewHolder> {

    private ArrayList<PlaylistTrack> tracks = new ArrayList<>();
    private final Activity activity;
    private OnTrackRemovedListener listener;

    public interface OnTrackRemovedListener {
        void onTrackRemoved();
    }

    public PlaylistTrackAdapter(Activity activity, OnTrackRemovedListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void setTracks(ArrayList<PlaylistTrack> tracks) {
        this.tracks.clear();
        this.tracks.addAll(tracks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tracks.get(position));
    }

    @Override
    public int getItemCount() { return tracks.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName, tvArtist, tvDuration, tvRemove;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_playlist_track_image);
            tvName = itemView.findViewById(R.id.tv_playlist_track_name);
            tvArtist = itemView.findViewById(R.id.tv_playlist_artist_name);
            tvDuration = itemView.findViewById(R.id.tv_playlist_track_duration);
            tvRemove = itemView.findViewById(R.id.tv_remove_track);
        }

        void bind(PlaylistTrack track) {
            tvName.setText(track.getTrackName());
            tvArtist.setText(track.getArtistName());
            tvDuration.setText(formatDuration(track.getDuration()));

            // ==================== PERBAIKAN GAMBAR DI SINI ====================
            // SEBELUMNYA: Memakai Picasso mentah
            com.noctune.app.utils.ImageLoader.loadTrackImage(
                    ivImage,
                    track.getImageUrl(),
                    track.getTrackName(),
                    track.getArtistName()
            );
            // ==================================================================

            // Klik item → konversi ke Track lalu buka DetailActivity
            itemView.setOnClickListener(v -> {
                Track detailTrack = convertToTrack(track);

                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtra("track", detailTrack);
                activity.startActivity(intent);
            });

            tvRemove.setOnClickListener(v -> {
                // Inflate layout dialog kustom brutalismu
                View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_delete, null);
                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
                        .setView(dialogView).create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                TextView tvMessage = dialogView.findViewById(R.id.tv_confirm_message);
                android.widget.Button btnCancel = dialogView.findViewById(R.id.btn_cancel_delete);
                android.widget.Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_delete);

                // Set pesan kustom untuk playlist
                tvMessage.setText("ARE YOU SURE YOU WANT TO REMOVE '" + track.getTrackName().toUpperCase() + "' FROM THIS PLAYLIST?");

                // Jika CANCEL
                btnCancel.setOnClickListener(vCancel -> dialog.dismiss());

                // Jika REMOVE (Konfirmasi)
                btnConfirm.setOnClickListener(vConfirm -> {
                    MusicHelper helper = MusicHelper.getInstance(activity.getApplicationContext());
                    helper.open();
                    helper.deleteTrackFromPlaylist(String.valueOf(track.getId()));
                    helper.close();

                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        tracks.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        ToastHelper.showError(activity, "[TRACK REMOVED FROM PLAYLIST]");
                    }

                    if (listener != null) listener.onTrackRemoved();
                    dialog.dismiss();
                });

                dialog.show();
            });
        }

        private Track convertToTrack(PlaylistTrack pt) {
            String playcountVal = (pt.getPlaycount() != null) ? pt.getPlaycount() : "0";
            String listenersVal = (pt.getListeners() != null) ? pt.getListeners() : "0";
            String durationVal = (pt.getDuration() != null) ? pt.getDuration() : "0";
            String imageUrlVal = (pt.getImageUrl() != null) ? pt.getImageUrl() : "";

            // PERBAIKAN FATAL: Buat objek TrackArtist secara utuh agar DetailActivity
            // tidak crash saat memanggil track.getArtist().getName()
            TrackArtist artist = new TrackArtist(pt.getArtistName());

            // Jika di model TrackArtist kamu ada field url/image, set default agar tidak null
            // artist.setUrl("");

            return new Track(
                    pt.getTrackName(),
                    durationVal,
                    playcountVal,
                    listenersVal,
                    "", // url Last.fm dikosongkan tidak masalah
                    artist,
                    imageUrlVal
            );
        }

        private String formatDuration(String durationStr) {
            try {
                int seconds = Integer.parseInt(durationStr);
                return String.format("%d:%02d", seconds / 60, seconds % 60);
            } catch (Exception e) {
                return "0:00";
            }
        }
    }
}