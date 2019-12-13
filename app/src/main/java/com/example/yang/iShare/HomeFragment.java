package com.example.yang.iShare;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TabLayout tabs;
    View view;
    ViewPager vp;
    FragmentPagerAdapter adapter;
    private HomeHotFragment homeHotFragment;
    private HomeMeFragment homeMeFragment;
    List<String> list_title;

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabhome);
        tabLayout.addTab(tabLayout.newTab().setText("首页"), true);//添加 Tab,默认选中
        tabLayout.addTab(tabLayout.newTab().setText("热门"), false);//添加 Tab,默认不选中
        vp = (ViewPager) view.findViewById(R.id.home_pager);
        vp.setOffscreenPageLimit(1);
        list_title = new ArrayList<>();
        list_title.add("首页");
        list_title.add("热门");

        vp.setAdapter(new MyPagerAdapter(getChildFragmentManager()));;
        tabLayout.setupWithViewPager(vp);
        return view;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "首页", "热门"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (homeMeFragment == null) {
                        homeMeFragment = new HomeMeFragment();
                    }
                    return homeMeFragment;
                case 1:
                    if (homeHotFragment == null) {
                        homeHotFragment = new HomeHotFragment();
                    }
                    return homeHotFragment;
                default:
                    return null;
            }
        }

    }
}
