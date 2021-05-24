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
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class LecturerFragment extends Fragment {

    Button btnAddNewLecturer;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<GeneralListItem> listItems = new ArrayList<>();

    RecyclerView mRecyclerView;

    GeneralListAdapter mRecyclerAdapter;

    RecyclerView.LayoutManager mLayoutManager;

    public LecturerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lecturer, container, false);

        btnAddNewLecturer = (Button) view.findViewById(R.id.btn_add_new_lecturer);

        btnAddNewLecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewLecturer();
            }
        });


        configureRecyclerView(view);

        getAllLecturers();

        return view;
    }

    private void configureRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recview_lecturer);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerAdapter = new GeneralListAdapter(listItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        mRecyclerAdapter.setOnItemClickListener(new GeneralListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), listItems.get(position).getmText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                Toast.makeText(getContext(), "Deleting " + listItems.get(position).getmText(), Toast.LENGTH_SHORT).show();
                deleteLecturer(position);
            }
        });
    }

    private void addNewLecturer() {
        showCustomDialog();
    }

    private void deleteLecturer(int position) {
        String lecturerFullName = listItems.get(position).getmText();

        String lastName = lecturerFullName.split(" ")[1].trim();

        db.collection("users").whereEqualTo("Lastname", lastName.toLowerCase()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() < 1)
                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                        listItems.remove(position);
                        mRecyclerAdapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), lecturerFullName + " deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "An error occurred 22", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getAllLecturers() {
        db.collection("users").whereEqualTo("Type", "lecturer") .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        listItems.add(new GeneralListItem(document.getString("Firstname") + " " + document.getString("Lastname")));
                        mRecyclerAdapter.notifyItemInserted(listItems.size());
                    }
                } else Log.w("GetLecturer", "Error getting documents.", task.getException());
            }
        });

    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(getContext());
        Random random = new Random();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_lecturer);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

//      input fields
        final TextInputEditText editText_lecturer_firstName = dialog.findViewById(R.id.lecturer_firstname);
        final TextInputEditText editText_lecturer_lastName = dialog.findViewById(R.id.lecturer_lastname);
        final TextInputEditText editText_lecturer_staffId = dialog.findViewById(R.id.lecturer_staffId);

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
                lecturer.put("Firstname", editText_lecturer_firstName.getText().toString().toLowerCase());
                lecturer.put("Lastname", editText_lecturer_lastName.getText().toString().toLowerCase());
                lecturer.put("Staffid", editText_lecturer_staffId.getText().toString().toLowerCase());
                lecturer.put("Password", editText_lecturer_lastName.getText().toString().toUpperCase());
                lecturer.put("Type", "lecturer");

                db.collection("users")
                        .add(lecturer)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("LecturerAdd", editText_lecturer_firstName.getText().toString() + " added successfully ");
                                listItems.add(new GeneralListItem(editText_lecturer_firstName.getText().toString() + " " +editText_lecturer_lastName.getText().toString() ));
                                mRecyclerAdapter.notifyItemInserted(listItems.size());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("LecturerAdd", "Error adding document", e);
                            }
                        });

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }
}