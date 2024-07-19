package com.sharonsalman.fittrack;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("users/{user}")
    Call<User> getUser(@Path("user") String user);
}

