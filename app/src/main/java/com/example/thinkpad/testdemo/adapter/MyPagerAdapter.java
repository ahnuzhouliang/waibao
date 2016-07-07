package com.example.thinkpad.testdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by thinkpad on 2016/7/7.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mViewList;
    private String tabTitles[] = new String[]{"LET网络","TD网络","GSM网络"};
    public MyPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mViewList = list;

    }



    @Override
    public int getCount() {
        return mViewList.size();//页卡数
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }



    @Override
    public Fragment getItem(int position) {
        return mViewList.get(position);
    }
}

