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
import com.sharonsalman.fittrack.databinding.FragmentFitnessLevelBinding;

public class FitnessLevelFragment extends Fragment {
    private FragmentFitnessLevelBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFitnessLevelBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up the Spinner
        Spinner ageSpinner = binding.fitnesslevelSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.fitness_level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String selectedAgeRange = (String) ageSpinner.getSelectedItem();
            sharedViewModel.setAge(Integer.parseInt(selectedAgeRange.split("-")[0])); // Example: Setting the lower bound as age
            Navigation.findNavController(v).navigate(R.id.action_fiFragment_to_workoutFrequencyFragment);
        });

        return binding.getRoot();
    }
}
