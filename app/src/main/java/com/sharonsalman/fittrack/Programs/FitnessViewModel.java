package com.sharonsalman.fittrack.Programs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharonsalman.fittrack.User;

import java.util.ArrayList;
import java.util.List;

public class FitnessViewModel extends ViewModel {

    private static final String TAG = "FitnessViewModel";
    private final MutableLiveData<List<FitnessProgram>> fitnessPrograms = new MutableLiveData<>();
    private final MutableLiveData<List<FitnessProgram>> filteredPrograms = new MutableLiveData<>();
    private final MutableLiveData<FitnessProgram> selectedProgram = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final DatabaseReference reference;
    private List<FitnessProgram> programs = new ArrayList<>();

    public void setPrograms(List<FitnessProgram> programs) {
        this.programs = programs;
    }

    public FitnessViewModel() {
        reference = FirebaseDatabase.getInstance("https://fittrack-70436-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("fitness_programs");
        Log.d(TAG, "Firebase reference initialized: " + reference.toString());
        loadFitnessPrograms();
        fetchUserData();
    }

    public LiveData<FitnessProgram> getSelectedProgram() {
        return selectedProgram;
    }

    public LiveData<List<FitnessProgram>> getFitnessPrograms() {
        return filteredPrograms;
    }

    public void setSelectedProgram(FitnessProgram program) {
        selectedProgram.setValue(program);
    }

    public void selectProgram(FitnessProgram program) {
        selectedProgram.setValue(program);
        saveSelectedProgram(program);
    }

    private void saveSelectedProgram(FitnessProgram program) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("selectedProgram").setValue(program.getId())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Program saved successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error saving program", e));
        } else {
            Log.e(TAG, "No authenticated user found");
        }
    }

    List<Integer> convertStringListToIntegerList(List<String> stringList) {
        List<Integer> integerList = new ArrayList<>();
        for (String s : stringList) {
            try {
                integerList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                // Handle the exception if the string cannot be parsed to an integer
            }
        }
        return integerList;
    }

    private void loadFitnessPrograms() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FitnessProgram> programs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Snapshot value: " + snapshot.getValue());
                    FitnessProgram program = snapshot.getValue(FitnessProgram.class);
                    if (program != null) {
                        Log.d(TAG, "Loaded Program: " + program.getName());
                        programs.add(program);
                    } else {
                        Log.e(TAG, "Failed to parse program from snapshot: " + snapshot.getValue());
                    }
                }
                Log.d(TAG, "Total programs loaded: " + programs.size());
                fitnessPrograms.setValue(programs);
                filteredPrograms.setValue(programs);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    public void loadSelectedProgram() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("selectedProgram").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String programId = dataSnapshot.getValue(String.class);
                    if (programId != null) {
                        List<FitnessProgram> allPrograms = fitnessPrograms.getValue();
                        if (allPrograms != null) {
                            for (FitnessProgram program : allPrograms) {
                                if (program.getId().equals(programId)) {
                                    selectedProgram.setValue(program);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error loading selected program", databaseError.toException());
                }
            });
        } else {
            Log.e(TAG, "No authenticated user found");
        }
    }

    public void filterPrograms(String workoutType, String frequency, String difficulty) {
        List<FitnessProgram> allPrograms = fitnessPrograms.getValue();
        if (allPrograms == null) {
            Log.d("FitnessViewModel", "No programs available for filtering");
            filteredPrograms.setValue(new ArrayList<>());
            return;
        }

        List<FitnessProgram> filteredList = new ArrayList<>();
        for (FitnessProgram program : allPrograms) {
            boolean matchesWorkoutType = workoutType.equals("All") || program.getWorkoutType().toLowerCase().contains(workoutType.toLowerCase());
            boolean matchesFrequency = frequency.equals("All") || program.getFrequency().equalsIgnoreCase(frequency);
            boolean matchesDifficulty = difficulty.equals("All") || program.getDifficulty().equalsIgnoreCase(difficulty);
            Log.d("FitnessViewModel", "Comparing - Program: " + program.getWorkoutType() + ", Filter: " + workoutType + ", Match: " + matchesWorkoutType);
            if (matchesWorkoutType && matchesFrequency && matchesDifficulty) {
                filteredList.add(program);
            }
        }

        Log.d("FitnessViewModel", "Filtered programs count: " + filteredList.size());
        filteredPrograms.setValue(filteredList);
    }

    public List<FitnessProgram> getFilteredPrograms(String workoutType, String frequency, String difficulty) {
        List<FitnessProgram> filteredPrograms = new ArrayList<>();
        for (FitnessProgram program : programs) {
            boolean matches = true;
            if (workoutType != null && !program.getWorkoutType().equalsIgnoreCase(workoutType)) {
                matches = false;
            }
            if (frequency != null && !program.getFrequency().equalsIgnoreCase(frequency)) {
                matches = false;
            }
            if (difficulty != null && !program.getDifficulty().equalsIgnoreCase(difficulty)) {
                matches = false;
            }
            if (matches) {
                filteredPrograms.add(program);
            }
        }
        return filteredPrograms;
    }

    public LiveData<User> getUserData() {
        return user;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void updateUserData(User updatedUser) {
        Log.d(TAG, "Saving user data");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.setValue(updatedUser)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User data updated successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating user data", e));
        } else {
            Log.e(TAG, "No authenticated user found");
        }
    }

    public void fetchUserData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User data = dataSnapshot.getValue(User.class);
                    if (data != null) {
                        user.setValue(data);
                        Log.d(TAG, "User data fetched successfully");
                    } else {
                        Log.d(TAG, "No data found for the user");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user data", databaseError.toException());
                }
            });
        } else {
            Log.e(TAG, "No authenticated user found");
        }
    }
}