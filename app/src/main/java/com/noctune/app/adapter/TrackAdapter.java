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
        // Kita kirim posisi asli list ke holder
        holder.setData(track, position + 1);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRank; // PERBAIKAN: Tambah variabel TextView untuk penomoran
        private ImageView ivImage;
        private TextView tvTrackName;
        private TextView tvArtistName;
        private TextView tvPlaycount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // PERBAIKAN: Inisialisasi tv_rank dari layout item_track.xml kamu
            tvRank = itemView.findViewById(R.id.tv_rank);
            ivImage = itemView.findViewById(R.id.iv_track_image);
            tvTrackName = itemView.findViewById(R.id.tv_track_name);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvPlaycount = itemView.findViewById(R.id.tv_playcount);
        }

        // PERBAIKAN: Tambah parameter int rank
        public void setData(Track track, int rank) {

            // PERBAIKAN: Format angka agar jika di bawah 10 tampil berawalan nol (01, 02, ... 10, 11)
            if (tvRank != null) {
                String formattedRank = (rank < 10) ? "0" + rank : String.valueOf(rank);
                tvRank.setText(formattedRank);
            }

            tvTrackName.setText(track.getName());

            if (track.getArtist() != null) {
                tvArtistName.setText(track.getArtist().getName());
            }

            if (track.getPlaycount() != null && !track.getPlaycount().isEmpty()) {
                tvPlaycount.setText(formatCount(track.getPlaycount()) + " PLAYS");
                tvPlaycount.setVisibility(View.VISIBLE);
            } else {
                // Jika Last.fm tidak mengembalikan data playcount di tag genre,
                // kita tampilkan text default alih-alih menyembunyikannya agar layout tidak pincang
                tvPlaycount.setText("0 PLAYS");
                tvPlaycount.setVisibility(View.VISIBLE);
            }

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