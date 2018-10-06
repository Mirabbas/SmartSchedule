package mirabbas.smartschedule.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mirabbas.smartschedule.Group;
import mirabbas.smartschedule.Pair;
import mirabbas.smartschedule.R;
import mirabbas.smartschedule.StartPairNotificationService;
import mirabbas.smartschedule.Student;
import mirabbas.smartschedule.Teacher;
import mirabbas.smartschedule.TeacherData;

public class MainStudentActivity extends AppCompatActivity {

    TextView mTvGroupName;
    TextView mTvCurrentDate;
    TextView mTvTypeWeek;
    TextView mTvNumberWeek;
    TextView mTvNamePair;
    TextView mTvDateDayPair;
    TextView mTvLectureHall;
    TextView mTvStartTimePair;
    TextView mTvTeacherName;
    TextView mTvTypePair;
    TextView mTvNews;
    Button mBtnSchedule;
    Button mBtnChat;
    ImageButton mBtnSendToTeacher;

    String mGroupKey;
    String mNameLastNameUser;

    Date mDate;
    String mDayOfWeek;
    int mNumWeek;
    int mCount;

    SimpleDateFormat mSdfTime;
    SimpleDateFormat mSdfDate;

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mGroupReference;

    ForegroundColorSpan style;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_student_activity);

        mTvGroupName = (TextView) findViewById(R.id.tv_group_name);
        mTvCurrentDate = (TextView) findViewById(R.id.tv_date_student);
        mTvTypeWeek = (TextView) findViewById(R.id.tv_type_week_student);
        mTvNumberWeek = (TextView) findViewById(R.id.tv_number_week_student);
        mTvNamePair = (TextView) findViewById(R.id.tv_name_pair);
        mTvDateDayPair = (TextView) findViewById(R.id.tv_date_day_pair);
        mTvLectureHall = (TextView) findViewById(R.id.tv_lecture_hall);
        mTvStartTimePair = (TextView) findViewById(R.id.tv_start_time_pair);
        mTvTeacherName = (TextView) findViewById(R.id.tv_teacher_name);
        mTvTypePair = (TextView) findViewById(R.id.tv_type_pair);
        mTvNews = (TextView) findViewById(R.id.tv_news_student);
        mBtnSchedule = (Button) findViewById(R.id.schedule_student_button);
        mBtnChat = (Button) findViewById(R.id.chat_group_button);
        mBtnSendToTeacher = (ImageButton) findViewById(R.id.send_to_teacher_button);

        stopService(new Intent(this, StartPairNotificationService.class));

        style = new ForegroundColorSpan(getResources().getColor(R.color.light_yellow));

        mDate = new Date();

        mDayOfWeek = getDayOfWeek();
        mNumWeek = getNumberWeek();
        mCount = 0;

        mSdfTime = new SimpleDateFormat("HH:mm");
        mSdfDate = new SimpleDateFormat("dd.MM.yyyy");

        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        mAuth.signInWithEmailAndPassword(getIntent().getStringExtra("EMAIL"), getIntent().getStringExtra("PASSWORD")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    mUser = mAuth.getCurrentUser();
                    Toast.makeText(MainStudentActivity.this, mUser.getEmail(),
                            Toast.LENGTH_SHORT).show();
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    if (mUser != null) {
                        mDatabase.child("students").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Student student = snapshot.getValue(Student.class);
                                    if (student.getGroupStudent() != null) {
                                        mTvGroupName.setText(student.getGroupStudent());
                                        mNameLastNameUser = student.getName() + " " + student.getLastName();
                                    }
                                }

                                mDatabase = FirebaseDatabase.getInstance().getReference().child("schedule");

                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Log.d("GRRRR", mTvGroupName.getText().toString());

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Group group = snapshot.getValue(Group.class);
                                            if (group.getNameGroup().equals(mTvGroupName.getText())) {
                                                mGroupKey = snapshot.getKey();
                                                break;
                                            }
                                        }
                                        if (mGroupKey != null) {
                                            setPairReference();
                                        } else Log.d("NOOOOO", "NILL");
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else Toast.makeText(MainStudentActivity.this, "Пользователь ноль!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTvCurrentDate.setText(getCurDate() + ", " + mDayOfWeek);
        mTvTypeWeek.setText("Неделя: " + getTypeWeek(mNumWeek));
        mTvNumberWeek.setText("Номер недели: " + Integer.toString(mNumWeek));


        mBtnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainStudentActivity.this, ScheduleActivity.class);
                intent.putExtra("GROUP_KEY", mGroupKey);
                startActivity(intent);
            }
        });

        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainStudentActivity.this, GroupChatActivity.class);
                intent.putExtra("NAME_USER", mNameLastNameUser);
                intent.putExtra("GROUP_KEY", mGroupKey);
                startActivity(intent);
            }
        });

        mBtnSendToTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String teacherName = mTvTeacherName.getText().toString().split(" ")[1];
                Log.d("NAME", teacherName);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("teachers");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean flag = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Teacher teacher = snapshot1.getValue(Teacher.class);
                                if (teacher.getLastName().equals(teacherName)) {
                                    flag = true;
                                }
                            }
                        }
                        if (flag) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("teacher-data");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        TeacherData data = snapshot.getValue(TeacherData.class);
                                        if (data.getLastName().equals(teacherName)) {
                                            String teacherKey = snapshot.getKey();
                                            Intent intent = new Intent(MainStudentActivity.this, GroupTeacherChatActivity.class);
                                            intent.putExtra("GROUP_KEY", mGroupKey);
                                            intent.putExtra("TEACHER_KEY", teacherKey);
                                            intent.putExtra("USER_NAME", mTvGroupName.getText().toString());
                                            startActivity(intent);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else
                            Toast.makeText(MainStudentActivity.this, "Преподаватель не зарегистрирован!",
                                    Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    public String getDayOfWeek() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

        String dayOfTheWeek = sdf.format(mDate);
        return dayOfTheWeek;
    }

    public String getCurDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");
        return dateformat.format(c.getTime());
    }

    public int getNumberWeek() {
        int res = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = simpleDateFormat.parse("22.01.2018 00:00:01");
            date2 = simpleDateFormat.parse("28.01.2018 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long start = date1.getTime();
        long end = date2.getTime();
        long difDate = end - start;

        for (int i = 0; i < 13; i++) {
            if (mDate.getTime() > start && mDate.getTime() < end) {
                res = i + 1;
                break;
            } else {
                start += difDate;
                end += difDate;
            }
        }
        return res;

    }

    public String getTypeWeek(int numWeek) {
        String res = "";
        if (numWeek % 2 == 0) res = "Четная";
        else res = "Нечетная";
        return res;
    }

    public void setPairReference() {
        switch (mDayOfWeek) {
            case "понедельник":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                Log.d("DYA", mGroupKey);
                checkPairFields();
                break;
            case "вторник":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("tuesday");
                checkPairFields();
                break;
            case "среда":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("wednesday");
                checkPairFields();
                break;
            case "четверг":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("thursday");
                checkPairFields();
                break;
            case "пятница":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("friday");
                checkPairFields();
                break;
            case "суббота":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("saturday");
                checkPairFields();
                break;
            case "воскресенье":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                checkPairFields();
                break;
        }
    }

    public void setNextPairReference() {
        switch (mDayOfWeek) {
            case "понедельник":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("tuesday");
                goToNextDay();
                checkPairFields();
                break;
            case "вторник":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("wednesday");
                goToNextDay();
                checkPairFields();
                break;
            case "среда":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("thursday");
                goToNextDay();
                checkPairFields();
                break;
            case "четверг":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("friday");
                goToNextDay();
                checkPairFields();
                break;
            case "пятница":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("saturday");
                goToNextDay();
                checkPairFields();
                break;
            case "суббота":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                goToMonday();
                checkPairFields();
                break;
            case "воскресенье":
                mGroupReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                mNumWeek++;
                mDayOfWeek = getDayOfWeek();
                goToNextDay();
                checkPairFields();
                break;
        }
    }

    public void checkPairFields() {
        mGroupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Date startTime = null;
                Date endTime = null;
                boolean flag = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    SpannableStringBuilder namePair = new SpannableStringBuilder();
                    SpannableStringBuilder dateDayPair = new SpannableStringBuilder();
                    SpannableStringBuilder lectureHall = new SpannableStringBuilder();
                    SpannableStringBuilder teacherName = new SpannableStringBuilder();
                    SpannableStringBuilder typePair = new SpannableStringBuilder();
                    SpannableStringBuilder typeWeek = new SpannableStringBuilder();
                    SpannableStringBuilder numberWeek = new SpannableStringBuilder();
                    SpannableStringBuilder startTimeStr = new SpannableStringBuilder();

                    mCount++;
                    Pair pair = snapshot.getValue(Pair.class);
                    if (pair.getFlag() == 1) {
                        try {

                            startTime = mSdfTime.parse(pair.getStartTime());
                            endTime = mSdfTime.parse(pair.getEndTime());
                            startTime.setDate(mDate.getDate());
                            startTime.setMonth(mDate.getMonth());
                            startTime.setYear(mDate.getYear());
                            endTime.setDate(mDate.getDate());
                            endTime.setMonth(mDate.getMonth());
                            endTime.setYear(mDate.getYear());

                            namePair.append(pair.getPairName());
                            dateDayPair.append(mSdfDate.format(mDate));
                            lectureHall.append(Integer.toString(pair.getLectureHall()));
                            teacherName.append(pair.getTeacher());
                            typePair.append(pair.getTypePair());
                            typeWeek.append(pair.getTypeWeek());
                            numberWeek.append(pair.getNumberWeek());
                            startTimeStr.append(pair.getStartTime());

                            Log.d("START", startTime.toString());
                            Log.d("CUR", mDate.toString());
                            Log.d("END", endTime.toString());


                        } catch (ParseException e) {
                            e.printStackTrace();

                        }

                        if (typeWeek.toString().equals("нч") && mTvTypeWeek.getText().equals("Нечетная")) {

                            if (mDate.getTime() > startTime.getTime() && mDate.getTime() < endTime.getTime()) {
                                flag = true;
                                continue;
                            }

                            if (mDate.getTime() < startTime.getTime() || flag) {
                                namePair.setSpan(style, 0, namePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dateDayPair.setSpan(style, 0, dateDayPair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                lectureHall.setSpan(style, 0, lectureHall.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                startTimeStr.setSpan(style, 0, startTimeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                teacherName.setSpan(style, 0, teacherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mTvNamePair.append(" ");
                                mTvNamePair.append(namePair);
                                mTvDateDayPair.append(" ");
                                mTvDateDayPair.append(dateDayPair);
                                mTvLectureHall.append(" ");
                                mTvLectureHall.append(lectureHall);
                                mTvStartTimePair.append(" ");
                                mTvStartTimePair.append(startTimeStr);
                                mTvTeacherName.append(" ");
                                mTvTeacherName.append(teacherName);
                                mTvTypePair.append(" ");
                                mTvTypePair.append(typePair);
                                break;
                            }
                        } else if (typeWeek.toString().equals("чн") && mTvTypeWeek.getText().equals("Четная")) {

                            if (mDate.getTime() > startTime.getTime() && mDate.getTime() < endTime.getTime()) {
                                flag = true;
                                continue;
                            }

                            if (mDate.getTime() < startTime.getTime() || flag) {
                                namePair.setSpan(style, 0, namePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dateDayPair.setSpan(style, 0, dateDayPair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                lectureHall.setSpan(style, 0, lectureHall.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                startTimeStr.setSpan(style, 0, startTimeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                teacherName.setSpan(style, 0, teacherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mTvNamePair.append(" ");
                                mTvNamePair.append(namePair);
                                mTvDateDayPair.append(" ");
                                mTvDateDayPair.append(dateDayPair);
                                mTvLectureHall.append(" ");
                                mTvLectureHall.append(lectureHall);
                                mTvStartTimePair.append(" ");
                                mTvStartTimePair.append(startTimeStr);
                                mTvTeacherName.append(" ");
                                mTvTeacherName.append(teacherName);
                                mTvTypePair.append(" ");
                                mTvTypePair.append(typePair);
                                break;
                            }
                        } else {

                            if ((mDate.getTime() < startTime.getTime()) || flag) {

                                if (!numberWeek.toString().equals("")) {
                                    if (!numberWeek.toString().contains(",")) {
                                        String[] strNums = numberWeek.toString().split(" ");
                                        boolean tempFlag = false;
                                        int[] nums = new int[strNums.length];
                                        for (int i = 0; i < strNums.length; i++) {
                                            nums[i] = Integer.parseInt(strNums[i]);
                                            if (nums[i] == mNumWeek) {

                                                if (mDate.getTime() < startTime.getTime() || flag) {
                                                    namePair.setSpan(style, 0, namePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    dateDayPair.setSpan(style, 0, dateDayPair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    lectureHall.setSpan(style, 0, lectureHall.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    startTimeStr.setSpan(style, 0, startTimeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    teacherName.setSpan(style, 0, teacherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    mTvNamePair.append(" ");
                                                    mTvNamePair.append(namePair);
                                                    mTvDateDayPair.append(" ");
                                                    mTvDateDayPair.append(dateDayPair);
                                                    mTvLectureHall.append(" ");
                                                    mTvLectureHall.append(lectureHall);
                                                    mTvStartTimePair.append(" ");
                                                    mTvStartTimePair.append(startTimeStr);
                                                    mTvTeacherName.append(" ");
                                                    mTvTeacherName.append(teacherName);
                                                    mTvTypePair.append(" ");
                                                    mTvTypePair.append(typePair);
                                                    tempFlag = true;
                                                    break;
                                                }

                                            }
                                        }
                                        if (tempFlag) break;


                                    } else {
                                        String[] strNums = numberWeek.toString().split(",");
                                        String[] numsOne = strNums[0].split(" ");
                                        String[] numsTwo = strNums[1].split(" ");
                                        int[] intOne = new int[numsOne.length];
                                        int[] intTwo = new int[numsTwo.length];
                                        for (int i = 0; i < numsOne.length; i++) {
                                            intOne[i] = Integer.parseInt(numsOne[i]);
                                        }
                                        for (int i = 0; i < numsTwo.length; i++) {
                                            intTwo[i] = Integer.parseInt(numsTwo[i]);
                                        }

                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                                && !teacherName.toString().contains(",")
                                                && !typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    break;
                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (!namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && !teacherName.toString().contains(",")
                                                && !typePair.toString().contains(",")) {
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    break;
                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    break;

                                                }
                                            }

                                        }

                                        if (!namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                                && teacherName.toString().contains(",")
                                                && !typePair.toString().contains(",")) {
                                            String[] teachers = teacherName.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    break;
                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    break;

                                                }
                                            }
                                        }

                                        if (!namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                                && !teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            String[] types = typePair.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && !teacherName.toString().contains(",")
                                                && !typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    break;

                                                }
                                            }

                                        }

                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                                && teacherName.toString().contains(",")
                                                && !typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            String[] teachers = teacherName.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                                && !teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            String[] types = typePair.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && teacherName.toString().contains(",")
                                                && !typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            String[] teachers = teacherName.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    break;

                                                }
                                            }

                                        }
                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && !teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            String[] types = typePair.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (!namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && teacherName.toString().contains(",")
                                                && !typePair.toString().contains(",")) {
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            String[] teachers = teacherName.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    break;
                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (!namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && !teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            String[] types = typePair.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    break;
                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (!namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            String[] teachers = teacherName.toString().split(",");
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            String[] types = typePair.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {

                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {

                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (!namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                                && teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            String[] teachers = teacherName.toString().split(",");
                                            String[] types = typePair.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    break;
                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    break;

                                                }
                                            }
                                        }

                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                                && teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            String[] types = typePair.toString().split(",");
                                            String[] teachers = teacherName.toString().split(",");
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    break;

                                                }
                                            }

                                        }

                                        if (namePair.toString().contains(",") &&
                                                getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                                && teacherName.toString().contains(",")
                                                && typePair.toString().contains(",")) {
                                            String[] pairNames = namePair.toString().split(",");
                                            String[] types = typePair.toString().split(",");
                                            String[] teachers = teacherName.toString().split(",");
                                            int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                            int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                            for (int i = 0; i < intOne.length; i++) {
                                                if (intOne[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[0]);
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[0]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[0]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(firstLectHall));
                                                    break;

                                                }
                                            }

                                            for (int i = 0; i < intTwo.length; i++) {
                                                if (intTwo[i] == mNumWeek) {
                                                    namePair.delete(0, namePair.toString().length());
                                                    namePair.append(pairNames[1]);
                                                    teacherName.delete(0, teacherName.toString().length());
                                                    teacherName.append(teachers[1]);
                                                    typePair.delete(0, typePair.toString().length());
                                                    typePair.append(types[1]);
                                                    lectureHall.delete(0, lectureHall.toString().length());
                                                    lectureHall.append(Integer.toString(secondLectHall));
                                                    break;

                                                }
                                            }

                                        }


                                    }

                                }

                                namePair.setSpan(style, 0, namePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dateDayPair.setSpan(style, 0, dateDayPair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                lectureHall.setSpan(style, 0, lectureHall.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                startTimeStr.setSpan(style, 0, startTimeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                teacherName.setSpan(style, 0, teacherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mTvNamePair.append(" ");
                                mTvNamePair.append(namePair);
                                mTvDateDayPair.append(" ");
                                mTvDateDayPair.append(dateDayPair);
                                mTvLectureHall.append(" ");
                                mTvLectureHall.append(lectureHall);
                                mTvStartTimePair.append(" ");
                                mTvStartTimePair.append(startTimeStr);
                                mTvTeacherName.append(" ");
                                mTvTeacherName.append(teacherName);
                                mTvTypePair.append(" ");
                                mTvTypePair.append(typePair);
                                break;
                            }

                                /*if (mDate.getTime() > startTime.getTime() && mDate.getTime() < endTime.getTime()) {
                                    flag = true;
                                }*/

                        }

                    }
                    if (mCount == 6 && !flag) {
                        mCount = 0;
                        setNextPairReference();

                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public void goToNextDay() {
        mDate.setDate(mDate.getDate() + 1);
        mDate.setHours(0);
        mDate.setMinutes(1);
    }

    public void goToMonday() {
        mDate.setDate(mDate.getDate() + 2);
        mDate.setHours(0);
        mDate.setMinutes(1);
    }

    public int getLectureHallLength(int n) {
        int countOfNumbers = 0;
        for (; n != 0; n /= 10) ++countOfNumbers;
        return countOfNumbers;
    }

   public void startFirstPairNotificationService(){
       Intent intent = new Intent(this, StartPairNotificationService.class);
       intent.putExtra("GROUP_KEY", mGroupKey);
       startService(intent);
   }

    @Override
    protected void onStop() {
        super.onStop();
        startFirstPairNotificationService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
        mDatabase.onDisconnect().cancel();
        mGroupReference.onDisconnect().cancel();
    }

}
