package com.noctune.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.Playlist;
import com.noctune.app.ui.PlaylistDetailActivity;
import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private ArrayList<Playlist> playlists = new ArrayList<>();
    private final Activity activity;

    public PlaylistAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists.clear();
        this.playlists.addAll(playlists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(playlists.get(position));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCount, tvDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_playlist_name);
            tvCount = itemView.findViewById(R.id.tv_track_count);
            tvDelete = itemView.findViewById(R.id.tv_delete_playlist);
        }

        void bind(Playlist playlist) {
            tvName.setText(playlist.getName().toUpperCase());
            tvCount.setText(playlist.getTrackCount() + " TRACKS");

            // Klik → buka PlaylistDetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, PlaylistDetailActivity.class);
                intent.putExtra("playlist", playlist);
                activity.startActivity(intent);
            });

            // Hapus playlist
            tvDelete.setOnClickListener(v -> {
                MusicHelper helper = MusicHelper.getInstance(
                        activity.getApplicationContext());
                helper.open();
                helper.deletePlaylist(String.valueOf(playlist.getId()));
                helper.close();

                playlists.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                Toast.makeText(activity,
                        "Playlist deleted", Toast.LENGTH_SHORT).show();
            });
        }
    }
}