package com.example.safetymanagementapp;

import android.icu.text.Transliterator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class NoticeViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> items = new ArrayList<Fragment>();
    int pageCount = 0;

    public NoticeViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("tag", "getItem");
        return items.get(position);
        //return NoticeViewPagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return items.size();
        //return pageCount;
    }

    public void addItem(Fragment item){
        items.add(item);
    }


    //삭제시 정상적으로 적용되게 할려면 아래 필요!
    @Override
    public int getItemPosition(@NonNull Object object) {
        return FragmentPagerAdapter.POSITION_NONE;
    }
}
