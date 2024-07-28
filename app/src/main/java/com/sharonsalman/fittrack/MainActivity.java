package com.sharonsalman.fittrack;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<User> call = apiService.getUser("john_doe");

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        Log.d("MainActivity", "User Email: " + user.getEmail());
                        Log.d("MainActivity", "User Name: " + user.getName());
                        Log.d("MainActivity", "User Age: " + user.getAge());
                        Log.d("MainActivity", "User Workout Frequency: " + user.getWorkoutFrequency());
                        Log.d("MainActivity", "User Fitness Level: " + user.getFitnessLevel());
                        Log.d("MainActivity", "User Workout Location: " + user.getWorkoutLocation());
                        Log.d("MainActivity", "User Goal: " + user.getGoal());
                        Log.d("MainActivity", "User Current Weight: " + user.getCurrentWeight());
                        Log.d("MainActivity", "User Target Weight: " + user.getTargetWeight());
                    }
                } else {
                    Log.e("MainActivity", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("MainActivity", "Network request failed", t);
            }
        });
    }
}
