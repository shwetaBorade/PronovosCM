package com.pronovoscm.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.util.Log;

import com.pronovoscm.persistence.domain.DrawingList;

import java.util.List;

public class DrawingViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Fragment fragment = null;
    List<DrawingList> drwList= null;

    public DrawingViewPagerAdapter(FragmentManager fm, int NumOfTabs, List<DrawingList> drwList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.drwList=drwList;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("test", "newInstance: aa 1 "+position+" test ");

        for (int i = 0; i < mNumOfTabs; i++) {
            if (i == position) {
//                fragment = (Fragment) DrawingListFragment.newInstance(drwList.get(i));
                break;
            }
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
