package com.pronovoscm.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.util.Log;

import com.pronovoscm.fragments.TransferOverviewDetailFragment;
import com.pronovoscm.model.response.transferoverview.Transfers;

import java.util.ArrayList;
import java.util.List;

public class TransferOverviewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<TransferOverviewDetailFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Fragment mCurrentFragment;
    private List<Transfers> transfersList;

    public TransferOverviewPagerAdapter(FragmentManager manager) {
        super(manager);
//        this.transfersList = transfersList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(TransferOverviewDetailFragment fragment, String title, int position) {
        mFragmentList.add(position, fragment);
        mFragmentTitleList.add(position, title);
    }

    public void addFragment(TransferOverviewDetailFragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
/*

    public void removeFragment(TransferOverviewDetailFragment fragment, int position) {
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
    }

    public void removeAll() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }
*/

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i("Test", "getPageTitle: " + mFragmentTitleList.get(position));
        return mFragmentTitleList.get(position);

    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public void setCurrentFragment(int position) {
        if (mFragmentList.size() > position) {
            mCurrentFragment = mFragmentList.get(position);
        }
    }



/*
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }
*/

    /*   @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        TransferOverviewDetailFragment fragment = (TransferOverviewDetailFragment) super.instantiateItem(container, position);
//        = fragment;
        return mFragmentList.get(position);

    }*/

    /*public List<TransferOverviewDetailFragment> getmFragmentList() {
        return mFragmentList;
    }

    public List<String> getmFragmentTitleList() {
        return mFragmentTitleList;
    }*/
}