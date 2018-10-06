package mirabbas.smartschedule.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import mirabbas.smartschedule.Group;
import mirabbas.smartschedule.InfoLectureHall;
import mirabbas.smartschedule.InfoLectureHallService;
import mirabbas.smartschedule.Pair;
import mirabbas.smartschedule.R;
import mirabbas.smartschedule.Teacher;
import mirabbas.smartschedule.TeacherData;

import static java.util.Collections.max;

/**
 * Created by о on 28.03.2018.
 */

public class MainTeacherActivity extends AppCompatActivity {

    TextView mTvName;
    TextView mTvCurrentDate;
    TextView mTvTypeWeek;
    TextView mTvNumberWeek;
    TextView mTvNamePair;
    TextView mTvDateDayPair;
    TextView mTvLectureHall;
    TextView mTvStartTimePair;
    TextView mTvGroup;
    TextView mTvTypePair;
    TextView mTvNews;
    Button mBtnChat;
    Button mBtnSearch;

    ImageView boardImage;
    ImageView computerImage;
    ImageView projectorImage;

    boolean mBoard;
    boolean mComputer;
    boolean mProjector;

    StringBuilder mTeacherFullName;
    String mTeacherLastName;
    String mTeacherKey;

    Date mDate;
    String mDayOfWeek;
    int mNumWeek;

    String mLectureHall;

    SimpleDateFormat mSdfTime;
    SimpleDateFormat mSdfDate;

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mPairReference;
    DatabaseReference mReference;
    DatabaseReference mTeacherDataReference;
    DatabaseReference mGroupNameReference;
    DatabaseReference mLectureHallReference;
    DatabaseReference mSearchReference;

    ForegroundColorSpan style;

    Map<String,ArrayList<String>> map;

    int mCount;
    boolean mainFlag;

    String mainDay;
    String tempGroup;
    String nameGroup;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_teacher_activity);

        mTvName = (TextView) findViewById(R.id.tv_name_t);
        mTvCurrentDate = (TextView) findViewById(R.id.tv_date_teacher);
        mTvTypeWeek = (TextView) findViewById(R.id.tv_type_week_teacher);
        mTvNumberWeek = (TextView) findViewById(R.id.tv_number_week_teacher);
        mTvNamePair = (TextView) findViewById(R.id.tv_name_pair_teacher);
        mTvDateDayPair = (TextView) findViewById(R.id.tv_date_day_pair_teacher);
        mTvLectureHall = (TextView) findViewById(R.id.tv_lecture_hall_teacher);
        mTvStartTimePair = (TextView) findViewById(R.id.tv_start_time_pair_teacher);
        mTvGroup = (TextView) findViewById(R.id.tv_group_name_teacher);
        mTvTypePair = (TextView) findViewById(R.id.tv_type_pair_teacher);
        mTvNews = (TextView) findViewById(R.id.tv_news_teacher);
        mBtnChat = (Button) findViewById(R.id.groups_button);
        mBtnSearch = (Button) findViewById(R.id.search_btn);

        boardImage = (ImageView) findViewById(R.id.board_image);
        computerImage = (ImageView) findViewById(R.id.computer_image);
        projectorImage = (ImageView) findViewById(R.id.projector_image);

        style = new ForegroundColorSpan(getResources().getColor(R.color.light_yellow));

        mDate = new Date();
        map = new LinkedHashMap<>();

        mCount = 0;
        mainFlag = false;

        mDayOfWeek = getDayOfWeek();
        mNumWeek = getNumberWeek();

        mTeacherLastName = null;
        mTeacherKey = null;

        mSdfTime = new SimpleDateFormat("HH:mm");
        mSdfDate = new SimpleDateFormat("dd.MM.yyyy");

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(getIntent().getStringExtra("EMAIL"), getIntent().getStringExtra("PASSWORD")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    mUser = mAuth.getCurrentUser();
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    if(mUser != null){

                        mDatabase.child("teachers").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Teacher teacher = snapshot.getValue(Teacher.class);
                                    if (teacher.getLastName() != null){
                                        mTeacherLastName = teacher.getLastName();
                                        getGroupsForSearching(mTeacherLastName);
                                        mTeacherFullName = new StringBuilder();
                                        mTeacherFullName.append(mTeacherLastName)
                                                .append(" ").append(teacher.getName().substring(0,1))
                                                .append(". ").append(teacher.getPatronymic().substring(0,1))
                                                .append(".");
                                        mTvName.setText(mTeacherFullName);
                                    }

                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    else Toast.makeText(MainTeacherActivity.this, "Пользователь ноль!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        mTvCurrentDate.setText(getCurDate() + ", " + mDayOfWeek);
        mTvTypeWeek.setText("Неделя: " + getTypeWeek(mNumWeek));
        mTvNumberWeek.setText("Номер недели: " + Integer.toString(mNumWeek));

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchReference = FirebaseDatabase.getInstance().getReference().child("schedule");
                mSearchReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Integer> auditList = new ArrayList<Integer>();
                        int lectureHall = 0;
                        if(mTvLectureHall.getText().toString().split(" ").length > 1)
                            lectureHall = Integer.parseInt(mTvLectureHall.getText().toString().split(" ")[1]);
                        final int firstDigit = getFirstDigit(lectureHall);
                        String curDay = getEnglishDayOfWeek();
                        String start = mTvStartTimePair.getText().toString().split(" ")[1];
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                for(DataSnapshot snapshot2 : snapshot1.getChildren()){
                                    if(snapshot1.getKey().equals(curDay)){
                                        Pair pair = snapshot2.getValue(Pair.class);
                                        if(pair.getFlag() == 1) {
                                            if(firstDigit == getFirstDigit(pair.getLectureHall()) && lectureHall != pair.getLectureHall()){
                                                if(!start.equals(pair.getStartTime())){
                                                    if(auditList.size() != 0){
                                                        for(int i = 0; i < auditList.size(); i++){
                                                            if(auditList.get(i) == pair.getLectureHall()){
                                                                auditList.remove(i);
                                                                break;
                                                            }
                                                            else auditList.add(pair.getLectureHall());
                                                        }
                                                    }
                                                    else auditList.add(pair.getLectureHall());
                                                }
                                            }
                                        }

                                    }

                                }
                            }
                        }

                        if(auditList.size() != 0) {
                            Intent intent = new Intent(MainTeacherActivity.this, ChoiceLectureHall.class);
                            intent.putIntegerArrayListExtra("LECTURE_HALLS", auditList);
                            intent.putExtra("IS_BOARD", mBoard);
                            intent.putExtra("IS_COMPUTER", mComputer);
                            intent.putExtra("IS_PROJECTOR", mProjector);
                            startActivity(intent);
                        }
                        else Toast.makeText(MainTeacherActivity.this, "Нет свободных аудиторий", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTeacherDataReference = FirebaseDatabase.getInstance().getReference().child("teacher-data");
                mTeacherDataReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            TeacherData data = snapshot.getValue(TeacherData.class);
                            if(data.getLastName().equals(mTeacherLastName)){
                                mTeacherKey = snapshot.getKey();
                                break;
                            }
                        }
                        if(mTeacherKey != null) {
                            Intent intent = new Intent(MainTeacherActivity.this, ListGroupsActivity.class);
                            intent.putExtra("TEACHER", mTeacherLastName);
                            intent.putExtra("TEACHER_KEY", mTeacherKey);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    public String getDayOfWeek(){

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

        String dayOfTheWeek = sdf.format(mDate);
        return dayOfTheWeek;
    }

    public String getEnglishDayOfWeek(){

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        String dayOfTheWeek = sdf.format(mDate);
        return dayOfTheWeek.toLowerCase();
    }


    public String getCurDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");
        return dateformat.format(c.getTime());
    }

    public int getNumberWeek(){
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

        for(int i = 0; i < 13; i++){
            if(mDate.getTime() > start && mDate.getTime() < end){
                res = i + 1;
                break;
            }
            else {
                start += difDate;
                end += difDate;
            }
        }
        return res;

    }

    public String getTypeWeek(int numWeek){
        String res = "";
        if(numWeek % 2 == 0) res = "Четная";
        else res = "Нечетная";
        return res;
    }

    public void getGroupsForSearching(final String teacherName){
        mPairReference = FirebaseDatabase.getInstance().getReference().child("schedule");
        mPairReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArrayList<String> temp = new ArrayList<String>();
                    Group group = snapshot.getValue(Group.class);
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                            Pair pair = snapshot2.getValue(Pair.class);
                            if (teacherName != null) {
                                if (pair.getFlag() == 1) {
                                    if (pair.getTeacher().contains(teacherName)) {
                                        if (snapshot1.getKey().equals("monday"))
                                            temp.add("1" + snapshot1.getKey());
                                        if (snapshot1.getKey().equals("tuesday"))
                                            temp.add("2" + snapshot1.getKey());
                                        if (snapshot1.getKey().equals("wednesday"))
                                            temp.add("3" + snapshot1.getKey());
                                        if (snapshot1.getKey().equals("thursday"))
                                            temp.add("4" + snapshot1.getKey());
                                        if (snapshot1.getKey().equals("friday"))
                                            temp.add("5" + snapshot1.getKey());
                                        if (snapshot1.getKey().equals("saturday"))
                                            temp.add("6" + snapshot1.getKey());
                                    }
                                }
                            } else Log.d("NOT", "Not Found");
                        }
                    }
                    if (temp.size() != 0) {
                        Set<String> hs = new HashSet<>();
                        hs.addAll(temp);
                        temp.clear();
                        temp.addAll(hs);
                        Collections.sort(temp);
                        Log.d("ARRAY", temp.toString());
                        map.put(snapshot.getKey(), temp);
                    }

                }
                LinkedHashMap<String, ArrayList<Integer>> hashMap = new LinkedHashMap<String, ArrayList<Integer>>();

                for (String groupKey : map.keySet()) {
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    for (String day : map.get(groupKey)) {
                        temp.add(getDaysDif(getEnglishDayOfWeek(), day.substring(1, day.length())));
                    }
                    hashMap.put(groupKey, temp);

                }

                ArrayList<Integer> numList = new ArrayList<Integer>();
                ArrayList<Integer> indexList = new ArrayList<Integer>();
                ArrayList<String> groupList = new ArrayList<String>();

                for (final String groupKey : hashMap.keySet()) {
                    groupList.add(groupKey);
                    ArrayList<Integer> minList = new ArrayList<Integer>();
                    boolean flag = false;
                    int num = 100;
                    int index = -1;
                    String mainDay = null;
                    for (Integer n : hashMap.get(groupKey)) {
                        if (n <= 0) {
                            minList.add(n);
                            flag = true;
                        }
                    }
                    if (flag) num = max(minList);
                    else num = max(hashMap.get(groupKey));

                    for (int i = 0; i < hashMap.get(groupKey).size(); i++) {
                        if (hashMap.get(groupKey).get(i) == num) index = i;
                    }

                    if (num == 0) {
                        for (int i = 0; i < map.get(groupKey).size(); i++) {
                            if (index == i)
                                mainDay = map.get(groupKey).get(i).substring(1, map.get(groupKey).get(i).length());
                        }

                        if (mainDay != null) {
                            mGroupNameReference = FirebaseDatabase.getInstance().getReference().child("schedule").child(groupKey);
                            mGroupNameReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Group group = dataSnapshot.getValue(Group.class);
                                    nameGroup = group.getNameGroup();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            mReference = FirebaseDatabase.getInstance().getReference().child("schedule").child(groupKey).child(mainDay);
                            Intent intent = new Intent(getApplicationContext(), InfoLectureHallService.class);
                            intent.putExtra("GROUP_KEY", groupKey);
                            intent.putExtra("TEACHER_NAME", mTeacherLastName);
                            startService(intent);
                            mReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Date startTime = null;
                                    Date endTime = null;
                                    boolean flag = false;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        SpannableStringBuilder namePair = new SpannableStringBuilder();
                                        SpannableStringBuilder dateDayPair = new SpannableStringBuilder();
                                        final SpannableStringBuilder lectureHall = new SpannableStringBuilder();
                                        SpannableStringBuilder groupName = new SpannableStringBuilder();
                                        SpannableStringBuilder typePair = new SpannableStringBuilder();
                                        SpannableStringBuilder typeWeek = new SpannableStringBuilder();
                                        SpannableStringBuilder numberWeek = new SpannableStringBuilder();
                                        SpannableStringBuilder startTimeStr = new SpannableStringBuilder();
                                        StringBuilder teacherName = new StringBuilder();

                                        mCount++;
                                        Pair pair = snapshot.getValue(Pair.class);
                                        if (pair.getFlag() == 1) {
                                            if (pair.getTeacher().contains(mTeacherLastName)) {
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
                                                    groupName.append(nameGroup);

                                                    Log.d("START1", startTime.toString());
                                                    Log.d("CUR1", mDate.toString());
                                                    Log.d("END1", endTime.toString());


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
                                                        groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        mTvNamePair.append(" ");
                                                        mTvNamePair.append(namePair);
                                                        mTvDateDayPair.append(" ");
                                                        mTvDateDayPair.append(dateDayPair);
                                                        mTvLectureHall.append(" ");
                                                        mTvLectureHall.append(lectureHall);
                                                        mTvStartTimePair.append(" ");
                                                        mTvStartTimePair.append(startTimeStr);
                                                        mTvGroup.append(" ");
                                                        mTvGroup.append(groupName);
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
                                                        groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        mTvNamePair.append(" ");
                                                        mTvNamePair.append(namePair);
                                                        mTvDateDayPair.append(" ");
                                                        mTvDateDayPair.append(dateDayPair);
                                                        mTvLectureHall.append(" ");
                                                        mTvLectureHall.append(lectureHall);
                                                        mTvStartTimePair.append(" ");
                                                        mTvStartTimePair.append(startTimeStr);
                                                        mTvGroup.append(" ");
                                                        mTvGroup.append(groupName);
                                                        mTvTypePair.append(" ");
                                                        mTvTypePair.append(typePair);
                                                        mainFlag = true;
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
                                                                            groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                            typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                            mTvNamePair.append(" ");
                                                                            mTvNamePair.append(namePair);
                                                                            mTvDateDayPair.append(" ");
                                                                            mTvDateDayPair.append(dateDayPair);
                                                                            mTvLectureHall.append(" ");
                                                                            mTvLectureHall.append(lectureHall);
                                                                            mTvStartTimePair.append(" ");
                                                                            mTvStartTimePair.append(startTimeStr);
                                                                            mTvGroup.append(" ");
                                                                            mTvGroup.append(groupName);
                                                                            mTvTypePair.append(" ");
                                                                            mTvTypePair.append(typePair);
                                                                            tempFlag = true;
                                                                            mainFlag = true;
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
                                                                        && !typePair.toString().contains(",")) {
                                                                    String[] teachers = groupName.toString().split(",");
                                                                    for (int i = 0; i < intOne.length; i++) {
                                                                        if (intOne[i] == mNumWeek) {
                                                                            break;
                                                                        }
                                                                    }

                                                                    for (int i = 0; i < intTwo.length; i++) {
                                                                        if (intTwo[i] == mNumWeek) {
                                                                            break;

                                                                        }
                                                                    }
                                                                }

                                                                if (!namePair.toString().contains(",") &&
                                                                        getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                        && !typePair.toString().contains(",")) {
                                                                    String[] pairNames = namePair.toString().split(",");
                                                                    String[] teachers = groupName.toString().split(",");
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

                                                                if (namePair.toString().contains(",") &&
                                                                        getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                        && !typePair.toString().contains(",")) {
                                                                    String[] pairNames = namePair.toString().split(",");
                                                                    int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                                                    int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                                                    String[] teachers = groupName.toString().split(",");
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
                                                                        getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
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
                                                                        getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
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
                                                                        getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                        getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                        && typePair.toString().contains(",")) {
                                                                    String[] pairNames = namePair.toString().split(",");
                                                                    String[] types = typePair.toString().split(",");
                                                                    int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                                                    int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                                                    for (int i = 0; i < intOne.length; i++) {
                                                                        if (intOne[i] == mNumWeek) {
                                                                            namePair.delete(0, namePair.toString().length());
                                                                            namePair.append(pairNames[0]);
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
                                                        groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        mTvNamePair.append(" ");
                                                        mTvNamePair.append(namePair);
                                                        mTvDateDayPair.append(" ");
                                                        mTvDateDayPair.append(dateDayPair);
                                                        mTvLectureHall.append(" ");
                                                        mTvLectureHall.append(lectureHall);
                                                        mTvStartTimePair.append(" ");
                                                        mTvStartTimePair.append(startTimeStr);
                                                        mTvGroup.append(" ");
                                                        mTvGroup.append(groupName);
                                                        mTvTypePair.append(" ");
                                                        mTvTypePair.append(typePair);
                                                        mainFlag = true;
                                                        mLectureHall = lectureHall.toString();
                                                        Log.d("LECTURE1", mLectureHall);

                                                        mLectureHallReference = FirebaseDatabase.getInstance().getReference().child("infoLectureHall");
                                                        mLectureHallReference.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                Log.d("HERE", "yes");
                                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                                    Log.d("TAG", snapshot1.getKey());
                                                                    Log.d("HALL", mLectureHall);
                                                                    if (snapshot1.getKey().equals(mLectureHall)) {

                                                                        InfoLectureHall info = snapshot1.getValue(InfoLectureHall.class);
                                                                        if (info.isBoard())
                                                                            boardImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                                                                        else
                                                                            boardImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                                                                        if (info.isComputer())
                                                                            computerImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                                                                        else
                                                                            computerImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                                                                        if (info.isProjector())
                                                                            projectorImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                                                                        else
                                                                            projectorImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                        break;
                                                    }


                                                }
                                                if (mainFlag) {
                                                    break;
                                                }

                                            }
                                            if (mCount == 6) {
                                                mCount = 0;
                                            }
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                        if (!mainFlag) {
                            Log.d("MAINFLAG", Boolean.toString(mainFlag));
                            //groupList.clear();
                            groupList.add(groupKey);
                            minList.clear();
                            flag = false;
                            num = 100;
                            index = -1;
                            for (Integer n : hashMap.get(groupKey)) {
                                if (n < 0) {
                                    minList.add(n);
                                    flag = true;
                                }
                            }
                            if (flag) num = max(minList);
                            else num = max(hashMap.get(groupKey));

                            for (int i = 0; i < hashMap.get(groupKey).size(); i++) {
                                if (hashMap.get(groupKey).get(i) == num) index = i;
                            }

                            numList.add(num);
                            indexList.add(index);
                        }
                    }

                    int tempNum = 100;
                    int tempIndex = -1;

                    ArrayList<Integer> secondMinList = new ArrayList<Integer>();

                    for (Integer nb : numList) {
                        if (nb < 0) secondMinList.add(nb);
                    }
                    if (secondMinList.size() != 0) tempNum = max(secondMinList);
                    else tempNum = numList.get(numList.size() - 1);
                    for (int i = 0; i < numList.size(); i++) {
                        if (tempNum == numList.get(i)) {
                            tempIndex = indexList.get(i);
                            tempGroup = groupList.get(i);
                            break;
                        }
                    }

                    for (int i = 0; i < map.get(tempGroup).size(); i++) {
                        if (tempIndex == i) {
                            mainDay = map.get(tempGroup).get(i).substring(1, map.get(tempGroup).get(i).length());
                        }
                    }


                    if (mainDay != null) {
                        Log.d("POLOPO", "main day");
                        mGroupNameReference = FirebaseDatabase.getInstance().getReference().child("schedule").child(tempGroup);
                        mGroupNameReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Group group = dataSnapshot.getValue(Group.class);
                                nameGroup = group.getNameGroup();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mReference = FirebaseDatabase.getInstance().getReference().child("schedule").child(tempGroup).child(mainDay);
                        Intent intent = new Intent(getApplicationContext(), InfoLectureHallService.class);
                        intent.putExtra("GROUP_KEY", tempGroup);
                        intent.putExtra("TEACHER_NAME", mTeacherLastName);
                        startService(intent);
                        final String finalMainDay = mainDay;
                        mReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Date startTime = null;
                                Date endTime = null;
                                boolean flag = false;

                                Log.d("POLOPO", tempGroup);
                                Log.d("POLOPO", finalMainDay);

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    SpannableStringBuilder namePair = new SpannableStringBuilder();
                                    SpannableStringBuilder dateDayPair = new SpannableStringBuilder();
                                    SpannableStringBuilder lectureHall = new SpannableStringBuilder();
                                    SpannableStringBuilder groupName = new SpannableStringBuilder();
                                    SpannableStringBuilder typePair = new SpannableStringBuilder();
                                    SpannableStringBuilder typeWeek = new SpannableStringBuilder();
                                    SpannableStringBuilder numberWeek = new SpannableStringBuilder();
                                    SpannableStringBuilder startTimeStr = new SpannableStringBuilder();
                                    StringBuilder teacherName = new StringBuilder();

                                    mCount++;
                                    Pair pair = snapshot.getValue(Pair.class);
                                    if (pair.getFlag() == 1) {
                                        Log.d("POLOPO", pair.getPairName());
                                        if (pair.getTeacher().contains(mTeacherLastName)) {
                                            try {

                                                Log.d("POLOPO", "inside");
                                                startTime = mSdfTime.parse(pair.getStartTime());
                                                endTime = mSdfTime.parse(pair.getEndTime());

                                                startTime.setMonth(mDate.getMonth());
                                                startTime.setYear(mDate.getYear());
                                                if (getDaysDif(getEnglishDayOfWeek(), finalMainDay) < 0) {
                                                    int date = mDate.getDate() - getDaysDif(getEnglishDayOfWeek(), finalMainDay) + 8;
                                                    startTime.setDate(date);
                                                } else
                                                    startTime.setDate(mDate.getDate() + Math.abs(getDaysDif(getEnglishDayOfWeek(), finalMainDay)));
                                                startTime.setHours(0);
                                                startTime.setMinutes(1);

                                                namePair.append(pair.getPairName());
                                                dateDayPair.append(mSdfDate.format(mDate));
                                                lectureHall.append(Integer.toString(pair.getLectureHall()));
                                                teacherName.append(pair.getTeacher());
                                                typePair.append(pair.getTypePair());
                                                typeWeek.append(pair.getTypeWeek());
                                                numberWeek.append(pair.getNumberWeek());
                                                startTimeStr.append(pair.getStartTime());
                                                groupName.append(nameGroup);

                                                Log.d("START", startTime.toString());
                                                Log.d("CUR", mDate.toString());
                                                Log.d("END", endTime.toString());


                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            }

                                            if (typeWeek.toString().equals("нч") && mTvTypeWeek.getText().equals("Нечетная")) {

                                                if (mDate.getTime() < startTime.getTime()) {
                                                    namePair.setSpan(style, 0, namePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    dateDayPair.setSpan(style, 0, dateDayPair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    lectureHall.setSpan(style, 0, lectureHall.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    startTimeStr.setSpan(style, 0, startTimeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    mTvNamePair.append(" ");
                                                    mTvNamePair.append(namePair);
                                                    mTvDateDayPair.append(" ");
                                                    mTvDateDayPair.append(dateDayPair);
                                                    mTvLectureHall.append(" ");
                                                    mTvLectureHall.append(lectureHall);
                                                    mTvStartTimePair.append(" ");
                                                    mTvStartTimePair.append(startTimeStr);
                                                    mTvGroup.append(" ");
                                                    mTvGroup.append(groupName);
                                                    mTvTypePair.append(" ");
                                                    mTvTypePair.append(typePair);
                                                    break;
                                                }
                                            } else if (typeWeek.toString().equals("чн") && mTvTypeWeek.getText().equals("Четная")) {

                                                if (mDate.getTime() < startTime.getTime()) {
                                                    namePair.setSpan(style, 0, namePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    dateDayPair.setSpan(style, 0, dateDayPair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    lectureHall.setSpan(style, 0, lectureHall.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    startTimeStr.setSpan(style, 0, startTimeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    mTvNamePair.append(" ");
                                                    mTvNamePair.append(namePair);
                                                    mTvDateDayPair.append(" ");
                                                    mTvDateDayPair.append(dateDayPair);
                                                    mTvLectureHall.append(" ");
                                                    mTvLectureHall.append(lectureHall);
                                                    mTvStartTimePair.append(" ");
                                                    mTvStartTimePair.append(startTimeStr);
                                                    mTvGroup.append(" ");
                                                    mTvGroup.append(groupName);
                                                    mTvTypePair.append(" ");
                                                    mTvTypePair.append(typePair);
                                                    mainFlag = true;
                                                    break;
                                                }
                                            } else {

                                                if ((mDate.getTime() < startTime.getTime())) {

                                                    if (!numberWeek.toString().equals("")) {
                                                        if (!numberWeek.toString().contains(",")) {
                                                            String[] strNums = numberWeek.toString().split(" ");
                                                            boolean tempFlag = false;
                                                            int[] nums = new int[strNums.length];
                                                            for (int i = 0; i < strNums.length; i++) {
                                                                nums[i] = Integer.parseInt(strNums[i]);
                                                                if (nums[i] == mNumWeek) {

                                                                    if (mDate.getTime() < startTime.getTime()) {
                                                                        namePair.setSpan(style, 0, namePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                        dateDayPair.setSpan(style, 0, dateDayPair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                        lectureHall.setSpan(style, 0, lectureHall.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                        startTimeStr.setSpan(style, 0, startTimeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                        groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                        typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                        mTvNamePair.append(" ");
                                                                        mTvNamePair.append(namePair);
                                                                        mTvDateDayPair.append(" ");
                                                                        mTvDateDayPair.append(dateDayPair);
                                                                        mTvLectureHall.append(" ");
                                                                        mTvLectureHall.append(lectureHall);
                                                                        mTvStartTimePair.append(" ");
                                                                        mTvStartTimePair.append(startTimeStr);
                                                                        mTvGroup.append(" ");
                                                                        mTvGroup.append(groupName);
                                                                        mTvTypePair.append(" ");
                                                                        mTvTypePair.append(typePair);
                                                                        tempFlag = true;
                                                                        mainFlag = true;
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
                                                                    && !typePair.toString().contains(",")) {
                                                                String[] teachers = groupName.toString().split(",");
                                                                for (int i = 0; i < intOne.length; i++) {
                                                                    if (intOne[i] == mNumWeek) {
                                                                        break;
                                                                    }
                                                                }

                                                                for (int i = 0; i < intTwo.length; i++) {
                                                                    if (intTwo[i] == mNumWeek) {
                                                                        break;

                                                                    }
                                                                }
                                                            }

                                                            if (!namePair.toString().contains(",") &&
                                                                    getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                    && !typePair.toString().contains(",")) {
                                                                String[] pairNames = namePair.toString().split(",");
                                                                String[] teachers = groupName.toString().split(",");
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

                                                            if (namePair.toString().contains(",") &&
                                                                    getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                    && !typePair.toString().contains(",")) {
                                                                String[] pairNames = namePair.toString().split(",");
                                                                int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                                                int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                                                String[] teachers = groupName.toString().split(",");
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
                                                                    getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
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
                                                                    getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
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
                                                                    getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                    getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
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
                                                                    && typePair.toString().contains(",")) {
                                                                String[] pairNames = namePair.toString().split(",");
                                                                String[] types = typePair.toString().split(",");
                                                                int firstLectHall = Integer.parseInt(lectureHall.toString().substring(0, 4));
                                                                int secondLectHall = Integer.parseInt(lectureHall.toString().substring(4, 8));
                                                                for (int i = 0; i < intOne.length; i++) {
                                                                    if (intOne[i] == mNumWeek) {
                                                                        namePair.delete(0, namePair.toString().length());
                                                                        namePair.append(pairNames[0]);
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
                                                    groupName.setSpan(style, 0, groupName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    typePair.setSpan(style, 0, typePair.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    mTvNamePair.append(" ");
                                                    mTvNamePair.append(namePair);
                                                    mTvDateDayPair.append(" ");
                                                    mTvDateDayPair.append(dateDayPair);
                                                    mTvLectureHall.append(" ");
                                                    mTvLectureHall.append(lectureHall);
                                                    mTvStartTimePair.append(" ");
                                                    mTvStartTimePair.append(startTimeStr);
                                                    mTvGroup.append(" ");
                                                    mTvGroup.append(groupName);
                                                    mTvTypePair.append(" ");
                                                    mTvTypePair.append(typePair);
                                                    mainFlag = true;
                                                    mLectureHall = lectureHall.toString();

                                                    mLectureHallReference = FirebaseDatabase.getInstance().getReference().child("infoLectureHall");
                                                    mLectureHallReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.d("HERE", "yes");
                                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                                Log.d("TAG", snapshot1.getKey());
                                                                Log.d("HALL", mLectureHall);
                                                                if (snapshot1.getKey().equals(mLectureHall)) {

                                                                    InfoLectureHall info = snapshot1.getValue(InfoLectureHall.class);
                                                                    if (info.isBoard()) {
                                                                        boardImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                                                                        mBoard = true;
                                                                    }
                                                                    else {
                                                                        boardImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                                                                        mBoard = false;
                                                                    }
                                                                    if (info.isComputer()) {
                                                                        computerImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                                                                        mComputer = true;
                                                                    }
                                                                    else {
                                                                        computerImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                                                                        mComputer = false;
                                                                    }

                                                                    if (info.isProjector()) {
                                                                        projectorImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.acid), PorterDuff.Mode.MULTIPLY);
                                                                        mProjector = true;
                                                                    }
                                                                    else {
                                                                        projectorImage.setColorFilter(MainTeacherActivity.this.getResources().getColor(R.color.light_red), PorterDuff.Mode.MULTIPLY);
                                                                        mProjector = false;
                                                                    }


                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    break;
                                                }


                                            }

                                        }
                                        if (mCount == 6) {
                                            mCount = 0;
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                //else Log.d("POLOPO", " do main day");

            }
                @Override
                public void onCancelled (DatabaseError databaseError){

                }

        });
    }

    public int getDaysDif(String curDay, String day){
        int curDayInteger = 0;
        int dayInteger = 0;

        if(curDay.equals("monday")) curDayInteger = 1;
        if(curDay.equals("tuesday")) curDayInteger = 2;
        if(curDay.equals("wednesday")) curDayInteger = 3;
        if(curDay.equals("thursday")) curDayInteger = 4;
        if(curDay.equals("friday")) curDayInteger = 5;
        if(curDay.equals("saturday")) curDayInteger = 6;

        if(day.equals("monday")) dayInteger = 1;
        if(day.equals("tuesday")) dayInteger = 2;
        if(day.equals("wednesday")) dayInteger = 3;
        if(day.equals("thursday")) dayInteger = 4;
        if(day.equals("friday")) dayInteger = 5;
        if(day.equals("saturday")) dayInteger = 6;

        return curDayInteger - dayInteger;

    }

    public int getLectureHallLength(int n){
        int countOfNumbers = 0;
        for ( ; n != 0 ; n /= 10) ++countOfNumbers;
        return countOfNumbers;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, InfoLectureHallService.class));
    }

    public int getFirstDigit(int num){
        while (num > 9) {
            num /= 10;
        }
        return num;
    }

}
