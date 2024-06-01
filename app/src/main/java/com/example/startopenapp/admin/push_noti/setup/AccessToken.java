package com.example.startopenapp.admin.push_noti.setup;

import android.os.AsyncTask;
import android.util.Log;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    public static final String firebaseScope = "https://www.googleapis.com/auth/firebase.messaging";

    public void getAccessToken(final AccessTokenCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String jsonString = "{\n" +
                            "  \"type\": \"service_account\",\n" +
                            "  \"project_id\": \"send-otp-bd7de\",\n" +
                            "  \"private_key_id\": \"56369bc3338d332b936df7e69f1fb7d628561921\",\n" +
                            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCKWOiLG7Sxecs+\\nejgg0YnI04mCZIhcC3DC2toG/3TlnheGZBqtQWGozMH5VcNiOAzbFhsX8k9FeNk4\\nOeK7eSPBehRRXUJpWFP/wBspAZ5vcMk2vfdEazIgFSY42k68M41H2ciSSVUCkSMd\\nQMVYw6KbZ/DSQ7PdBhtCeXmMuSpR9QBlV/XiM2PWePWqArjjBQYkLy9o/7zSq75O\\nRGLaFKF4A/tOYfZGn3w/tIPfju7ksW4EEF/0+3vPuyKPgsfpaai7BsdoVlcymYrP\\nO/qJrfUCsZnAaJKl6LatDPRVTXYmh64LJy+KBOrYAmTeAlD3MPZ3OjnxcmAS9A+l\\nIaX4V6lHAgMBAAECggEAD9l+uWJ7sYd0CZzZQv1D1/OlgdWVqlc53GupOyWsv++m\\n3058Sz690FJg+KPi20+ePW1LaP4UJy/gpuiOzrk3/PRI+/H/Z+/myQU6YgNy925y\\n8ZK9j3jpHbQpPwMtA7JeQbiJb3pM7cXbQYCIUL0yVbPd6vY3AXUvzfj6ZsJXPwXz\\nm83fwe3GQS6nmad/gIa+WgAI8ke2KTxr4tZYfxVyFDS1Fuy3bZs/8RGdlAdvNKqq\\nskaTTbjMLVhgTyZuP41o0mitexwQS89JEJ2OdXbF9Ouiurm7gik6sT8ojCLpmdqW\\n06LN3Hk9u+X9E5lvmIpIjgpFR0onULdcEvr452cb1QKBgQDAIaH6gCyVRimJQThv\\n+OvVmJZ5DaK6hzsKilEsrmr+hGN7d5h8/k4N/B1ag8BDmpLRnNzfU1pK2ddnJ/KU\\nuVLq4P0DiT38cgK1aYgjQ4UkwmVZRlmc/WYd/2bObXGq6QgtnaiiE566+NN63T75\\nsb5CUMvUu20i14sz6O5bWBLuEwKBgQC4VkENd2AaskMFFlkOrteZLxYF/TxADOY4\\n1BNI5cAWydIn9oDKldL156jS9aeWO08ib/F6Z4PQrHNBOErFrVX7pVqVI3TdzLmD\\ngdssjbyLLsTuJoqEKr5tyrLJqOPSNxZQfIgwx4/LWfesY1z4xM6kEYNzRJlSNKcQ\\nCdvLEKYufQKBgBEusNK7RcFvzeUlfQueglDO3Z4j1wrBb0lJB2tsicsLQe5Xyr5a\\n69YaVvGyWK5zE3BFjAc46BlLlBT4oJwmWsEAr//x9SYveAiLUI5+ylh4PiIWxYrh\\nqpFCNwp9BS5Yus+BiwQ7pvVnrVoOkUQ5Xh+GJER5IpB8IEnbKf3nSYIbAoGASVX/\\ncXURpe1PnoGCksXaflXuRBm4U58OJIOy3GiTID3Qvxdhq7cpg61TTAJxVuJnnEK+\\n5jyyg11oSQU1MinAsGnbfMPrZaZiE8Cgyrvky9aigECTEPvUYOeoc3Qelu5VkVXJ\\n9OYf/f3f/BRX05ywvTM+gahD+yF5aXcSm1oXUx0CgYBDBG9qHTc0QNhxdKo+xR9/\\nQ3gOxjaggkgxQAjKWmGIiHkFHPom24pUAHj+TxyHv3wiPz8x8fDWEAczeZPG1WZC\\nYtQKdMH9lkAHxiClnjEdnCJE95/NHn2BhoG+NUy6np6+vaiOz9NrhseYwP8u0sBB\\n4ixFl3cEUWeYdtO/uCT1ww==\\n-----END PRIVATE KEY-----\\n\",\n" +
                            "  \"client_email\": \"firebase-adminsdk-9sc7l@send-otp-bd7de.iam.gserviceaccount.com\",\n" +
                            "  \"client_id\": \"109751318593394549453\",\n" +
                            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-9sc7l%40send-otp-bd7de.iam.gserviceaccount.com\",\n" +
                            "  \"universe_domain\": \"googleapis.com\"\n" +
                            "}";

                    InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

                    GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream).createScoped(
                            Lists.newArrayList(firebaseScope));
                    googleCredentials.refresh();

                    // Lấy key token
                    return googleCredentials.getAccessToken().getTokenValue();
                } catch (IOException e) {
                    Log.e("error", " " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String token) {
                if (callback != null) {
                    callback.onTokenReceived(token);
                }
            }
        }.execute();
    }

    public interface AccessTokenCallback {
        void onTokenReceived(String token);
    }
}

