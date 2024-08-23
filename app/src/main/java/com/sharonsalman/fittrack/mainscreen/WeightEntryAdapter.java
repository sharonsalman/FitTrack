// WeightEntryAdapter.java
package com.sharonsalman.fittrack.mainscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharonsalman.fittrack.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WeightEntryAdapter extends RecyclerView.Adapter<WeightEntryAdapter.WeightEntryViewHolder> {

    private final List<WeightMeasurement> weightMeasurements;

    public WeightEntryAdapter(List<WeightMeasurement> weightMeasurements) {
        this.weightMeasurements = weightMeasurements;
    }

    @NonNull
    @Override
    public WeightEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_entry_item, parent, false);
        return new WeightEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightEntryViewHolder holder, int position) {
        WeightMeasurement measurement = weightMeasurements.get(position);
        holder.weightEntryTextView.setText(
                String.format(Locale.getDefault(), "Weight: %.1f kg on %s",
                        measurement.getWeight(),
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(measurement.getDate()))
        );
    }

    @Override
    public int getItemCount() {
        return weightMeasurements.size();
    }

    static class WeightEntryViewHolder extends RecyclerView.ViewHolder {
        TextView weightEntryTextView;

        WeightEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            weightEntryTextView = itemView.findViewById(R.id.weightEntryTextView);
        }
    }
}
