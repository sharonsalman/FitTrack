package com.sharonsalman.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.sharonsalman.fittrack.databinding.FragmentWorkoutLocationBinding;

public class WorkoutLocationFragment extends Fragment {
    private FragmentWorkoutLocationBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutLocationBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up the Spinner
        Spinner workoutlocationSpinner = binding.workoutlocationSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.workout_location, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutlocationSpinner.setAdapter(adapter);

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String selectedWorkoutLocation = (String) workoutlocationSpinner.getSelectedItem();
            sharedViewModel.setAge(Integer.parseInt(selectedWorkoutLocation.split("-")[0]));
            Navigation.findNavController(v).navigate(R.id.action_fitnessLevelFragment_to_workoutlocationFragment);
        });
        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_FitnessLevelFragment_to_workoutFrequencyFragment);
        });

        return binding.getRoot();
    }
}
