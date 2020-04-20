package com.mani.lost_found;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private String laf_post_id;
    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;

    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        firebaseAuth=FirebaseAuth.getInstance();
        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);
        final String curr_user_id=firebaseAuth.getCurrentUser().getUid();
        final String user_id = commentsList.get(position).getUser_id();
        final String commentId=commentsList.get(position).commentId;
        if(user_id.equals(curr_user_id)){
            holder.deleteCommentBtn.setVisibility(View.VISIBLE);;
            holder.deleteCommentBtn.setVisibility(View.VISIBLE);;
        }else
        {
            holder.deleteCommentBtn.setVisibility(View.INVISIBLE);;
            holder.deleteCommentBtn.setVisibility(View.INVISIBLE);;
        }
        laf_post_id=commentsList.get(position).getPostId();
        database= FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        ref.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                String username = dataSnapshot.child("userName").getValue(String.class);
                String userPhoto = dataSnapshot.child("userPhoto").getValue(String.class);
                System.out.println(username+"     "+userPhoto);
                holder.setUserData(username,userPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.deleteCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){


                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this comment?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database=FirebaseDatabase.getInstance();
                        ref = database.getReference();
                        ref.child("Posts").child(laf_post_id).child("Comments").child(commentId).removeValue();
                        Intent commentIntent = new Intent(context, CommentsActivity.class);
                        commentIntent.putExtra("laf_post_id", laf_post_id);
                        context.startActivity(commentIntent);

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });
                builder.create().show();
            }
        });
    }
    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;
        private TextView commentUserName;
        private CircleImageView commentUserImage;

        private ImageView deleteCommentBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            deleteCommentBtn=mView.findViewById(R.id.delete_comment);
        }
        public void setComment_message(String message){
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }
        public void setUserData(String name, String image){
            commentUserImage = mView.findViewById(R.id.comment_image);
            commentUserName = mView.findViewById(R.id.comment_username);
            commentUserName.setText(name);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentUserImage);
        }


    }

}

