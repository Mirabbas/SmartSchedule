package mirabbas.smartschedule.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mirabbas.smartschedule.ChatMessage;
import mirabbas.smartschedule.FirebaseListAdapter;
import mirabbas.smartschedule.R;

/**
 * Created by о on 02.04.2018.
 */

public class GroupTeacherChatActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    FirebaseListAdapter<ChatMessage> adapter;

    String mUserName;
    String mGroupKey;
    String mTeacherKey;

    FloatingActionButton mFab;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_teacher_chat_activity);

        mUserName = getIntent().getStringExtra("USER_NAME");
        mGroupKey = getIntent().getStringExtra("GROUP_KEY");
        mTeacherKey = getIntent().getStringExtra("TEACHER_KEY");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(mTeacherKey + mGroupKey + "chat");

        mFab = (FloatingActionButton) findViewById(R.id.fab_teacher_chat);
        listView = (ListView) findViewById(R.id.list_of_messages_with_teacher);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input_msgt);

                mDatabase.push().setValue(new ChatMessage(input.getText().toString(), mUserName));
                input.setText("");
            }
        });

        displayChats();

    }

    public void displayChats(){
        adapter = new FirebaseListAdapter<ChatMessage>(mDatabase, ChatMessage.class, R.layout.message_view, this) {
            @Override
            protected void populateView(View v, ChatMessage model) {

                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                        model.getMessageTime()));
            }

        };

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
