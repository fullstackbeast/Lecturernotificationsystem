package com.ultraplex.lecturernotificationsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class LevelFragment extends Fragment {

    Button btnAddNewLevel;

    public LevelFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        btnAddNewLevel = (Button) fi
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_level, container, false);
        btnAddNewLevel = (Button) view.findViewById(R.id.btn_add_new_level);

        btnAddNewLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Adding level", Toast.LENGTH_SHORT).show();
            }
        });


        return view;

    }
}