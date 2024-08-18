package com.sharonsalman.fittrack;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FitTrackApplication extends Application {
    private static final String TAG = "FitTrackApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        initializeFirebase();
    }

    private void initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this);
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://fittrack-70436-default-rtdb.europe-west1.firebasedatabase.app");
            database.setPersistenceEnabled(true);
            Log.d(TAG, "Firebase initialized successfully");


            DatabaseReference testRef = database.getReference("test");
            testRef.setValue("test_value")
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Firebase connection test successful"))
                    .addOnFailureListener(e -> Log.e(TAG, "Firebase connection test failed", e));
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
        }
    }
}