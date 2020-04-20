package com.mani.lost_found;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
  private SignInButton gsignIn;
  public static final int RC_SIGN_IN = 1;
  GoogleSignInClient mGoogleSignInClient;
  private FirebaseAuth mAuth;
  public static final String TAG = "LoginActivity";
  private FirebaseFirestore firebaseFirestore;
  private StorageReference storageReference;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    mAuth = FirebaseAuth.getInstance();
    gsignIn = (SignInButton) findViewById(R.id.sign_in_button);
    // Configure Google Sign In
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    gsignIn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        signIn();
      }
    });



  }

  @Override
  protected void onStart() {
    super.onStart();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    if(currentUser != null){

      sendToMain();

    }
  }



  private void signIn() {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = task.getResult(ApiException.class);
        String mail=account.getEmail();
        String email=mail;
        String str = email.substring(mail.length() - 11);
        if (!str.equalsIgnoreCase("@nitc.ac.in") && !mail.equals("manisinghasd@gmail.com")) {
           logOut();
        }else{
             firebaseAuthWithGoogle(account);
        }
      } catch (ApiException e) {
        // Google Sign In failed, update UI appropriately
        Log.w(TAG, "Google sign in failed", e);
        // ...
      }
    }
  }

    private void logOut() {
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this,
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut(); //signout firebase
                        Intent setupIntent = new Intent(getBaseContext(),LoginActivity.class);
                        Toast.makeText(getBaseContext(), "Login With NITC Email Id", Toast.LENGTH_LONG).show(); //if u want to show some text
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        finish();
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  // Sign in success, update UI with the signed-in user's information
                  Log.d(TAG, "signInWithCredential:success");
                  FirebaseUser user = mAuth.getCurrentUser();
                  String admin = user.getEmail();
                  if(admin.equals("manisinghasd@gmail.com")){
                    updateUI(user);
                    sendToAdmin();
                  }else {
                    updateUI(user);
                    sendToMain();
                  }
                } else {
                  // If sign in fails, display a message to the user.
                  Log.w(TAG, "signInWithCredential:failure", task.getException());
                  Toast.makeText(LoginActivity.this, "you are not able to log in to google", Toast.LENGTH_LONG).show();
                  // updateUI(null);
                }

                // ...
              }
            });
  }


  private void updateUI(FirebaseUser user) {

    if (user != null) {
      final String personName = user.getDisplayName();
      final String Email = user.getEmail();
      final String personId = user.getUid();
      final Uri image = user.getPhotoUrl();
      // Toast.makeText(this, "Name of the user :" + personName + " user id is : " + personId + "Email id" +  Email, Toast.LENGTH_SHORT).show();
      Map<String, String> userData = new HashMap<>();
      userData.put("userName", personName);
      userData.put("UserEmail", Email);
      userData.put("userPhoto", image.toString());
      DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
      ref.child("users").child(personId)
              .setValue(userData)
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  Log.i(TAG, "onComplete: ");

                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.i(TAG, "onFailure: "+e.toString());
                }
              }).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          Log.i(TAG, "onSuccess: ");
        }
      });

    }
  }
  private void sendToMain(){
    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(mainIntent);
    finish();
  }
  private void sendToAdmin(){
    Intent adminIntent = new Intent(LoginActivity.this,AdminActivity.class);
    startActivity(adminIntent);
    finish();
  }

}
