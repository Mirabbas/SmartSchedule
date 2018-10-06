package mirabbas.smartschedule.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mirabbas.smartschedule.R;
import mirabbas.smartschedule.Teacher;
import mirabbas.smartschedule.TeacherData;

/**
 * Created by о on 15.02.2018.
 */

public class LoginActivity extends BaseActivity {

    EditText mEmail;
    EditText mPassword;
    Button mInputButton;
    Button mRegButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mInputButton = (Button) findViewById(R.id.login_button);
        mRegButton = (Button) findViewById(R.id.reg_button);

        mAuth = FirebaseAuth.getInstance();
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, StudentTeacherActivity.class);
                startActivity(intent);
            }
        });


        mInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mEmail.getText().toString().equals("") && !mPassword.getText().toString().equals("")) {
                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                reference.child("teacher-data").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean isTeacher = false;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            TeacherData teacherData = snapshot.getValue(TeacherData.class);
                                            if(teacherData.getEmail().equals(mEmail.getText().toString())){
                                                isTeacher = true;
                                                break;
                                            }
                                        }

                                        if(isTeacher){
                                            Intent intent = new Intent(LoginActivity.this, MainTeacherActivity.class);
                                            intent.putExtra("EMAIL", mEmail.getText().toString());
                                            intent.putExtra("PASSWORD", mPassword.getText().toString());
                                            startActivity(intent);
                                        }
                                        else {
                                            Intent intent = new Intent(LoginActivity.this, MainStudentActivity.class);
                                            intent.putExtra("EMAIL", mEmail.getText().toString());
                                            intent.putExtra("PASSWORD", mPassword.getText().toString());
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else
                                Toast.makeText(LoginActivity.this, "Проверьте правильность введенных данных!",
                                        Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else Toast.makeText(LoginActivity.this, "Проверьте правильность введенных данных!",
                        Toast.LENGTH_SHORT).show();
            }


        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }
}
