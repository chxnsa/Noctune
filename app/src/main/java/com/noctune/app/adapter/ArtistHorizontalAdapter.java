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

            String imageUrl = artist.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()
                    && !imageUrl.contains("2a96cbd8b46e442fc41c2b86b821562f")) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivImage);
            } else {
                // Fallback warna solid per artist
                int[] colors = {0xFFE63329, 0xFFFFD600, 0xFF2D6A4F,
                        0xFF1565C0, 0xFF9C27B0, 0xFFFF6B35};
                int colorIndex = Math.abs(artist.getName().hashCode()) % colors.length;
                ivImage.setImageResource(R.drawable.ic_launcher_background);
                ivImage.setBackgroundColor(colors[colorIndex]);
            }
        }
    }
}