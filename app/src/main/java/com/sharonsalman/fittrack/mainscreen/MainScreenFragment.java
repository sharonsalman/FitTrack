package com.sharonsalman.fittrack.mainscreen;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sharonsalman.fittrack.Programs.FitnessProgram;
import com.sharonsalman.fittrack.Programs.FitnessViewModel;
import com.sharonsalman.fittrack.R;

import javax.sql.DataSource;

public class MainScreenFragment extends Fragment {

    private FitnessViewModel fitnessViewModel;
    private TextView programNameTextView;
    private ImageView programImageView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fitnessViewModel.loadSelectedProgram();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_screen_fragment, container, false);

        fitnessViewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);

        programNameTextView = view.findViewById(R.id.programNameTextView);
        programImageView = view.findViewById(R.id.programImageView);

        fitnessViewModel.getSelectedProgram().observe(getViewLifecycleOwner(), program -> {
            Log.d("MainScreenFragment", "Observed program: " + program);
            if (program != null) {
                programNameTextView.setText(program.getName());
                Glide.with(this)
                        .load(program.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("MainScreenFragment", "Error loading image", e);
                                return false; // Let Glide handle the error drawable
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }

                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false; // Let Glide handle setting the resource
                            }
                        })
                        .into(programImageView);
            } else {
                Log.d("MainScreenFragment", "Program is null");
                programNameTextView.setText(R.string.no_program_selected);
                programImageView.setImageResource(R.drawable.error);
            }
        });

// Create a custom OnNavigationItemSelectedListener
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.calendarFragment, R.id.fitnessProgramsFragment, R.id.trackFragment, R.id.goalsFragment)
                .build();

// Create a custom OnNavigationItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.calendarFragment) {
                FitnessProgram selectedProgram = fitnessViewModel.getSelectedProgram().getValue();
                if (selectedProgram != null) {
                    Bundle args = new Bundle();
                    args.putParcelable("selected_program", selectedProgram);
                    navController.navigate(R.id.calendarFragment, args);
                } else {
                    Log.d("MainScreenFragment", "No program selected for Calendar");
                    navController.navigate(R.id.calendarFragment);
                }
                return true;
            } else if (itemId == R.id.fitnessProgramsFragment
                    || itemId == R.id.trackFragment
                    || itemId == R.id.goalsFragment) {
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
            return false;
        });

        NavigationUI.setupActionBarWithNavController((AppCompatActivity) requireActivity(), navController, appBarConfiguration);

        return view;
    }
}
