package com.example.startopenapp.display_manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.startopenapp.display_manager.DialogHelper;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && (action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || action.equals("com.example.StartApp"))) {
            boolean isConnected = isNetworkConnected(context);
            if (!isConnected) {
                // Trạng thái kết nối mạng chuyển từ có mạng hoặc không có mạng (không rõ) sang không có mạng
                DialogHelper.DialogWarning(context, "Cảnh báo!","Không có kết nối mạng");
            }
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}