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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {

    private List<Track> tracks;
    private Context context;

    public ChartAdapter(Context context) {
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
                .inflate(R.layout.item_charts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tracks.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRank, tvTrackName, tvArtistName, tvPlaycount;
        ImageView ivImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvTrackName = itemView.findViewById(R.id.tv_chart_track_name);
            tvArtistName = itemView.findViewById(R.id.tv_chart_artist_name);
            tvPlaycount = itemView.findViewById(R.id.tv_chart_playcount);
            ivImage = itemView.findViewById(R.id.iv_chart_image);
        }

        void bind(Track track, int rank) {
            // Nomor ranking — top 3 warna spesial
            tvRank.setText(String.valueOf(rank));
            if (rank == 1) tvRank.setTextColor(0xFFFFD600);      // Gold
            else if (rank == 2) tvRank.setTextColor(0xFFCCCCCC); // Silver
            else if (rank == 3) tvRank.setTextColor(0xFFCD7F32); // Bronze
            else tvRank.setTextColor(0xFF666666);                 // Normal

            tvTrackName.setText(track.getName());

            if (track.getArtist() != null) {
                tvArtistName.setText(track.getArtist().getName());
            }

            tvPlaycount.setText(formatCount(track.getPlaycount()) + " PLAYS");

            String imageUrl = track.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivImage);
            }

            // Klik → DetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("track", track);
                context.startActivity(intent);
            });
        }

        private String formatCount(String countStr) {
            try {
                long count = Long.parseLong(countStr);
                if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000.0);
                else if (count >= 1_000) return String.format("%.1fK", count / 1_000.0);
                return String.valueOf(count);
            } catch (Exception e) {
                return countStr;
            }
        }
    }
}
