package com.example.startopenapp.admin.push_noti.setup;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmNotificationsSender {
    public static final String fcmApiUrl = "https://fcm.googleapis.com/v1/projects/send-otp-bd7de/messages:send";

    public static void sendNotificationToTopic(String topic, String title, String body) {
        OkHttpClient client = new OkHttpClient();

        // Lấy key token
        AccessToken accessToken = new AccessToken();
        accessToken.getAccessToken(token -> {
            if (token != null) {
                // Xây dựng JSON payload
                String json = "{\n" +
                        "  \"message\": {\n" +
                        "    \"topic\": \"" + topic + "\",\n" +
                        "    \"notification\": {\n" +
                        "      \"title\": \"" + title + "\",\n" +
                        "      \"body\": \"" + body + "\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

                // Tạo request
                Request request = new Request.Builder()
                        .url(fcmApiUrl)
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .build();

                // Thực hiện gọi API bằng cách sử dụng client
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            System.out.println("Notification sent successfully");
                        } else {
                            System.out.println("Failed to send notification: " + Objects.requireNonNull(response.body()).string());
                        }
                    }
                });
            }
        });
    }
}
