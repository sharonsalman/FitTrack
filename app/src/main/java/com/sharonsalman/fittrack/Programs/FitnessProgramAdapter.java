package com.sharonsalman.fittrack.Programs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sharonsalman.fittrack.R;

import java.util.ArrayList;
import java.util.List;

public class FitnessProgramAdapter extends RecyclerView.Adapter<FitnessProgramAdapter.ViewHolder> {

    private static final String TAG = "FitnessProgramAdapter";
    private List<FitnessProgram> fitnessPrograms = new ArrayList<>();
    private List<FitnessProgram> fitnessProgramsFull = new ArrayList<>(); // list for filtering
    private Context context;
    private OnProgramSelectedListener listener;

    public interface OnProgramSelectedListener {
        void onProgramSelected(FitnessProgram program);
    }

    public void setOnProgramSelectedListener(OnProgramSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Creating new ViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_program, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Binding ViewHolder at position " + position);
        FitnessProgram program = fitnessPrograms.get(position);
        holder.nameTextView.setText(program.getName());
        holder.descriptionTextView.setText(program.getDescription());

        String imageUrl = program.getImageUrl();
        Log.d(TAG, "Loading image from URL: " + imageUrl);

        Glide.with(context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_delete)
                .into(holder.programImageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProgramSelected(program);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: Total items " + fitnessPrograms.size());
        return fitnessPrograms.size();
    }

    public void setFitnessPrograms(List<FitnessProgram> fitnessPrograms) {
        Log.d(TAG, "setFitnessPrograms: Updating list with " + fitnessPrograms.size() + " items");
        this.fitnessPrograms = fitnessPrograms;
        this.fitnessProgramsFull = new ArrayList<>(fitnessPrograms);
        notifyDataSetChanged();
    }

    public void filter(String query, String filterType) {
        List<FitnessProgram> filteredList = new ArrayList<>();

        for (FitnessProgram program : fitnessProgramsFull) {
            if (filterType.equals("type")) {
                if (program.getWorkoutType().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(program);
                }
            } else if (filterType.equals("difficulty")) {
                if (program.getDifficulty().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(program);
                }
            }
        }

        fitnessPrograms.clear();
        fitnessPrograms.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        ImageView programImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.program_name);
            descriptionTextView = itemView.findViewById(R.id.text_view_description);
            programImageView = itemView.findViewById(R.id.program_image);
            Log.d(TAG, "ViewHolder created");
        }
    }
}
