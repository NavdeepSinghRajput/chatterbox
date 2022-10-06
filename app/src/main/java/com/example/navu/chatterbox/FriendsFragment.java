package com.example.navu.chatterbox;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class FriendsFragment extends Fragment {

    private DatabaseReference mFriendDatabase,mUserDatabase;
    private String mCurrent_user_id;
    private FirebaseRecyclerAdapter<Friends, FriendsViewHolder> mFirebaseAdapter;
    private RecyclerView mFriendlist;
    private View mMainView;
    private FirebaseAuth mAuth;
    private Context c;
    private LinearLayoutManager manager;
    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendlist = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        Log.d("mauth",mCurrent_user_id);
        //Toast.makeText(FriendsFragment.this, "", Toast.LENGTH_SHORT).show();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
      //  mFriendlist.setHasFixedSize(true);
        mFriendlist.setLayoutManager(new LinearLayoutManager(getContext()));
      // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter =  new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(Friends.class,R.layout.user_single_layout
                ,FriendsViewHolder.class,mFriendDatabase) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
//                Log.d("TAG",model.getData());
                viewHolder.setDate(model.getData());
                final String list_user_id = getRef(position).getKey();
                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        if(dataSnapshot.hasChild("online")){
                           String userOnline =  dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);

                        }
                        Log.d("UserName",userName);
                        viewHolder.setName(userName);
                        viewHolder.setDate(userStatus);
                        viewHolder.setImageView(thumb_image,getContext());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{"Open Profile","Send Messgae"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(i == 0){
                                            Intent profile = new Intent(getContext(),ProfileActivity.class);
                                            profile.putExtra("user_id",list_user_id);
                                            startActivity(profile);
                                        }
                                        if(i == 1){
                                            Intent chat = new Intent(getContext(),ChatActivity.class);
                                            chat.putExtra("user_id",list_user_id);
                                            chat.putExtra("user_name",userName);
                                            startActivity(chat);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                     }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendlist.setAdapter(mFirebaseAdapter);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDate(String date) {
            TextView post_name = (TextView) mView.findViewById(R.id.user_single_status);
            post_name.setText(date);
        }

        public void setName(String name){
            TextView userNAme = (TextView)mView.findViewById(R.id.user_single_name);
            userNAme.setText(name);
        }
        public void setImageView(String imageView, Context context) {
            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(imageView).placeholder(R.drawable.circle).into(userImageView);
        }
        public  void setUserOnline(String online_icon){
            ImageView userOnline = (ImageView)mView.findViewById(R.id.User_image);
            if(online_icon.equals(true)){
                userOnline.setVisibility(View.VISIBLE);

            }else
            {
                userOnline.setVisibility(View.INVISIBLE);
            }
        }
    }
}
