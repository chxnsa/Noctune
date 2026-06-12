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

            if (track.getImageUrl() != null && !track.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(track.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.ic_launcher_background);
            }

            // Klik item → konversi ke Track lalu buka DetailActivity
            // Fix No.8 — sebelumnya tidak ada listener sama sekali
            itemView.setOnClickListener(v -> {
                Track detailTrack = convertToTrack(track);

                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtra("track", detailTrack);
                activity.startActivity(intent);
            });

            // Hapus dari playlist
            tvRemove.setOnClickListener(v -> {
                MusicHelper helper = MusicHelper.getInstance(
                        activity.getApplicationContext());
                helper.open();
                helper.deleteTrackFromPlaylist(String.valueOf(track.getId()));
                helper.close();

                tracks.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                Toast.makeText(activity,
                        "Removed from playlist", Toast.LENGTH_SHORT).show();

                if (listener != null) listener.onTrackRemoved();
            });
        }

        private Track convertToTrack(PlaylistTrack pt) {
            return new Track(
                    pt.getTrackName(),
                    pt.getDuration(),
                    "0",                 // playcount tidak disimpan di playlist
                    "0",                 // listeners tidak disimpan di playlist
                    "",                  // url last.fm tidak disimpan
                    new TrackArtist(pt.getArtistName()),
                    pt.getImageUrl()
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