package com.sharonsalman.fittrack.Programs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FitnessProgram implements Parcelable {
    private String id;
    private String name;
    private String description;
    private List<String> exerciseNames = new ArrayList<>();
    private List<String> exerciseCategories = new ArrayList<>();
    private List<String> exerciseHowToPerform = new ArrayList<>();
    private List<Integer> exerciseSets = new ArrayList<>();
    private List<Integer> exerciseReps = new ArrayList<>();
    private String workoutType;
    private String frequency;
    private String difficulty;
    private String imageUrl;


    public FitnessProgram() {
        exerciseSets = new ArrayList<>();
        exerciseReps = new ArrayList<>();
    }

    public FitnessProgram(String id, String name, String description, List<String> exerciseNames, List<String> exerciseCategories, List<String> exerciseHowToPerform, List<Integer> exerciseSets, List<Integer> exerciseReps, String workoutType, String frequency, String difficulty, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.exerciseNames = exerciseNames != null ? exerciseNames : new ArrayList<>();
        this.exerciseCategories = exerciseCategories != null ? exerciseCategories : new ArrayList<>();
        this.exerciseHowToPerform = exerciseHowToPerform != null ? exerciseHowToPerform : new ArrayList<>();
        this.exerciseSets = exerciseSets != null ? exerciseSets : new ArrayList<>();
        this.exerciseReps = exerciseReps != null ? exerciseReps : new ArrayList<>();
        this.workoutType = workoutType;
        this.frequency = frequency;
        this.difficulty = difficulty;
        this.imageUrl = imageUrl;
    }

    public FitnessProgram(String name, String frequency, String description) {
        this.name = name;
        this.frequency = frequency;
        this.description = description;
    }

    public FitnessProgram(String name, String frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getExerciseNames() { return exerciseNames; }
    public void setExerciseNames(List<String> exerciseNames) { this.exerciseNames = exerciseNames; }

    public List<String> getExerciseCategories() { return exerciseCategories; }
    public void setExerciseCategories(List<String> exerciseCategories) { this.exerciseCategories = exerciseCategories; }

    public List<String> getExerciseHowToPerform() { return exerciseHowToPerform; }
    public void setExerciseHowToPerform(List<String> exerciseHowToPerform) { this.exerciseHowToPerform = exerciseHowToPerform; }

    public List<Integer> getExerciseSets() { return exerciseSets; }
    public void setExerciseSets(List<Integer> exerciseSets) { this.exerciseSets = exerciseSets; }

    public List<Integer> getExerciseReps() { return exerciseReps; }
    public void setExerciseReps(List<Integer> exerciseReps) { this.exerciseReps = exerciseReps; }

    public String getWorkoutType() { return workoutType; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    protected FitnessProgram(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        exerciseNames = in.createStringArrayList();
        exerciseCategories = in.createStringArrayList();
        exerciseHowToPerform = in.createStringArrayList();
        exerciseSets = new ArrayList<>();
        in.readList(exerciseSets, Integer.class.getClassLoader());
        exerciseReps = new ArrayList<>();
        in.readList(exerciseReps, Integer.class.getClassLoader());
        workoutType = in.readString();
        frequency = in.readString();
        difficulty = in.readString();
        imageUrl = in.readString();
    }

    public static final Parcelable.Creator<FitnessProgram> CREATOR = new Parcelable.Creator<FitnessProgram>() {
        @Override
        public FitnessProgram createFromParcel(Parcel in) {
            return new FitnessProgram(in);
        }

        @Override
        public FitnessProgram[] newArray(int size) {
            return new FitnessProgram[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeStringList(exerciseNames);
        dest.writeStringList(exerciseCategories);
        dest.writeStringList(exerciseHowToPerform);
        dest.writeList(exerciseSets);
        dest.writeList(exerciseReps);
        dest.writeString(workoutType);
        dest.writeString(frequency);
        dest.writeString(difficulty);
        dest.writeString(imageUrl);
    }
}