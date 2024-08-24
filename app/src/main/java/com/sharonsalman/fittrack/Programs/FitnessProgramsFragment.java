package com.sharonsalman.fittrack.Programs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sharonsalman.fittrack.R;
import com.sharonsalman.fittrack.User;

public class FitnessProgramsFragment extends Fragment implements FitnessProgramAdapter.OnProgramSelectedListener {

    private FitnessViewModel fitnessViewModel;
    private RecyclerView recyclerView;
    private FitnessProgramAdapter adapter;
    private Spinner workoutTypeSpinner;
    private Spinner frequencySpinner;
    private Spinner difficultySpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_programs, container, false);

        fitnessViewModel = new ViewModelProvider(this).get(FitnessViewModel.class);
        recyclerView = view.findViewById(R.id.recycler_view_fitness_programs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FitnessProgramAdapter();
        adapter.setOnProgramSelectedListener(this);
        recyclerView.setAdapter(adapter);

        workoutTypeSpinner = view.findViewById(R.id.spinner_workout_type);
        frequencySpinner = view.findViewById(R.id.spinner_frequency);
        difficultySpinner = view.findViewById(R.id.spinner_difficulty);

        setupSpinners();
        fetchAndApplyUserData();

        fitnessViewModel.getFitnessPrograms().observe(getViewLifecycleOwner(), fitnessPrograms -> {
            if (fitnessPrograms != null) {
                adapter.setFitnessPrograms(fitnessPrograms);
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSpinners();

        workoutTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("FitnessProgramsFragment", "Selected Workout Type: " + parent.getItemAtPosition(position).toString());
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("FitnessProgramsFragment", "Selected Frequency: " + parent.getItemAtPosition(position).toString());
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("FitnessProgramsFragment", "Selected Difficulty: " + parent.getItemAtPosition(position).toString());
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    @Override
    public void onProgramSelected(FitnessProgram program) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("program", program);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fitnessProgramsFragment_to_programDetailsFragment, bundle);
    }

    private void fetchAndApplyUserData() {
        fitnessViewModel.fetchUserData();
        fitnessViewModel.getUserData().observe(getViewLifecycleOwner(), this::applyUserData);
    }
    private void applyUserData(User user) {
        if (user != null) {
            setSpinnerSelection(workoutTypeSpinner, R.array.workout_location, user.getWorkoutLocation());
            setSpinnerSelection(frequencySpinner, R.array.workout_frequency, user.getWorkoutFrequency());
            setSpinnerSelection(difficultySpinner, R.array.fitness_level, user.getFitnessLevel());
            applyFilters();
        }
    }



    private String mapWorkoutLocation(String location) {
        switch (location.toLowerCase()) {
            case "home workout":
                return "Home";
            case "gym workout":
                return "Gym";
            default:
                return "All";
        }
    }

    private int mapWorkoutFrequency(String frequency) {
        switch (frequency.toLowerCase()) {
            case "1-2 times a week":
                return 2;
            case "2-3 times a week":
                return 3;
            case "4-5 times a week":
                return 4;
            default:
                return 0;
        }
    }



    private String mapFitnessLevel(String level) {
        switch (level.toLowerCase()) {
            case "beginner":
                return "Beginner";
            case "intermediate":
                return "Intermediate";
            case "advanced":
                return "Advanced";
            default:
                return "All";
        }
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }
    private void setSpinnerSelection(Spinner spinner, int arrayResId, String value) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }


    private void applyFilters() {
        String workoutType = workoutTypeSpinner.getSelectedItem().toString();
        String frequency = frequencySpinner.getSelectedItem().toString();
        String difficulty = difficultySpinner.getSelectedItem().toString();

        Log.d("FitnessProgramsFragment", "Applying filters - Workout Type: " + workoutType + ", Frequency: " + frequency + ", Difficulty: " + difficulty);

        fitnessViewModel.filterPrograms(workoutType, frequency, difficulty);
    }


    private void setupSpinners() {
        ArrayAdapter<CharSequence> workoutTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.workout_location, android.R.layout.simple_spinner_item);
        workoutTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutTypeSpinner.setAdapter(workoutTypeAdapter);
        Log.d("FitnessProgramsFragment", "Workout Type: " + workoutTypeSpinner.getSelectedItem().toString());


        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.workout_frequency, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencyAdapter);
        Log.d("FitnessProgramsFragment", "Frequency: " + frequencySpinner.getSelectedItem().toString());

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.fitness_level, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
        Log.d("FitnessProgramsFragment", "Difficulty: " + difficultySpinner.getSelectedItem().toString());

    }

}
