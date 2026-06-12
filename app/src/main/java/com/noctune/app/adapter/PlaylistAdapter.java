package com.noctune.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // 1. PASTIKAN INI DIIMPORT
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.Playlist;
import com.noctune.app.ui.PlaylistDetailActivity;
import com.noctune.app.ui.ToastHelper;
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
        ImageView ivPlaylistImage; // Deklarasi ImageView kustom

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_playlist_name);
            tvCount = itemView.findViewById(R.id.tv_track_count);
            tvDelete = itemView.findViewById(R.id.tv_delete_playlist);

            // Hubungkan dengan ID baru dari XML
            ivPlaylistImage = itemView.findViewById(R.id.iv_playlist_image);
        }

        void bind(Playlist playlist) {
            tvName.setText(playlist.getName().toUpperCase());
            tvCount.setText(playlist.getTrackCount() + " TRACKS REGISTERED");

            // Klik → buka PlaylistDetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, PlaylistDetailActivity.class);
                intent.putExtra("playlist", playlist);
                activity.startActivity(intent);
            });

            // ==================== OPERASI PEMUATAN IMAGE COVER ====================
            String coverUriStr = playlist.getCover();

            if (ivPlaylistImage != null) {
                if (coverUriStr != null && !coverUriStr.isEmpty()) {
                    try {
                        // Jika ada cover dari galeri, bersihkan padding dan pasang gambarnya
                        ivPlaylistImage.setPadding(0, 0, 0, 0);
                        ivPlaylistImage.setImageURI(android.net.Uri.parse(coverUriStr));
                    } catch (Exception e) {
                        // Fallback aman jika terjadi kegagalan pemuatan berkas galeri
                        ivPlaylistImage.setImageResource(R.drawable.ic_launcher_background);
                    }
                } else {
                    // Jika belum diset covernya, kita kasih background retro bawaan proyekmu
                    ivPlaylistImage.setImageResource(R.drawable.ic_launcher_background);
                }
            }
            // =======================================================================

            // Tombol hapus seluruh playlist dengan dialog konfirmasi kustom
            tvDelete.setOnClickListener(v -> {
                View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_delete, null);
                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
                        .setView(dialogView).create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                TextView tvMessage = dialogView.findViewById(R.id.tv_confirm_message);
                android.widget.Button btnCancel = dialogView.findViewById(R.id.btn_cancel_delete);
                android.widget.Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_delete);

                tvMessage.setText("ARE YOU SURE YOU WANT TO WIPE THE ENTIRE PLAYLIST '" + playlist.getName().toUpperCase() + "'? THIS WILL EJECT ALL TRACKS INSIDE.");

                btnCancel.setOnClickListener(vCancel -> dialog.dismiss());

                btnConfirm.setOnClickListener(vConfirm -> {
                    MusicHelper helper = MusicHelper.getInstance(activity.getApplicationContext());
                    helper.open();

                    int result = helper.deletePlaylist(String.valueOf(playlist.getId()));
                    if (result > 0) {
                        int currentPosition = getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            playlists.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            ToastHelper.showError(activity, "[PLAYLIST DELETED]");
                        }
                    }
                    helper.close();
                    dialog.dismiss();
                });

                dialog.show();
            });
        }
    }
}