package com.example.navu.chatterbox;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private  TextView mProfileDisplay_name,mProfileStatus,mProfilefrdscount;
    private ImageView mProfileimage;
    private Button mProfileSendrequest,mDeclinerequest;
    private FirebaseUser mCureent_user;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;


    private DatabaseReference mUerDatabase,mFriendReqDatabase,mFriendDatabase,mNotificationDatabase,mRootRef;
    private ProgressDialog mprofileDialog;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mCurrent_state ="not_friends";
        mAuth = FirebaseAuth.getInstance();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        final String user_ids = getIntent().getStringExtra("user_id");
        mprofileDialog = new ProgressDialog(this);
        mprofileDialog.setTitle("Loading User Data");
        mprofileDialog.setMessage("Please Wait while we Load the User data.");
        mprofileDialog.setCanceledOnTouchOutside(false);
        mprofileDialog.show();


        mUerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_ids);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase =FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCureent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileDisplay_name = (TextView)findViewById(R.id.profile_display_name);
        mProfileStatus = (TextView)findViewById(R.id.profile_display_status);
        mProfilefrdscount = (TextView)findViewById(R.id.profile_totalfrds);
        mProfileimage = (ImageView)findViewById(R.id.profile_image);
        mProfileSendrequest =(Button)findViewById(R.id.profile_send_request);
        mDeclinerequest = (Button)findViewById(R.id.profile_decline_request);
        mDeclinerequest.setVisibility(View.INVISIBLE);
        mDeclinerequest.setEnabled(false);

        mUerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String display_status = dataSnapshot.child("status").getValue().toString();
                String display_image = dataSnapshot.child("image").getValue().toString();


                mProfileDisplay_name.setText(display_name);
                mProfileStatus.setText(display_status);
                Picasso.with(ProfileActivity.this).load(display_image).placeholder(R.drawable.ic_person_black_24dp).into(mProfileimage);

                mFriendReqDatabase.child(mCureent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_ids)){
                            String req_type = dataSnapshot.child(user_ids).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                mCurrent_state ="req_received";
                                mProfileSendrequest.setText("Accept Friend Request");

                                mDeclinerequest.setVisibility(View.VISIBLE);
                                mDeclinerequest.setEnabled(true);

                            }else if(req_type.equals("sent")){

                                mCurrent_state ="req_sent";
                                mProfileSendrequest.setText("Cancel Friend Request");

                                mDeclinerequest.setVisibility(View.INVISIBLE);
                                mDeclinerequest.setEnabled(false);

                            }
                            mprofileDialog.dismiss();

                        }else{
                            mFriendDatabase.child(mCureent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_ids)){
                                        mCurrent_state = "friends";
                                        mProfileSendrequest.setText("unfriend this person");
                                        mDeclinerequest.setVisibility(View.INVISIBLE);
                                        mDeclinerequest.setEnabled(false);

                                    }
                                    mprofileDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mprofileDialog.dismiss();

                                }
                            });
                        }
                      }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendrequest.setEnabled(false);

                //--------------------------NOT FRIENDS State--------------------------------
                if(mCurrent_state.equals("not_friends")){

                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(user_ids).push();
                    String newNotification = newNotificationRef.getKey();

                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",mCureent_user.getUid());
                    notificationData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/"+mCureent_user.getUid() +"/"+user_ids+"/request_type","sent");
                    requestMap.put( "Friend_req/"+user_ids+"/"+mCureent_user.getUid()+"/request_type","received");
                    requestMap.put("notification/"+user_ids+"/"+newNotification,notificationData);
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError !=null){
                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                            }

                            mProfileSendrequest.setEnabled(true);
                            mCurrent_state ="req_sent";
                            mProfileSendrequest.setText("Cancel  Friend Request");

                        }

                    });
                }
                //--------------------------Cancel friend request--------------------------------

                if(mCurrent_state.equals("req_sent")){
                    mFriendReqDatabase.child(mCureent_user.getUid()).child(user_ids).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(user_ids).child(mCureent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProfileSendrequest.setEnabled(true);
                                            mCurrent_state ="not_friends";
                                            mProfileSendrequest.setText("Send  Friend Request");

                                            mDeclinerequest.setVisibility(View.INVISIBLE);
                                            mDeclinerequest.setEnabled(false);

                                        }
                                    });
                                }
                            });
                }

                //--------------------------  request received state--------------------------------
                if(mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateInstance().format(new Date());

                    Map friendMap = new HashMap();
                    friendMap.put("Friends/"+mCureent_user.getUid() +"/"+user_ids+"/date",currentDate);
                    friendMap.put( "Friends/"+user_ids+"/"+mCureent_user.getUid()+"/date",currentDate);
                    friendMap.put("Friend_req/"+mCureent_user.getUid()+"/"+user_ids,null);
                    friendMap.put("Friend_req/"+user_ids+"/"+mCureent_user.getUid(),null);
                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){
                                 mProfileSendrequest.setEnabled(true);
                                 mCurrent_state = "friends";
                                 mProfileSendrequest.setText("Unfriend this Person");

                                 mDeclinerequest.setVisibility(View.INVISIBLE);
                                 mDeclinerequest.setEnabled(false);
                            }else{
                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


//                    mFriendDatabase.child(mCureent_user.getUid()).child(user_ids).setValue(currentDate)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    mFriendDatabase.child(user_ids).child(mCureent_user.getUid()).setValue(currentDate)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//
//                                                    mFriendReqDatabase.child(mCureent_user.getUid()).child(user_ids).child("request_type")
//                                                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if(task.isSuccessful()){
//                                                                mFriendReqDatabase.child(user_ids).child(mCureent_user.getUid()).child("request_type")
//                                                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                    @Override
//                                                                    public void onSuccess(Void aVoid) {
//                                                                        mCurrent_state ="friends";
//                                                                        mProfileSendrequest.setText("UnFriend Request");
//
//                                                                        mDeclinerequest.setVisibility(View.INVISIBLE);
//                                                                        mDeclinerequest.setEnabled(false);
//
//                                                                    }
//                                                                });
//
//                                                            }else{
//                                                                Toast.makeText(ProfileActivity.this, "Fail Sending Request", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                            mProfileSendrequest.setEnabled(true);
//
//                                                        }
//                                                    });
//
//
//                                                }
//                                            });
//                                }
//                            });
//

                }
                //--------------------------  unfriend--------------------------------

                if(mCureent_user.equals("friends")){
                    Map umfriendMap = new HashMap();
                    umfriendMap.put("Friends/"+mCureent_user.getUid() +"/"+user_ids,"null");
                    umfriendMap.put( "Friends/"+user_ids+"/"+mCureent_user.getUid(),"null");

                    mRootRef.updateChildren(umfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                mCurrent_state = "not_friends";
                                mProfileSendrequest.setText("Send Friend Request");

                                mDeclinerequest.setVisibility(View.INVISIBLE);
                                mDeclinerequest.setEnabled(false);
                            }else{
                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendrequest.setEnabled(true);

                        }
                    });
                }




            }
        });
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

}
