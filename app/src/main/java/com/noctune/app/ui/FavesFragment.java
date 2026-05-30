package com.noctune.app.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.FavesAdapter;
import com.noctune.app.database.MappingHelper;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.FavoriteTrack;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavesFragment extends Fragment {

    private RecyclerView rvFaves;
    private FavesAdapter favesAdapter;
    private TextView tvEmpty;
    private MusicHelper musicHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faves, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFaves = view.findViewById(R.id.rv_faves);
        tvEmpty = view.findViewById(R.id.tv_empty);

        // Setup RecyclerView
        rvFaves.setLayoutManager(new LinearLayoutManager(getActivity()));
        favesAdapter = new FavesAdapter(getActivity());
        rvFaves.setAdapter(favesAdapter);

        // Init SQLite
        musicHelper = MusicHelper.getInstance(getActivity().getApplicationContext());

        loadFavorites();
    }

    private void loadFavorites() {
        // Pakai Executor — sesuai pola modul SQLite
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            musicHelper.open();
            Cursor cursor = musicHelper.queryAll();
            ArrayList<FavoriteTrack> favorites =
                    MappingHelper.mapCursorToArrayList(cursor);
            cursor.close();

            handler.post(() -> {
                if (favorites.size() > 0) {
                    favesAdapter.setFavorites(favorites);
                    tvEmpty.setVisibility(View.GONE);
                    rvFaves.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvFaves.setVisibility(View.GONE);
                }
            });
        });
    }

    // Refresh saat fragment kembali ditampilkan
    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (musicHelper != null) {
            musicHelper.close();
        }
    }
}