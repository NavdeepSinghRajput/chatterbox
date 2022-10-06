package com.example.navu.chatterbox;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;


    private RecyclerView mUserLists;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mAuth = FirebaseAuth.getInstance();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mToolbar = (Toolbar)findViewById(R.id.Users_toolbar);
         setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUserLists = (RecyclerView)findViewById(R.id.userlist);
        mUserLists.setHasFixedSize(true);
        mUserLists.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserRef.child("online").setValue(true);


        FirebaseRecyclerAdapter<UsersModel,UserViewHolder> adapter = new FirebaseRecyclerAdapter<UsersModel, UserViewHolder>
                (UsersModel.class,R.layout.user_single_layout,UserViewHolder.class,mUserDatabase) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, UsersModel model, int position) {

                viewHolder.setDisplayname(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImageView(model.getImage(),getApplicationContext());

                final String user_id = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UsersActivity.this,ProfileActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
            }
        };

        mUserLists.setAdapter(adapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);

            mView =itemView;
        }

        public void setDisplayname(String name) {
            TextView userNameview = (TextView)mView.findViewById(R.id.user_single_name);
            userNameview.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatusview = (TextView)mView.findViewById(R.id.user_single_status);
            userStatusview.setText(status);

        }

        public void setImageView(String imageView, Context context) {
            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(imageView).placeholder(R.drawable.circle).into(userImageView);
        }
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
