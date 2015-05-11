package com.pattaya.pattayacallcenter.customview;

import android.app.Activity;
import android.view.Display;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by SWF on 1/29/2015.
 */
public class SlideMenuManage {

    public final int SETTING_MENU_SHOW = 0;
    public final int SETTING_MENU_HIDE = 1;


    int STATE_ANIMATE = 1;
    public int STATE_TOP = 1;
    public int STATE_BOTTOM = 0;


    View mSliderMenu;
    View mViewClick;
    Activity mActivity;
    int mScreenY;
    int mScreenX;


    public void setStateAnimate(int state) {
        STATE_ANIMATE = state;
        if (STATE_ANIMATE == STATE_BOTTOM) {
            ViewPropertyAnimator.animate(mSliderMenu).translationY(+mScreenY).setDuration(0).start();
        }

    }

    public void setmSliderMenu(View mSliderMenu) {
        this.mSliderMenu = mSliderMenu;
        getScreenHeight();
        stateShowMenu(SETTING_MENU_HIDE);
        clickLayoutMenu();
    }


    public void setmViewClick(View mViewClick) {
        this.mViewClick = mViewClick;
        clickIc();
        eventShow();
    }

    public SlideMenuManage(View mSliderMenu, View mViewClick, Activity ac) {
        this.mSliderMenu = mSliderMenu;
        this.mViewClick = mViewClick;
        this.mActivity = ac;
        getScreenHeight();
        ViewPropertyAnimator.animate(mSliderMenu).translationY(-mScreenY).setDuration(0).start();
        clickLayoutMenu();
        clickIc();
    }

    public SlideMenuManage(View mSliderMenu, Activity mActivity) {
        this.mSliderMenu = mSliderMenu;
        this.mActivity = mActivity;
        getScreenHeight();
        ViewPropertyAnimator.animate(mSliderMenu).translationY(-mScreenY).setDuration(0).start();
        clickLayoutMenu();
    }


    private void getScreenHeight() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        mScreenX = display.getWidth();  // deprecated
        mScreenY = display.getHeight();  // deprecated
    }


    public void stateShowMenu(int state) {
        // Log.d("TAG", "" + state);
        // Log.d("TAG", "" + mViewClick);
        //  Log.d("TAG", "" + this.mSliderMenu);
        switch (state) {
            case SETTING_MENU_HIDE:
                if (STATE_ANIMATE == STATE_BOTTOM) {
                    ViewPropertyAnimator.animate(mSliderMenu).translationY(+mScreenY).setDuration(300).start();
                } else {
                    ViewPropertyAnimator.animate(mSliderMenu).translationY(-mScreenY).setDuration(300).start();
                }
                break;
            case SETTING_MENU_SHOW:
                ViewPropertyAnimator.animate(mSliderMenu).translationY(0).setDuration(100).start();
                break;
            default:
                break;

        }

    }

    public void clickLayoutMenu() {
        mSliderMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventShow();
            }
        });
    }

    public void clickIc() {
        mViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventShow();
            }
        });
    }


    public void eventShow() {
        if (ViewHelper.getTranslationY(mSliderMenu) == SETTING_MENU_SHOW) {
            stateShowMenu(SETTING_MENU_HIDE);
        } else {
            stateShowMenu(SETTING_MENU_SHOW);
        }
    }

}
