package com.example.urbanmart.network;

import android.content.Context;

import com.google.gson.Gson;
import com.example.urbanmart.model.User;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiService {

    private static final String BASE_URL = "https://urbanmartapi-bqdwczd9gqcrdkc7.eastus-01.azurewebsites.net/api/";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;
    private Gson gson;

    public ApiService(Context context) {
        // Initialize logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Build OkHttp client with interceptor
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(logging);

        this.client = builder.build();
        this.gson = new Gson();
    }

    /**
     * Login user by making a POST request
     *
     * @param email    User email
     * @param password User password
     * @param callback Callback to handle response or failure
     */
    public void loginUser(String email, String password, Callback callback) {
        UserLoginRequest loginRequest = new UserLoginRequest(email, password);
        String json = gson.toJson(loginRequest);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "users/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Sign up a new user by making a POST request
     *
     * @param user     The user object containing name, email, and password
     * @param callback Callback to handle response or failure
     */
    public void signUpUser(User user, Callback callback) {
        String json = gson.toJson(user);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "Users")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Update user profile by making a PUT request
     *
     * @param userId   The user ID to update
     * @param user     The user object containing updated data
     * @param callback Callback to handle response or failure
     */
    public void updateUserProfile(String userId, User user, Callback callback) {
        String json = gson.toJson(user);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "Users/" + userId)
                .put(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Fetch list of products from the API
     *
     * @param callback Callback to handle response or failure
     */
    public void fetchProducts(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "Products")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Inner class to represent a user login request body
     */
    static class UserLoginRequest {
        String email;
        String password;

        public UserLoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
