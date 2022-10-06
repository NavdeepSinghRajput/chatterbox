package com.example.navu.chatterbox;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;
    private ViewPager mViewPager;
    private DatabaseReference mUserRef;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatterBox");

        if(mAuth.getCurrentUser()!= null){
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

       mViewPager = (ViewPager)findViewById(R.id.mainpager);
       mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

       mViewPager.setAdapter(mSectionPagerAdapter);

       mTabLayout = (TabLayout)findViewById(R.id.mains_tabs);
       mTabLayout.setupWithViewPager(mViewPager);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
           sendTostart();
        }else{
            mUserRef.child("online").setValue(true);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser !=null ){
        mUserRef.child("online").setValue(false);
        mUserRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void sendTostart() {
        Intent i = new Intent(this,StartActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId()==R.id.main_logout_btn){
             FirebaseAuth.getInstance().signOut();
             sendTostart();
         }
         if(item.getItemId()==R.id.main_setting_btn){
             Intent i = new Intent(this,SettingsActivity.class);
             startActivity(i);

         }
        if(item.getItemId()==R.id.main_all_btn){
            Intent i = new Intent(this,UsersActivity.class);
            startActivity(i);

        }


        return true;
    }
}
