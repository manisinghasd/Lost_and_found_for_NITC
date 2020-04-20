package com.mani.lost_found;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.google.firebase.database.ServerValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import id.zelory.compressor.Compressor;
import io.reactivex.annotations.NonNull;

public class NewPostActivity extends AppCompatActivity {

  private Toolbar newPostToolbar;
  private ImageView newPostImage;
  private EditText newPostTitle;
  private Spinner category;
  private EditText newPostDesc;
  private Button newPostBtn;
  private Uri postImageUri = null;
  private ProgressBar newPostProgress;
  private StorageReference storageReference;
  private FirebaseAuth firebaseAuth;
  private String current_user_id;
  private Bitmap compressedImageFile;
  private String curr_username;
  private String currImage;
  @Override

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_post);

    newPostToolbar = findViewById(R.id.new_post_toolbar);
    setSupportActionBar(newPostToolbar);
    getSupportActionBar().setTitle("Make New Post");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    storageReference = FirebaseStorage.getInstance().getReference();
    firebaseAuth = FirebaseAuth.getInstance();
    current_user_id = firebaseAuth.getCurrentUser().getUid();
    curr_username= firebaseAuth.getCurrentUser().getDisplayName();
    currImage=firebaseAuth.getCurrentUser().getPhotoUrl().toString();

    newPostImage = findViewById(R.id.new_post_image);
    newPostDesc = findViewById(R.id.new_post_desc);
    newPostBtn = findViewById(R.id.make_new_post_btn);
    newPostProgress = findViewById(R.id.new_post_progress);
    category=findViewById(R.id.new_post_cat);
    newPostTitle=findViewById(R.id.new_post_title);


    newPostImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(NewPostActivity.this);

      }
    });


    newPostBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        final String desc = newPostDesc.getText().toString();
        final String type = (String) category.getSelectedItem();
        final String title=newPostTitle.getText().toString();

        if(!TextUtils.isEmpty(desc) && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(title) ) {

          newPostProgress.setVisibility(View.VISIBLE);

          final String randomName = UUID.randomUUID().toString();

          // PHOTO UPLOAD
          if (postImageUri != null) {
          File newImageFile = new File(postImageUri.getPath());
          try {

            compressedImageFile = new Compressor(NewPostActivity.this)
                    .setMaxHeight(720)
                    .setMaxWidth(720)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

          } catch (IOException e) {
            e.printStackTrace();
          }

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
          byte[] imageData = baos.toByteArray();

          // PHOTO UPLOAD
          final StorageReference ref = storageReference.child("post_images").child(randomName + ".jpg");
          UploadTask filePath = ref.putBytes(imageData);
          filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

              Task<Uri> urlTask = task.getResult().getStorage().getDownloadUrl();
              while (!urlTask.isSuccessful()) ;
              Uri du = urlTask.getResult();
              final String downloadUri = du.toString();

              if (task.isSuccessful()) {

                File newThumbFile = new File(postImageUri.getPath());
                try {

                  compressedImageFile = new Compressor(NewPostActivity.this)
                          .setMaxHeight(100)
                          .setMaxWidth(100)
                          .setQuality(1)
                          .compressToBitmap(newThumbFile);

                } catch (IOException e) {
                  e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbData = baos.toByteArray();

                UploadTask uploadTask = storageReference.child("post_images/thumbs")
                        .child(randomName + ".jpg").putBytes(thumbData);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                  @RequiresApi(api = Build.VERSION_CODES.O)
                  @Override
                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri dtu = urlTask.getResult();
                    String downloadthumbUri = dtu.toString();

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("post_type", type);
                    postMap.put("title", title);
                    postMap.put("post_desc", desc);
                    postMap.put("user_id", current_user_id);
                    postMap.put("user_name", curr_username);
                    postMap.put("image_url", downloadUri);
                    postMap.put("image_thumb", downloadthumbUri);
                    postMap.put("timestamp", ServerValue.TIMESTAMP);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("Posts").push()
                            .setValue(postMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                  notification(type, " created", currImage);
                                  Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                  Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                  startActivity(mainIntent);
                                  finish();

                                } else {

                                  //Error
                                }

                                newPostProgress.setVisibility(View.INVISIBLE);

                              }
                            });

                  }
                }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {

                    //Error

                  }
                });
              } else {

                newPostProgress.setVisibility(View.INVISIBLE);

              }

            }
          });
        }else{

            Map<String, Object> postMap = new HashMap<>();
            postMap.put("post_type", type);
            postMap.put("title", title);
            postMap.put("post_desc", desc);
            postMap.put("user_id", current_user_id);
            postMap.put("user_name", curr_username);
            postMap.put("image_url","NO");
            postMap.put("image_thumb","NO");
            postMap.put("timestamp", ServerValue.TIMESTAMP);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Posts").push()
                    .setValue(postMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                          notification(type, " created", currImage);
                          Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                          Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                          startActivity(mainIntent);
                          finish();

                        } else {

                          //Error
                        }

                        newPostProgress.setVisibility(View.INVISIBLE);

                      }
                    });
          }
        }else{
          Toast.makeText(NewPostActivity.this, "Fill All Field. ", Toast.LENGTH_LONG).show(); //if u want to show some text
        }
      }
    });
  }

  private void notification(String type,String m,String Image){
    String name=firebaseAuth.getCurrentUser().getDisplayName();
    Map<String, Object> NotMap = new HashMap<>();
    String msg=name +  m + " a " + type + " post.";
    NotMap.put("Notification",msg);
    NotMap.put("userImage",Image);
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    ref.child("Notification").push()
            .setValue(NotMap);

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
     {
         NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
         NotificationManager manager=getSystemService(NotificationManager.class);
         manager.createNotificationChannel(channel);
     }
      NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"n")
              .setContentTitle("Lost & Found")
              .setSmallIcon(R.drawable.ic_notifications_black_24dp)
              .setAutoCancel(true)
              .setContentText(msg);

      NotificationManagerCompat managerCompat= NotificationManagerCompat.from(this);
      managerCompat.notify(999,builder.build());
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK) {

        postImageUri = result.getUri();
        newPostImage.setImageURI(postImageUri);

      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

        Exception error = result.getError();

      }
    }



  }



}