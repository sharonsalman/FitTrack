package com.sharonsalman.fittrack;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserViewModel extends ViewModel {
    private static final String TAG = "UserViewModel";
    private final MutableLiveData<UserFitnessData> userFitnessData = new MutableLiveData<>();
    private final DatabaseReference reference;

    public UserViewModel() {
        reference = FirebaseDatabase.getInstance("https://fittrack-70436-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("users");
        fetchUserData();
    }

    public LiveData<UserFitnessData> getUserFitnessData() {
        return userFitnessData;
    }

    public void fetchUserData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid(); // Use UID for fetching data
            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        // Extract and handle String fields
                        String workoutFrequency = getStringFromSnapshot(dataSnapshot.child("workoutFrequency"));
                        String fitnessLevel = getStringFromSnapshot(dataSnapshot.child("fitnessLevel"));
                        String workoutLocation = getStringFromSnapshot(dataSnapshot.child("workoutLocation"));
                        String goal = getStringFromSnapshot(dataSnapshot.child("goals"));

                        // Handle conversion for Long and Double
                        float currentWeight = getFloatFromSnapshot(dataSnapshot.child("currentWeight"));
                        float targetWeight = getFloatFromSnapshot(dataSnapshot.child("targetWeight"));

                        UserFitnessData data = new UserFitnessData(workoutFrequency, fitnessLevel, workoutLocation, goal, currentWeight, targetWeight);
                        userFitnessData.setValue(data);
                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error deserializing user fitness data", e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user fitness data", databaseError.toException());
                }
            });
        } else {
            Log.e(TAG, "No authenticated user found");
        }
    }


    // Helper method to get String from DataSnapshot
    private String getStringFromSnapshot(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Long) {
            return Long.toString((Long) value);
        } else if (value instanceof Double) {
            return Double.toString((Double) value);
        } else {
            return null;
        }
    }

    // Helper method to get Float from DataSnapshot
    private float getFloatFromSnapshot(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        if (value instanceof Long) {
            return ((Long) value).floatValue();
        } else if (value instanceof Double) {
            return ((Double) value).floatValue();
        } else if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing float from String", e);
                return 0.0f;
            }
        } else {
            return 0.0f; // Default value or handle as appropriate
        }
    }

    public void updateUserFitnessData(Map<String, Object> updates) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User fitness data successfully updated"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update user fitness data", e));
        }
    }


}
