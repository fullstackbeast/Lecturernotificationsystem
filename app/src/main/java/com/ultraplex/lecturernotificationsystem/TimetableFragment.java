package com.ultraplex.lecturernotificationsystem;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ultraplex.lecturernotificationsystem.entities.Department;
import com.ultraplex.lecturernotificationsystem.entities.Level;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class TimetableFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btnAddTimetable;

    ArrayList<Department> departments = new ArrayList<>();
    String[] departmentNames;

    ArrayList<Level> levels = new ArrayList<>();
    String[] levelNames;

    ArrayList<Level> courses = new ArrayList<>();
    String[] courseTitles;

    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    ArrayAdapter dayAdapter;
    ArrayAdapter deptNameAdapter;
    ArrayAdapter levelAdapter;
    ArrayAdapter courseAdapter;


    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        btnAddTimetable = view.findViewById(R.id.btn_add_new_timetable);

        btnAddTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addnewTimetable();
            }
        });


        return view;
    }

    private void addnewTimetable() {
        getAllDepartments();
    }

    private void getAllDepartments() {
        db.collection("departments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    departmentNames = new String[task.getResult().size()];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        departments.add(new Department(document.getString("Id"), document.getString("Name")));
                        departmentNames[i] = document.getString("Name");
                        i++;
                    }
                    deptNameAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, departmentNames);
                    deptNameAdapter.notifyDataSetChanged();
                    deptNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    getAllLevels();
                }
            }
        });
    }

    private void getAllLevels() {
        db.collection("levels").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    levelNames = new String[task.getResult().size()];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        levels.add(new Level(document.getString("Id"), document.getString("Name")));
                        levelNames[i] = document.getString("Name");
                        i++;
                    }
                    levelAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, levelNames);
                    levelAdapter.notifyDataSetChanged();
                    levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    getCourses();
                } else {

                }
            }
        });
    }

    private void getCourses(){
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    courseTitles = new String[task.getResult().size()];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        courses.add(new Level(document.getString("Id"), document.getString("Title")));
                        courseTitles[i] = document.getString("Title");
                        i++;
                    }
                    courseAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, courseTitles);
                    courseAdapter.notifyDataSetChanged();
                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    showCustomDialog();
                } else {

                }
            }
        });
    }

    private void showTimePicker(View rootView, String type) {
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                if (type.equals("Start")) {
                    ((TextInputEditText) rootView.findViewById(R.id.editText_add_timetable_starttime)).setText(hourOfDay + " : " + minute);
                } else if (type.equals("Stop")) {
                    ((TextInputEditText) rootView.findViewById(R.id.editText_add_timetable_stoptime)).setText(hourOfDay + " : " + minute);
                }
            }
        }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), false);
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Timepickerdialog");
    }


    private boolean isValidTimes(int startHour, int startMinute, int stopHour, int stopMinute) {
        return false;
    }


    private void showCustomDialog() {
        final Dialog dialog = new Dialog(getContext());
        Random random = new Random();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_timetable);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, days);

        Spinner daySpinner = dialog.findViewById(R.id.spinner_timetable_days);
        Spinner deptSpinner = dialog.findViewById(R.id.spinner_timetable_dept);
        Spinner levelSpinner = dialog.findViewById(R.id.spinner_timetable_level);
        Spinner courseSpinner = dialog.findViewById(R.id.spinner_timetable_add_courses);

        daySpinner.setOnItemSelectedListener(this);
        deptSpinner.setOnItemSelectedListener(this);
        levelSpinner.setOnItemSelectedListener(this);
        courseSpinner.setOnItemSelectedListener(this);


        deptSpinner.setAdapter(deptNameAdapter);
        levelSpinner.setAdapter(levelAdapter);
        courseSpinner.setAdapter(courseAdapter);

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);


//      input fields
        final TextInputEditText editText_lecturer_firstName = dialog.findViewById(R.id.lecturer_firstname);
        final LinearLayout linearFieldsContainer = dialog.findViewById(R.id.linear_timetable_fields);

        final Button btnAddNewField = dialog.findViewById(R.id.btn_add_timetable_field);

        configureDialogTimePickers(linearFieldsContainer);

        btnAddNewField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Adding new field", Toast.LENGTH_SHORT).show();

                View courseFields = LayoutInflater.from(getContext()).inflate(R.layout.timetable_fields, null, false);
                courseFields.setTag("Field_" + linearFieldsContainer.getChildCount());

                Spinner newCourseSpinner = courseFields.findViewById(R.id.spinner_timetable_add_courses);
                newCourseSpinner.setOnItemSelectedListener(new TimetableFragment());
                newCourseSpinner.setAdapter(courseAdapter);

                linearFieldsContainer.addView(courseFields);

                ((TextView) courseFields.findViewById(R.id.txt_add_timetable_courseno)).setText("Course "+ linearFieldsContainer.getChildCount());

                configureDialogTimePickers(linearFieldsContainer);

                Log.v("Tagly", courseFields.getTag().toString());
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> timetable = new HashMap<>();

                String firstString = String.valueOf(random.nextInt(1000));
                String secondString = String.valueOf(random.nextInt(1000));

                timetable.put("Id", firstString + secondString);

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void configureDialogTimePickers(LinearLayout fieldContainer) {

        for (int i = 0; i < fieldContainer.getChildCount(); i++) {
            View currentFieldView = fieldContainer.getChildAt(i);

            Button btnStartTime = currentFieldView.findViewById(R.id.btn_add_timetable_starttime);
            Button btnStopTime = currentFieldView.findViewById(R.id.btn_add_timetable_stoptime);

            btnStartTime.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Showing Start Time Dialog", Toast.LENGTH_SHORT).show();
                showTimePicker(currentFieldView, "Start");
            });

            btnStopTime.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Showing Stop Time Dialog", Toast.LENGTH_SHORT).show();
                showTimePicker(currentFieldView, "Stop");
            });
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}