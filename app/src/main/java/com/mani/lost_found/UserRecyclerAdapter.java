package com.mani.lost_found;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {
    public List<Users> usersList;
    public Context context;

    public UserRecyclerAdapter(List<Users> usersList){
        this.usersList=usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);
        context = parent.getContext();
        return new UserRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

      final String un=usersList.get(position).getUserName();
      final String ue=usersList.get(position).getUserEmail();
      final String up=usersList.get(position).getUserPhoto();
      holder.setUserData(un,ue,up);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private ImageView userphoto;
        private TextView username;
        private TextView useremail;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserData(String name,String email, String image){
             userphoto = mView.findViewById(R.id.user_image);
             username = mView.findViewById(R.id.username);
             useremail=mView.findViewById(R.id.useremail);
             username.setText(name);
             useremail.setText(email);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(userphoto);
        }
    }
}
