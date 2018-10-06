package mirabbas.smartschedule.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mirabbas.smartschedule.R;
import mirabbas.smartschedule.Registration;
import mirabbas.smartschedule.Student;

/**
 * Created by о on 12.02.2018.
 */

public class StudentRegistrationActivity extends BaseActivity implements Registration {

    EditText mNameStudent;
    EditText mLastNameStudent;
    EditText mPatronymicStudent;
    EditText mEmailStudent;
    EditText mPasswordStudent;
    RadioButton mManStudent;
    RadioButton mWomanStudent;
    EditText mAgeStudent;
    EditText mPhoneStudent;
    TextView textViewGroup;
    EditText mHeadingStudent;
    EditText mCourseStudent;
    RadioButton mFullTimeStudent;
    RadioButton mExtramuralStudent;
    RadioButton mExtramuralFullStudent;
    RadioButton mBaccalaureate;
    RadioButton mMagistracy;
    RadioButton mSpecialty;
    RadioButton mYesDriverLicense;
    RadioButton mNoDriverLicense;
    EditText mInterestStudent;
    EditText mWorkPlaceStudent;
    EditText mAchievementsStudent;
    EditText mAddressStudent;
    Button mConfirmButton;

    Spinner groupSpinner;
    Spinner facultetSpinner;
    Spinner cafedraSpinner;
    Spinner mCountStudentsSpinner;

    ArrayAdapter<String> adapter;


    String userEmail;
    String userPassword;

    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPreferences;
    DatabaseReference mDatabase;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_registration_activity);

        mNameStudent = (EditText) findViewById(R.id.name_student);
        mLastNameStudent = (EditText) findViewById(R.id.lastname_student);
        mPatronymicStudent = (EditText) findViewById(R.id.patronymic_student);
        mEmailStudent = (EditText) findViewById(R.id.email_student);
        mPasswordStudent = (EditText) findViewById(R.id.password_student);
        mPhoneStudent = (EditText) findViewById(R.id.phone_student);
        mAgeStudent = (EditText) findViewById(R.id.age_student);
        textViewGroup = (TextView) findViewById(R.id.tv_group);

        mHeadingStudent = (EditText) findViewById(R.id.heading_student);
        mCourseStudent = (EditText) findViewById(R.id.course_student);
        mInterestStudent = (EditText) findViewById(R.id.interests_student);
        mWorkPlaceStudent = (EditText) findViewById(R.id.work_place_student);
        mAchievementsStudent = (EditText) findViewById(R.id.professional_student);
        mAddressStudent = (EditText) findViewById(R.id.address_student);


        mConfirmButton = (Button) findViewById(R.id.confirm_student);
        mManStudent = (RadioButton) findViewById(R.id.man_radio_student);
        mWomanStudent = (RadioButton) findViewById(R.id.woman_radio_student);
        mFullTimeStudent = (RadioButton) findViewById(R.id.full_time_student);
        mExtramuralStudent = (RadioButton) findViewById(R.id.extramural_student);
        mExtramuralFullStudent = (RadioButton) findViewById(R.id.extramural_full_student);
        mBaccalaureate = (RadioButton) findViewById(R.id.baccalaureate);
        mMagistracy = (RadioButton) findViewById(R.id.magistracy);
        mSpecialty = (RadioButton) findViewById(R.id.specialty);
        mYesDriverLicense = (RadioButton) findViewById(R.id.yes_license_student);
        mNoDriverLicense = (RadioButton) findViewById(R.id.no_license_student);


        groupSpinner = (Spinner) findViewById(R.id.group_spinner);
        cafedraSpinner = (Spinner) findViewById(R.id.cafedra_student_spinner);
        facultetSpinner = (Spinner) findViewById(R.id.facultet_student_spinner);
        mCountStudentsSpinner = (Spinner) findViewById(R.id.count_students_spinner);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = null;

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cafedraSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        facultetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);

                switch (i){
                    case 0:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.its_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                    case 1:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.irit_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                    case 2:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.iptm_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                    case 3:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.iyaetf_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                    case 4:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.ineu_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                    case 5:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.inel_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                    case 6:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.ifhtm_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                    case 7:
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.ochno_zaochno_cafedres));
                        cafedraSpinner.setAdapter(adapter);
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCountStudentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidate()) {
                    if(mPasswordStudent.getText().toString().length() >= 6) {
                        createAccount(mEmailStudent.getText().toString(), mPasswordStudent.getText().toString());
                    }
                    else  Toast.makeText(StudentRegistrationActivity.this, R.string.min_passwd_length,
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(StudentRegistrationActivity.this, R.string.warning_fill_all_fields,
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean isValidate() {
        return !mNameStudent.getText().toString().equals("") && !mLastNameStudent.getText().toString().equals("") &&
                !mPatronymicStudent.getText().toString().equals("")
                && !mPhoneStudent.getText().toString().equals("")
                && !mEmailStudent.getText().toString().equals("") && !mPasswordStudent.getText().toString().equals("")
                && !mAgeStudent.getText().toString().equals("")
                && !mHeadingStudent.getText().toString().equals("") && !mCourseStudent.getText().toString().equals("")
                && (mManStudent.isChecked() || mWomanStudent.isChecked()) && (mFullTimeStudent.isChecked() || mExtramuralStudent.isChecked());
    }

    @Override
    public void createAccount(final String email, final String password) {

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(task.isSuccessful()){
                        Toast.makeText(StudentRegistrationActivity.this, "Регистрация прошла успешно!",
                                Toast.LENGTH_SHORT).show();
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mUser = mAuth.getCurrentUser();
                                    Toast.makeText(StudentRegistrationActivity.this, "Вы вошли успешно под " + mUser.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                    if(mUser != null) {
                                        pushAdditionalInfo(mUser);
                                    }
                                    else {
                                        Toast.makeText(StudentRegistrationActivity.this, "Не удалось выполнить вход!",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    hideProgressDialog();
                                    Intent intent = new Intent(StudentRegistrationActivity.this, MainStudentActivity.class);
                                    intent.putExtra("EMAIL", email);
                                    intent.putExtra("PASSWORD", password);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                    else Toast.makeText(StudentRegistrationActivity.this, "Не удалось зарегистрироваться!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void pushAdditionalInfo(FirebaseUser user){

            Toast.makeText(StudentRegistrationActivity.this, user.getEmail(),
                    Toast.LENGTH_SHORT).show();

            Student student = new Student();
            student.setName(mNameStudent.getText().toString());
            student.setLastName(mLastNameStudent.getText().toString());
            student.setPatronymic(mPatronymicStudent.getText().toString());
            student.setPhoneStudent(mPhoneStudent.getText().toString());
            student.setAgeStudent(mAgeStudent.getText().toString());
            student.setGroupStudent(groupSpinner.getSelectedItem().toString());
            student.setFacultetStudent(facultetSpinner.getSelectedItem().toString());
            student.setCafedraStudent(cafedraSpinner.getSelectedItem().toString());
            student.setHeadingStudent(mHeadingStudent.getText().toString());
            student.setCourseStudent(mCourseStudent.getText().toString());
            student.setCountStudents(mCountStudentsSpinner.getSelectedItem().toString());

            if (!mInterestStudent.getText().toString().equals(""))
                student.setInterestStudent(mInterestStudent.getText().toString());
            if (!mWorkPlaceStudent.getText().toString().equals(""))
                student.setWorkPlaceStudent(mWorkPlaceStudent.getText().toString());
            if (!mAchievementsStudent.getText().toString().equals(""))
                student.setAchievementsStudent(mAchievementsStudent.getText().toString());
            if (!mAddressStudent.getText().toString().equals(""))
                student.setAddressStudent(mAddressStudent.getText().toString());

            if (mManStudent.isChecked())
                student.setGenderStudent((String) mManStudent.getText());
            if (mWomanStudent.isChecked())
                student.setGenderStudent((String) mWomanStudent.getText());
            if (mFullTimeStudent.isChecked())
                student.setFormLearning((String) mFullTimeStudent.getText());
            if (mExtramuralStudent.isChecked())
                student.setFormLearning((String) mExtramuralStudent.getText());
        if(mExtramuralFullStudent.isChecked()) student.setFormLearning((String) mExtramuralFullStudent.getText());
            if (mBaccalaureate.isChecked())
            student.setFormLearningCourse((String) mBaccalaureate.getText());
        if (mMagistracy.isChecked())
            student.setFormLearningCourse((String) mMagistracy.getText());
        if (mSpecialty.isChecked())
            student.setFormLearningCourse((String) mSpecialty.getText());

        if(mYesDriverLicense.isChecked()) student.setDriverLicense(true);
        if(mNoDriverLicense.isChecked()) student.setDriverLicense(false);


            mDatabase.child("students").child(mUser.getUid()).push().setValue(student);

    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.signOut();
    }
}