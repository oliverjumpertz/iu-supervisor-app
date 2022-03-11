package com.example.supervisionapp.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.supervisionapp.R;
import com.example.supervisionapp.ui.login.LoggedInUserView;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final List<Fragment> FRAGMENTS = new ArrayList<>();
    private static final int [] TAB_TITLES = new int[]{R.string.tab_advertised_theses, R.string.tab_my_thesis};

    static {
        FRAGMENTS.add(FragmentAdvertisedTheses.newInstance());
        FRAGMENTS.add(FragmentMyThesis.newInstance());
    }

    private final Context mContext;
    private final LoggedInUserView loggedInUser;

    public SectionsPagerAdapter(Context context, FragmentManager fm, LoggedInUserView loggedInUser) {
        super(fm);
        mContext = context;
        // TODO: setup tabs according to STUDENT and SUPERVISOR
        this.loggedInUser = loggedInUser;
    }

    @Override
    public Fragment getItem(int position) {
        return FRAGMENTS.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return FRAGMENTS.size();
    }
}