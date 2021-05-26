package com.ultraplex.lecturernotificationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ultraplex.lecturernotificationsystem.entities.Course;
import com.ultraplex.lecturernotificationsystem.entities.Level;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LecturerDashboardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<String> days = Arrays.asList(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});

    ArrayAdapter dayAdapter;

    RecyclerView mRecyclerView;
    TimetableListAdapter mRecyclerAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    ArrayList<TimetableListItem> recylerListItems = new ArrayList<>();

    List<Course> lecturerCourses = new ArrayList<>();
    List<String> courseIds = new ArrayList<>();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedprefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_dashboard);

        sharedPreferences = getSharedPreferences("TimetableSharedPref", MODE_PRIVATE);
        sharedprefEditor = sharedPreferences.edit();

        initToolbar();

        createNotificationChannel();


        configureRecyclerView();

        Spinner spinnerDays = findViewById(R.id.spinner_lecturer_days);

        dayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setOnItemSelectedListener(this);
        spinnerDays.setAdapter(dayAdapter);

        spinnerDays.setSelection(days.indexOf(getToday()));

        getLecturerCourses();

        Button btnFetchTimetable = findViewById(R.id.btn_lecturer_fetch_timetable);

        btnFetchTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setVisibility(View.GONE);
                recylerListItems.clear();
                mRecyclerAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mRecyclerAdapter);
                getTimeTableByDayCourses(spinnerDays.getSelectedItem().toString());
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getIntent().getStringExtra("staffId"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private String getToday() {
        Date date = Calendar.getInstance().getTime();
        String day = StringUtils.capitalizeText(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()));

        if (days.indexOf(day) == -1) return "Monday";
        return day;
    }

    private void getLecturerCourses() {
        db.collection("courses")
                .whereEqualTo("LecturerId", getIntent().getStringExtra("staffId"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() < 1)
                        Toast.makeText(LecturerDashboardActivity.this, "No courses", Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Course course = new Course(
                                document.getString("Id"),
                                document.getString("Code"),
                                document.getString("Title"),
                                document.getString("DepartmentId"),
                                document.getString("LevelId"),
                                document.getString("LecturerId")
                        );

                        lecturerCourses.add(course);
                        courseIds.add(course.getId());
                        Log.v("LecturersCourse", StringUtils.capitalizeText(document.getString("Title")));
                    }
                    getTimeTableByDayCourses(getToday());
                }
            }
        });
    }

    private void getTimeTableByDayCourses(String day) {
        db.collection("timetables")
                .whereEqualTo("Day", day)
                .whereIn("CourseId", courseIds).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().size() < 1) {
                                Toast.makeText(LecturerDashboardActivity.this, "No Timetable for the selected day.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            mRecyclerView.setVisibility(View.VISIBLE);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Course currentCourse = null;
                                for (Course c : lecturerCourses) {
                                    if (c.getId().equals(document.getString("CourseId"))) {
                                        currentCourse = c;
                                        break;
                                    }
                                }

                                recylerListItems.add(new TimetableListItem(
                                        document.getString("Id"),
                                        StringUtils.convertTo12Hr(document.getString("StartTime")),
                                        StringUtils.convertTo12Hr(document.getString("StopTime")),
                                        StringUtils.capitalizeText(currentCourse.getTitle()),
                                        currentCourse.getCode().toUpperCase(),
                                        currentCourse.getLevelId()
                                ));
                                Log.v("recylerTest", document.getString("Id"));
                                mRecyclerAdapter.notifyItemInserted(recylerListItems.size());
                            }
                        }
                    }
                });
    }

    private void configureRecyclerView() {
        mRecyclerView = findViewById(R.id.recview_lecturer_timetable);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(LecturerDashboardActivity.this);
        mRecyclerAdapter = new TimetableListAdapter(recylerListItems, true, LecturerDashboardActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);


        mRecyclerAdapter.setOnItemClickListener(new TimetableListAdapter.OnItemClickListener() {
            @Override
            public void onAlarmClick(int position) {
                Toast.makeText(LecturerDashboardActivity.this, "Setting alarm for " + recylerListItems.get(position).getCourseTitle(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LecturerDashboardActivity.this, ReminderBroadcast.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(LecturerDashboardActivity.this, 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                SharedPreferences sh = getSharedPreferences("TimetableSharedPref", MODE_PRIVATE);

                boolean isSet = sh.getBoolean(recylerListItems.get(position).getId(), false);

                if (!isSet) {
                    intent.putExtra("message", "Time for " + recylerListItems.get(position).getCourseCode() + " in Computer Science");
                    setTimetableNotification(alarmManager, pendingIntent);
                    sharedprefEditor.putBoolean(recylerListItems.get(position).getId(), true);
                } else {
                    cancelTimetableNotification(alarmManager, pendingIntent);
                    sharedprefEditor.remove(recylerListItems.get(position).getId());
                }
                sharedprefEditor.commit();
            }

            @Override
            public void onItemClick(int position) {
                Toast.makeText(LecturerDashboardActivity.this, recylerListItems.get(position).getCourseTitle(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onDeleteClick(int position) {

            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TimetableReminderChannel";
            String description = "Channel for Timetable Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("notifyTimetable", name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setTimetableNotification(AlarmManager alarmManager, PendingIntent pendingIntent) {

//        StringUtils.NotificationString = "Time for " + recylerListItems.get(position).getCourseCode() + " in Computer Science";

        long timeAtButtonClick = System.currentTimeMillis();

        long tenSeconds = 1000 * 10;

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSeconds, pendingIntent);
    }

    private void cancelTimetableNotification(AlarmManager alarmManager, PendingIntent pendingIntent) {
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lecturer_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}