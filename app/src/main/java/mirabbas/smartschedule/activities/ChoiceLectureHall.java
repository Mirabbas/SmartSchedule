package mirabbas.smartschedule.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import mirabbas.smartschedule.InfoLectureHall;
import mirabbas.smartschedule.LectureHallAdapter;
import mirabbas.smartschedule.R;

public class ChoiceLectureHall extends AppCompatActivity {

    DatabaseReference mReference;
    ArrayList<Integer> listOfLectHalls;
    ListView listView;
    LectureHallAdapter adapter;

    boolean mBoard;
    boolean mComputer;
    boolean mProjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_lecture_hall_activity);

        listOfLectHalls = getIntent().getIntegerArrayListExtra("LECTURE_HALLS");
        listView = (ListView) findViewById(R.id.list_lect_halls);

        mBoard = getIntent().getBooleanExtra("IS_BOARD", false);
        mComputer = getIntent().getBooleanExtra("IS_COMPUTER", false);
        mProjector = getIntent().getBooleanExtra("IS_PROJECTOR", false);

        mReference = FirebaseDatabase.getInstance().getReference().child("infoLectureHall");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, ArrayList<Boolean>> map = new LinkedHashMap<String, ArrayList<Boolean>>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(int i = 0; i < listOfLectHalls.size(); i++){
                        if(snapshot.getKey().equals(Integer.toString(listOfLectHalls.get(i)))){
                            InfoLectureHall info = snapshot.getValue(InfoLectureHall.class);
                            ArrayList<Boolean> list = new ArrayList<Boolean>();
                            list.add(info.isBoard());
                            list.add(info.isComputer());
                            list.add(info.isProjector());
                            map.put(Integer.toString(listOfLectHalls.get(i)), list);
                        }
                    }
                }
                    adapter = new LectureHallAdapter(ChoiceLectureHall.this, R.layout.lecture_halls_row, listOfLectHalls, (LinkedHashMap<String, ArrayList<Boolean>>) map);
                    listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
