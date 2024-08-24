package com.sharonsalman.fittrack.registerFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.sharonsalman.fittrack.R;
import com.sharonsalman.fittrack.SharedViewModel;
import com.sharonsalman.fittrack.databinding.FragmentCurrentWeightBinding;

public class current_weight_fragment extends Fragment {
    private FragmentCurrentWeightBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCurrentWeightBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Initialize the EditText for current weight
        EditText currentWeightEditText = binding.currentWeightEditText;

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String currentWeightString = currentWeightEditText.getText().toString();
            if (!currentWeightString.isEmpty()) {
                try {
                    float currentWeight = Float.parseFloat(currentWeightString);
                    sharedViewModel.setCurrentWeight(currentWeight);
                    Navigation.findNavController(v).navigate(R.id.action_currentweightFragment_to_targetweightFragment);
                } catch (NumberFormatException e) {
                    currentWeightEditText.setError("Invalid weight format");
                }
            } else {
                currentWeightEditText.setError("Weight cannot be empty");
            }
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
