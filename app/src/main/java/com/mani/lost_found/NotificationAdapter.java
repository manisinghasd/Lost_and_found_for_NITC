package com.mani.lost_found;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.TaskExecutor;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    List<NotificationCl> Noti_list;
    private Context context;


    public NotificationAdapter(List<NotificationCl> Noti_list){
        this.Noti_list=Noti_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String noti=Noti_list.get(position).getNotification();
        final String img=Noti_list.get(position).getUserImage();
        holder.setNoti(noti,img);

    }

    @Override
    public int getItemCount() {
        return Noti_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView notitext;
        private ImageView userphoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setNoti(String text,String img){
            notitext=mView.findViewById(R.id.noti_txt);
            userphoto=mView.findViewById(R.id.noti_user_image);
            notitext.setText(text);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(img).into(userphoto);
        }
    }
}
