package com.direct.apps.user.direct;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    MessageListAdapter messageListAdapter;
    RecyclerView messagesRV;
    EditText messageTV;
    Button sendMessageBtn;
    List<Message> messageList;
    SimpleDateFormat simpleDateFormat;
    Dialog currentDialog;
    String currentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        messagesRV = findViewById(R.id.reyclerview_message_list);
        messagesRV.setLayoutManager(new LinearLayoutManager(MessagesActivity.this));
        messageTV = findViewById(R.id.edittext_chatbox);
        sendMessageBtn = findViewById(R.id.button_chatbox_send);

        messageList = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
/*
        App.getInstance().myRef.orderByChild("dialogId").equalTo(getIntent().getIntExtra("dialog", 0)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dialog dialog = dataSnapshot.getValue(Dialog.class);

                Toast.makeText(MessagesActivity.this, dialog.getDialogId() + "", Toast.LENGTH_SHORT).show();


                currentDialog = dialog;
                currentKey = dataSnapshot.getKey();
                messageList = dialog.getMessages();
                messageListAdapter = new MessageListAdapter(MessagesActivity.this, messageList);
                messagesRV.setAdapter(messageListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        App.getInstance().myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot item :
                            dataSnapshot.getChildren()) {
                        if (item.getValue(Dialog.class).getDialogId() == getIntent().getIntExtra("dialog", 0)) {
                            currentDialog = item.getValue(Dialog.class);
                            currentKey = item.getKey();
                            messageList = currentDialog.getMessages();
                            messageListAdapter = new MessageListAdapter(MessagesActivity.this, messageList);
                            messagesRV.setAdapter(messageListAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra("role").equals("user")){
                    Message message = new Message();
                    message.setAuthor(App.getInstance().receiver);
                    message.setDate(simpleDateFormat.format(new Date()));
                    message.setMessage(messageTV.getText().toString());

                    if (currentDialog.getMessages() == null){
                        currentDialog.setMessages(new ArrayList<Message>());
                    }

                    currentDialog.addMessage(message);
                    Map<String, Object> map = new HashMap<>();
                    map.put(currentKey, currentDialog);
                    App.getInstance().myRef.updateChildren(map);
                    messageListAdapter.notifyDataSetChanged();
                }else{
                    Message message = new Message();
                    message.setAuthor(App.getInstance().sender);
                    message.setDate(simpleDateFormat.format(new Date()));
                    message.setMessage(messageTV.getText().toString());

                    if (currentDialog.getMessages() == null){
                        currentDialog.setMessages(new ArrayList<Message>());
                    }

                    currentDialog.addMessage(message);
                    Map<String, Object> map = new HashMap<>();
                    map.put(currentKey, currentDialog);
                    App.getInstance().myRef.updateChildren(map);
                    messageListAdapter.notifyDataSetChanged();
                }
                messageTV.setText("");
            }
        });
    }
}
