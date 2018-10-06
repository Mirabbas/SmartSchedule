package mirabbas.smartschedule.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mirabbas.smartschedule.Group;
import mirabbas.smartschedule.Pair;
import mirabbas.smartschedule.R;

/**
 * Created by о on 01.04.2018.
 */

public class ListGroupsActivity extends BaseActivity {

    DatabaseReference mReference;
    DatabaseReference mDatabase;
    String mTeacherLastName;
    String mTeacherKey;
    String mGroupKey;

    ArrayList<String> mGroupList;
    ListView listView;

    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_activity);

        mTeacherLastName = getIntent().getStringExtra("TEACHER");
        mTeacherKey = getIntent().getStringExtra("TEACHER_KEY");

        listView = (ListView) findViewById(R.id.list_groups);

        getListGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                final String groupName = textView.getText().toString();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("schedule");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Group group = snapshot.getValue(Group.class);
                            if(group.getNameGroup().equals(groupName)){
                                mGroupKey = snapshot.getKey();
                                break;
                            }
                        }
                        Intent intent = new Intent(ListGroupsActivity.this, GroupTeacherChatActivity.class);
                        intent.putExtra("GROUP_KEY", mGroupKey);
                        intent.putExtra("USER_NAME", mTeacherLastName);
                        intent.putExtra("TEACHER_KEY", mTeacherKey);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void getListGroups(){
        mGroupList = new ArrayList<String>();
        mReference = FirebaseDatabase.getInstance().getReference().child("schedule");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showProgressDialog();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Group group = snapshot.getValue(Group.class);
                    outer : for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        for(DataSnapshot snapshot2 : snapshot1.getChildren()){
                            Pair pair = snapshot2.getValue(Pair.class);
                            if(pair.getFlag() == 1){
                                if(pair.getTeacher().contains(mTeacherLastName)) {
                                    mGroupList.add(group.getNameGroup());
                                    break outer;
                                }
                            }
                        }
                    }
                }
                adapter = new ArrayAdapter<String>(ListGroupsActivity.this, R.layout.row_view, mGroupList);
                listView.setAdapter(adapter);
                hideProgressDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
