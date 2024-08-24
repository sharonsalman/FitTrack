package com.sharonsalman.fittrack.mainscreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sharonsalman.fittrack.Location.LocationData;
import com.sharonsalman.fittrack.R;

public class TrackFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap mMap;
    private DatabaseReference userLocationRef;
    private Location lastLocation;
    private long startTime;
    private boolean isTracking = false;
    private double userWeight = 70.0;

    private TextView tvDuration, tvPace, tvCalories;
    private Spinner spinnerExerciseType;
    private Button btnStartStop;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);

        // Initialize Firebase references
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userLocationRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("locationTracks");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize UI components
        tvDuration = view.findViewById(R.id.tv_duration);
        tvPace = view.findViewById(R.id.tv_pace);
        tvCalories = view.findViewById(R.id.tv_calories);
        spinnerExerciseType = view.findViewById(R.id.spinner_exercise_type);
        btnStartStop = view.findViewById(R.id.btn_start_stop);

        // Set up exercise type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.exercise_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExerciseType.setAdapter(adapter);

        // Set up start/stop button click listener
        btnStartStop.setOnClickListener(v -> {
            if (isTracking) {
                stopTracking();
                btnStartStop.setText("Start");
            } else {
                checkLocationPermission();
            }
            isTracking = !isTracking;
        });

        // Get user weight from Firebase
        DatabaseReference weightRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("currentWeight");
        weightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userWeight = snapshot.getValue(Double.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TrackFragment", "Failed to get user weight", error.toException());
            }
        });

        return view;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission granted, start tracking
            startTracking();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start tracking
                startTracking();
            } else {
                Toast.makeText(getContext(), "Location permission is required to track your activities", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTracking() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("TrackFragment", "Location: " + location.getLatitude() + ", " + location.getLongitude());
                    saveLocation(location);

                    // Update the map with new location
                    if (mMap != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        addMarker(latLng);
                    }

                    // Update UI
                    updateUI(location);
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e("TrackFragment", "Permission not granted for location updates", e);
            Toast.makeText(getContext(), "Location permission is required to track your activities", Toast.LENGTH_SHORT).show();
        }

        startTime = SystemClock.elapsedRealtime(); // Start the timer
        btnStartStop.setText("Finish"); // Change button text to Finish
    }

    private void stopTracking() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        btnStartStop.setText("Start"); // Change button text to Start
    }

    private void saveLocation(Location location) {
        long currentTime = SystemClock.elapsedRealtime();
        long duration = currentTime - startTime;

        double pace = 0;
        if (lastLocation != null) {
            float distance = location.distanceTo(lastLocation); // Distance in meters
            long timeElapsed = location.getTime() - lastLocation.getTime(); // Time in milliseconds

            if (timeElapsed > 0) {
                pace = (distance / 1000) / (timeElapsed / 3600000.0); // Convert pace to km/h
            }
        }

        LocationData locationData = new LocationData(
                location.getLatitude(),
                location.getLongitude(),
                location.getTime(),
                duration,
                pace
        );

        // Save to Firebase
        userLocationRef.push().setValue(locationData);

        lastLocation = location; // Update lastLocation for the next calculation
    }

    private void updateUI(Location location) {
        long currentTime = SystemClock.elapsedRealtime();
        long duration = currentTime - startTime;
        double pace = 0;
        if (lastLocation != null) {
            float distance = location.distanceTo(lastLocation); // Distance in meters
            long timeElapsed = location.getTime() - lastLocation.getTime(); // Time in milliseconds

            if (timeElapsed > 0) {
                pace = (distance / 1000) / (timeElapsed / 3600000.0); // Convert pace to km/h
            }
        }
        double calories = calculateCalories(duration, pace);

        tvDuration.setText("Duration: " + formatDuration(duration));
        tvPace.setText("Pace: " + String.format("%.1f", pace) + " km/h");
        tvCalories.setText("Calories: " + String.format("%.1f", calories) + " kcal");
    }

    private String formatDuration(long durationMillis) {
        int hours = (int) (durationMillis / 3600000);
        int minutes = (int) ((durationMillis % 3600000) / 60000);
        int seconds = (int) ((durationMillis % 60000) / 1000);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private double calculateCalories(long duration, double pace) {
        double met = 8.0; // MET value for running
        return (met * userWeight * (duration / 3600000.0)); // Calories burned
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("TrackFragment", "onMapReady called");

        mMap = googleMap;

        if (mMap == null) {
            Log.e("TrackFragment", "GoogleMap instance is null");
            return;
        }

        LatLng testLocation = new LatLng(-34, 151); // Sydney, Australia
        mMap.addMarker(new MarkerOptions().position(testLocation).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testLocation, 10));

        Log.d("TrackFragment", "Added marker at: " + testLocation.latitude + ", " + testLocation.longitude);
        Log.d("TrackFragment", "Camera moved to: " + testLocation.latitude + ", " + testLocation.longitude);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("TrackFragment", "Location permission granted");
            mMap.setMyLocationEnabled(true);
        } else {
            Log.d("TrackFragment", "Location permission not granted");
            // Request permission if not granted
            checkLocationPermission();
        }
    }

    private void addMarker(LatLng latLng) {
        mMap.clear(); // Clear previous markers
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
}
