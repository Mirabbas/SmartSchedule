package mirabbas.smartschedule.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ScrollView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import mirabbas.smartschedule.ExpListAdapter;
import mirabbas.smartschedule.Pair;
import mirabbas.smartschedule.R;

/**
 * Created by о on 22.03.2018.
 */

public class ScheduleActivity extends AppCompatActivity {

    Button mMonthBtn;
    Button mWeekBtn;
    Button mDayBtn;
    ScrollView scrollBody;

    ExpandableListView listView;

    HashMap<String, List<String>> childItems;
    List<String> parentItems;

    DatabaseReference mDatabase;
    String mGroupKey;
    SimpleDateFormat mSdfTime;

    private int mYear, mMonth, mDay;
    private String mWeek;
    private String mMonthFilter;


    StringBuilder mSchedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);

        mMonthBtn = (Button) findViewById(R.id.month_filter_btn);
        mWeekBtn = (Button) findViewById(R.id.week_filter_btn);
        mDayBtn = (Button) findViewById(R.id.day_filter_btn);
        scrollBody = (ScrollView) findViewById(R.id.dcr);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSdfTime = new SimpleDateFormat("HH:mm");
        mGroupKey = getIntent().getStringExtra("GROUP_KEY");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("schedule").child(mGroupKey);


        listView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        mDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDayFilter();
            }
        });

        mWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callWeekFilter();
            }
        });

        mMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMonthFilter();
            }
        });

        setHeaders();
        childItems = new HashMap<String, List<String>>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int countPairs = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    mSchedule = new StringBuilder();

                    for(DataSnapshot snap : snapshot.getChildren()){

                        countPairs++;

                        StringBuilder namePair = new StringBuilder();
                        StringBuilder lectureHall = new StringBuilder();
                        StringBuilder teacherName = new StringBuilder();
                        StringBuilder typePair = new StringBuilder();
                        StringBuilder typeWeek = new StringBuilder();
                        StringBuilder numberWeek = new StringBuilder();
                        StringBuilder startTimeStr = new StringBuilder();

                        Pair pair = snap.getValue(Pair.class);

                        if(pair.getFlag() == 0){
                            mSchedule.append(Integer.toString(countPairs)).append(" пара: ").append("нет\n\n");
                        }
                        else {
                            namePair.append(pair.getPairName());
                            lectureHall.append(Integer.toString(pair.getLectureHall()));
                            teacherName.append(pair.getTeacher());
                            typePair.append(pair.getTypePair());
                            typeWeek.append(pair.getTypeWeek());
                            numberWeek.append(pair.getNumberWeek());
                            startTimeStr.append(pair.getStartTime());

                            mSchedule.append(Integer.toString(countPairs)).append(" пара\n");

                            if (!numberWeek.toString().equals("") ) {
                                if (numberWeek.toString().contains(",")) {
                                    String[] strNums = numberWeek.toString().split(",");
                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                            && !teacherName.toString().contains(",")
                                            && !typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");

                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n\n");
                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("Аудитория: ").append(lectureHall).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        continue;

                                    }

                                    if (!namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && !teacherName.toString().contains(",")
                                            && !typePair.toString().contains(",")) {
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        mSchedule.append("Название: ").append(namePair).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0])
                                                .append("(ауд. ").append(Integer.toString(firstLectHall)).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1])
                                                .append("(ауд. ").append(Integer.toString(secondLectHall)).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        continue;

                                    }

                                    if (!namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                            && teacherName.toString().contains(",")
                                            && !typePair.toString().contains(",")) {
                                        String[] teachers = teacherName.toString().split(",");
                                        mSchedule.append("Название: ").append(namePair).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("Аудитория: ").append(lectureHall).append("\n");
                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        continue;
                                    }

                                    if (!namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                            && !teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] types = typePair.toString().split(",");
                                        mSchedule.append("Название: ").append(namePair).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("Аудитория: ").append(lectureHall).append("\n");

                                        continue;

                                    }

                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && !teacherName.toString().contains(",")
                                            && !typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));

                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n\n");
                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n\n");
                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                        continue;

                                    }

                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                            && teacherName.toString().contains(",")
                                            && !typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        String[] teachers = teacherName.toString().split(",");
                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n\n");

                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        mSchedule.append("Аудитория: ").append(lectureHall).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                            && !teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        String[] types = typePair.toString().split(",");
                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n\n");

                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                        mSchedule.append("Аудитория: ").append(lectureHall).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && teacherName.toString().contains(",")
                                            && !typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        String[] teachers = teacherName.toString().split(",");

                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n\n");

                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n");

                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }
                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && !teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        String[] types = typePair.toString().split(",");

                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n\n");

                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n");

                                        mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (!namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && teacherName.toString().contains(",")
                                            && !typePair.toString().contains(",")) {
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        String[] teachers = teacherName.toString().split(",");

                                        mSchedule.append("Название: ").append(namePair).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n\n");

                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n");

                                        mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (!namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && !teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        String[] types = typePair.toString().split(",");

                                        mSchedule.append("Название: ").append(namePair).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n\n");

                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n");

                                        mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (!namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] teachers = teacherName.toString().split(",");
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        String[] types = typePair.toString().split(",");

                                        mSchedule.append("Название: ").append(namePair).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n\n");

                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");

                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (!namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                            && teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] teachers = teacherName.toString().split(",");
                                        String[] types = typePair.toString().split(",");

                                        mSchedule.append("Название: ").append(namePair).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n\n");

                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");

                                        mSchedule.append("Аудитория: ").append(lectureHall).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;
                                    }

                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) != 8
                                            && teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        String[] types = typePair.toString().split(",");
                                        String[] teachers = teacherName.toString().split(",");

                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n\n");

                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");

                                        mSchedule.append("Аудитория: ").append(lectureHall).append("\n");
                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        String[] types = typePair.toString().split(",");
                                        String[] teachers = teacherName.toString().split(",");
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n\n");

                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n");

                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                    if (namePair.toString().contains(",") &&
                                            getLectureHallLength(Integer.parseInt(lectureHall.toString())) == 8
                                            && teacherName.toString().contains(",")
                                            && typePair.toString().contains(",")) {
                                        String[] pairNames = namePair.toString().split(",");
                                        String[] types = typePair.toString().split(",");
                                        String[] teachers = teacherName.toString().split(",");
                                        int firstLectHall = Integer.parseInt(lectureHall.substring(0,4));
                                        int secondLectHall = Integer.parseInt(lectureHall.substring(4,8));
                                        mSchedule.append("Название: ").append(pairNames[0]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[0]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[0]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[0]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(firstLectHall)).append("\n\n");

                                        mSchedule.append("Название: ").append(pairNames[1]).append("\n");
                                        mSchedule.append("Номера недель: ").append(strNums[1]).append("\n");
                                        mSchedule.append("Тип пары: ").append(types[1]).append("\n");
                                        mSchedule.append("Преподаватель: ").append(teachers[1]).append("\n");
                                        mSchedule.append("Аудитория: ").append(Integer.toString(secondLectHall)).append("\n");

                                        mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                        mSchedule.append("\n");
                                        continue;

                                    }

                                }

                                else {
                                    mSchedule.append("Название: ").append(namePair).append("\n");
                                    mSchedule.append("Номера недель: ").append(numberWeek).append("\n");
                                    mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                    mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                    mSchedule.append("Аудитория: ").append(lectureHall).append("\n\n");
                                    mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                    mSchedule.append("\n");
                                }

                            }

                            else {
                                mSchedule.append("Название: ").append(namePair).append("\n");
                                mSchedule.append("Номера недель: ").append(numberWeek).append("\n");
                                mSchedule.append("Тип пары: ").append(typePair).append("\n");
                                mSchedule.append("Преподаватель: ").append(teacherName).append("\n");
                                mSchedule.append("Аудитория: ").append(lectureHall).append("\n");
                                mSchedule.append("Начало в: ").append(startTimeStr).append("\n");
                                mSchedule.append("\n");
                            }
                        }

                        }

                    if(countPairs == 6) {
                        Log.d("DAY", mSchedule.toString());
                        if (snapshot.getKey().equals("monday")) {
                            List<String> monday = new ArrayList<String>();
                            monday.add(mSchedule.toString());
                            childItems.put(parentItems.get(0), monday);
                        }
                        if (snapshot.getKey().equals("tuesday")) {
                            List<String> tuesday = new ArrayList<String>();
                            tuesday.add(mSchedule.toString());
                            childItems.put(parentItems.get(1), tuesday);
                        }
                        if (snapshot.getKey().equals("wednesday")) {
                            List<String> wednesday = new ArrayList<String>();
                            wednesday.add(mSchedule.toString());
                            childItems.put(parentItems.get(2), wednesday);
                        }
                        if (snapshot.getKey().equals("thursday")) {
                            List<String> thursday = new ArrayList<String>();
                            thursday.add(mSchedule.toString());
                            childItems.put(parentItems.get(3), thursday);
                        }
                        if (snapshot.getKey().equals("friday")) {
                            List<String> friday = new ArrayList<String>();
                            friday.add(mSchedule.toString());
                            childItems.put(parentItems.get(4), friday);
                        }
                        if (snapshot.getKey().equals("saturday")) {
                            List<String> saturday = new ArrayList<String>();
                            saturday.add(mSchedule.toString());
                            childItems.put(parentItems.get(5), saturday);
                        }

                        countPairs = 0;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ExpListAdapter adapter = new ExpListAdapter(this, parentItems, childItems);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        setExpandableListViewHeight(listView);
    }

    public void setHeaders(){

        parentItems = new ArrayList<String>();

        parentItems.add("Понедельник");
        parentItems.add("Вторник");
        parentItems.add("Среда");
        parentItems.add("Четверг");
        parentItems.add("Пятница");
        parentItems.add("Суббота");

    }


    private void setExpandableListViewHeight(ExpandableListView listView) {
        try {
            ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getGroupCount(); i++) {
                View listItem = listAdapter.getGroupView(i, false, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
            if (height < 10) height = 200;
            params.height = height;
            listView.setLayoutParams(params);
            listView.requestLayout();
            scrollBody.post(new Runnable() {
                public void run() {
                    scrollBody.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLectureHallLength(int n){
        int countOfNumbers = 0;
        for ( ; n != 0 ; n /= 10) ++countOfNumbers;
        return countOfNumbers;
    }

    public void callDayFilter(){
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        /*String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        editTextDate.setText(editTextDateParam);*/
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("Выберите дату");
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if( i == DialogInterface.BUTTON_NEGATIVE) dialogInterface.cancel();
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        datePickerDialog.show();
    }

    public void callWeekFilter(){
        final String[] weeks = {"Четные", "Нечетные", "1", "2", "3", "4","5","6","7","8","9","10","11","12","13"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбор недели");
        builder.setItems(weeks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mWeek = weeks[i];
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        builder.create().show();

    }

    public void callMonthFilter(){
        final String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Сентябрь","Октябрь","Ноябрь","Декабрь"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбор месяца");
        builder.setItems(months, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mMonthFilter = months[i];
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        builder.create().show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
