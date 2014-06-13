package com.example.android.photogallery.utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class ScreenSizeUtils {

	private int width;
	private int height;

	public ScreenSizeUtils(Activity activity){
	    Display display = activity.getWindowManager().getDefaultDisplay();	 
	    final Point size = new Point();
    	display.getSize(size);
	    
	    this.width = size.x;
	    this.height = size.y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
