package com.example.supervisionapp.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.model.UserTypeModel;
import com.example.supervisionapp.ui.login.LoggedInUserView;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final List<Fragment> STUDENT_FRAGMENTS = new ArrayList<>();
    private static final int [] STUDENT_TAB_TITLES = new int[]{R.string.tab_advertised_theses, R.string.tab_my_thesis};

    private static final List<Fragment> SUPERVISOR_FRAGMENTS = new ArrayList<>();
    private static final int [] SUPERVISOR_TAB_TITLES = new int[]{R.string.tab_theses_requests, R.string.tab_supervised_thesis, R.string.tab_my_research};


    static {
        STUDENT_FRAGMENTS.add(FragmentAdvertisedTheses.newInstance());
        STUDENT_FRAGMENTS.add(FragmentMyThesis.newInstance());
        SUPERVISOR_FRAGMENTS.add(FragmentThesesRequests.newInstance());
        SUPERVISOR_FRAGMENTS.add(FragmentSupervisedThesis.newInstance());
        SUPERVISOR_FRAGMENTS.add(FragmentMyResearch.newInstance());
    }

    private final Context mContext;
    private final LoggedInUserView loggedInUser;

    public SectionsPagerAdapter(Context context, FragmentManager fm, LoggedInUserView loggedInUser) {
        super(fm);
        mContext = context;
        this.loggedInUser = loggedInUser;
    }

    @Override
    public Fragment getItem(int position) {
        if (loggedInUser.getUserType() == UserTypeModel.STUDENT) {
            return STUDENT_FRAGMENTS.get(position);
        } else {
            return SUPERVISOR_FRAGMENTS.get(position);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        int tabTitle;
        if (loggedInUser.getUserType() == UserTypeModel.STUDENT) {
            tabTitle = STUDENT_TAB_TITLES[position];
        } else {
            tabTitle = SUPERVISOR_TAB_TITLES[position];
        }
        return mContext.getResources().getString(tabTitle);
    }

    @Override
    public int getCount() {
        if (loggedInUser.getUserType() == UserTypeModel.STUDENT) {
            return STUDENT_FRAGMENTS.size();
        } else {
            return SUPERVISOR_FRAGMENTS.size();
        }
    }
}