package com.mani.lost_found;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class EditPostActivity extends AppCompatActivity {

    private Toolbar editPostToolbar;
    private ImageView editPostImage;
    private EditText editPostTitle;
    private Spinner category;
    private EditText editPostDesc;
    private Button editPostBtn;
    private Uri postImageUri = null;
    private ProgressBar editPostProgress;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private Bitmap compressedImageFile;
    private String laf_post_id;
    private String postTitle;
    private String postType;
    private String postDesc;
    private String imageUri;
    private String uName;
    private String thumbUri;

    @SuppressLint("CheckResult")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editPostToolbar = findViewById(R.id.edit_post_toolbar);
        setSupportActionBar(editPostToolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        laf_post_id = getIntent().getStringExtra("laf_post_id");
        postTitle = getIntent().getStringExtra("postTitle");
        postType = getIntent().getStringExtra("postType");
        postDesc = getIntent().getStringExtra("postDesc");
        imageUri = getIntent().getStringExtra("imageUri");
        thumbUri=getIntent().getStringExtra("thumbUri");
        uName=getIntent().getStringExtra("uName");
        editPostImage = findViewById(R.id.edit_post_image);
        editPostDesc = findViewById(R.id.edit_post_desc);
        editPostBtn = findViewById(R.id.make_edit_post_btn);
        editPostProgress = findViewById(R.id.edit_post_progress);
        category = findViewById(R.id.edit_post_cat);
        editPostTitle = findViewById(R.id.edit_post_title);

        System.out.println(postTitle);
        editPostTitle.setText(postTitle);
        editPostDesc.setText(postDesc);

        if(!imageUri.equals("NO")) {
            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.default_image);
            Glide.with(EditPostActivity.this).setDefaultRequestOptions(placeholderRequest).load(imageUri).into(editPostImage);
        }

        editPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(EditPostActivity.this);

            }
        });

        editPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


        String ty = (String) category.getSelectedItem();
        if (ty.equals("Category")) {
            ty = postType;
        }

        final String desc = editPostDesc.getText().toString();
        final String type = ty;
        final String title = editPostTitle.getText().toString();

        if (!TextUtils.isEmpty(desc) && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(title)) {

            editPostProgress.setVisibility(View.VISIBLE);

            final String randomName = UUID.randomUUID().toString();

            // PHOTO UPLOAD
            if (postImageUri != null) {
            File newImageFile = new File(postImageUri.getPath());
            try {

                compressedImageFile = new Compressor(EditPostActivity.this)
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

                            compressedImageFile = new Compressor(EditPostActivity.this)
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
                                postMap.put("user_name", uName);
                                postMap.put("image_url", downloadUri);
                                postMap.put("image_thumb", downloadthumbUri);
                                postMap.put("timestamp", ServerValue.TIMESTAMP);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("Posts").child(laf_post_id)
                                        .setValue(postMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    notification(type, " edited");
                                                    Toast.makeText(EditPostActivity.this, "Post is Edited", Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(EditPostActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();

                                                } else {

                                                    //Error
                                                }

                                                editPostProgress.setVisibility(View.INVISIBLE);

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

                        editPostProgress.setVisibility(View.INVISIBLE);

                    }

                }
            });
        }else
            {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("post_type", type);
                postMap.put("title", title);
                postMap.put("post_desc", desc);
                postMap.put("user_id", current_user_id);
                postMap.put("user_name", uName);
                postMap.put("image_url",imageUri);
                postMap.put("image_thumb",thumbUri);
                postMap.put("timestamp", ServerValue.TIMESTAMP);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("Posts").child(laf_post_id)
                        .setValue(postMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    notification(type, " edited");
                                    Toast.makeText(EditPostActivity.this, "Post is Edited", Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(EditPostActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();

                                } else {

                                    //Error
                                }

                                editPostProgress.setVisibility(View.INVISIBLE);

                            }
                        });
            }
        }else
        {
            Toast.makeText(EditPostActivity.this, "Fill All Field. ", Toast.LENGTH_LONG).show(); //if u
        }
            }
    });
    }

    private void notification(String type, String edited) {
        String name=firebaseAuth.getCurrentUser().getDisplayName();
        String image=firebaseAuth.getCurrentUser().getPhotoUrl().toString();
        Map<String, Object> NotMap = new HashMap<>();
        String msg=name + edited + " a " + type + " post.";
        NotMap.put("Notification",msg);
        NotMap.put("userImage",image);
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
                editPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }



    }



}