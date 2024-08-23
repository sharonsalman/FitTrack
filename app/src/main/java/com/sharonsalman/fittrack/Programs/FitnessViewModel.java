package com.sharonsalman.fittrack.Programs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class FitnessViewModel extends ViewModel {

    private static final String TAG = "FitnessViewModel";
    private final MutableLiveData<List<FitnessProgram>> fitnessPrograms = new MutableLiveData<>();
    private final MutableLiveData<List<FitnessProgram>> filteredPrograms = new MutableLiveData<>();
    private final MutableLiveData<FitnessProgram> selectedProgram = new MutableLiveData<>();
    private final DatabaseReference reference;

    public FitnessViewModel() {
        reference = FirebaseDatabase.getInstance("https://fittrack-70436-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("fitness_programs");
        Log.d(TAG, "Firebase reference initialized: " + reference.toString());
        loadFitnessPrograms();
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

    private void loadFitnessPrograms() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FitnessProgram> programs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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
            filteredPrograms.setValue(new ArrayList<>());
            return;
        }

        List<FitnessProgram> filteredList = new ArrayList<>();
        for (FitnessProgram program : allPrograms) {
            boolean matchesWorkoutType = workoutType.isEmpty() || workoutType.equals("All") ||
                    program.getWorkoutType().toLowerCase().contains(workoutType.toLowerCase());
            boolean matchesFrequency = frequency.isEmpty() || frequency.equals("All") ||
                    program.getFrequency().equalsIgnoreCase(frequency);
            boolean matchesDifficulty = difficulty.isEmpty() || difficulty.equals("All") ||
                    program.getDifficulty().equalsIgnoreCase(difficulty);

            if (matchesWorkoutType && matchesFrequency && matchesDifficulty) {
                filteredList.add(program);
            }

            Log.d(TAG, "Program: " + program.getName()
                    + "\nType: " + program.getWorkoutType() + " (Matches: " + matchesWorkoutType + ")"
                    + "\nFrequency: " + program.getFrequency() + " (Matches: " + matchesFrequency + ")"
                    + "\nDifficulty: " + program.getDifficulty() + " (Matches: " + matchesDifficulty + ")");
        }

        Log.d(TAG, "Filtered programs count: " + filteredList.size());
        filteredPrograms.setValue(filteredList);
    }

}
