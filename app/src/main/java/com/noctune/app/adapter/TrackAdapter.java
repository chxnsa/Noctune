package com.noctune.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.model.Track;
import com.noctune.app.ui.DetailActivity;
import com.noctune.app.utils.ImageLoader;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private List<Track> tracks;
    private Context context;

    public TrackAdapter(Context context) {
        this.context = context;
        this.tracks = new ArrayList<>();
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.setData(track);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvTrackName;
        private TextView tvArtistName;
        private TextView tvPlaycount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_track_image);
            tvTrackName = itemView.findViewById(R.id.tv_track_name);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvPlaycount = itemView.findViewById(R.id.tv_playcount);
        }

        public void setData(Track track) {
            tvTrackName.setText(track.getName());

            if (track.getArtist() != null) {
                tvArtistName.setText(track.getArtist().getName());
            }

            tvPlaycount.setText(formatCount(track.getPlaycount()) + " PLAYS");


            String artistName = track.getArtist() != null ?
                    track.getArtist().getName() : "";

            ImageLoader.loadTrackImage(
                    ivImage,
                    track.getImageUrl(),
                    track.getName(),
                    artistName
            );

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("track", track);
                context.startActivity(intent);
            });
        }

        // Format angka — 27304609 → 27.3M
        private String formatCount(String countStr) {
            try {
                long count = Long.parseLong(countStr);
                if (count >= 1_000_000) {
                    return String.format("%.1fM", count / 1_000_000.0);
                } else if (count >= 1_000) {
                    return String.format("%.1fK", count / 1_000.0);
                }
                return String.valueOf(count);
            } catch (Exception e) {
                return countStr;
            }
        }
    }
}