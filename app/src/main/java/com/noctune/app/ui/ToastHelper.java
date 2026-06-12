package com.noctune.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.noctune.app.R;

public class ToastHelper {

    public interface OnConfirmListener {
        void onConfirmed();
    }

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

    // Dialog Konfirmasi Global
    public static void showConfirmDialog(Context context, String message, OnConfirmListener listener) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvMessage = dialogView.findViewById(R.id.tv_confirm_message);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_delete);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_delete);

        tvMessage.setText(message.toUpperCase());

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmed();
            }
            dialog.dismiss();
        });

        dialog.show();
    }
}
