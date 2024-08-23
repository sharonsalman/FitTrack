package com.sharonsalman.fittrack.Programs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharonsalman.fittrack.R;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<String> exerciseNames;
    private List<String> exerciseCategories;
    private List<String> exerciseHowToPerform;
    private List<Integer> exerciseSets;
    private List<Integer> exerciseReps;

    public ExerciseAdapter(List<String> exerciseNames, List<String> exerciseCategories,
                           List<String> exerciseHowToPerform, List<Integer> exerciseSets,
                           List<Integer> exerciseReps) {
        this.exerciseNames = exerciseNames;
        this.exerciseCategories = exerciseCategories;
        this.exerciseHowToPerform = exerciseHowToPerform;
        this.exerciseSets = exerciseSets;
        this.exerciseReps = exerciseReps;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exerciseNames.get(position), exerciseCategories.get(position),
                exerciseHowToPerform.get(position), exerciseSets.get(position), exerciseReps.get(position));
    }

    @Override
    public int getItemCount() {
        return exerciseNames.size();
    }

    public void setExercises(List<String> names, List<String> categories, List<String> howToPerform, List<Integer> sets, List<Integer> reps) {
        this.exerciseNames = names;
        this.exerciseCategories = categories;
        this.exerciseHowToPerform = howToPerform;
        this.exerciseSets = sets;
        this.exerciseReps = reps;
        notifyDataSetChanged();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView category;
        private TextView howToPerform;
        private TextView sets;
        private TextView reps;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_exercise_name);
            category = itemView.findViewById(R.id.text_view_exercise_category);
            howToPerform = itemView.findViewById(R.id.text_view_exercise_how_to_perform);
            sets = itemView.findViewById(R.id.text_view_sets);
            reps = itemView.findViewById(R.id.text_view_reps);
        }

        public void bind(String name, String category, String howToPerform, int sets, int reps) {
            this.name.setText(name);
            this.category.setText(category);
            this.howToPerform.setText(howToPerform);
            this.sets.setText(String.valueOf(sets));
            this.reps.setText(String.valueOf(reps));
        }
    }
}
