package com.sharonsalman.fittrack.Programs;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharonsalman.fittrack.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProgramDetailsFragment extends Fragment {

    private static final String TAG = "ProgramDetailsFragment";
    private RecyclerView recyclerViewExercises;
    private ExerciseAdapter exerciseAdapter;
    private List<String> exerciseNames = new ArrayList<>();
    private List<String> exerciseCategories = new ArrayList<>();
    private List<String> exerciseHowToPerform = new ArrayList<>();
    private List<Integer> exerciseSets = new ArrayList<>();
    private List<Integer> exerciseReps = new ArrayList<>();
    private FitnessProgram program; // Change from programId to FitnessProgram object

    // UI components
    private ImageView programImage;
    private TextView programName;
    private TextView programDescription;
    private TextView programWorkoutType;
    private TextView programFrequency;
    private TextView programDifficulty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_program_details, container, false);

        // Initialize UI components
        programImage = view.findViewById(R.id.program_image);
        programName = view.findViewById(R.id.program_name);
        programDescription = view.findViewById(R.id.text_view_description);
        programWorkoutType = view.findViewById(R.id.program_workout_type);
        programFrequency = view.findViewById(R.id.program_frequency);
        programDifficulty = view.findViewById(R.id.program_difficulty);

        // Initialize RecyclerView
        recyclerViewExercises = view.findViewById(R.id.exercise_recycler_view);
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter and set it to RecyclerView
        exerciseAdapter = new ExerciseAdapter(exerciseNames, exerciseCategories, exerciseHowToPerform, exerciseSets, exerciseReps);
        recyclerViewExercises.setAdapter(exerciseAdapter);

        // Retrieve program from arguments if available
        if (getArguments() != null) {
            program = getArguments().getParcelable("program");
            if (program != null) {
                Log.d(TAG, "Program retrieved from arguments: " + program.toString());
                populateUI(program);
            } else {
                Log.e(TAG, "Program object is null.");
            }
        }

        return view;
    }

    private void populateUI(FitnessProgram program) {
        // Update UI elements with program details
        programName.setText(program.getName() != null ? program.getName() : "No Name");
        programDescription.setText(program.getDescription() != null ? program.getDescription() : "No Description");
        programWorkoutType.setText(program.getWorkoutType() != null ? program.getWorkoutType() : "No Type");
        programFrequency.setText(program.getFrequency() != null ? program.getFrequency() : "No Frequency");
        programDifficulty.setText(program.getDifficulty() != null ? program.getDifficulty() : "No Difficulty");

        String imageUrl = program.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d(TAG, "Loading image from URL: " + imageUrl);
            Picasso.get().load(imageUrl).into(programImage);
        } else {
            Log.d(TAG, "No image URL provided, using placeholder.");
            programImage.setImageResource(R.drawable.placeholder); // Placeholder image
        }

        Log.d(TAG, "Program details populated successfully.");
        exerciseNames.clear();
        exerciseCategories.clear();
        exerciseHowToPerform.clear();
        exerciseSets.clear();
        exerciseReps.clear();

        exerciseNames.addAll(program.getExerciseNames());
        exerciseCategories.addAll(program.getExerciseCategories());
        exerciseHowToPerform.addAll(program.getExerciseHowToPerform());
        exerciseSets.addAll(program.getExerciseSets());
        exerciseReps.addAll(program.getExerciseReps());

        exerciseAdapter.notifyDataSetChanged();

        Log.d(TAG, "Populated exercises: " + exerciseNames.size());
    }

    private void fetchExercises() {
        if (program == null) {
            Log.e(TAG, "Program object is null");
            return;
        }

        Log.d(TAG, "Fetching exercises for program: " + program.getName());

        DatabaseReference programRef = FirebaseDatabase.getInstance()
                .getReference("fitness_programs")
                .child(program.getId());

        programRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange called. DataSnapshot exists: " + dataSnapshot.exists());
                Log.d(TAG, "DataSnapshot value: " + dataSnapshot.getValue());

                if (dataSnapshot.exists()) {
                    exerciseNames.clear();
                    exerciseCategories.clear();
                    exerciseHowToPerform.clear();
                    exerciseSets.clear();
                    exerciseReps.clear();

                    List<String> names = getStringList(dataSnapshot, "exerciseNames");
                    List<String> categories = getStringList(dataSnapshot, "exerciseCategories");
                    List<String> howToPerform = getStringList(dataSnapshot, "exerciseHowToPerform");
                    List<Integer> sets = getIntegerList(dataSnapshot, "exerciseSets");
                    List<Integer> reps = getIntegerList(dataSnapshot, "exerciseReps");

                    int size = Math.min(names.size(),
                            Math.min(categories.size(),
                                    Math.min(howToPerform.size(),
                                            Math.min(sets.size(), reps.size()))));

                    for (int i = 0; i < size; i++) {
                        exerciseNames.add(names.get(i));
                        exerciseCategories.add(categories.get(i));
                        exerciseHowToPerform.add(howToPerform.get(i));
                        exerciseSets.add(sets.get(i));
                        exerciseReps.add(reps.get(i));
                        Log.d(TAG, "Added exercise: " + names.get(i));
                    }

                    Log.d(TAG, "Fetched exercises: " + exerciseNames);
                    updateAdapter();
                } else {
                    Log.d(TAG, "No data found for the program.");
                }
            }

            private void updateAdapter() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        exerciseAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Adapter updated with " + exerciseNames.size() + " exercises");
                    });
                } else {
                    Log.e(TAG, "Cannot update adapter: Activity is null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching exercises", databaseError.toException());
            }
        });
    }

    private List<String> getStringList(DataSnapshot dataSnapshot, String key) {
        List<String> list = new ArrayList<>();
        DataSnapshot snapshot = dataSnapshot.child(key);
        if (snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                String value = child.getValue(String.class);
                if (value != null) list.add(value);
            }
        }
        return list;
    }

    private List<Integer> getIntegerList(DataSnapshot dataSnapshot, String key) {
        List<Integer> list = new ArrayList<>();
        DataSnapshot snapshot = dataSnapshot.child(key);
        if (snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                Long value = child.getValue(Long.class);
                if (value != null) list.add(value.intValue());
            }
        }
        return list;
    }
}
