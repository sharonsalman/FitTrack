// GoalsFragment.java
package com.sharonsalman.fittrack.mainscreen;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sharonsalman.fittrack.R;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment {

    private EditText weightInput;
    private Button saveWeightButton;
    private TextView currentWeightTextView;
    private TextView goalWeightTextView;
    private LineChart weightChart;
    private RecyclerView weightEntriesRecyclerView;
    private WeightEntryAdapter weightEntryAdapter;
    private List<WeightMeasurement> weightMeasurements = new ArrayList<>();
    private List<Entry> chartEntries = new ArrayList<>();
    private DatabaseReference weightRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        weightInput = view.findViewById(R.id.weightInput);
        saveWeightButton = view.findViewById(R.id.saveWeightButton);
        currentWeightTextView = view.findViewById(R.id.currentWeightTextView);
        goalWeightTextView = view.findViewById(R.id.goalWeightTextView);
        weightChart = view.findViewById(R.id.weightChart);
        weightEntriesRecyclerView = view.findViewById(R.id.weightEntriesRecyclerView);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Initialize Firebase reference
        String userId = currentUser.getUid();
        weightRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Initialize RecyclerView
        weightEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weightEntryAdapter = new WeightEntryAdapter(weightMeasurements);
        weightEntriesRecyclerView.setAdapter(weightEntryAdapter);

        // Load weight data and measurements
        loadWeightData();
        loadMeasurements();

        saveWeightButton.setOnClickListener(v -> {
            String weightStr = weightInput.getText().toString();
            if (!weightStr.isEmpty()) {
                try {
                    float weight = Float.parseFloat(weightStr);
                    saveWeightMeasurement(weight);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid weight format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter a weight", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadWeightData() {
        weightRef.child("currentWeight").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Float currentWeight = dataSnapshot.getValue(Float.class);
                    if (currentWeight != null) {
                        currentWeightTextView.setText("Current Weight: " + currentWeight + " kg");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GoalsFragment", "Error fetching current weight", databaseError.toException());
            }
        });

        weightRef.child("targetWeight").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Float targetWeight = dataSnapshot.getValue(Float.class);
                    if (targetWeight != null) {
                        goalWeightTextView.setText("Goal Weight: " + targetWeight + " kg");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GoalsFragment", "Error fetching goal weight", databaseError.toException());
            }
        });
    }


    private void saveWeightMeasurement(float weight) {
        long currentTime = System.currentTimeMillis();

        // Create a new weight measurement object
        WeightMeasurement newMeasurement = new WeightMeasurement(weight, currentTime);

        // Save the weight measurement to Firebase under weightMeasurements
        weightRef.child("weightMeasurements").push().setValue(newMeasurement)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("GoalsFragment", "Weight measurement logged successfully");

                        // Update current weight
                        weightRef.child("currentWeight").setValue(weight)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Log.d("GoalsFragment", "Current weight updated successfully");

                                        // Immediately update the TextView and chart without reloading from Firebase
                                        currentWeightTextView.setText("Current Weight: " + weight + " kg");

                                        // Retrieve existing chart data and add the new entry
                                        weightRef.child("weightMeasurements").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                weightMeasurements.clear();
                                                chartEntries.clear();
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    WeightMeasurement measurement = snapshot.getValue(WeightMeasurement.class);
                                                    if (measurement != null) {
                                                        weightMeasurements.add(measurement);
                                                        chartEntries.add(new Entry(measurement.getDate() / 1000f, measurement.getWeight()));
                                                    }
                                                }
                                                // Add the latest weight entry
                                                chartEntries.add(new Entry(currentTime / 1000f, weight));
                                                updateChart();
                                                weightEntryAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("GoalsFragment", "Error fetching weight measurements", databaseError.toException());
                                            }
                                        });
                                    } else {
                                        Log.e("GoalsFragment", "Error updating current weight", updateTask.getException());
                                    }
                                });
                    } else {
                        Log.e("GoalsFragment", "Error logging weight measurement", task.getException());
                    }
                });
    }


    private void updateChart() {
        if (chartEntries.isEmpty()) {
            // Handle empty chart case
            return;
        }

        LineDataSet dataSet = new LineDataSet(chartEntries, "Weight Measurements");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawFilled(true); // Fill under the line
        dataSet.setFillColor(Color.BLUE); // Fill color

        LineData lineData = new LineData(dataSet);
        weightChart.setData(lineData);
        weightChart.setDrawGridBackground(false); // Optional: Hide grid background
        weightChart.setDrawBorders(true); // Optional: Show chart border
        weightChart.getDescription().setEnabled(false); // Hide description
        weightChart.getXAxis().setLabelRotationAngle(-45); // Rotate X axis labels
        weightChart.animateXY(1000, 1000); // Add animation
        weightChart.invalidate(); // Refresh the chart
    }


    private void loadMeasurements() {
        weightRef.child("weightMeasurements").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weightMeasurements.clear();
                chartEntries.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WeightMeasurement measurement = snapshot.getValue(WeightMeasurement.class);
                    if (measurement != null) {
                        weightMeasurements.add(measurement);
                        chartEntries.add(new Entry(measurement.getDate() / 1000f, measurement.getWeight()));
                    }
                }
                updateChart();
                weightEntryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GoalsFragment", "Error fetching weight measurements", databaseError.toException());
            }
        });
    }
}
