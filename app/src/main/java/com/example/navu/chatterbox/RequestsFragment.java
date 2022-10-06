package com.example.navu.chatterbox;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private DatabaseReference mFriendRequestDatabase, mUserDatabase;
    private String mCurrent_user_id;
    private FirebaseRecyclerAdapter<Request, RequestsFragment.friendrequestViewHolder> mFirebaseAdapter;
    private RecyclerView mRequestList;
    private View mMainView;
    private FirebaseAuth mAuth;
    private Context c;
    private LinearLayoutManager manager;
    String Requesttype;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRequestList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        Log.d("mauth",mCurrent_user_id);
        //Toast.makeText(FriendsFragment.this, "", Toast.LENGTH_SHORT).show();
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mFriendRequestDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        //  mFriendlist.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        mFirebaseAdapter=new FirebaseRecyclerAdapter<Request, friendrequestViewHolder>(Request.class,R.layout.user_single_layout,friendrequestViewHolder.class,mFriendRequestDatabase) {
            @Override
            protected void populateViewHolder(final friendrequestViewHolder viewHolder, Request model, int position) {

                final String list_user_id = getRef(position).getKey();
                mFriendRequestDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("request_type").getValue().toString()=="sent"){

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        Log.d("UserName",userName);

                            viewHolder.setName(userName);
                            viewHolder.setuserstatus(userStatus);
                            viewHolder.setImageView(thumb_image, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(),ProfileActivity.class);
                                intent.putExtra("user_id",list_user_id);
                                startActivity(intent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mRequestList.setAdapter(mFirebaseAdapter);
    }

    public static class friendrequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public friendrequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setuserstatus(String status) {
            TextView post_name = (TextView) mView.findViewById(R.id.user_single_status);
            post_name.setText(status);
        }
        public void setName(String name) {
            TextView userNAme = (TextView) mView.findViewById(R.id.user_single_name);
            userNAme.setText(name);
        }

        public void setImageView(String imageView, Context context) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(imageView).placeholder(R.drawable.circle).into(userImageView);
        }

    }
}