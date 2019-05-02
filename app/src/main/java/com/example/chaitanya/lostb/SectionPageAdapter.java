package com.example.chaitanya.lostb;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFL = new ArrayList<>();
    private final List<String> mFTL = new ArrayList<>();

    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(Fragment f, String t){
        mFL.add(f);
        mFTL.add(t);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFTL.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return mFL.get(i);
    }

    @Override
    public int getCount() {
        return mFL.size();
    }


}
