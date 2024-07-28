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
import com.sharonsalman.fittrack.databinding.FragmentFitnessLevelBinding;

public class fitness_level_fragment extends Fragment {
    private FragmentFitnessLevelBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFitnessLevelBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up the Spinner
        Spinner fitnesslevelSpinner = binding.fitnesslevelSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.fitness_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnesslevelSpinner.setAdapter(adapter);

        // Handle button clicks
        binding.nextButton.setOnClickListener(v -> {
            String selectedFitnessLevel = (String) fitnesslevelSpinner.getSelectedItem();
            sharedViewModel.setFitnessLevel(selectedFitnessLevel);
            Navigation.findNavController(v).navigate(R.id.action_fitnessLevelFragment_to_workoutlocationFragment);

        });

        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_FitnessLevelFragment_to_workoutFrequencyFragment);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up the binding
    }
}
