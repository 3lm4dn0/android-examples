package com.example.android.photogallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 
 * Example from
 * 
 * @link 
 *       http://danielsaidi.wordpress.com/2013/08/02/android-fullscreen-activity-
 *       base-class/
 * 
 *       Sample with AdapterPage to swipe between pictures
 *       http://www.androidhive
 *       .info/2013/09/android-fullscreen-image-slider-with
 *       -swipe-and-pinch-zoom-gestures/
 * 
 */
public class FullScreenFragment extends Activity {

	private ImageView mImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.custom_fullscreen_view);

		/* get ImageView */
		this.mImageView = (ImageView) findViewById(R.id.full_image_view);

		/* get image */
		Bitmap original = BitmapFactory.decodeFile(getIntent().getExtras()
				.getString(getString(R.string.EXTRA_IMAGE_PATH)), null);

		/* get size */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		/* set image to ImageView and total size */
		this.mImageView.setImageBitmap(Bitmap.createScaledBitmap(original,
				width, height, true));
	}

	@Override
	protected void onDestroy() {
		this.mImageView.setImageBitmap(null);
		super.onDestroy();
	}

}
