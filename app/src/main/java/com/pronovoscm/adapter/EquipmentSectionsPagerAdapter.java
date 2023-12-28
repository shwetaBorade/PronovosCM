package com.pronovoscm.adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pronovoscm.fragments.EquipmentDetailFragment;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSectionsPagerAdapter extends FragmentStatePagerAdapter {

    private final List<EquipmentDetailFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Fragment mCurrentFragment;
    private List<EquipmentSubCategoriesMaster> equipmentSubCategoriesMasters;
    public EquipmentSectionsPagerAdapter(FragmentManager manager, List<EquipmentSubCategoriesMaster> equipmentSubCategoriesMasters) {
        super(manager);
        this.equipmentSubCategoriesMasters =equipmentSubCategoriesMasters;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(EquipmentDetailFragment fragment, String title, int position) {
        mFragmentList.add(position, fragment);
        mFragmentTitleList.add(position, title);
    }

    public void addFragment(EquipmentDetailFragment fragment, String title) {
        mFragmentList.add( fragment);
        mFragmentTitleList.add(title);
    }
/*

    public void removeFragment(EquipmentDetailFragment fragment, int position) {
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
        Log.i("Test", "getPageTitle: "+mFragmentTitleList.get(position));
        return mFragmentTitleList.get(position);

    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
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
//        EquipmentDetailFragment fragment = (EquipmentDetailFragment) super.instantiateItem(container, position);
//        = fragment;
        return mFragmentList.get(position);

    }*/

    /*public List<EquipmentDetailFragment> getmFragmentList() {
        return mFragmentList;
    }

    public List<String> getmFragmentTitleList() {
        return mFragmentTitleList;
    }*/
}