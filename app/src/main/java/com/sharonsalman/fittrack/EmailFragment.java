package com.sharonsalman.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.sharonsalman.fittrack.databinding.FragmentEmailBinding;

public class EmailFragment extends Fragment {
    private FragmentEmailBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmailBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        binding.setSharedViewModel(sharedViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.nextButton.setOnClickListener(v -> {
            sharedViewModel.setEmail(binding.emailEditText.getText().toString());
            Navigation.findNavController(v).navigate(R.id.action_emailFragment_to_nameFragment);
        });

        return binding.getRoot();
    }
}
