package com.noctune.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.noctune.app.R;

public class ToastHelper {

    // Fungsi global untuk Toast Normal (Warna Kuning Brand)
    public static void showSuccess(Context context, String message) {
        showCustomToast(context, message, false);
    }

    // Fungsi global untuk Toast Error/Gagal (Warna Merah Brand)
    public static void showError(Context context, String message) {
        showCustomToast(context, message, true);
    }

    private static void showCustomToast(Context context, String message, boolean isError) {
        // 1. Inflate layout kustom brutalist
        View layout = LayoutInflater.from(context).inflate(R.layout.brutalist_toast, null);

        // 2. Set pesan teksnya
        TextView text = layout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        // 3. Atur warna teks berdasarkan tipe (Error atau Sukses)
        if (isError) {
            text.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.brand_red));
        } else {
            text.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.brand_yellow));
        }

        // 4. Racik Toast bawaan Android menggunakan View kustom kita
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout); // Pasang view kustom di sini
        toast.show();
    }
}