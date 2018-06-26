package com.direct.apps.user.direct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ListView dialogsView;
    FirebaseListAdapter<Dialog> dialogs;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson from = new Gson();
        Gson to = new Gson();

        final User userFrom = from.fromJson(getIntent().getStringExtra("from"), User.class);
        final User userTo = to.fromJson(getIntent().getStringExtra("to"), User.class);

        App.getInstance().sender = userFrom;
        App.getInstance().receiver = userTo;


        if (getIntent().getStringExtra("role").equals("user")) {
            query = App.getInstance().myRef.orderByChild("receiver/userId").equalTo(userTo.getUserId());
        } else {
            query = App.getInstance().myRef.orderByChild("sender/userId").equalTo(userFrom.getUserId());
        }


        //if (userFrom == null || userTo == null)
        //finish();

        dialogsView = findViewById(R.id.dialogsListView);

        dialogs = new FirebaseListAdapter<Dialog>(this, Dialog.class, R.layout.invite_item, query) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void populateView(View v, Dialog model, int position) {

                ((CircleImageView) v.findViewById(R.id.userFromView)).setImageBitmap(Helper.createIconForUser(userFrom.getUserName().charAt(0), MainActivity.this));
                ((CircleImageView) v.findViewById(R.id.userToView)).setImageBitmap(Helper.createIconForUser(userTo.getUserName().charAt(0), MainActivity.this));
                ((TextView) v.findViewById(R.id.inviteTextView)).setText(model.getSender().getUserName()
                        + "/"
                        + model.getReceiver().getUserName());
            }
        };

        dialogsView.setAdapter(dialogs);
        dialogsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                intent.putExtra("dialog", dialogs.getItem(i).getDialogId());
                intent.putExtra("role", getIntent().getStringExtra("role"));
                startActivity(intent);
            }
        });

        App.getInstance().myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() >= 0) {

                    boolean flag = false;

                    for (DataSnapshot item :
                            dataSnapshot.getChildren()) {
                        if (item.getValue(Dialog.class).getReceiver().getUserId() == userTo.getUserId()
                                && item.getValue(Dialog.class).getSender().getUserId() == userFrom.getUserId()) {
                            flag = true;
                        }
                    }

                    if (!flag) {
                        Dialog dialog = new Dialog();
                        dialog.setDialogId(userFrom.getUserId() + userTo.getUserId());
                        dialog.setMessages(new ArrayList<Message>());
                        dialog.setSender(userFrom);
                        dialog.setReceiver(userTo);

                        App
                                .getInstance()
                                .myRef
                                .push()
                                .setValue(dialog);

                        Toast.makeText(MainActivity.this, "Dialog is created", Toast.LENGTH_SHORT).show();
                    } else {
                        flag = false;
                        Toast.makeText(MainActivity.this, "Dialog is exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

