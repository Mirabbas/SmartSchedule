package mirabbas.smartschedule.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import mirabbas.smartschedule.R;
import mirabbas.smartschedule.Teacher;
import mirabbas.smartschedule.TeacherData;

/**
 * Created by о on 14.03.2018.
 */

public class TeacherEmailActivity extends AppCompatActivity {

    EditText mTeacherEmail;
    Button mSendButton;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_email_activity);

        mTeacherEmail = (EditText) findViewById(R.id.correct_email_teacher);
        mSendButton = (Button) findViewById(R.id.send_button);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("teacher-data");

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean checked = false;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            TeacherData teacherData = snapshot.getValue(TeacherData.class);
                            if(teacherData.getEmail().equals(mTeacherEmail.getText().toString())){
                                Intent intent = new Intent(TeacherEmailActivity.this, TeacherRegistrationActivity.class);
                                intent.putExtra("EMAIL", teacherData.getEmail());
                                intent.putExtra("NAME", teacherData.getName());
                                intent.putExtra("LAST_NAME", teacherData.getLastName());
                                intent.putExtra("PATRONYMIC", teacherData.getPatronymic());
                                startActivity(intent);
                                checked = true;
                                break;
                            }
                        }
                        if(!checked) Toast.makeText(TeacherEmailActivity.this, "Введенный e-mail не найден!",
                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });





    }
}
