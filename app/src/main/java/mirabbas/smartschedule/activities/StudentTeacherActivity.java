package mirabbas.smartschedule.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import mirabbas.smartschedule.R;

import static mirabbas.smartschedule.SharedPreferencesConstants.REG_SETTINGS;

/**
 * Created by о on 12.02.2018.
 */

public class StudentTeacherActivity extends Activity {


    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_teacher_activity);

        View studLayout = findViewById(R.id.stud_layout);
        View teachLayout = findViewById(R.id.teach_layout);

        final String userEmail = getIntent().getStringExtra("EMAIL");
        final String userPassword = getIntent().getStringExtra("PASSWORD");

        mSharedPreferences = getSharedPreferences(REG_SETTINGS, Context.MODE_PRIVATE);

        studLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentTeacherActivity.this, StudentRegistrationActivity.class);
                intent.putExtra("EMAIL", userEmail);
                intent.putExtra("PASSWORD", userPassword);
                startActivity(intent);

            }
        });

        teachLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentTeacherActivity.this, TeacherEmailActivity.class);
                intent.putExtra("EMAIL", userEmail);
                intent.putExtra("PASSWORD", userPassword);
                startActivity(intent);

            }
        });


    }

}
