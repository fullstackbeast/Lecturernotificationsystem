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
import android.widget.Spinner;
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
import com.ultraplex.lecturernotificationsystem.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CourseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Button btnAddCourse;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> resultTask;

    ArrayAdapter deptNameAdapter;
    ArrayAdapter levelAdapter;
    ArrayAdapter lecturerAdapter;

    ArrayList<Department> departments = new ArrayList<>();
    String[] departmentNames;

    ArrayList<Level> levels = new ArrayList<>();
    String[] levelNames;

    ArrayList<User> lecturers = new ArrayList<>();
    String[] lecturerNames;

    ArrayList<CourseListItem> listItems = new ArrayList<>();

    RecyclerView mRecyclerView;

    CourseListAdapter mRecyclerAdapter;

    RecyclerView.LayoutManager mLayoutManager;

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        btnAddCourse = view.findViewById(R.id.btn_add_new_course);

        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCourse();
            }
        });

        configureRecyclerView(view);

        getAllCourses();

        return view;
    }

    private void configureRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recview_course);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerAdapter = new CourseListAdapter(listItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        mRecyclerAdapter.setOnItemClickListener(new CourseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), listItems.get(position).getTextCourseTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                Toast.makeText(getContext(), "Deleting: " + listItems.get(position).getTextCourseTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllCourses() {
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CourseListItem courseListItem = new CourseListItem(
                                document.getString("Title"),
                                document.getString("DepartmentId"),
                                document.getString("LecturerId"),
                                document.getString("Code").toUpperCase()
                        );
                        listItems.add(courseListItem);
                        mRecyclerAdapter.notifyItemInserted(listItems.size());
                    }
                } else Log.w("GetDepts", "Error getting documents.", task.getException());
            }
        });
    }

    private void addNewCourse() {

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
                    getAllLecturers();
                } else {

                }
            }
        });
    }

    private void getAllLecturers() {
        db.collection("users").whereEqualTo("Type", "lecturer").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    lecturerNames = new String[task.getResult().size()];
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User lecturer = new User(
                                document.getString("Id"),
                                document.getString("Firstname"),
                                document.getString("Lastname"),
                                document.getString("Staffid"),
                                document.getString("Password"),
                                document.getString("Type"));
                        lecturers.add(lecturer);
                        lecturerNames[i] = document.getString("Firstname") + " " + document.getString("Lastname");
                        i++;
                    }
                    lecturerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, lecturerNames);
                    lecturerAdapter.notifyDataSetChanged();
                    lecturerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    showCustomDialog();
                } else {
                    Log.e("Lecturer", "Not successfull");
                }
            }
        });
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(getContext());
        Random random = new Random();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_course);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Spinner deptSpinner = dialog.findViewById(R.id.spinner_dept);
        Spinner levelSpinner = dialog.findViewById(R.id.spinner_level);
        Spinner lecturerSpinner = dialog.findViewById(R.id.spinner_lecturer);

        deptSpinner.setOnItemSelectedListener(this);
        levelSpinner.setOnItemSelectedListener(this);
        lecturerSpinner.setOnItemSelectedListener(this);

        deptSpinner.setAdapter(deptNameAdapter);
        levelSpinner.setAdapter(levelAdapter);
        lecturerSpinner.setAdapter(lecturerAdapter);

        final TextInputEditText editText_courseCode = dialog.findViewById(R.id.editText_course_code);
        final TextInputEditText editText_courseTitle = dialog.findViewById(R.id.editText_course_title);


        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Department selectedDepartment = departments.get(deptSpinner.getSelectedItemPosition());
                Level selectedLevel = levels.get(levelSpinner.getSelectedItemPosition());
                User selectedLecturer = lecturers.get(lecturerSpinner.getSelectedItemPosition());

                Map<String, Object> course = new HashMap<>();

                String firstString = String.valueOf(random.nextInt(1000));
                String secondString = String.valueOf(random.nextInt(1000));

                course.put("Id", firstString + secondString);
                course.put("Code", editText_courseCode.getText().toString().trim());
                course.put("Title", editText_courseTitle.getText().toString().trim());
                course.put("DepartmentId", selectedDepartment.getId());
                course.put("LecturerId", selectedLecturer.getId());
                course.put("LevelId", selectedLevel.getId());

                addCourseToDb(course);


                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void addCourseToDb(Map<String, Object> course) {
        db.collection("courses")
                .add(course)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("CourseAdd", course.get("Title") + " added successfully ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("CourseAdd", "Error adding document", e);
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}