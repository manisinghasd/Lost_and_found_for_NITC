package com.mani.lost_found;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsActivity extends AppCompatActivity {
    private Toolbar userToolbar;
    private List<Users> usersList;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private UserRecyclerAdapter userRecyclerAdapter;
    private FirebaseAuth firebaseAuth;
    private RecyclerView user_list;
    Users user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        userToolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(userToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        user_list = findViewById(R.id.user_recycler);
        usersList = new ArrayList<>();
        userRecyclerAdapter = new UserRecyclerAdapter(usersList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        user_list.setLayoutManager(mLayoutManager);
        user_list.setAdapter(userRecyclerAdapter);
        user_list.setHasFixedSize(true);
        user = new Users();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        user= ds.getValue(Users.class);
                        usersList.add(user);
                        userRecyclerAdapter.notifyDataSetChanged();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
