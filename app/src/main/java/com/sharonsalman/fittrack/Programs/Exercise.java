package com.sharonsalman.fittrack.Programs;

public class Exercise {
    private String name;
    private String description;
    private String category;
    private String howToPerform;
    private int sets;
    private int reps;

    public Exercise() {} // Empty constructor for Firebase

    public Exercise(String name, String description, String category, String howToPerform, int sets, int reps) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.howToPerform = howToPerform;
        this.sets = sets;
        this.reps = reps;
    }

    public Exercise(String exerciseName) {
        this.name = exerciseName;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getHowToPerform() { return howToPerform; }
    public void setHowToPerform(String howToPerform) { this.howToPerform = howToPerform; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    @Override
    public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", howToPerform='" + howToPerform + '\'' +
                ", sets=" + sets +
                ", reps=" + reps +
                '}';
    }
}
