package com.pattaya.pattayacallcenter.member.Adapter;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.pattaya.pattayacallcenter.R;
import com.pattaya.pattayacallcenter.member.CaseFragment;
import com.pattaya.pattayacallcenter.member.ChatListFragment;
import com.pattaya.pattayacallcenter.member.FriendFragment;
import com.pattaya.pattayacallcenter.member.PostListFragment;
import com.pattaya.pattayacallcenter.member.SettingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWF on 2/4/2015.
 */
public class AdapterPager extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private final int NUM_ITEMS = 5;
    Activity context;
    List<Fragment> fragmentList = new ArrayList<>();
    private int tabIcons[] = {R.drawable.asset_icon_menu_friends, R.drawable.asset_icon_menu_chat
            , R.drawable.asset_icon_menu_case, R.drawable.asset_icon_menu_post, R.drawable.asset_icon_menu_setting};

    public AdapterPager(FragmentManager fm, Activity context) {
        super(fm);
        this.context = context;
        fragmentList.add(FriendFragment.newInstance());
        fragmentList.add(ChatListFragment.newInstance());
        fragmentList.add(CaseFragment.newInstance());
        fragmentList.add(PostListFragment.newInstance());
        fragmentList.add(SettingFragment.newInstance());
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentList.get(position);

        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public int getPageIconResId(int position) {
        return tabIcons[position];
    }
}

