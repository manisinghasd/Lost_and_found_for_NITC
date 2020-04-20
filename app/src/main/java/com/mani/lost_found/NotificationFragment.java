package com.mani.lost_found;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
  private RecyclerView noti_list_view;
  private List<NotificationCl> noti_list;
  private FirebaseDatabase database;
  private DatabaseReference ref;
  private FirebaseAuth firebaseAuth;
  private NotificationAdapter notificationAdapter;
 NotificationCl notificationCl;
  public NotificationFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view= inflater.inflate(R.layout.fragment_notification, container, false);
      noti_list=new ArrayList<>();
    noti_list_view=view.findViewById(R.id.notification_view);
    notificationAdapter = new NotificationAdapter(noti_list);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
    mLayoutManager.setReverseLayout(true);
    mLayoutManager.setStackFromEnd(true);
    noti_list_view.setLayoutManager(mLayoutManager);
    noti_list_view.setAdapter(notificationAdapter);
    noti_list_view.setHasFixedSize(true);
    loadNotification();
    return view;
  }

  private void loadNotification() {
    notificationCl=new NotificationCl();
    database = FirebaseDatabase.getInstance();
    ref = database.getReference("Notification");
    ref.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()){
          noti_list.clear();
          int i=0;
          for (DataSnapshot ds : dataSnapshot.getChildren()) {
            notificationCl = ds.getValue(NotificationCl.class);
            noti_list.add(notificationCl);
            System.out.println(notificationCl.getNotification());
            notificationAdapter.notifyDataSetChanged();
          }
        }
      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });

  }

}