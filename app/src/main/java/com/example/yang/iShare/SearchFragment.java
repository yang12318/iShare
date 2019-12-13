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
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private TabLayout tabs;
    View view;
    ViewPager vp;
    FragmentPagerAdapter adapter;
    private SearchUserFragment searchUserFragment;
    private SearchDynamicFragment searchDynamicFragment;
    List<String> list_title;

    public static SearchFragment newInstance(String param1) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button button = (Button) view.findViewById(R.id.btn_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("用户"), true);//添加 Tab,默认选中
        tabLayout.addTab(tabLayout.newTab().setText("动态"), false);//添加 Tab,默认不选中
        vp = (ViewPager) view.findViewById(R.id.pager);
        vp.setOffscreenPageLimit(1);
        list_title = new ArrayList<>();
        list_title.add("用户");
        list_title.add("动态");

        vp.setAdapter(new MyPagerAdapter(getChildFragmentManager()));;
        tabLayout.setupWithViewPager(vp);
        return view;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "用户", "动态"};

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
                    if (searchUserFragment == null) {
                        searchUserFragment = new SearchUserFragment();
                    }
                    return searchUserFragment;
                case 1:
                    if (searchDynamicFragment == null) {
                        searchDynamicFragment = new SearchDynamicFragment();
                    }
                    return searchDynamicFragment;
                default:
                    return null;
            }
        }


    }
}
