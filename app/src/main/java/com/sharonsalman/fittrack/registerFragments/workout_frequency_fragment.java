package com.sharonsalman.fittrack.registerFragments;

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

import com.sharonsalman.fittrack.R;
import com.sharonsalman.fittrack.SharedViewModel;
import com.sharonsalman.fittrack.databinding.FragmentWorkoutFrequencyBinding;

public class workout_frequency_fragment extends Fragment {
    private FragmentWorkoutFrequencyBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutFrequencyBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Spinner workoutFrequencySpinner = binding.workoutfrequencySpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.workout_frequency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutFrequencySpinner.setAdapter(adapter);

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String selectedFrequency = (String) workoutFrequencySpinner.getSelectedItem();
            int frequencyValue = parseFrequency(selectedFrequency);
            sharedViewModel.setWorkoutFrequency(frequencyValue);
            Navigation.findNavController(v).navigate(R.id.action_workoutFrequencyFragment_to_fitnessLevelFragment);
        });

        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_workoutFrequencyFragment_to_ageFragment);
        });

        return binding.getRoot();

    }
    private int parseFrequency(String frequency) {
        if (frequency.contains("1-2 times a week")) return 2;
        if (frequency.contains("2-3 times a week")) return 3;
        if (frequency.contains("4-5 times a week")) return 4;
        return 0; // Default value if none of the cases match
    }

}
