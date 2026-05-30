package com.noctune.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.model.Artist;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private List<Artist> artists;
    private Context context;
    private String currentGenre;

    public ArtistAdapter(Context context) {
        this.context = context;
        this.artists = new ArrayList<>();
    }

    public void setArtists(List<Artist> artists, String genre) {
        this.artists = artists;
        this.currentGenre = genre;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName, tvGenre;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_artist_image);
            tvName = itemView.findViewById(R.id.tv_artist_name);
            tvGenre = itemView.findViewById(R.id.tv_artist_genre);
        }

        void bind(Artist artist) {
            tvName.setText(artist.getName());
            tvGenre.setText(currentGenre.toUpperCase());

            String imageUrl = artist.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivImage);
            }
        }
    }
}