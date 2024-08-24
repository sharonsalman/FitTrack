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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel extends ViewModel {
    private static final String TAG = "UserViewModel";
    private final MutableLiveData<User> userFitnessData = new MutableLiveData<>();
    private final DatabaseReference reference;

    public UserViewModel() {
        reference = FirebaseDatabase.getInstance("https://fittrack-70436-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("users");
        fetchUserData();
    }

    public LiveData<User> getUserFitnessData() {
        return userFitnessData;
    }

    public void fetchUserData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        // Retrieve data as strings
                        String workoutFrequency = getStringFromSnapshot(dataSnapshot.child("workoutFrequency"));
                        String fitnessLevel = getStringFromSnapshot(dataSnapshot.child("fitnessLevel"));
                        String workoutLocation = getStringFromSnapshot(dataSnapshot.child("workoutLocation"));
                        String goal = getStringFromSnapshot(dataSnapshot.child("goals"));

                        // Retrieve data as floats
                        float currentWeight = getFloatFromSnapshot(dataSnapshot.child("currentWeight"));
                        float targetWeight = getFloatFromSnapshot(dataSnapshot.child("targetWeight"));

                        Map<String, Map<String, User.ProgramDate>> program_dates = new HashMap<>();
                        DataSnapshot programDatesSnapshot = dataSnapshot.child("program_dates");
                        for (DataSnapshot dateSnapshot : programDatesSnapshot.getChildren()) {
                            String date = dateSnapshot.getKey();
                            Map<String, User.ProgramDate> innerMap = new HashMap<>();
                            for (DataSnapshot programSnapshot : dateSnapshot.getChildren()) {
                                String programId = programSnapshot.child("programId").getValue(String.class);
                                String programName = programSnapshot.child("programName").getValue(String.class);
                                User.ProgramDate programDate = new User.ProgramDate();
                                programDate.programId = programId;
                                programDate.programName = programName;
                                innerMap.put(programSnapshot.getKey(), programDate);
                            }
                            program_dates.put(date, innerMap);
                        }

                        // Create and set the User object
                        User data = new User(firebaseUser.getEmail(), firebaseUser.getDisplayName(), "", 0,
                                workoutFrequency, fitnessLevel, workoutLocation, goal,
                                currentWeight, targetWeight, program_dates, null);
                        userFitnessData.setValue(data);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing user fitness data", e);
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
            return 0.0f;
        }
    }

    public void updateUserFitnessData(User updatedFitnessData) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid(); // Use UID for updating data
            reference.child(uid).setValue(updatedFitnessData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User fitness data updated successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating user fitness data", e));
        }
    }

}
