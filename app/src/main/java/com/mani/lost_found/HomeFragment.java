package com.mani.lost_found;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView post_list_view;
    private List<LostAndFoundPost> post_list;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    LostAndFoundPost lfp;
    private LostAndFoundRecyclerAdapter lostAndFoundRecyclerAdapter;
    public HomeFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        post_list_view=view.findViewById(R.id.post_list_view);
        post_list=new ArrayList<>();
        lostAndFoundRecyclerAdapter = new LostAndFoundRecyclerAdapter(post_list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        post_list_view.setLayoutManager(mLayoutManager);
        post_list_view.setAdapter(lostAndFoundRecyclerAdapter);
        post_list_view.setHasFixedSize(true);
        loadPosts();

        // Inflate the layout for this fragment
        return view;
    }

    private void loadPosts() {
        if(firebaseAuth.getCurrentUser() != null) {
            lfp = new LostAndFoundPost();
            database = FirebaseDatabase.getInstance();
            Query ref = database.getReference("Posts").orderByChild("timestamp");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        post_list.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            System.out.println(key);
                            lfp = ds.getValue(LostAndFoundPost.class).withID(key);
                            post_list.add(lfp);
                            lostAndFoundRecyclerAdapter.notifyDataSetChanged();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         MenuItem item= menu.findItem(R.id.action_search_btn);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    searchPost(query);
                }else{
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)){
                    searchPost(newText);
                }else{
                    loadPosts();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    private void searchPost(String Query) {
        final String SearchQuery=Query;
        lfp = new LostAndFoundPost();
        database = FirebaseDatabase.getInstance();
        Query ref = database.getReference("Posts").orderByChild("timestamp");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    post_list.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        System.out.println(key);
                        lfp = ds.getValue(LostAndFoundPost.class).withID(key);
                        if(lfp.getUser_name().toLowerCase().contains(SearchQuery.toLowerCase())||lfp.getPost_type().toLowerCase().contains(SearchQuery.toLowerCase())||lfp.getTitle().toLowerCase().contains(SearchQuery.toLowerCase()) || lfp.getPost_desc().toLowerCase().contains(SearchQuery.toLowerCase()))
                        {
                            post_list.add(lfp);
                            lostAndFoundRecyclerAdapter.notifyDataSetChanged();
                        }
                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout_btn:
                //logOut();
                return true;
            default:
                return false;
        }
    }





}
