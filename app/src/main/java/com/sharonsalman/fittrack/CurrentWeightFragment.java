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

import com.sharonsalman.fittrack.databinding.FragmentCurrentWeightBinding;

public class CurrentWeightFragment extends Fragment {
    private FragmentCurrentWeightBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCurrentWeightBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up the Spinner for current weight
        Spinner currentWeightSpinner = binding.currentWeightSpinner;
        ArrayAdapter<CharSequence> currentWeightAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.weight_options, android.R.layout.simple_spinner_item);
        currentWeightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentWeightSpinner.setAdapter(currentWeightAdapter);

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String selectedCurrentWeight = (String) currentWeightSpinner.getSelectedItem();
            sharedViewModel.setCurrentWeight(Float.parseFloat(selectedCurrentWeight));
            Navigation.findNavController(v).navigate(R.id.action_currentweightFragment_to_targetweightFragment);
        });

        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_currentweightFragment_to_goalFragment);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
