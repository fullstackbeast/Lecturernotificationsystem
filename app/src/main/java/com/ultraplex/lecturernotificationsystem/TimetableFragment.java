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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
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

    String [] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    ArrayAdapter dayAdapter;
    ArrayAdapter deptNameAdapter;
    ArrayAdapter levelAdapter;


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
                    showCustomDialog();
                } else {

                }
            }
        });
    }

    private void setTimePickerListener(Dialog dialogView) {
        (dialogView.findViewById(R.id.btn_time_picker)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTimePickerLight(dialogView);
            }
        });
    }

    private void dialogTimePickerLight(Dialog dialogView) {
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
//                ((TextView) dialogView.findViewById(R.id.result)).setText(hourOfDay + " : " + minute);
                Log.v("Timepicker", "Hour of the day: " + hourOfDay);
                Log.v("Timepicker", "Minute: " + minute);
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

        setTimePickerListener(dialog);

        dayAdapter =  new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, days);

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

        btnAddNewField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Adding new field", Toast.LENGTH_SHORT).show();

//                TextInputEditText newField = new TextInputEditText(getContext());
//                newField.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT));

                View courseField = LayoutInflater.from(getContext()).inflate(R.layout.timetable_fields, null, false);
                courseField.setTag("Field_" + linearFieldsContainer.getChildCount());

                linearFieldsContainer.addView(courseField);

                Log.v("Tagly", courseField.getTag().toString());
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

                Map<String, Object> lecturer = new HashMap<>();

                String firstString = String.valueOf(random.nextInt(1000));
                String secondString = String.valueOf(random.nextInt(1000));

                lecturer.put("Id", firstString + secondString);


                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}