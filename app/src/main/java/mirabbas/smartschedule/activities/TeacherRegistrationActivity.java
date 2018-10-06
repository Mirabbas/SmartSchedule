package mirabbas.smartschedule.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import mirabbas.smartschedule.Teacher;

/**
 * Created by о on 12.02.2018.
 */

public class TeacherRegistrationActivity extends BaseActivity implements Registration {

    EditText mNameTeacher;
    EditText mLastNameTeacher;
    EditText mPatronymicTeacher;
    EditText mEmailTeacher;
    EditText mPasswordTeacher;
    RadioButton mManTeacher;
    RadioButton mWomanTeacher;
    EditText mAgeTeacher;
    EditText mPhoneTeacher;
    EditText mPositionTeacher;
    EditText mDisciplineTeacher;
    EditText mExperienceTeacher;
    EditText mInterestTeacher;
    EditText mWorkPlaceTeacher;
    EditText mAchievementsTeacher;
    EditText mAddressTeacher;
    CheckBox mCheckLectureBoard;
    CheckBox mCheckLectureProjector;
    CheckBox mCheckPracticeBoard;
    CheckBox mCheckPracticeProjector;
    CheckBox mCheckPracticeComputer;
    CheckBox mCheckLabsBoard;
    CheckBox mCheckLabsProjector;
    CheckBox mCheckLabsComputer;

    TextView mTvStanding;


    RadioButton mYesDriverLicense;
    RadioButton mNoDriverLicense;
    RadioButton mYesJobPlace;
    RadioButton mNoJobPlace;

    RadioGroup radioGroupJobPlace;

    EditText mVak;
    EditText mScopus;
    EditText mRinc;
    EditText mWebScience;

    Button mConfirmButton;
    ImageView mAddDisciplineImg;
    ImageView mAddProfAreaImg;

    Spinner facultetSpinner;
    Spinner cafedraSpinner;
    Spinner standingSpinner;

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
        setContentView(R.layout.teacher_registration_activity);

        mNameTeacher = (EditText) findViewById(R.id.name_teacher);
        mLastNameTeacher = (EditText) findViewById(R.id.lastname_teacher);
        mPatronymicTeacher = (EditText) findViewById(R.id.patronymic_teacher);
        mEmailTeacher = (EditText) findViewById(R.id.email_teacher) ;
        mPasswordTeacher = (EditText) findViewById(R.id.password_teacher);
        mPhoneTeacher = (EditText) findViewById(R.id.phone_teacher);
        mAgeTeacher = (EditText) findViewById(R.id.age_teacher);
        mPositionTeacher = (EditText) findViewById(R.id.position_teacher);
        mDisciplineTeacher = (EditText) findViewById(R.id.discipline_teacher);
        mExperienceTeacher = (EditText) findViewById(R.id.experience_teacher);
        mInterestTeacher = (EditText) findViewById(R.id.interests_teacher);
        mWorkPlaceTeacher = (EditText) findViewById(R.id.work_place_teacher);
        mAchievementsTeacher = (EditText) findViewById(R.id.professional_teacher);
        mAddressTeacher = (EditText) findViewById(R.id.address_teacher);

        mConfirmButton = (Button) findViewById(R.id.confirm_teacher);

        mAddDisciplineImg = (ImageView) findViewById(R.id.add_discipline_img);
        mAddProfAreaImg = (ImageView) findViewById(R.id.add_prof_area_img);

        mTvStanding = (TextView) findViewById(R.id.tv_standing);
        radioGroupJobPlace = (RadioGroup) findViewById(R.id.teacher_has_job_place_radio);

        cafedraSpinner = (Spinner) findViewById(R.id.cafedra_teacher_spinner);
        facultetSpinner = (Spinner) findViewById(R.id.facultet_teacher_spinner);
        standingSpinner = (Spinner) findViewById(R.id.job_standing_teacher_spinner);

        mManTeacher = (RadioButton) findViewById(R.id.man_teacher);
        mWomanTeacher = (RadioButton) findViewById(R.id.woman_teacher);
        mCheckLectureBoard = (CheckBox) findViewById(R.id.lecture_check_1);
        mCheckLectureProjector = (CheckBox) findViewById(R.id.lecture_check_2);
        mCheckPracticeBoard = (CheckBox) findViewById(R.id.practice_check_1);
        mCheckPracticeProjector = (CheckBox) findViewById(R.id.practice_check_2);
        mCheckPracticeComputer = (CheckBox) findViewById(R.id.practice_check_3);
        mCheckLabsBoard = (CheckBox) findViewById(R.id.labs_check_1);
        mCheckLabsProjector = (CheckBox) findViewById(R.id.labs_check_2);
        mCheckLabsComputer = (CheckBox) findViewById(R.id.labs_check_3);

        mYesDriverLicense = (RadioButton) findViewById(R.id.yes_license_teacher);
        mNoDriverLicense = (RadioButton) findViewById(R.id.no_license_teacher);
        mYesJobPlace = (RadioButton) findViewById(R.id.yes_job_place_teacher);
        mNoJobPlace = (RadioButton) findViewById(R.id.no_job_place_teacher);

        mVak = (EditText) findViewById(R.id.vak);
        mScopus = (EditText) findViewById(R.id.scopus);
        mRinc = (EditText) findViewById(R.id.rinc);
        mWebScience = (EditText) findViewById(R.id.web_of_science);

        initJobPlaceOptions();

        radioGroupJobPlace.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.yes_job_place_teacher:

                        mWorkPlaceTeacher.setEnabled(true);
                        mWorkPlaceTeacher.setBackground(getResources().getDrawable(R.drawable.edit_text_style));
                        mTvStanding.setTextColor(getResources().getColor(R.color.white));
                        standingSpinner.setEnabled(true);
                        break;

                    case R.id.no_job_place_teacher:
                        mWorkPlaceTeacher.setEnabled(false);
                        mWorkPlaceTeacher.setBackground(getResources().getDrawable(R.drawable.edit_text_not_enable_style));
                        mTvStanding.setTextColor(getResources().getColor(R.color.light_red));
                        standingSpinner.setEnabled(false);
                        break;
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = null;

        setTeacherData();

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidate()) {
                    if(mPasswordTeacher.getText().toString().length() >= 6) {
                        createAccount(mEmailTeacher.getText().toString(), mPasswordTeacher.getText().toString());
                    }
                    else  Toast.makeText(TeacherRegistrationActivity.this, R.string.min_passwd_length,
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(TeacherRegistrationActivity.this, R.string.warning_fill_all_fields,
                            Toast.LENGTH_SHORT).show();
            }
        });

        mAddDisciplineImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = mDisciplineTeacher.getText().toString();
                if(!temp.equals("")){
                    mDisciplineTeacher.setText(temp + ";\n");
                    mDisciplineTeacher.setSelection(mDisciplineTeacher.getText().length());
                }
            }
        });

        mAddProfAreaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = mAchievementsTeacher.getText().toString();
                if(!temp.equals("")){
                    mAchievementsTeacher.setText(temp + ";\n");
                    mAchievementsTeacher.setSelection(mAchievementsTeacher.getText().length());
                }
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

        standingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean isValidate() {
        return !mNameTeacher.getText().toString().equals("") && !mLastNameTeacher.getText().toString().equals("") &&
                !mPatronymicTeacher.getText().toString().equals("")
                && !mPhoneTeacher.getText().toString().equals("")
                && !mPasswordTeacher.getText().toString().equals("")
                && !mAgeTeacher.getText().toString().equals("") && !mPositionTeacher.getText().toString().equals("")
                && !mDisciplineTeacher.getText().toString().equals("")
                && (mManTeacher.isChecked() || mWomanTeacher.isChecked());
    }

    @Override
    public void createAccount(final String email, final String password) {

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(TeacherRegistrationActivity.this, "Регистрация прошла успешно!",
                            Toast.LENGTH_SHORT).show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mUser = mAuth.getCurrentUser();
                                Toast.makeText(TeacherRegistrationActivity.this, "Вы вошли успешно под " + mUser.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                if(mUser != null) {
                                    pushAdditionalInfo(mUser);
                                }
                                else {
                                    Toast.makeText(TeacherRegistrationActivity.this, "Не удалось выполнить вход!",
                                            Toast.LENGTH_SHORT).show();
                                }

                                hideProgressDialog();
                            }
                        }
                    });
                }
                else Toast.makeText(TeacherRegistrationActivity.this, "Не удалось зарегистрироваться!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void pushAdditionalInfo(FirebaseUser user){

        //Toast.makeText(TeacherRegistrationActivity.this, user.getEmail(),
         //       Toast.LENGTH_SHORT).show();


        Teacher teacher = new Teacher();
        teacher.setName(mNameTeacher.getText().toString());
        teacher.setLastName(mLastNameTeacher.getText().toString());
        teacher.setPatronymic(mPatronymicTeacher.getText().toString());
        teacher.setAgeTeacher(mAgeTeacher.getText().toString());
        teacher.setPhoneTeacher(mPhoneTeacher.getText().toString());
        teacher.setPositionTeacher(mPositionTeacher.getText().toString());
        teacher.setCafedraTeacher(cafedraSpinner.getSelectedItem().toString());
        teacher.setFacultetTeacher(facultetSpinner.getSelectedItem().toString());
        teacher.setExperienceTeacher(mExperienceTeacher.getText().toString());
        teacher.setDisciplinesTeacher(mDisciplineTeacher.getText().toString());

        if (!mInterestTeacher.getText().toString().equals(""))
            teacher.setInterestTeacher(mInterestTeacher.getText().toString());
        if (!mWorkPlaceTeacher.getText().toString().equals(""))
            teacher.setWorkPlaceTeacher(mWorkPlaceTeacher.getText().toString());
        if (!mAchievementsTeacher.getText().toString().equals(""))
            teacher.setAchievementsTeacher(mAchievementsTeacher.getText().toString());
        if (!mAddressTeacher.getText().toString().equals(""))
            teacher.setAddressTeacher(mAddressTeacher.getText().toString());

        if(!mVak.getText().toString().equals("")) teacher.setVak(mVak.getText().toString());
        if(!mScopus.getText().toString().equals("")) teacher.setScopus(mScopus.getText().toString());
        if(!mRinc.getText().toString().equals("")) teacher.setRinc(mRinc.getText().toString());
        if(!mWebScience.getText().toString().equals("")) teacher.setWebOfScience(mWebScience.getText().toString());

        if(mYesDriverLicense.isChecked()) teacher.setDriverLicense(true);
        if(mNoDriverLicense.isChecked()) teacher.setDriverLicense(false);

        if(mCheckLectureBoard.isChecked()) {
            teacher.setBoardForLecture(true);
        }
        else teacher.setBoardForLecture(false);

        if(mCheckLectureProjector.isChecked()) {
            teacher.setProjectorForLection(true);
        }
        else teacher.setProjectorForLection(false);

        if(mCheckPracticeBoard.isChecked()) {
            teacher.setBoardForPractice(true);
        }
        else teacher.setBoardForPractice(false);

        if(mCheckPracticeProjector.isChecked()) {
            teacher.setProjectorForPractice(true);
        }
        else teacher.setProjectorForPractice(false);

        if(mCheckPracticeComputer.isChecked()) {
            teacher.setComputerForPractice(true);
        }
        else teacher.setComputerForPractice(false);

        if(mCheckLabsBoard.isChecked()) {
            teacher.setBoardForLabs(true);
        }
        else teacher.setBoardForLabs(false);

        if(mCheckLabsProjector.isChecked()) {
            teacher.setProjectorForLabs(true);
        }
        else teacher.setProjectorForLabs(false);

        if(mCheckLabsComputer.isChecked()) {
            teacher.setComputerForLabs(true);
        }
        else teacher.setComputerForLabs(false);

        if(mYesJobPlace.isChecked()) teacher.setStanding(standingSpinner.getSelectedItem().toString());

        mDatabase.child("teachers").child(mUser.getUid()).push().setValue(teacher);

    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.signOut();
    }

    public void initJobPlaceOptions(){
        mWorkPlaceTeacher.setEnabled(false);
        mWorkPlaceTeacher.setBackground(getResources().getDrawable(R.drawable.edit_text_not_enable_style));
        mTvStanding.setTextColor(getResources().getColor(R.color.light_red));
        standingSpinner.setEnabled(false);

    }

    public void setTeacherData(){
        mNameTeacher.setText(getIntent().getStringExtra("NAME"));
        mLastNameTeacher.setText(getIntent().getStringExtra("LAST_NAME"));
        mPatronymicTeacher.setText(getIntent().getStringExtra("PATRONYMIC"));
        mEmailTeacher.setText(getIntent().getStringExtra("EMAIL"));
        mNameTeacher.setEnabled(false);
        mLastNameTeacher.setEnabled(false);
        mPatronymicTeacher.setEnabled(false);
        mEmailTeacher.setEnabled(false);
        mNameTeacher.setTextColor(getResources().getColor(R.color.black));
        mLastNameTeacher.setTextColor(getResources().getColor(R.color.black));
        mPatronymicTeacher.setTextColor(getResources().getColor(R.color.black));
        mEmailTeacher.setTextColor(getResources().getColor(R.color.black));
    }
}
