package com.noctune.app.adapter;

import android.app.Activity;
import android.content.ContentValues;
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
import com.noctune.app.model.FavoriteTrack;
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
            tvTrackName.setText(fave.getTrackName());
            tvArtistName.setText(fave.getArtistName());

            // Load gambar
            if (fave.getImageUrl() != null && !fave.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(fave.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivImage);
            }

            // Tombol hapus dari favorites
            tvDelete.setOnClickListener(v -> {
                musicHelper = MusicHelper.getInstance(activity.getApplicationContext());
                musicHelper.open();
                int result = musicHelper.deleteById(String.valueOf(fave.getId()));
                if (result > 0) {
                    favorites.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    Toast.makeText(activity,
                            "Removed from Favorites",
                            Toast.LENGTH_SHORT).show();
                }
                musicHelper.close();
            });
        }
    }
}