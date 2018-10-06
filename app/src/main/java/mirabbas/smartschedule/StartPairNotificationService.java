package mirabbas.smartschedule;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class StartPairNotificationService extends Service {

    private static final int NOTIFY_ID = 101 ;
    final long MILLIS_IN_HOUR_AND_HALF = 5400000;

    DatabaseReference mReference;
    Date mDate;
    String mDayOfWeek;
    int mNumWeek;
    String mGroupKey;
    int mCount;
    boolean isStarted;

    public StartPairNotificationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mCount = 0;
        isStarted = false;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGroupKey = intent.getStringExtra("GROUP_KEY");
        mDate = new Date();
        mNumWeek = getNumberWeek();
        mDayOfWeek = getDayOfWeek();
        setPairReference();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDate = new Date();
                if(!isStarted) checkFirstPair();
            }
        }, 0L, 60L*1000);
        return super.onStartCommand(intent, flags, startId);
    }

    public void setPairReference() {
        switch (mDayOfWeek) {
            case "понедельник":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                checkFirstPair();
                break;
            case "вторник":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("tuesday");
                checkFirstPair();
                break;
            case "среда":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("wednesday");
                checkFirstPair();
                break;
            case "четверг":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("thursday");
                checkFirstPair();
                break;
            case "пятница":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("friday");
                checkFirstPair();
                break;
            case "суббота":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("saturday");
                checkFirstPair();
                break;
            case "воскресенье":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                checkFirstPair();
                break;
        }
    }

    public void setNextPairReference() {
        switch (mDayOfWeek) {
            case "понедельник":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("tuesday");
                goToNextDay();
                checkFirstPair();
                break;
            case "вторник":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("wednesday");
                goToNextDay();
                checkFirstPair();
                break;
            case "среда":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("thursday");
                goToNextDay();
                checkFirstPair();
                break;
            case "четверг":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("friday");
                goToNextDay();
                checkFirstPair();
                break;
            case "пятница":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("saturday");
                goToNextDay();
                checkFirstPair();
                break;
            case "суббота":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                goToMonday();
                checkFirstPair();
                break;
            case "воскресенье":
                mReference = FirebaseDatabase.getInstance().getReference().child("schedule")
                        .child(mGroupKey).child("monday");
                mNumWeek++;
                mDayOfWeek = getDayOfWeek();
                goToNextDay();
                checkFirstPair();
                break;
        }
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

    public void checkFirstPair(){

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Date startTime = null;
                Date endTime = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mCount++;

                    Pair pair = snapshot.getValue(Pair.class);
                    if(pair.getFlag() == 1){
                        try {
                            startTime = dateFormat.parse(pair.getStartTime());
                            startTime.setDate(mDate.getDate());
                            startTime.setMonth(mDate.getMonth());
                            startTime.setYear(mDate.getYear());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        isStarted = true;
                            if(startTime.getTime() - mDate.getTime() <= MILLIS_IN_HOUR_AND_HALF){
                                Log.d("START", startTime.toString());
                                Log.d("CUR", mDate.toString());
                                Log.d("DIF", Long.toString(startTime.getTime() - mDate.getTime()));
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                                builder.setContentTitle("Скоро начало пары!")
                                        .setContentText(pair.getPairName() + " " + pair.getStartTime())
                                        .setSmallIcon(R.mipmap.ic_launcher)
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
