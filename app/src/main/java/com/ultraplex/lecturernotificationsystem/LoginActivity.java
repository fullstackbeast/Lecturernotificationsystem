package com.ultraplex.lecturernotificationsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    Button Btn_Login;
    TextInputEditText EditText_StaffId;
    TextInputEditText EditText_Password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText_StaffId = (TextInputEditText) findViewById(R.id.editText_staffId);
        EditText_Password = (TextInputEditText) findViewById(R.id.editText_password);
        Btn_Login = (Button) findViewById(R.id.btn_loginButton);

        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputStaffId = EditText_StaffId.getText().toString().trim();
                String inputPassword = EditText_Password.getText().toString().trim();

                db.collection("users").whereEqualTo("Staffid", inputStaffId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() < 1) Toast.makeText(LoginActivity.this, "Invalid Staff Id", Toast.LENGTH_SHORT).show();

                            else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getString("Password").equals(inputPassword)){
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        switchToDashboard(document.getString("Type"));
                                    }
                                    else Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else Log.w("LoginActivity", "Error getting documents.", task.getException());
                    }
                });
            }
        });
    }

    private void switchToDashboard(String staffType){
        switch (staffType){
            case "Admin":
                Toast.makeText(LoginActivity.this, "Logging in as admin", Toast.LENGTH_SHORT).show();
                break;
            case "Lecturer":
                Toast.makeText(LoginActivity.this, "Logging in as lecturer", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(LoginActivity.this, "Error signing you in", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}