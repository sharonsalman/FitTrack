package com.sharonsalman.fittrack.registerFragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.sharonsalman.fittrack.R;
import com.sharonsalman.fittrack.SharedViewModel;
import com.sharonsalman.fittrack.databinding.FragmentEmailBinding;

public class email_fragment extends Fragment {
    private FragmentEmailBinding binding;
    private SharedViewModel sharedViewModel;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmailBinding.inflate(inflater, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        binding.nextButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            Log.d("Register", "Email entered: " + email);

            if (isValidEmail(email) && isValidPassword(password)) {
                sharedViewModel.setEmail(email);
                sharedViewModel.setPassword(password);
                checkExistingEmail(email);
            }
        });

        return binding.getRoot();
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            binding.emailEditText.setError("Email cannot be empty");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Please enter a valid email address");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            binding.passwordEditText.setError("Password cannot be empty");
            return false;
        }
        if (password.length() < 8) {
            binding.passwordEditText.setError("Password must be at least 8 characters long");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            binding.passwordEditText.setError("Password must contain at least one uppercase letter");
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            binding.passwordEditText.setError("Password must contain at least one lowercase letter");
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            binding.passwordEditText.setError("Password must contain at least one digit");
            return false;
        }
        return true;
    }

    private void checkExistingEmail(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SignInMethodQueryResult result = task.getResult();
                if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                    // Email exists
                    binding.emailEditText.setError("This email is already in use");
                } else {
                    // Email doesn't exist, proceed with account creation
                    createAccount(email, binding.passwordEditText.getText().toString().trim());
                }
            } else {
                // Handle the error
                Toast.makeText(getContext(), "Error checking email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount(String email, String password) {
        sharedViewModel.setEmail(email);
        sharedViewModel.setPassword(password);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_emailFragment_to_nameFragment);
    }

    private void showError(String errorMessage) {
        Toast.makeText(getContext(), "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}
