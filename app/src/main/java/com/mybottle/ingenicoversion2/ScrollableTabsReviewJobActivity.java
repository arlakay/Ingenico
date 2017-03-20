package com.mybottle.ingenicoversion2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.mybottle.ingenicoversion2.fragment.FragmentHistoryEDCToday;
import com.mybottle.ingenicoversion2.fragment.FragmentHistoryEDCYesterday;
import com.mybottle.ingenicoversion2.fragment.FragmentHistoryMainboardToday;
import com.mybottle.ingenicoversion2.fragment.FragmentHistoryMainboardYesterday;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ERD on 26/09/2016.
 */
public class ScrollableTabsReviewJobActivity extends BaseActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollable_tabs_review_job);
        ButterKnife.bind(this);

        setupToolbar();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .show(FragmentHistoryEDCToday.newInstance())
                    //.replace(R.id.viewpager, OneFragment.newInstance())
                    .commit();
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentHistoryEDCToday(), "EDC TODAY");
        adapter.addFrag(new FragmentHistoryEDCYesterday(), "EDC YESTERDAY");
        adapter.addFrag(new FragmentHistoryMainboardToday(), "MAINBOARD TODAY");
        adapter.addFrag(new FragmentHistoryMainboardYesterday(), "MAINBOARD YESTERDAY");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
