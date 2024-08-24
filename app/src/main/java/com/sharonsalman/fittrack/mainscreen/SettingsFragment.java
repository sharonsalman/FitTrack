package com.sharonsalman.fittrack.mainscreen;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharonsalman.fittrack.R;
import com.sharonsalman.fittrack.UserViewModel;
import com.sharonsalman.fittrack.User;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private UserViewModel userViewModel;
    private EditText targetWeightEdit;
    private Spinner fitnessLevelSpinner, goalSpinner, workoutFrequencySpinner, workoutLocationSpinner;
    private Button saveButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating view for SettingsFragment");
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        fitnessLevelSpinner = view.findViewById(R.id.fitness_level_spinner);
        goalSpinner = view.findViewById(R.id.goal_spinner);
        targetWeightEdit = view.findViewById(R.id.target_weight_edit);
        workoutFrequencySpinner = view.findViewById(R.id.workout_frequency_spinner);
        workoutLocationSpinner = view.findViewById(R.id.workout_location_spinner);
        saveButton = view.findViewById(R.id.save_button);

        // Setting up the adapters for the spinners
        setupSpinner(fitnessLevelSpinner, R.array.fitness_level);
        setupSpinner(goalSpinner, R.array.goal);
        setupSpinner(workoutFrequencySpinner, R.array.workout_frequency);
        setupSpinner(workoutLocationSpinner, R.array.workout_location);

        userViewModel.fetchUserData();
        loadUserData();

        saveButton.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        Log.d(TAG, "setupSpinner: Setting up spinner with array resource ID: " + arrayResId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void loadUserData() {
        Log.d(TAG, "loadUserData: Loading user data");
        userViewModel.getUserFitnessData().observe(getViewLifecycleOwner(), userFitnessData -> {
            Log.d(TAG, "loadUserData: Observer triggered");
            if (userFitnessData != null) {
                Log.d(TAG, "loadUserData: User fitness data loaded: " + userFitnessData.toString());
                setSpinnerSelection(fitnessLevelSpinner, R.array.fitness_level, userFitnessData.getFitnessLevel());
                setSpinnerSelection(goalSpinner, R.array.goal, userFitnessData.getGoal());
                targetWeightEdit.setText(String.valueOf(userFitnessData.getTargetWeight()));
                setSpinnerSelection(workoutFrequencySpinner, R.array.workout_frequency, userFitnessData.getWorkoutFrequency());
                setSpinnerSelection(workoutLocationSpinner, R.array.workout_location, userFitnessData.getWorkoutLocation());
            } else {
                Log.e(TAG, "loadUserData: UserFitnessData object is null");
            }
        });
    }

    private void saveUserData() {
        Log.d(TAG, "saveUserData: Saving user data");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            Log.d(TAG, "Authenticated User ID: " + userId);

            // Reference to the user data in Firebase
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Fetch existing data
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User currentUserFitnessData = dataSnapshot.getValue(User.class);

                    if (currentUserFitnessData != null) {
                        Log.d(TAG, "Current user fitness data: " + currentUserFitnessData);

                        // Get updated values
                        String fitnessLevel = fitnessLevelSpinner.getSelectedItem().toString();
                        String goal = goalSpinner.getSelectedItem().toString();
                        String workoutFrequency = workoutFrequencySpinner.getSelectedItem().toString();
                        String workoutLocation = workoutLocationSpinner.getSelectedItem().toString();
                        float targetWeight = currentUserFitnessData.getTargetWeight(); // Default to existing value

                        String targetWeightInput = targetWeightEdit.getText().toString().trim();
                        if (!targetWeightInput.isEmpty()) {
                            try {
                                targetWeight = Float.parseFloat(targetWeightInput);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Invalid target weight format", e);
                            }
                        }

                        // Create a map for the updated values
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("fitnessLevel", fitnessLevel);
                        updates.put("goals", goal);
                        updates.put("workoutFrequency", workoutFrequency);
                        updates.put("workoutLocation", workoutLocation);
                        updates.put("targetWeight", targetWeight);

                        // Update only the specified fields
                        userRef.updateChildren(updates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User data updated successfully");
                            } else {
                                Log.e(TAG, "Error updating user data", task.getException());
                            }
                        });
                    } else {
                        Log.e(TAG, "UserFitnessData object is null");
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

    private void setSpinnerSelection(Spinner spinner, int arrayResourceId, String value) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (value != null) {
            int spinnerPosition = adapter.getPosition(value);
            spinner.setSelection(spinnerPosition);
        }
    }
}
