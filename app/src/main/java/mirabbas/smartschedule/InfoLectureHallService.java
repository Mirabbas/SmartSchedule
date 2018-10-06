package mirabbas.smartschedule;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import mirabbas.smartschedule.activities.TeacherQuestionnaireActivity;

/**
 * Created by о on 09.04.2018.
 */

public class InfoLectureHallService extends Service {

    private static final int NOTIFY_ID = 102;
    final long MILLIS_IN_15_MINUTES = 1350000;

    DatabaseReference mReference;
    String mGroupKey;
    String mDayOfWeek;
    String mTeacherName;
    Date mDate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mDate = new Date();
        mDayOfWeek = getDayOfWeek();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGroupKey = intent.getStringExtra("GROUP_KEY");
        mTeacherName = intent.getStringExtra("TEACHER_NAME");
        setPairReference();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDate = new Date();
                checkInfo();
            }
        },0L, 60L*10000);
        return super.onStartCommand(intent, flags, startId);
    }

    public void checkInfo(){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Date startTime = null;
                Date endTime = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Pair pair = snapshot.getValue(Pair.class);
                    if(pair.getFlag() == 1){

                        try {
                            endTime = dateFormat.parse(pair.getEndTime());
                            startTime = dateFormat.parse(pair.getStartTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        startTime.setDate(mDate.getDate());
                        startTime.setMonth(mDate.getMonth());
                        startTime.setYear(mDate.getYear());
                        endTime.setDate(mDate.getDate());
                        endTime.setMonth(mDate.getMonth());
                        endTime.setYear(mDate.getYear());
                        if(pair.getTeacher().split(" ")[0].equals(mTeacherName)){
                            if(mDate.getTime() > endTime.getTime() && mDate.getTime() - endTime.getTime() < MILLIS_IN_15_MINUTES){
                                Intent notificationIntent = new Intent(getApplicationContext(), TeacherQuestionnaireActivity.class);
                                notificationIntent.putExtra("LECTURE_HALL", Integer.toString(pair.getLectureHall()));
                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                                        0, notificationIntent,
                                        PendingIntent.FLAG_CANCEL_CURRENT);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                                builder.setContentTitle("Оповещение")
                                        .setContentIntent(contentIntent)
                                        .setContentText("Информация об аудитории " + pair.getLectureHall())
                                        .setSmallIcon(R.drawable.student_icon)
                                        .setWhen(System.currentTimeMillis())
                                        .setAutoCancel(true);

                                NotificationManager notificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(NOTIFY_ID, builder.build());
                                break;

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setPairReference() {
        switch (mDayOfWeek) {
            case "понедельник":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                checkInfo();
                break;
            case "вторник":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("tuesday");
                checkInfo();
                break;
            case "среда":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("wednesday");
                checkInfo();
                break;
            case "четверг":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("thursday");
                checkInfo();
                break;
            case "пятница":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("friday");
                checkInfo();
                break;
            case "суббота":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("saturday");
                checkInfo();
                break;
            case "воскресенье":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                checkInfo();
                break;
        }
    }

    public String getDayOfWeek() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

        String dayOfTheWeek = sdf.format(mDate);
        return dayOfTheWeek;
    }
}
