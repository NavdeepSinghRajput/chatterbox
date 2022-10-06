package com.example.navu.chatterbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserdatabase;
    private FirebaseUser mcurrentUser;

    private CircleImageView mDisplayImage;
    private TextView mName,mStatus;
    private Button mStatusBtn,mImageBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;


    private static final int GALLERY_PICK = 1;

    private ProgressDialog mSettingProgrees;

    private StorageReference mImageStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDisplayImage = (CircleImageView)findViewById(R.id.setting_image);
        mName =(TextView)findViewById(R.id.settings_displayname);
        mStatus =(TextView)findViewById(R.id.setting_status);
        mStatusBtn = (Button)findViewById(R.id.setting_statusbtn);
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mStatus.getText().toString();
                Intent intent = new Intent(SettingsActivity.this,StatusActivity.class);
                intent.putExtra("status_value",status_value);
                startActivity(intent);
            }
        });
        mImageBtn = (Button)findViewById(R.id.setting_imagebtn);
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
            }
        });

        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorageRef = FirebaseStorage.getInstance().getReference();

        String Current_Uid = mcurrentUser.getUid();

        mUserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_Uid);
        mUserdatabase.keepSynced(true);

        mUserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final   String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("default"))

//                Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.circle).into(mDisplayImage);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.circle).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.circle).into(mDisplayImage);

                        }
                    });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode ==RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mSettingProgrees = new ProgressDialog(SettingsActivity.this);
                mSettingProgrees.setTitle("Uploading Image");
                mSettingProgrees.setMessage("Please Wait while we Upload an image");
                mSettingProgrees.setCanceledOnTouchOutside(false);
                mSettingProgrees.show();

                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());

                String current_user = mcurrentUser.getUid();


                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this).setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75).compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = mImageStorageRef.child("profiles_images").child(current_user+"jpg");
                final StorageReference thumb_filepath = mImageStorageRef.child("profiles_images").child("thumbs").child(current_user+"jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String  thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image",downloadUrl);
                                        update_hashMap.put("thumb_image",thumb_downloadurl);

                                        mUserdatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    mSettingProgrees.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Pic Updated", Toast.LENGTH_SHORT).show();
                                                }else
                                                {
                                                    mSettingProgrees.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Error in uploading pic", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });

                                    }else
                                    {
                                        mSettingProgrees.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Error in uploading thumb", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                            }else{
                           mSettingProgrees.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error in uploading", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mUserRef.child("online").setValue(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser !=null ){
            mUserRef.child("online").setValue(false);
        }

    }
//    public static String random() {
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(1000);
//        char tempChar;
//        for (int i = 0; i < randomLength; i++){
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//    }
}
