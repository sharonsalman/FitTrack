package com.sharonsalman.fittrack.registerFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.sharonsalman.fittrack.R;
import com.sharonsalman.fittrack.SharedViewModel;
import com.sharonsalman.fittrack.databinding.FragmentTargetWeightBinding;

public class target_weight_fragment extends Fragment {
    private FragmentTargetWeightBinding binding;
    private SharedViewModel sharedViewModel;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTargetWeightBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        // Initialize the EditText for target weight
        EditText targetWeightEditText = binding.currentWeightEditText; // Ensure the ID is correct

        // Handle button click
        binding.nextButton.setOnClickListener(v -> {
            String targetWeightString = targetWeightEditText.getText().toString();
            if (!targetWeightString.isEmpty()) {
                try {
                    float targetWeight = Float.parseFloat(targetWeightString);
                    sharedViewModel.setTargetWeight(targetWeight);

                    String email = sharedViewModel.getEmail().getValue();
                    String password = sharedViewModel.getPassword().getValue();
                    Log.d("Registration", "Retrieved email from SharedViewModel: " + email);

                    if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                        Toast.makeText(requireContext(), "Email or password is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d("Registration", "Registering with email: " + email);

                    // Proceed directly to registration
                    registerUser(email, password);
                } catch (NumberFormatException e) {
                    targetWeightEditText.setError("Invalid weight format");
                }
            } else {
                targetWeightEditText.setError("Weight cannot be empty");
            }
        });

        binding.prevButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_targetweightFragment_to_currentweightFragment);
        });

        return binding.getRoot();
    }

    private String sanitizeEmail(String email) {
        return email.replace(".", ",");
    }

    private void registerUser(String email, String password) {
        String sanitizedEmail = sanitizeEmail(email);
        Log.d("Registration", "Starting user registration");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Registration", "Firebase authentication successful");
                        sharedViewModel.saveDataToFirebase(new SharedViewModel.OnSaveCompleteListener() {
                            @Override
                            public void onSaveComplete(boolean saveSuccess, String saveMessage) {
                                Log.d("Registration", "Save data complete. Success: " + saveSuccess + ", Message: " + saveMessage);
                                requireActivity().runOnUiThread(() -> {
                                    if (saveSuccess) {
                                        Log.d("Registration", "Attempting to show success toast and navigate");
                                        Toast.makeText(requireContext(), "Registration complete!", Toast.LENGTH_SHORT).show();
                                        try {
                                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_targetweightFragment_to_main_screen_fragment);
                                            Log.d("Registration", "Navigation action called");
                                        } catch (Exception e) {
                                            Log.e("Registration", "Navigation failed", e);
                                        }
                                    } else {
                                        Log.e("Registration", "Failed to save data: " + saveMessage);
                                        Toast.makeText(requireContext(), "Error saving data: " + saveMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    } else {
                        Log.e("Registration", "Firebase authentication failed", task.getException());
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
