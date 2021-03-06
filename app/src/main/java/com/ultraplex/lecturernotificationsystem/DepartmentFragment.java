package com.ultraplex.lecturernotificationsystem;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class DepartmentFragment extends Fragment {

    Button btnAddDepartment;

    View generalView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<GeneralListItem> listItems = new ArrayList<>();

    RecyclerView mRecyclerView;

    GeneralListAdapter mRecyclerAdapter;

    RecyclerView.LayoutManager mLayoutManager;


    public DepartmentFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_department, container, false);

        btnAddDepartment = (Button) view.findViewById(R.id.btn_add_new_department);

        configureRecyclerView(view);

        btnAddDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDept();
            }
        });

        getAllDepts();

        generalView = view;

        return view;
    }

    private void configureRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recview_department);
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
                Toast.makeText(getContext(), "Deleting" + listItems.get(position).getmText(), Toast.LENGTH_SHORT).show();
                deleteDept(position);
            }
        });
    }

    private void getAllDepts() {
        db.collection("departments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.v("GetDepts", document.getString("Name"));
                        listItems.add(new GeneralListItem(document.getString("Name")));
                        mRecyclerAdapter.notifyItemInserted(listItems.size());
                    }


                } else Log.w("GetDepts", "Error getting documents.", task.getException());
            }
        });


    }

    private void addNewDept() {
        showCustomDialog();
    }

    private void deleteDept(int position) {

        String departmentText = listItems.get(position).getmText();

        db.collection("departments").whereEqualTo("Name", departmentText).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().size() < 1)
                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                        listItems.remove(position);
                        mRecyclerAdapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), departmentText + " deleted successfully", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(getContext());
        Random random = new Random();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_department);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextInputEditText editText_deptname = (TextInputEditText) dialog.findViewById(R.id.dept_name);


        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getContext(), editText_deptname.getText(), Toast.LENGTH_SHORT).show();

                Map<String, Object> department = new HashMap<>();
                String firstString = String.valueOf(random.nextInt(1000));
                String secondString = String.valueOf(random.nextInt(1000));

                department.put("Id", firstString + secondString);
                department.put("Name", editText_deptname.getText().toString().toLowerCase());

                db.collection("departments")
                        .add(department)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("DepartmentAdd", editText_deptname.getText().toString() + " added successfully ");
                                listItems.add(new GeneralListItem(editText_deptname.getText().toString()));
                                mRecyclerAdapter.notifyItemInserted(listItems.size());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("DepartmentAdd", "Error adding document", e);
                            }
                        });


                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}