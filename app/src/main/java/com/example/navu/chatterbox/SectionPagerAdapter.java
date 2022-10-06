package com.example.navu.chatterbox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Navu on 07-May-18.
 */

class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 1:
                ChatsFragments chatsFragments = new ChatsFragments();
                return chatsFragments;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 3:
                EarnFragment earnFragment = new EarnFragment();
                return earnFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4 ;
    }

    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            case 3:
                return "Earn";
            default:
                return null;
        }

    }
}
