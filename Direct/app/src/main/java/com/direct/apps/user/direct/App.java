package com.direct.apps.user.direct;

import android.app.Application;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class App extends Application {
    private static final App ourInstance = new App();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firebaseDatabase.getReference("direct-ca8ef ");

    User sender;
    User receiver;

    static App getInstance() {
        return ourInstance;
    }

    private App() {}
}
