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

import com.sharonsalman.fittrack.databinding.FragmentGoalsBinding;

public class GoalFragment extends Fragment {
    private FragmentGoalsBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGoalsBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up the Spinner for goals
        Spinner goalsSpinner = binding.goalsSpinner;
        ArrayAdapter<CharSequence> goalsAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.goal, android.R.layout.simple_spinner_item);
        goalsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalsSpinner.setAdapter(goalsAdapter);

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String selectedGoal = (String) goalsSpinner.getSelectedItem();
            sharedViewModel.setGoals(selectedGoal);
            Navigation.findNavController(v).navigate(R.id.action_goalsFragment_to_currentweightFragment);
        });
        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_goalsFragment_to_workoutlocationFragment);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up the binding
    }
}
