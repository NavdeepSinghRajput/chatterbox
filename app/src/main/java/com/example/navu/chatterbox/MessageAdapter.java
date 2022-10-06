package com.example.navu.chatterbox;

import android.graphics.Color;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Navu on 12-May-18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabse;

    public MessageAdapter(List<Messages> mMessagesList){
        this.mMessagesList = mMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(v);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView timeText;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public MessageViewHolder(View view){
            super(view);

            messageText = (TextView)view.findViewById(R.id.message_single_textview);
           // timeText = (TextView)view.findViewById(R.id.message_item_time);
            profileImage =(CircleImageView)view.findViewById(R.id.message_single_image);
            messageImage = (ImageView)view.findViewById(R.id.message_image_layout);
        }

    }
    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
       // mAuth = FirebaseAuth.getInstance();
       /// String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessagesList.get(position);


        String from_user = c.getFrom();
        String message_type = c.getType();

        mUserDatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();;
                String image = dataSnapshot.child("thumb_image").getValue().toString();


                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.circle).into(holder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("Text")){

            holder.messageText.setText(c.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);
//            holder.messageText.setBackgroundColor(Color.WHITE);
//            holder.messageText.setTextColor(Color.BLACK);

        }else{

            holder.messageText.setText(View.INVISIBLE);
            Picasso.with(holder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.circle).into(holder.profileImage);


//            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
//            holder.messageText.setBackgroundColor(Color.BLUE);
        }

      //  holder.messageText.setText(c.getMessage());
       // holder.timeText.setText(c.getTime());
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}