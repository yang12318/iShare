package com.example.yang.ins;

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

public class AboutFragment extends Fragment {

    private TabLayout tabs;
    View view;
    ViewPager vp;
    FragmentPagerAdapter adapter;
    private AboutFollowFragment aboutFollowFragment;
    private AboutMeFragment aboutMeFragment;
    List<String> list_title;

    public static AboutFragment newInstance(String param1) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("已关注"), true);//添加 Tab,默认选中
        tabLayout.addTab(tabLayout.newTab().setText("你"), false);//添加 Tab,默认不选中
        vp = (ViewPager) view.findViewById(R.id.pager);
        vp.setOffscreenPageLimit(1);
        list_title = new ArrayList<>();
        list_title.add("已关注");
        list_title.add("你");

        vp.setAdapter(new MyPagerAdapter(getChildFragmentManager()));;
        tabLayout.setupWithViewPager(vp);
        return view;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "已关注", "你"};

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
                    if (aboutFollowFragment == null) {
                        aboutFollowFragment = new AboutFollowFragment();
                    }
                    return aboutFollowFragment;
                case 1:
                    if (aboutMeFragment == null) {
                        aboutMeFragment = new AboutMeFragment();
                    }
                    return aboutMeFragment;
                default:
                    return null;
            }
        }

    }
}