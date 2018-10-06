package mirabbas.smartschedule.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mirabbas.smartschedule.InfoLectureHall;
import mirabbas.smartschedule.R;

public class TeacherQuestionnaireActivity extends AppCompatActivity {

    ListView choiceList;
    TextView textView;
    Button confirmBtn;

    String checked;
    DatabaseReference mReference;

    InfoLectureHall infoLectureHall;
    String lectureHall;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_questionnaire);

        choiceList = (ListView) findViewById(R.id.questionnaire_list_view);
        textView = (TextView) findViewById(R.id.questionnaire_tv);
        confirmBtn = (Button) findViewById(R.id.questionnaire_confirm_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.questionnaire_list_fields));
        choiceList.setAdapter(adapter);

        lectureHall = getIntent().getStringExtra("LECTURE_HALL");
        textView.append(" ");
        textView.append(lectureHall);

        choiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int count = getResources().getStringArray(R.array.questionnaire_list_fields).length;
                SparseBooleanArray sparseBooleanArray = choiceList.getCheckedItemPositions();
                infoLectureHall = new InfoLectureHall();
                for (int j = 0; j < count; j++) {
                    if (sparseBooleanArray.get(j)) {
                        if(j == 0) infoLectureHall.setBoard(true);
                        if(j == 1) infoLectureHall.setComputer(true);
                        if(j == 2) infoLectureHall.setProjector(true);
                    }
                    else {
                        if(j == 0) infoLectureHall.setBoard(false);
                        if(j == 1) infoLectureHall.setComputer(false);
                        if(j == 2) infoLectureHall.setProjector(false);
                    }
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(infoLectureHall != null){
                    Log.d("INFO", lectureHall);
                    mReference = FirebaseDatabase.getInstance().getReference().child("infoLectureHall").child(lectureHall);
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mReference.setValue(infoLectureHall);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
