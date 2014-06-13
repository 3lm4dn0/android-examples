package com.example.android.photogallery.adapters;

import android.support.v4.app.Fragment;	
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.photogallery.ShowGalleryFragment;
import com.example.android.photogallery.TakePhotoFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the primary sections of the app.
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

	private final static int TOTAL_PAGES = 2;

	public AppSectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {

		Fragment fragment = null;

		switch (i) {
			// Take photo
			case 0:
				fragment = new TakePhotoFragment();
				break;

			// Show Gallery
			case 1:				
				fragment = new ShowGalleryFragment();
				break;			
			}		

		return fragment;
	}

	@Override
	public int getCount() {
		return TOTAL_PAGES;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Tab " + (position + 1);
	}
}