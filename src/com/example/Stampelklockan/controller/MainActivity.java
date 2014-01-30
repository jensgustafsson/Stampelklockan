package com.example.Stampelklockan.controller;


import java.util.Locale;

import com.example.Stampelklockan.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;



public class MainActivity extends FragmentActivity{
	private static final int NUM_PAGES = 2;
	
	/* Pager widget that allows swiping horizontally */
	private ViewPager mPager;

	/* Provides the pages to the ViewPager */
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager)findViewById(R.id.viewpager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		mPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				if(ClockInFragment.getInstance().existActiveWork()){
					TimeListFragment.getInstance().showActive(true);
				}else{
					TimeListFragment.getInstance().showActive(false);
				}
				if(arg0 == 1){
					TimeListFragment.getInstance().writeActiveTime();
				}
			}
			
		});
	}
	
	public MainActivity(){

	}

	
	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the
			// system to handle the
			// Back button. This calls finish() on this activity and pops the
			// back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}
	

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case 0:
				return ClockInFragment.getInstance();
			case 1: 
				return TimeListFragment.getInstance();
			}
			return null; 
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
		
		@Override  
	    public CharSequence getPageTitle(int position) {  
	        Locale l = Locale.getDefault();  
	        switch (position) {  
	        case 0:  
	            return "ST€MPELKLOCKA";  
	        case 1:  
	            return "arbetshistorik".toUpperCase(l);
	        }  
	        return null;  
	    }  
	}
}