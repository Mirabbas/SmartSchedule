package mirabbas.smartschedule.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mirabbas.smartschedule.ChatMessage;
import mirabbas.smartschedule.FirebaseListAdapter;
import mirabbas.smartschedule.R;


/**
 * Created by о on 27.03.2018.
 */

public class GroupChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser mUser;
    FirebaseListAdapter<ChatMessage> adapter;

    String mNameUser;
    String mGroupKey;

    FloatingActionButton mFab;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat_activity);

        mNameUser = getIntent().getStringExtra("NAME_USER");
        mGroupKey = getIntent().getStringExtra("GROUP_KEY");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(mGroupKey + "chat");

        mFab = (FloatingActionButton) findViewById(R.id.fab_group_chat);
        listView = (ListView) findViewById(R.id.list_of_messages);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input_msg);

                mDatabase.push().setValue(new ChatMessage(input.getText().toString(), mNameUser));
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
