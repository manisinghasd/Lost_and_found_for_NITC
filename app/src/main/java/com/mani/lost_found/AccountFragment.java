package com.mani.lost_found;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private TextView username;
    private TextView useremail;
    private ImageView userimage;
    private String user_id;
    private Toolbar view_toolbar;
    private FirebaseAuth firebaseAuth;

    public AccountFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        username=view.findViewById(R.id.user_name);
        useremail=view.findViewById(R.id.user_email);
        userimage=view.findViewById(R.id.user_image);
        final String personName = user.getDisplayName();
        final String Email = user.getEmail();
        final Uri image = user.getPhotoUrl();
        username.setText(personName);
        useremail.setText(Email);
        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.default_image);
        Glide.with(view.getRootView().getContext()).setDefaultRequestOptions(placeholderRequest).load(image).into(userimage);
        return view;
    }
}
