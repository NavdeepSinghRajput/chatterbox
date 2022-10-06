package com.example.navu.chatterbox;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSavebtn;

    private ProgressDialog mStatusProgress;
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCureentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

         mCureentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = mCureentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);


        mToolbar = (Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");


        mStatus = (TextInputLayout)findViewById(R.id.status_input);
        mStatus.getEditText().setText(status_value);
        mSavebtn = (Button)findViewById(R.id.status_save_btn);
        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatusProgress = new ProgressDialog(StatusActivity.this);

                mStatusProgress.setTitle("Saving Status");
                mStatusProgress.setMessage("Please Wait while we save the changes");
                mStatusProgress.setCanceledOnTouchOutside(false);
                mStatusProgress.show();

                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mStatusProgress.dismiss();
                        }else{
                            Toast.makeText(StatusActivity.this, "There were error in saving Changes  ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
