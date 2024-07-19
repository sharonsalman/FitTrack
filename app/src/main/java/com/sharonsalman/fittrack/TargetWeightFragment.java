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

import com.sharonsalman.fittrack.databinding.FragmentTargetWeightBinding;

public class TargetWeightFragment extends Fragment {
    private FragmentTargetWeightBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTargetWeightBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        Spinner targetWeightSpinner = binding.targetWeightSpinner;
        ArrayAdapter<CharSequence> targetWeightAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.weight_options, android.R.layout.simple_spinner_item);
        targetWeightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetWeightSpinner.setAdapter(targetWeightAdapter);

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String selectedtargetWeight = (String) targetWeightSpinner.getSelectedItem();
            sharedViewModel.setCurrentWeight(Float.parseFloat(selectedtargetWeight));
            Navigation.findNavController(v).navigate(R.id.action_targetweightFragment_to_mainActivity);
        });

        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_targetweightFragment_to_currentweightFragment);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
