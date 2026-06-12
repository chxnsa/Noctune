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
import com.noctune.app.model.Artist;
import com.noctune.app.utils.ImageLoader;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import com.noctune.app.ui.ArtistDetailActivity;

public class ArtistHorizontalAdapter extends
        RecyclerView.Adapter<ArtistHorizontalAdapter.ViewHolder> {

    private List<Artist> artists;
    private Context context;

    public ArtistHorizontalAdapter(Context context) {
        this.context = context;
        this.artists = new ArrayList<>();
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(artists.get(position));
    }

    @Override
    public int getItemCount() { return artists.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_artist_image);
            tvName = itemView.findViewById(R.id.tv_artist_name);
        }

        void bind(Artist artist) {
            tvName.setText(artist.getName());

            ImageLoader.loadArtistImage(
                    ivImage,
                    artist.getImageUrl(),
                    artist.getName()
            );

            // Klik artist → ArtistDetailActivity (untuk fix No.3)
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ArtistDetailActivity.class);
                intent.putExtra("artist_name", artist.getName());
                context.startActivity(intent);
            });
        }
    }
}