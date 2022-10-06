package com.example.navu.chatterbox;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragments extends Fragment {

  /*  private DatabaseReference mChatsDatabase,mUserDatabase,mMessagesDatabase;
    private String mCurrent_user_id;
  //  private FirebaseRecyclerAdapter<Chats,ChatsFragments.ChatsViewHolder> mFirebaseAdapter;
    private RecyclerView mChatList;

    private FirebaseAuth mAuth;
    private Context c;
    private LinearLayoutManager manager;  */
  private View mMainView;

    public ChatsFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_chats_fragments, container, false);
        return mMainView;
     /*  mChatList = (RecyclerView) mMainView.findViewById(R.id.chat_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        Log.d("mauth",mCurrent_user_id);
        mChatsDatabase= FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mChatsDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        //  mFriendlist.setHasFixedSize(true);
        mMessagesDatabase=FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mMessagesDatabase.keepSynced(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mChatList.setLayoutManager(linearLayoutManager);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mChatList.setHasFixedSize(true);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query conversationquery=mChatsDatabase.orderByChild("timestamp");
        mFirebaseAdapter=new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(Chats.class,R.layout.chat_single_layout,ChatsViewHolder.class,conversationquery) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, Chats model, int position) {

                final String list_user_id = getRef(position).getKey();
                Query lastMessageQuery = mMessagesDatabase.child(list_user_id).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        viewHolder.setMessage(data, Chats.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
                        viewHolder.setName(userName);
                        viewHolder.setImageView(thumb_image,getContext());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mChatList.setAdapter(mFirebaseAdapter);

           }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImageView(String imageView, Context context) {
            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image1);
            Picasso.with(context).load(imageView).placeholder(R.drawable.circle).into(userImageView);
        }

        public void setName(String name){
            TextView userNAme = (TextView)mView.findViewById(R.id.user_single_name1);
            userNAme.setText(name);
        }


        public void setMessage(String message, boolean isSeen){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(message);

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }  */
    }


}
