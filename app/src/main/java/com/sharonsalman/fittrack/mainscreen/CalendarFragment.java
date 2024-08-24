package com.sharonsalman.fittrack.mainscreen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.sharonsalman.fittrack.Programs.FitnessProgram;
import com.sharonsalman.fittrack.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private Spinner programSpinner;
    private MaterialCalendarView materialCalendarView;
    private Button addToCalendarButton;
    private List<FitnessProgram> programs = new ArrayList<>();
    private FitnessProgram selectedProgram;
    private static final int REQUEST_CALENDAR_PERMISSION = 1;
    private DatabaseReference databaseReference;

    private Map<LocalDate, List<FitnessProgram>> programSchedule = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        programSpinner = view.findViewById(R.id.program_spinner);
        materialCalendarView = view.findViewById(R.id.material_calendar_view);
        addToCalendarButton = view.findViewById(R.id.add_to_calendar_button);

        // Initialize Firebase Database reference
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("program_dates");

        if (getArguments() != null) {
            selectedProgram = getArguments().getParcelable("selected_program");
            if (selectedProgram != null) {
                updateUIWithSelectedProgram(selectedProgram);
            } else {
                Log.d("CalendarFragment", "No selected program found in arguments.");
            }
        }

        setupMaterialCalendarView();
        setupAddToCalendarButton();
        populatePrograms();
        loadProgramsFromFirebase();

        return view;
    }

    private void loadProgramsFromFirebase() {
        if (databaseReference == null) {
            Log.e("CalendarFragment", "DatabaseReference is null.");
            return;
        }

        // Reference to the 'program_dates' node
        DatabaseReference programDatesReference = databaseReference.child("program_dates");

        programDatesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                programSchedule.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dateStr = snapshot.getKey();
                    if (dateStr != null) {
                        LocalDate date = LocalDate.parse(dateStr);
                        List<FitnessProgram> programs = new ArrayList<>();
                        for (DataSnapshot programSnapshot : snapshot.getChildren()) {
                            String name = programSnapshot.child("programName").getValue(String.class);
                            if (name != null) {
                                programs.add(new FitnessProgram(name, "")); // Pass an empty string for details
                            }
                        }
                        programSchedule.put(date, programs);
                    }
                }
                refreshCalendarView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading programs", databaseError.toException());
            }
        });
    }




    private void saveProgramsToFirebase(FitnessProgram program) {
        if (databaseReference == null) {
            Log.e("Firebase", "DatabaseReference is null.");
            return;
        }

        DatabaseReference programsReference = databaseReference.child("programs");

        String programId = programsReference.push().getKey();
        Map<String, Object> programData = new HashMap<>();
        programData.put("name", program.getName());

        programsReference.child(programId).setValue(programData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Program saved: " + program.getName());
                        generateAndSaveRandomDates(program, programId);
                    } else {
                        Log.e("Firebase", "Error saving program", task.getException());
                    }
                });
    }

    private void generateAndSaveRandomDates(FitnessProgram program, String programId) {
        String frequency = program.getFrequency();
        int minTimes = Integer.parseInt(frequency.split("-")[0]);
        int maxTimes = Integer.parseInt(frequency.split("-")[1].split(" ")[0]);

        LocalDate startDate = LocalDate.now();
        DatabaseReference datesReference = databaseReference.child("program_dates");

        for (int week = 0; week < 4; week++) {
            int timesThisWeek = minTimes + (int)(Math.random() * ((maxTimes - minTimes) + 1));
            List<LocalDate> weekDates = new ArrayList<>();

            for (int day = 0; day < 7; day++) {
                weekDates.add(startDate.plusDays(day));
            }

            List<LocalDate> selectedDates = new ArrayList<>();
            for (int i = 0; i < timesThisWeek; i++) {
                if (weekDates.isEmpty()) break;
                int randomIndex = (int)(Math.random() * weekDates.size());
                LocalDate randomDate = weekDates.remove(randomIndex);
                selectedDates.add(randomDate);
            }

            // Sort the selected dates to ensure they're in chronological order
            Collections.sort(selectedDates);

            for (LocalDate date : selectedDates) {
                String dateKey = date.toString();
                Map<String, Object> dateData = new HashMap<>();
                dateData.put("programId", programId);
                dateData.put("programName", program.getName());

                datesReference.child(dateKey).push().setValue(dateData);
            }

            startDate = startDate.plusWeeks(1);
        }
    }
    private void updateUIWithSelectedProgram(FitnessProgram program) {
        Log.d("CalendarFragment", "Updating UI with selected program: " + program.getName());
        List<FitnessProgram> programList = new ArrayList<>();
        programList.add(program);
        ArrayAdapter<FitnessProgram> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, programList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        programSpinner.setAdapter(adapter);
        programSpinner.setEnabled(false);
    }

    private void setupMaterialCalendarView() {
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                LocalDate selectedDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
                Log.d("CalendarFragment", "Date selected: " + formatDate(selectedDate));
                displayEventsForDate(selectedDate);
            }
        });

        materialCalendarView.setDateTextAppearance(R.style.CustomDateTextAppearance);
    }

    private void setupAddToCalendarButton() {
        addToCalendarButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED) {
                if (selectedProgram != null) {
                    Log.d("CalendarFragment", "Adding program to calendar: " + selectedProgram.getName());
                    addProgramToCalendar(selectedProgram);
                } else {
                    Log.d("CalendarFragment", "Selected program is null.");
                }
            } else {
                Log.d("CalendarFragment", "Calendar permission not granted. Requesting permission.");
                requestCalendarPermissions();
            }
        });
    }

    private void requestCalendarPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    REQUEST_CALENDAR_PERMISSION);
        }
    }
    private void loadProgramDatesAndAddToCalendar() {
        DatabaseReference datesReference = databaseReference.child("program_dates");
        datesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String dateStr = dateSnapshot.getKey();
                    LocalDate date = LocalDate.parse(dateStr);

                    for (DataSnapshot programSnapshot : dateSnapshot.getChildren()) {
                        String programName = programSnapshot.child("programName").getValue(String.class);
                        addEventToDeviceCalendar(date, programName);
                    }
                }
                refreshCalendarView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading program dates", databaseError.toException());
            }
        });
    }

    private void addEventToDeviceCalendar(LocalDate date, String programName) {
        ContentResolver contentResolver = getContext().getContentResolver();
        ZonedDateTime startDateTime = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endDateTime = startDateTime.plusHours(1);

        ContentValues eventValues = new ContentValues();
        eventValues.put(CalendarContract.Events.DTSTART, startDateTime.toInstant().toEpochMilli());
        eventValues.put(CalendarContract.Events.DTEND, endDateTime.toInstant().toEpochMilli());
        eventValues.put(CalendarContract.Events.TITLE, programName);
        eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().getId());

        contentResolver.insert(CalendarContract.Events.CONTENT_URI, eventValues);
    }

    private void populatePrograms() {
        Log.d("CalendarFragment", "Populating programs.");
        programs.add(new FitnessProgram("Program A", "3-4 days"));
        programs.add(new FitnessProgram("Program B", "1-2 days"));

        ArrayAdapter<FitnessProgram> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, programs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        programSpinner.setAdapter(adapter);
    }

    private void addProgramToCalendar(FitnessProgram program) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("CalendarFragment", "Permission denied. Requesting calendar permissions.");
            requestCalendarPermissions();
            return;
        }

        LocalDate today = LocalDate.now();
        List<LocalDate> datesToSave = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate startDate = today.plusWeeks(i);
            datesToSave.add(startDate);

            List<FitnessProgram> programsForDate = new ArrayList<>();
            programsForDate.add(program);
            programSchedule.put(startDate, programsForDate);

            // Add to device calendar (existing code)
            ContentResolver contentResolver = getContext().getContentResolver();
            ZonedDateTime startDateTime = startDate.atStartOfDay(ZoneId.systemDefault());
            ZonedDateTime endDateTime = startDateTime.plusHours(1);

            ContentValues eventValues = new ContentValues();
            eventValues.put(CalendarContract.Events.DTSTART, startDateTime.toInstant().toEpochMilli());
            eventValues.put(CalendarContract.Events.DTEND, endDateTime.toInstant().toEpochMilli());
            eventValues.put(CalendarContract.Events.TITLE, program.getName());
            eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().getId());

            contentResolver.insert(CalendarContract.Events.CONTENT_URI, eventValues);

            Log.d("CalendarFragment", "Added program to calendar: " + program.getName() + " on " + formatDate(startDate));
        }

        // Save all dates to Firebase
        saveProgramsToFirebase(program);
    }

    private void displayEventsForDate(LocalDate date) {
        List<FitnessProgram> programsForDate = programSchedule.get(date);
        if (programsForDate != null && !programsForDate.isEmpty()) {
            StringBuilder events = new StringBuilder();
            for (FitnessProgram program : programsForDate) {
                events.append(program.getName()).append("\n");
            }
            new AlertDialog.Builder(getContext())
                    .setTitle("Programs on " + formatDate(date))
                    .setMessage(events.toString())
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    private void refreshCalendarView() {
        materialCalendarView.removeDecorators();

        Set<CalendarDay> daysWithPrograms = new HashSet<>();
        for (LocalDate date : programSchedule.keySet()) {
            daysWithPrograms.add(CalendarDay.from(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        }

        for (CalendarDay day : daysWithPrograms) {
            materialCalendarView.addDecorator(new DotSpanDecorator(LocalDate.of(day.getYear(), day.getMonth(), day.getDay())));
        }
    }


    private String formatDate(LocalDate date) {
        return date.getMonth() + " " + date.getDayOfMonth() + ", " + date.getYear();
    }

    private static class DotSpanDecorator implements DayViewDecorator {
        private final LocalDate date;

        DotSpanDecorator(LocalDate date) {
            this.date = date;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            // Extract year, month, and day from CalendarDay
            int dayYear = day.getYear();
            Month dayMonth = Month.of(day.getMonth()); // Convert int to Month
            int dayOfMonth = day.getDay();

            // Compare with LocalDate components
            return dayYear == date.getYear() &&
                    dayMonth == date.getMonth() && // Compare Month enums
                    dayOfMonth == date.getDayOfMonth();
        }

        @Override
        public void decorate(@NonNull DayViewFacade view) {
            view.addSpan(new DotSpan(10, Color.RED));
        }
    }

}
