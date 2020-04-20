package com.mani.lost_found;

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LostAndFoundRecyclerAdapter extends RecyclerView.Adapter<LostAndFoundRecyclerAdapter.ViewHolder> {
     List<LostAndFoundPost> post_list;
    public Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String username;
    private String useremail;
    private String userphoto;
    public LostAndFoundRecyclerAdapter(List<LostAndFoundPost> post_list){
      this.post_list=post_list;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseAuth=FirebaseAuth.getInstance();
        final String lostAndFoundPostId = post_list.get(position).lostAndFoundPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        final String adminemail = firebaseAuth.getCurrentUser().getEmail();
        final String user_id = post_list.get(position).getUser_id();
        if(user_id.equals(currentUserId) || adminemail.equals("manisinghasd@gmail.com")){
            holder.deletePostBtn.setVisibility(View.VISIBLE);
            holder.deletepost.setVisibility(View.VISIBLE);;
            holder.editPostBtn.setVisibility(View.VISIBLE);
            holder.editpost.setVisibility(View.VISIBLE);
        }else
        {
            holder.deletePostBtn.setVisibility(View.INVISIBLE);;
            holder.deletepost.setVisibility(View.INVISIBLE);
            holder.editPostBtn.setVisibility(View.INVISIBLE);
            holder.editpost.setVisibility(View.INVISIBLE);
        }
        final String uname= post_list.get(position).getUser_name();
        final String desc_data=post_list.get(position).getPost_desc();
         holder.setDescText(desc_data);
        final String title=post_list.get(position).getTitle();
        final String type=post_list.get(position).getPost_type();

        final String image_url = post_list.get(position).getImage_url();
        final String thumbUri = post_list.get(position).getImage_thumb();
        holder.setPostImage(image_url, thumbUri);

        database=FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        ref.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("userName").getValue(String.class);
                userphoto = dataSnapshot.child("userPhoto").getValue(String.class);
                useremail = dataSnapshot.child("UserEmail").getValue(String.class);
                holder.setUserData(username,userphoto,useremail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        long ts=post_list.get(position).getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(new Date(ts));
        holder.setTime(date);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Posts/" + lostAndFoundPostId + "/Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               long num = dataSnapshot.getChildrenCount();
               holder.setCommentCount(num);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("laf_post_id", lostAndFoundPostId);
                context.startActivity(commentIntent);

            }
        });

        holder.deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this post?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(lostAndFoundPostId);
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

        holder.editPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, EditPostActivity.class);
                commentIntent.putExtra("laf_post_id", lostAndFoundPostId);
                commentIntent.putExtra("postTitle", title);
                commentIntent.putExtra("postType",type);
                commentIntent.putExtra("postDesc",desc_data);
                commentIntent.putExtra("imageUri", image_url);
                commentIntent.putExtra("thumbUri", thumbUri);
                commentIntent.putExtra("uName",uname);
                context.startActivity(commentIntent);
            }
        });

    }

    private void deletePost(String lostAndFoundPostId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Posts").child(lostAndFoundPostId).removeValue();
    }


    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public long getLastItemDate() {
        return post_list.get(post_list.size()-1).getTimestamp();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView descView;
        private ImageView postImageView;
        private TextView postDate;
        private TextView postUserName;
        private CircleImageView postUserImage;
        private ImageView postCommentBtn;
        private ImageView deletePostBtn;
        private TextView deletepost;
        private ImageView editPostBtn;
        private TextView editpost;
        private TextView commentcount;
        private ImageView post_user_image;
        private TextView postUserEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            postCommentBtn=mView.findViewById(R.id.post_comment_icon);
            deletePostBtn=mView.findViewById(R.id.deletePost);
            deletepost=mView.findViewById((R.id.delete));
            editPostBtn=mView.findViewById(R.id.edit_post_icon);
            editpost=mView.findViewById(R.id.edit_post);
            post_user_image=mView.findViewById(R.id.post_user_image);
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.post_desc);
            descView.setText(descText);
        }

        public void setPostImage(String downloadUri, String thumbUri){
            postImageView= mView.findViewById(R.id.post_image);
            if(downloadUri.equals("NO") && thumbUri.equals("NO")){
                postImageView.setVisibility(View.INVISIBLE);
                postImageView.getLayoutParams().height = 0;
            }else{
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(postImageView);
            }
        }

        public void setTime(String date) {
            postDate = mView.findViewById(R.id.post_date);
            postDate.setText(date);
        }

        public void setUserData(String name, String image,String email){
            postUserImage = mView.findViewById(R.id.post_user_image);
            postUserName = mView.findViewById(R.id.post_user_name);
            postUserEmail=mView.findViewById(R.id.post_user_email);
            postUserName.setText(name);
            postUserEmail.setText(email);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(postUserImage);
        }
        public void setCommentCount(long num){
            commentcount=mView.findViewById(R.id.post_comment_count);
            String c=String.valueOf(num);
            String s = c+" Comment";
            commentcount.setText(s);
        }
    }


}
