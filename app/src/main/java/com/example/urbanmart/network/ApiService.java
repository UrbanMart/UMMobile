package com.example.urbanmart.network;

import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiService {

    private static final String BASE_URL_DEV = "http://10.0.2.2:8082/api/";
    private static final String BASE_URL_PROD = "https://urbanmartapi-bqdwczd9gqcrdkc7.eastus-01.azurewebsites.net/api/";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;
    private Gson gson;

    public ApiService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(logging);

        this.client = builder.build();
        this.gson = new Gson();
    }

    public void loginUser(String email, String password, Callback callback) {
        UserLoginRequest loginRequest = new UserLoginRequest(email, password);
        String json = gson.toJson(loginRequest);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                // Always use BASE_URL_DEV when committing to Git
                .url(BASE_URL_DEV + "users/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    class UserLoginRequest {
        String email;
        String password;

        public UserLoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
