package com.pronovoscm.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.util.Log;
import android.view.ViewGroup;

import com.pronovoscm.fragments.DrawingListFragment;
import com.pronovoscm.persistence.domain.DrawingList;

import java.util.ArrayList;
import java.util.List;

public class DrawingSectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<DrawingListFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Fragment mCurrentFragment;
    private List<DrawingList> mDrawingLists;
    public DrawingSectionsPagerAdapter(FragmentManager manager, List<DrawingList> mDrawingLists) {
        super(manager);
        this.mDrawingLists=mDrawingLists;
    }

    @Override
    public Fragment getItem(int position) {
//        DrawingListFragment drawingListFragment=new DrawingListFragment();
//        Bundle bundle =new Bundle();
//        bundle.putSerializable("drawingObj", (Serializable) mDrawingLists.get(position));
//        DrawingListFragment fragment = DrawingListFragment.newInstance(mDrawingLists.get(position));
//        fragment.setArguments(bundle);
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(DrawingListFragment fragment, String title, int position) {
        mFragmentList.add(position, fragment);
        mFragmentTitleList.add(position, title);
    }

    public void addFragment(DrawingListFragment fragment, String title) {
        mFragmentList.add( fragment);
        mFragmentTitleList.add(title);
    }

    public void removeFragment(DrawingListFragment fragment, int position) {
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.i("Test", "getPageTitle: "+mFragmentTitleList.get(position));
        return mFragmentTitleList.get(position);

    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /*   @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        DrawingListFragment fragment = (DrawingListFragment) super.instantiateItem(container, position);
//        = fragment;
        return mFragmentList.get(position);

    }*/

    public List<String> getmFragmentTitleList() {
        return mFragmentTitleList;
    }
}