package com.example.urbanmart.network;

import android.content.Context;

import com.example.urbanmart.model.Feedback;
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
     * Fetch list of orders from the API
     *
     * @param callback Callback to handle response or failure
     */
    public void fetchOrders(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "Orders")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
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
     * Fetch users by a list of IDs (comma-separated).
     *
     * @param userId      Comma-separated string of user IDs.
     * @param callback Callback to handle response or failure.
     */
    public void fetchUsersByIds(String userId, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "Users/" + userId)
                .get()
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
                .url(BASE_URL + "Products?isActive=true")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Submit an order by making a POST request
     *
     * @param orderJson The order details in JSON format
     * @param callback  Callback to handle response or failure
     */
    public void submitOrder(String orderJson, Callback callback) {
        RequestBody body = RequestBody.create(orderJson, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "Orders")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }
    /**
     * Fetch notifications for a specific user.
     *
     * @param userId   The ID of the user whose notifications should be fetched.
     * @param callback Callback to handle response or failure.
     */
    public void fetchNotificationsForUser(String userId, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "Notifications/user/" + userId)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Mark notification as read.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @param callback       Callback to handle response or failure.
     */
    public void markNotificationAsRead(String notificationId, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "Notifications/" + notificationId + "/read")
                .put(RequestBody.create("", JSON)) // Empty body for the PUT request
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Fetch feedback for a specific vendor.
     *
     * @param vendorId The ID of the vendor whose feedback should be fetched.
     * @param callback Callback to handle response or failure.
     */
    public void fetchVendorFeedback(String vendorId, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "VendorFeedback/vendor/" + vendorId)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Post feedback for a vendor.
     *
     * @param feedback The feedback object to post.
     * @param callback
     */
    public void postVendorFeedback(Feedback feedback, Callback callback) {
        String json = gson.toJson(feedback);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "VendorFeedback")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Sends a request to cancel an order using the specified order ID.
     * This method constructs an HTTP PUT request with an empty body
     * and sends it to the cancellation endpoint of the API.
     *
     * @param orderId  The unique identifier of the order to be cancelled.
     * @param callback The callback to handle the response from the server.
     */
    public void requestOrderCancellation(String orderId, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "Orders/" + orderId + "/request-cancellation")
                .put(RequestBody.create("", JSON)) // Empty body
                .addHeader("Content-Type", "application/json")
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
