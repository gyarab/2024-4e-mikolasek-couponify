package com.example.couponify1;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification {
    private static final String PROJECT_ID = "couponify1-636d2";
    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send";
    public void SendPushNotification(String notifTitle, String notifBody, String fcmtoken) {
        try {
            OkHttpClient client = new OkHttpClient();
            String jsonPayload = String.format("{\"message\":{\"token\":\"%s\",\"notification\":{\"title\":\"%s\",\"body\":\"%s\"}}}", fcmtoken, notifTitle, notifBody);

            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=UTF-8"), jsonPayload);
            String accessToken = AccessToken.getAccessToken();
            System.out.println("accesstoken: " + accessToken);
            Request request = new Request.Builder().url(FCM_URL).post(body)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json").build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("notif successful");
                    } else {
                        System.out.println(response.code() + " " + response.message());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
