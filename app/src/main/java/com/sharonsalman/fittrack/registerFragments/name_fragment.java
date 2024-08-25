package com.sharonsalman.fittrack.registerFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.sharonsalman.fittrack.R;
import com.sharonsalman.fittrack.SharedViewModel;
import com.sharonsalman.fittrack.databinding.FragmentNameBinding;

public class name_fragment extends Fragment {
    private FragmentNameBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNameBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setName("");

        binding.setSharedViewModel(sharedViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.nextButton.setOnClickListener(v -> {
            sharedViewModel.setName(binding.nameEditText.getText().toString());
            Navigation.findNavController(v).navigate(R.id.action_nameFragment_to_ageFragment);
        });
        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nameFragment_to_emailFragment);
        });

        return binding.getRoot();
    }
}
