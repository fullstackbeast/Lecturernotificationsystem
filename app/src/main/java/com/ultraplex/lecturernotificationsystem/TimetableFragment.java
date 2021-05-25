package com.ultraplex.lecturernotificationsystem;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ultraplex.lecturernotificationsystem.entities.Course;
import com.ultraplex.lecturernotificationsystem.entities.Department;
import com.ultraplex.lecturernotificationsystem.entities.Level;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class TimetableFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    ArrayList<Course> coursesByDeptAndLevel = new ArrayList<>();


    RecyclerView mRecyclerView;
    TimetableListAdapter mRecyclerAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    ArrayList<TimetableListItem> recylerListItems = new ArrayList<>();

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

        Spinner spinnerDays = view.findViewById(R.id.spinner_timetable_show_days);
        Spinner spinnerDept = view.findViewById(R.id.spinner_timetable_show_dept);
        Spinner spinnerLevel = view.findViewById(R.id.spinner_timetable_show_level);

        dayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setOnItemSelectedListener(this);
        spinnerDays.setAdapter(dayAdapter);

        spinnerDept.setOnItemSelectedListener(this);
        spinnerLevel.setOnItemSelectedListener(this);

        getDeptsAndLevels(spinnerDept, spinnerLevel);

        //For showing timetable
        Button btnShowTimetable = view.findViewById(R.id.btn_show_timetable);

        configureRecyclerView(view);

        btnShowTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Showing Timetable for " + spinnerDays.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                recylerListItems.clear();
                mRecyclerAdapter.notifyDataSetChanged();
                getCoursesByDeptAndLevel(
                        departments.get(spinnerDept.getSelectedItemPosition()).getId(),
                        levels.get(spinnerLevel.getSelectedItemPosition()).getId(),
                        spinnerDays.getSelectedItem().toString());
            }
        });


        //For Adding timetable

        Button btnAddTimetable = view.findViewById(R.id.btn_add_new_timetable);

        btnAddTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addnewTimetable();
            }
        });


        return view;
    }

    private void addnewTimetable() {
        showCustomDialog();
    }

    private void getDeptsAndLevels(Spinner spinnerDept, Spinner spinnerLevel) {
        getAllDepartments(spinnerDept);
        getAllLevels(spinnerLevel);
    }

    private void getCoursesByDeptAndLevel(String deptId, String levelId, String day) {
        db.collection("courses")
                .whereEqualTo("DepartmentId", deptId)
                .whereEqualTo("LevelId", levelId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() < 1) {
                        Toast.makeText(getContext(), "No timetable for the selected department and level", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<String> courseIdS = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Course course = new Course(
                                document.getString("Id"),
                                document.getString("Code"),
                                document.getString("Title"),
                                document.getString("DepartmentId"),
                                document.getString("LevelId"),
                                document.getString("LecturerId")
                        );
                        coursesByDeptAndLevel.add(course);
                        courseIdS.add(course.getId());
                    }

                    getTimeTableByDayCourses(day, courseIdS);
                }
            }
        });
    }

    private void getTimeTableByDayCourses(String day, List<String> courseIdS) {
        db.collection("timetables")
                .whereEqualTo("Day", day)
                .whereIn("CourseId", courseIdS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().size() < 1) {
                                Toast.makeText(getContext(), "No Timetable for the selected day.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            mRecyclerView.setVisibility(View.VISIBLE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Course currentCourse = null;
                                Level currentLevel = null;

                                for (Course c : coursesByDeptAndLevel) {
                                    if (c.getId().equals(document.getString("CourseId"))) {
                                        currentCourse = c;
                                        break;
                                    }
                                }

                                for (Level l : levels) {
                                    if (l.getId().equals(currentCourse.getLevelId())) {
                                        currentLevel = l;
                                        break;
                                    }
                                }
                                recylerListItems.add(new TimetableListItem(
                                        document.getString("Id"),
                                        StringUtils.convertTo12Hr(document.getString("StartTime")),
                                        StringUtils.convertTo12Hr(document.getString("StopTime")),
                                        currentCourse.getTitle(),
                                        currentCourse.getCode(),
                                        currentLevel.getName()
                                ));
                                mRecyclerAdapter.notifyItemInserted(recylerListItems.size());
                            }
                        }
                    }
                });
    }

    private void getAllDepartments(Spinner spinnerDept) {
        db.collection("departments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    departmentNames = new String[task.getResult().size()];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        departments.add(new Department(document.getString("Id"), document.getString("Name")));
                        departmentNames[i] = StringUtils.capitalizeText(document.getString("Name"));
                        i++;
                    }
                    deptNameAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, departmentNames);
                    deptNameAdapter.notifyDataSetChanged();
                    deptNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDept.setAdapter(deptNameAdapter);
                }
            }
        });
    }

    private void getAllLevels(Spinner spinnerLevel) {
        db.collection("levels").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    levelNames = new String[task.getResult().size()];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        levels.add(new Level(document.getString("Id"), document.getString("Name")));
                        levelNames[i] = StringUtils.capitalizeText(document.getString("Name"));
                        i++;
                    }
                    levelAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, levelNames);
                    levelAdapter.notifyDataSetChanged();
                    levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLevel.setAdapter(levelAdapter);
                } else {

                }
            }
        });
    }

    private void getCourses(String deptId, String levelId, LinearLayout linearFieldContainer, Button btnAddNewField) {
        db.collection("courses")
                .whereEqualTo("DepartmentId", deptId)
                .whereEqualTo("LevelId", levelId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() < 1) {
                        Toast.makeText(getContext(), "No Courses for the selected Department and Level", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
                    btnAddNewField.setVisibility(View.VISIBLE);
                    addNewCourseFields(linearFieldContainer);
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
                    ((TextInputEditText) rootView.findViewById(R.id.editText_add_timetable_starttime)).setText(hourOfDay + ":" + minute);
                } else if (type.equals("Stop")) {
                    ((TextInputEditText) rootView.findViewById(R.id.editText_add_timetable_stoptime)).setText(hourOfDay + ":" + minute);
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

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_timetable);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        Spinner daySpinner = dialog.findViewById(R.id.spinner_timetable_days);
        Spinner deptSpinner = dialog.findViewById(R.id.spinner_timetable_dept);
        Spinner levelSpinner = dialog.findViewById(R.id.spinner_timetable_level);

        daySpinner.setOnItemSelectedListener(this);
        deptSpinner.setOnItemSelectedListener(this);
        levelSpinner.setOnItemSelectedListener(this);

        deptSpinner.setAdapter(deptNameAdapter);
        levelSpinner.setAdapter(levelAdapter);

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);


//      input fields
        final TextInputEditText editText_lecturer_firstName = dialog.findViewById(R.id.lecturer_firstname);
        final LinearLayout linearFieldsContainer = dialog.findViewById(R.id.linear_timetable_fields);

        final Button btnAddNewField = dialog.findViewById(R.id.btn_add_timetable_field);
        final Button btnStartCreate = dialog.findViewById(R.id.btn_add_timetable_startcreate);


        btnStartCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddNewField.setVisibility(View.GONE);
                linearFieldsContainer.removeAllViews();
                getCourses(departments.get(deptSpinner.getSelectedItemPosition()).getId(),
                        levels.get(levelSpinner.getSelectedItemPosition()).getId(), linearFieldsContainer, btnAddNewField);
            }
        });

        btnAddNewField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewCourseFields(linearFieldsContainer);
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

                saveTimetablesToD(dialog, linearFieldsContainer);

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void addNewCourseFields(LinearLayout linearFieldsContainer) {
        View courseFields = LayoutInflater.from(getContext()).inflate(R.layout.timetable_fields, null, false);
        courseFields.setTag("Field_" + linearFieldsContainer.getChildCount());

        Spinner newCourseSpinner = courseFields.findViewById(R.id.spinner_timetable_add_courses);
        newCourseSpinner.setOnItemSelectedListener(new TimetableFragment());
        newCourseSpinner.setAdapter(courseAdapter);

        linearFieldsContainer.addView(courseFields);

        ((TextView) courseFields.findViewById(R.id.txt_add_timetable_courseno)).setText("Course " + linearFieldsContainer.getChildCount());

        configureDialogTimePickers(linearFieldsContainer);
    }

    private void configureRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recview_timetable);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerAdapter = new TimetableListAdapter(recylerListItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        mRecyclerAdapter.setOnItemClickListener(new TimetableListAdapter.OnItemClickListener() {
            @Override
            public void onAlarmClick(int position) {
                Toast.makeText(getContext(), "Setting alarm for " + recylerListItems.get(position).getCourseTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), recylerListItems.get(position).getCourseTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                Toast.makeText(getContext(), "Deleting " + recylerListItems.get(position).getCourseTitle(), Toast.LENGTH_SHORT).show();
                deleteTimetable(position);
            }
        });
    }

    private void deleteTimetable(int position) {
        String courseTitle = recylerListItems.get(position).getCourseTitle();
        String id = recylerListItems.get(position).getId();

        db.collection("timetables")
                .whereEqualTo("Id", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("Id").equals(id)) {
                            document.getReference().delete();
                            recylerListItems.remove(position);
                            mRecyclerAdapter.notifyItemRemoved(position);
                            Toast.makeText(getContext(), courseTitle + " deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
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

    private void saveTimetablesToD(Dialog dialog, LinearLayout linearFieldsContainer) {
        Random random = new Random();

        String day = ((Spinner) dialog.findViewById(R.id.spinner_timetable_days)).getSelectedItem().toString().trim();

        if (linearFieldsContainer.getChildCount() < 1) {
            Toast.makeText(getContext(), "Invalid Timetable Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < linearFieldsContainer.getChildCount(); i++) {
            Map<String, Object> timetable = new HashMap<>();

            TextInputEditText editTextStartTime = linearFieldsContainer.getChildAt(i).findViewById(R.id.editText_add_timetable_starttime);
            TextInputEditText editTextStopTime = linearFieldsContainer.getChildAt(i).findViewById(R.id.editText_add_timetable_stoptime);
            Spinner spinnerCourse = linearFieldsContainer.getChildAt(i).findViewById(R.id.spinner_timetable_add_courses);

            String firstString = String.valueOf(random.nextInt(1000));
            String secondString = String.valueOf(random.nextInt(1000));

            timetable.put("Id", firstString + secondString);
            timetable.put("Day", day);
            timetable.put("CourseId", courses.get(spinnerCourse.getSelectedItemPosition()).getId());
            timetable.put("StartTime", editTextStartTime.getText().toString());
            timetable.put("StopTime", editTextStopTime.getText().toString());

            db.collection("timetables")
                    .add(timetable)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("TimetableAdd", timetable.get("CourseId") + " added successfully ");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TimetableAdd", "Error adding document", e);
                        }
                    });

        }

        Toast.makeText(getContext(), "Timetables added successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}