package com.sharonsalman.fittrack.Programs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProgramSeeder {
    private static final String TAG = "ProgramSeeder";
    private static final String PREFS_NAME = "FitTrackPrefs";
    private static final String KEY_SEEDING_DONE = "seedingDone";

    public static void seedProgramsIfNeeded(Context context) {
        Log.d(TAG, "seedProgramsIfNeeded called");
        checkAndSeedPrograms(context);
    }

    private static void checkAndSeedPrograms(Context context) {
        Log.d(TAG, "checkAndSeedPrograms called");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("fitness_programs");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange called, dataSnapshot exists: " + dataSnapshot.exists());
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "Programs do not exist, proceeding with seeding");
                    seedPrograms(context);
                } else {
                    Log.d(TAG, "Programs already exist, skipping seeding");
                    markSeedingDone(context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error checking for existing programs", databaseError.toException());
            }
        });

        // Add a timeout mechanism
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.e(TAG, "Firebase query timed out. Check your internet connection and Firebase setup.");
        }, 10000);
    }

    private static void seedPrograms(Context context) {
        Log.d(TAG, "seedPrograms called");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("fitness_programs");

        List<FitnessProgram> programs = getPrograms();
        Log.d(TAG, "Number of programs to seed: " + programs.size());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (FitnessProgram program : programs) {
            Log.d(TAG, "Seeding program: " + program.getName() + " with image URL: " + program.getImageUrl());
            reference.push().setValue(program)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Successfully added program: " + program.getName());
                        if (successCount.incrementAndGet() == programs.size()) {
                            Log.d(TAG, "All programs added successfully");
                            markSeedingDone(context);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to add program: " + program.getName(), e);
                        failureCount.incrementAndGet();
                        if (successCount.get() + failureCount.get() == programs.size()) {
                            Log.d(TAG, "Seeding completed with " + failureCount.get() + " failures");
                            if (failureCount.get() == 0) {
                                markSeedingDone(context);
                            }
                        }
                    });
        }
    }
    private static void markSeedingDone(Context context) {
        Log.d(TAG, "Marking seeding as done");
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_SEEDING_DONE, true).apply();
        Log.d(TAG, "Seeding marked as done");
    }
    private static List<FitnessProgram> getPrograms() {
        List<FitnessProgram> programs = new ArrayList<>();

        FitnessProgram program = new FitnessProgram(
                "1",
                "Home Workout for Beginners - 2 Times a Week",
                "A gentle introduction to fitness with exercises you can do at home.",
                Arrays.asList(
                        "Bodyweight squats",
                        "Wall push-ups",
                        "Knee push-ups",
                        "Seated leg raises",
                        "Standing calf raises"
                ),
                Arrays.asList(
                        "Stand with feet shoulder-width apart, send hips back and down, and stand back up.",
                        "Stand facing a wall, place hands on the wall at shoulder height, and perform a push-up motion.",
                        "Start in a high plank position with knees on the ground, lower chest to the ground, and push back up.",
                        "Sit on the edge of a chair or bench, keep legs straight, and raise them up and down.",
                        "Stand with feet shoulder-width apart, lift up on your toes, and lower back down."
                ),
                Arrays.asList("Strength", "Strength", "Strength", "Core", "Strength"),
                Arrays.asList(3, 2, 2, 2, 3),
                Arrays.asList(10, 12, 12, 12, 15),
                "Home workout",
                "1-2 times a week",
                "Beginner",
                "https://img.freepik.com/premium-photo/woman-doing-fitness-training-home-walking-high-knees_926199-2153476.jpg"
        );
        programs.add(new FitnessProgram(
                "2",
                "Gym Workout for Intermediate - 4 Times a Week",
                "Intermediate gym routine focusing on building strength with weight training.",
                Arrays.asList("Bench press", "Deadlifts", "Pull-ups", "Shoulder press", "Leg press"),
                Arrays.asList(
                        "Lie on a bench and press a barbell upward until arms are extended.",
                        "Lift a barbell from the ground to hip level while keeping your back straight.",
                        "Hang from a bar and pull your body up until your chin is above the bar.",
                        "Press a barbell or dumbbells overhead from shoulder height.",
                        "Push a weighted platform away from you using your legs."
                ),
                Arrays.asList("Strength", "Strength", "Strength", "Strength", "Strength"),
                Arrays.asList(4, 4, 3, 3, 4),
                Arrays.asList(8, 6, 10, 10, 10),
                "Gym workout",
                "4-5 times a week",
                "Intermediate",
                "https://cdn.shopify.com/s/files/1/0162/2116/files/people-2604149_1280_cd93e663-dd4a-47c5-b92c-189c1645b326.jpg?v=1576756194"
        ));

        programs.add(new FitnessProgram(
                "3",
                "Home HIIT for Advanced - 5 Times a Week",
                "High-Intensity Interval Training for advanced users with home-based exercises.",
                Arrays.asList("Burpees", "High knees", "Jump squats", "Mountain climbers", "Plank jacks"),
                Arrays.asList(
                        "Start in a standing position, drop to a squat, kick feet back, perform a push-up, return to squat, and jump up.",
                        "Run in place while bringing knees up to waist level.",
                        "Perform a squat and explode into a jump.",
                        "In a plank position, alternate bringing knees to chest quickly.",
                        "In a plank position, jump feet in and out while maintaining the plank."
                ),
                Arrays.asList("Cardio", "Cardio", "Strength", "Cardio", "Cardio"),
                Arrays.asList(4, 4, 4, 4, 4),
                Arrays.asList(12, 30, 15, 20, 20),
                "Home workout",
                "4-5 times a week",
                "Advanced",
                "https://images.contentstack.io/v3/assets/blt45c082eaf9747747/blta585249cb277b1c3/5fdcfa83a703d10ab87e827b/HIIT.jpg?width=1200&height=630&fit=crop"
        ));

        programs.add(new FitnessProgram(
                "4",
                "Gym Strength Training for Beginners - 2 Times a Week",
                "Strength training program designed for beginners focusing on gym equipment.",
                Arrays.asList("Lat pulldown", "Chest press", "Seated row", "Leg curls", "Dumbbell bicep curls"),
                Arrays.asList(
                        "Pull a bar down towards your chest while seated.",
                        "Press weights away from your chest while lying on a bench.",
                        "Pull weights towards you while seated.",
                        "Curl your legs up while lying on your stomach.",
                        "Curl dumbbells up towards your shoulders."
                ),
                Arrays.asList("Strength", "Strength", "Strength", "Strength", "Strength"),
                Arrays.asList(3, 3, 3, 3, 3),
                Arrays.asList(10, 10, 10, 10, 12),
                "Gym workout",
                "2-3 times a week",
                "Beginner",
                "https://cdn.pixabay.com/photo/2017/08/30/12/31/workout-2690756_960_720.jpg"
        ));

        programs.add(new FitnessProgram(
                "5",
                "Home Yoga for Relaxation - 3 Times a Week",
                "Yoga routine for relaxation and flexibility at home.",
                Arrays.asList("Downward dog", "Child's pose", "Warrior II", "Tree pose", "Seated forward bend"),
                Arrays.asList(
                        "Start on hands and knees, lift hips up and back while straightening legs.",
                        "Sit back on your heels, stretch arms forward, and relax.",
                        "Stand with one leg forward, bend that knee, and extend the other leg back, arms extended.",
                        "Stand on one leg, place the other foot on the inner thigh, and balance.",
                        "Sit with legs extended, bend forward and reach for feet."
                ),
                Arrays.asList("Flexibility", "Flexibility", "Strength", "Balance", "Flexibility"),
                Arrays.asList(3, 2, 2, 2, 2),
                Arrays.asList(30, 30, 30, 30, 30),
                "Home workout",
                "3-4 times a week",
                "Beginner",
                "https://www.verywellfit.com/thmb/izD9uqNOkcgMFXK-6HTbdU67-KY=/3000x2000/filters:fill(auto,1)/yoga-for-beginners-2f0bff5a601d4348b7b3942a469b0e93.jpg"
        ));

        programs.add(new FitnessProgram(
                "6",
                "Gym Cardio Blast - 4 Times a Week",
                "High-intensity cardio workouts for the gym to increase endurance.",
                Arrays.asList("Treadmill sprints", "Cycling", "Elliptical machine", "Rowing machine", "Jump rope"),
                Arrays.asList(
                        "Sprint on a treadmill at high speed for short intervals.",
                        "Cycle on a stationary bike with high resistance.",
                        "Use the elliptical machine at a fast pace.",
                        "Row using a rowing machine with high resistance.",
                        "Jump rope rapidly for a cardio workout."
                ),
                Arrays.asList("Cardio", "Cardio", "Cardio", "Cardio", "Cardio"),
                Arrays.asList(4, 4, 4, 4, 4),
                Arrays.asList(20, 20, 20, 20, 20),
                "Gym workout",
                "4-5 times a week",
                "Intermediate",
                "https://www.verywellfit.com/thmb/tqZmYZMLcftfYFZEBj7AG7uhZf8=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/CardioWorkout-5b8f5d8d46e0fb0037e3e774.jpg"
        ));

        programs.add(new FitnessProgram(
                "7",
                "Home Strength Training for Advanced - 5 Times a Week",
                "Advanced strength training program for home workouts.",
                Arrays.asList("Pistol squats", "Push-up variations", "Handstand push-ups", "Bulgarian split squats", "Tricep dips"),
                Arrays.asList(
                        "Perform a squat on one leg while extending the other leg forward.",
                        "Do various push-up variations, such as clapping push-ups or diamond push-ups.",
                        "Perform push-ups while in a handstand position.",
                        "Place one foot on a chair or bench, perform a squat with the other leg.",
                        "Dip your body down and up using a chair or bench."
                ),
                Arrays.asList("Strength", "Strength", "Strength", "Strength", "Strength"),
                Arrays.asList(5, 5, 3, 4, 4),
                Arrays.asList(10, 12, 8, 10, 12),
                "Home workout",
                "4-5 times a week",
                "Advanced",
                "https://www.wellandgood.com/wp-content/uploads/2022/02/what-is-an-advanced-workout-01.jpg"
        ));

        programs.add(new FitnessProgram(
                "8",
                "Gym Flexibility Training - 3 Times a Week",
                "Flexibility training program focusing on stretching exercises.",
                Arrays.asList("Hamstring stretches", "Quadriceps stretches", "Hip flexor stretches", "Calf stretches", "Back stretches"),
                Arrays.asList(
                        "Stretch the back of your thighs while seated or standing.",
                        "Stretch the front of your thighs while standing or lying on your side.",
                        "Stretch the front of your hips while in a lunge position.",
                        "Stretch the calves by leaning against a wall or using a step.",
                        "Stretch the back by reaching for your toes or performing a cat-cow stretch."
                ),
                Arrays.asList("Flexibility", "Flexibility", "Flexibility", "Flexibility", "Flexibility"),
                Arrays.asList(3, 3, 3, 3, 3),
                Arrays.asList(30, 30, 30, 30, 30),
                "Gym workout",
                "2-3 times a week",
                "Beginner",
                "https://cdn.shopify.com/s/files/1/0081/0471/articles/3_4_Restorative_Stretching_Exercises_3.jpg?v=1541615174"
        ));

        programs.add(new FitnessProgram(
                "9",
                "Home Cardio and Core - 4 Times a Week",
                "Combination of cardio and core exercises for home workouts.",
                Arrays.asList("Jumping jacks", "Bicycle crunches", "Mountain climbers", "Plank", "High knees"),
                Arrays.asList(
                        "Jump with feet wide and arms overhead, return to start.",
                        "Lie on your back, perform a crunch while bringing opposite elbow to knee.",
                        "In a plank position, alternate bringing knees to chest quickly.",
                        "Hold a plank position with your body in a straight line.",
                        "Run in place while bringing knees up to waist level."
                ),
                Arrays.asList("Cardio", "Core", "Cardio", "Core", "Cardio"),
                Arrays.asList(4, 4, 4, 4, 4),
                Arrays.asList(30, 20, 30, 60, 30),
                "Home workout",
                "3-4 times a week",
                "Intermediate",
                "https://cdn.pixabay.com/photo/2015/06/24/16/36/fitness-820317_960_720.jpg"
        ));

        programs.add(new FitnessProgram(
                "10",
                "Gym Bodyweight Training - 3 Times a Week",
                "Full-body workout using only bodyweight exercises in the gym.",
                Arrays.asList("Pull-ups", "Push-ups", "Dips", "Squats", "Lunges"),
                Arrays.asList(
                        "Hang from a bar and pull your body up until your chin is above the bar.",
                        "Perform push-ups with your body in a straight line from head to heels.",
                        "Lower and lift your body using parallel bars or a bench.",
                        "Perform squats with your bodyweight, keeping your back straight.",
                        "Step forward and lower your hips until both knees are bent at 90-degree angles."
                ),
                Arrays.asList("Strength", "Strength", "Strength", "Strength", "Strength"),
                Arrays.asList(3, 3, 3, 4, 4),
                Arrays.asList(10, 12, 10, 15, 15),
                "Gym workout",
                "3-4 times a week",
                "Intermediate",
                "https://www.muscleandfitness.com/wp-content/uploads/2018/09/MANUP3.jpg?quality=86&strip=all"
        ));

        return programs;
    }
}
