package com.pattaya.pattayacallcenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by SWF on 1/30/2015.
 */
public class BaseActivity extends ActionBarActivity  {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;



    public void setFragment(Fragment fragment, int TAG) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(String.valueOf(TAG));
        transaction.commit();
    }


    public interface  setChangeFragment {
        // TODO: Update argument type and name
        public void  onSectionAttached(int state);
    }

}
