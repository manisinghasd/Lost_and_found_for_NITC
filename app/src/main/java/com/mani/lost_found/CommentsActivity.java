package com.mani.lost_found;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    private Toolbar commentToolbar;

    private EditText comment_field;
    private ImageView comment_post_btn;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private FirebaseAuth firebaseAuth;
    Comments cmt;
    private String laf_post_id;
    private String current_user_id;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    boolean loadfirst =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentToolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        laf_post_id = getIntent().getStringExtra("laf_post_id");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        comment_list.setLayoutManager(mLayoutManager);
        comment_list.setAdapter(commentsRecyclerAdapter);
        comment_list.setHasFixedSize(true);
        cmt = new Comments();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Posts/" + laf_post_id + "/Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                        commentsList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String commentId = ds.getKey();
                            System.out.println(commentId);
                            cmt = ds.getValue(Comments.class).withID(commentId);
                            commentsList.add(cmt);
                            commentsRecyclerAdapter.notifyDataSetChanged();
                        }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment_message = comment_field.getText().toString();
                if (!comment_message.isEmpty()) {
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", comment_message);
                    commentsMap.put("user_id", current_user_id);
                    commentsMap.put("postId",laf_post_id);
                    commentsMap.put("timestamp", ServerValue.TIMESTAMP);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("Posts/" + laf_post_id + "/Comments").push()
                            .setValue(commentsMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(CommentsActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        comment_field.setText("");
                                    }


                                }
                            });
                }
                else{
                    Toast.makeText(CommentsActivity.this, "Write Somthing ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
