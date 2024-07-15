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
import com.sharonsalman.fittrack.databinding.FragmentAgeBinding;

public class AgeFragment extends Fragment {
    private FragmentAgeBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAgeBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Spinner ageSpinner = binding.ageSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.age_ranges, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);

        binding.nextButton.setOnClickListener(v -> {
            String selectedAgeRange = (String) ageSpinner.getSelectedItem();
            sharedViewModel.setAge(Integer.parseInt(selectedAgeRange.split("-")[0]));
            Navigation.findNavController(v).navigate(R.id.action_ageFragment_to_workoutFrequencyFragment);
        });
        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_ageFragment_to_nameFragment);
        });

        return binding.getRoot();
    }
}
