package com.example.couponify1;

import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

//this class was done by ai, source in documentation
public class AccessToken {

    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public static String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"couponify1-636d2\",\n" +
                    "  \"private_key_id\": \"572976ddb6d9494bc5a0786a6cdc85a21b7ea6f8\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDaU5X+EdXkeJeq\\ndz1kLii2gEUvhH9YCDdxKHhAfgcesEVEPevnrAU0YsjsDI5uXhK4Ls6hgmtTKmuf\\nL2ZIDcp/Pc+Y/Jls8vd8sjKErxisYDPFkwheyFLdSLq5JSOgonXKnAa5Zg/izpoB\\nikqnlp9ki3T/5VizGNTFhpoqmfXssOZF11mzD3t2olJzLu3eH4BQIxzU3uWrWA70\\norlVmeJpeTatwoe/ouTzhf6mJGJiOF6NaGH5pM5I4GBofI+/0zP4/ObqQlMZyXim\\nkEW4hg/zU8uVZUSOOz5ugEGi8VjZQ+1vOgFwgILU6vqnIa7cd3J/3fBuU4kDur7I\\nWmAy+6xhAgMBAAECggEADUD2wZI+nFyPxHrxoKPGt/GIga529palGvr8ddnQv9Wu\\nlsp1iD5ioIAAt4e5m8ydMU4yIA7lSgqwttyvRl+D8/6CHET1YW5MhObidziT9F7w\\ntaVL5Ekf8qwIHdY51/YU1YRsShP+keO96KsHBi54Jafrpd87579xpz4ZHM305nXn\\n/n+OAQQsy5uUGOugH31Of/fp45mHiVY3dLUkJKHk5kxsHxCE6s+7KIiCWXfhl4xV\\nvvr4rzF+GDoAy/JcnrVMs7bKxUqNEBa+F8hiQoRGgZjSJbgtiEbR2gXyOPIBPVnl\\nX4ZYVH3Cb4m9Z9HVCBIFX626mmhiZs6fZSGfmIKrKwKBgQD5Gbh5jPLwQozGzADf\\nej6QpzZLuBudHJdJGI4h4MiofyAXnJkrVFHTIFUFlO3hLVOQ0fC1o3px3v6NC0Aw\\nxpk9d9eGcLyoNmIocdbWS2b8JIGli5Xrl07SxqcupEFJS5jYMCYhO2Gm913l9Z5R\\n6diFRf36/U2yPzgPstSgKigVRwKBgQDgX6iTWPcjhipZJEEA1oPsx13AxISJlnVO\\n/E+8LiGHHjjr4Xjkx2sROpWV0LD7sYlRK6wzc8kNMJNrMQUgMxxWYQFw0CzhL+xe\\nbL0qIdWgBpZzu7zMucW1T0G3iMJjLhsgetdFd1vdCWQ5j0xK56261JZ9ZT+RxssP\\nXH6oUH2lFwKBgEYtH9Jmif5z0TMkP861orCf2uUEDgt6BYjbOReEOyN1BPaB8IjL\\nbW3cOveimE2KtnMjIfwZ/SHdX7seg0v6dytg+r/uPMfq4kZgo9SlU3MPWdLoeyju\\n7I7m1iyGq7nZaUrmbaeJEh62q6ZgRuTuy0foiv/NorjnpISPreUdW0N7AoGBANaS\\nZJJVGDN+0OziwM9eVleP6Va6GM3Dd3gLzuz1aNbgziq2O3j+RKP7VKYrJ7Xr+JgJ\\nnequpezZMYHnJTG/7y+kIU6TpvfLbs52SwkJxlP9ONCBD3LxjeNwYjZ0sQFMTGDp\\nWQlztgRSQOjS43EW0ovDVg+hTgPJCtQr7mxkXWQNAoGBANxu8cdAXYT0+CimcO3N\\nVCd6hlQz7I8fJqOoyVQgmkB7iuLiY2voKvisxwd5kVs7aPDWxHwTYG3LOyjgt19w\\nwsQjLp/4bu9i8ervyHUfcTimshRAYcOM4W+fcflwJ36NPaVAYE1fQB9MKjcQ/6nl\\ne4jZxRdxc1vAt6nG9t3XUjAC\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-stwls@couponify1-636d2.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"103116932723354258253\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-stwls%40couponify1-636d2.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";
            InputStream is = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(is)
                    .createScoped(firebaseMessagingScope);
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            System.out.println("error" + e.getMessage());
            return null;
        }
    }
}
