package com.ultraplex.lecturernotificationsystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Toolbar toolbar;
    private CoordinatorLayout mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mainContent = (CoordinatorLayout) findViewById(R.id.main_content);

        initToolbar();
        initNavigationMenu ();
        setPageMainContent("Home");
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Drawer Simple Light");
    }

    private void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
                actionBar.setTitle(item.getTitle());
                setPageMainContent(item.getTitle().toString());
                drawer.closeDrawers();
                return true;
            }
        });
    }



    private void setPageMainContent(String pageTitle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);

        // Replace whatever is in the fragment_container view with this fragment
        switch (pageTitle){
            case "Home":
                transaction.replace(R.id.main_content, HomeFragment.class, null);
                break;
            case "Departments":
                transaction.replace(R.id.main_content, DepartmentFragment.class, null);
                break;
            case "Levels":
                transaction.replace(R.id.main_content, LevelFragment.class, null);
                break;
            case "Lecturers":
                transaction.replace(R.id.main_content, LecturerFragment.class, null);
                break;
            case "Courses":
                transaction.replace(R.id.main_content, CourseFragment.class, null);
                break;
            case "Timetable":
                transaction.replace(R.id.main_content, CourseFragment.class, null);
                break;
            default:
                break;
        }

        // Commit the transaction
        transaction.commit();
    }
}