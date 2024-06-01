package com.example.startopenapp.display_manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.startopenapp.R;

public class DialogHelper {
    public static void DialogWarning(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button buttonOK = dialogView.findViewById(R.id.button_ok);

        dialogTitle.setText(title);
        dialogMessage.setText(message);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        buttonOK.setOnClickListener(v -> alertDialog.dismiss());
    }

    public static void DialogWarning1(Context context, String message, String negativeButton, DialogInterface.OnClickListener negativeListener, String positiveButton, DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setNegativeButton(negativeButton, negativeListener)
                .setPositiveButton(positiveButton, positiveListener)
                .show();
    }
}
