package com.mani.lost_found;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {
    private Toolbar admin_toolbar;
    private Button allPost;
    private Button userDetails;
    private Button logout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mAuth=FirebaseAuth.getInstance();
       FirebaseUser user=mAuth.getCurrentUser();
       if(user!=null) {
           admin_toolbar = findViewById(R.id.adminTollbar);
           setSupportActionBar(admin_toolbar);
           getSupportActionBar().setTitle("Admin");


           allPost = findViewById(R.id.all_posts_btn);
           userDetails = findViewById(R.id.user_details_btn);
           logout = findViewById(R.id.admin_logout_btn);

           allPost.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String admin="mani";
                   Intent mainIntent = new Intent(AdminActivity.this, MainActivity.class);
                   mainIntent.putExtra("Admin",admin);
                   startActivity(mainIntent);
               }
           });

           userDetails.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent userIntent = new Intent(AdminActivity.this, UserDetailsActivity.class);
                   startActivity(userIntent);
               }
           });

           logout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   logOut();
               }
           });
       }
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser curruser= FirebaseAuth.getInstance().getCurrentUser();
        if(curruser==null)
        {
            Intent mainIntent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private void logOut() { //funtion
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(AdminActivity.this,
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut(); //signout firebase
                        Intent setupIntent = new Intent(getBaseContext(),LoginActivity.class);
                        Toast.makeText(getBaseContext(), "Admin Logged Out", Toast.LENGTH_LONG).show(); //if u want to show some text
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        finish();
                    }
                });
    }


}
