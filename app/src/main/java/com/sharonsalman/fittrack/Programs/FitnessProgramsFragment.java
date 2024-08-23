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
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_fitness_programs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FitnessProgramAdapter();
        adapter.setOnProgramSelectedListener(this);
        recyclerView.setAdapter(adapter);

        workoutTypeSpinner = view.findViewById(R.id.spinner_workout_type);
        frequencySpinner = view.findViewById(R.id.spinner_frequency);
        difficultySpinner = view.findViewById(R.id.spinner_difficulty);

        setupSpinners();

        fitnessViewModel = new ViewModelProvider(this).get(FitnessViewModel.class);
        fitnessViewModel.getFitnessPrograms().observe(getViewLifecycleOwner(), fitnessPrograms -> {
            if (fitnessPrograms != null) {
                adapter.setFitnessPrograms(fitnessPrograms);
            }
        });

        workoutTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }
    @Override
    public void onProgramSelected(FitnessProgram program) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("program", program);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fitnessProgramsFragment_to_programDetailsFragment, bundle);
    }



    private void setupSpinners() {
        // Populate workout type spinner with existing values
        ArrayAdapter<CharSequence> workoutTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.workout_location, android.R.layout.simple_spinner_item);
        workoutTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutTypeSpinner.setAdapter(workoutTypeAdapter);

        // Populate frequency spinner with existing values
        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.workout_frequency, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencyAdapter);

        // Populate difficulty spinner with existing values
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.fitness_level, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
    }

    private void applyFilters() {
        String workoutType = workoutTypeSpinner.getSelectedItem().toString();
        String frequency = frequencySpinner.getSelectedItem().toString();
        String difficulty = difficultySpinner.getSelectedItem().toString();

        Log.d("FitnessProgramsFragment", "Applying filters: workoutType=" + workoutType + ", frequency=" + frequency + ", difficulty=" + difficulty);

        fitnessViewModel.filterPrograms(workoutType, frequency, difficulty);
    }

}
