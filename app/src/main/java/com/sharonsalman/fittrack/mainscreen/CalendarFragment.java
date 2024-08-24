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
import java.util.UUID;

public class CalendarFragment extends Fragment {

    private Spinner programSpinner;
    private MaterialCalendarView materialCalendarView;
    private Button addToCalendarButton;
    private FitnessProgram selectedProgram;
    private static final int REQUEST_CALENDAR_PERMISSION = 1;
    private DatabaseReference databaseReference;
    private Map<String, Map<String, FitnessProgram>> programSchedule = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        programSpinner = view.findViewById(R.id.program_spinner);
        materialCalendarView = view.findViewById(R.id.material_calendar_view);
        addToCalendarButton = view.findViewById(R.id.add_to_calendar_button);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

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
        DatabaseReference programDatesReference = databaseReference.child("program_dates");

        programDatesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                programSchedule.clear();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String dateStr = dateSnapshot.getKey();
                    Map<String, FitnessProgram> programsForDate = new HashMap<>();
                    for (DataSnapshot programSnapshot : dateSnapshot.getChildren()) {
                        String programId = programSnapshot.getKey();
                        String name = programSnapshot.child("programName").getValue(String.class);
                        if (name != null) {
                            programsForDate.put(programId, new FitnessProgram(name, ""));
                        }
                    }
                    programSchedule.put(dateStr, programsForDate);
                }
                refreshCalendarView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading programs", databaseError.toException());
            }
        });
    }

    private void saveProgramsToFirebase(FitnessProgram program, List<LocalDate> dates) {
        DatabaseReference programDatesReference = databaseReference.child("program_dates");

        for (LocalDate date : dates) {
            String dateStr = date.toString();
            String programId = UUID.randomUUID().toString();
            Map<String, Object> programData = new HashMap<>();
            programData.put("programId", programId);
            programData.put("programName", program.getName());

            programDatesReference.child(dateStr).child(programId).setValue(programData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Program saved for date: " + dateStr);
                        } else {
                            Log.e("Firebase", "Error saving program for date: " + dateStr, task.getException());
                        }
                    });
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
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            LocalDate selectedDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
            Log.d("CalendarFragment", "Date selected: " + formatDate(selectedDate));
            displayEventsForDate(selectedDate);
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
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_CALENDAR},
                REQUEST_CALENDAR_PERMISSION);
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
        List<FitnessProgram> programs = new ArrayList<>();
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

        // Parse the frequency to determine the number of occurrences per week
        int occurrencesPerWeek = parseFrequency(program.getFrequency());

        for (int i = 0; i < 7; i++) {
            LocalDate startDate = today.plusWeeks(i);
            List<LocalDate> datesForWeek = generateRandomDatesForWeek(startDate, occurrencesPerWeek);
            datesToSave.addAll(datesForWeek);

            // Add events to the calendar
            for (LocalDate date : datesForWeek) {
                addEventToDeviceCalendar(date, program.getName());
                Log.d("CalendarFragment", "Added program to calendar: " + program.getName() + " on " + formatDate(date));
            }
        }

        saveProgramsToFirebase(program, datesToSave);
    }

    private int parseFrequency(String frequency) {
        // Example parsing logic; adjust according to your frequency format
        if (frequency.equals("1-2 days")) {
            return (int) (Math.random() * 2) + 1;
        }
        if (frequency.equals("2-3 days")) {
            return (int) (Math.random() * 2) + 2;
        }
        if (frequency.equals("4-5 days")) {
            return (int) (Math.random() * 2) + 4;
        }
        // Add other frequency parsing logic if needed
        return 1;
    }

    private List<LocalDate> generateRandomDatesForWeek(LocalDate startDate, int occurrences) {
        List<LocalDate> dates = new ArrayList<>();
        Set<LocalDate> chosenDates = new HashSet<>();
        LocalDate endDate = startDate.plusDays(6);

        while (chosenDates.size() < occurrences) {
            LocalDate randomDate = startDate.plusDays((int) (Math.random() * 7));
            if (!chosenDates.contains(randomDate) && !randomDate.isAfter(endDate)) {
                chosenDates.add(randomDate);
                dates.add(randomDate);
            }
        }

        return dates;
    }

    private void displayEventsForDate(LocalDate date) {
        String dateStr = date.toString();
        Map<String, FitnessProgram> programsForDate = programSchedule.get(dateStr);
        if (programsForDate != null && !programsForDate.isEmpty()) {
            StringBuilder events = new StringBuilder();
            for (FitnessProgram program : programsForDate.values()) {
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

        for (String dateStr : programSchedule.keySet()) {
            LocalDate date = LocalDate.parse(dateStr);
            materialCalendarView.addDecorator(new DotSpanDecorator(date));
        }
    }

    private String formatDate(LocalDate date) {
        return date.getMonth() + " " + date.getDayOfMonth() + ", " + date.getYear();
    }

    private static class DotSpanDecorator implements DayViewDecorator {
        private final CalendarDay date;

        DotSpanDecorator(LocalDate date) {
            this.date = CalendarDay.from(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(date);
        }

        @Override
        public void decorate(@NonNull DayViewFacade view) {
            view.addSpan(new DotSpan(10, Color.BLACK));
        }
    }
}
