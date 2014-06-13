package com.example.android.photogallery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.android.photogallery.utils.AlbumStorageDirFactory;
import com.example.android.photogallery.utils.BaseAlbumDirFactory;
import com.example.android.photogallery.utils.BitmapUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TakePhotoFragment extends Fragment {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	/* album factory */
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	/* album path */
	private String mCurrentPhotoPath = null;

	/*
	 * Use SharedPreference to save path and updating ImageAdapter on Gallery
	 * View
	 */
	SharedPreferences.Editor mEditor;

	/* image for last taken picture */
	private ImageView mImageView;

	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	/**
	 * Setup a file image. Create new file image and store in private attribute
	 * 
	 * @return File path from new image file
	 * @throws IOException
	 */
	private File setUpPhotoFile() throws IOException {
		// Create an image file name from actual locale time
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				getResources().getConfiguration().locale).format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp;

		/* get album or create */
		File albumFile = ((BaseAlbumDirFactory) mAlbumStorageDirFactory)
				.getAlbumDir(getAlbumName(), getString(R.string.app_name));

		/* create file with an unique id */
		return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumFile);
	}

	/**
	 * Scale size of a new picture to the object ImageView from Fragment
	 */
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int reqWidth = mImageView.getWidth();
		int reqHeight = mImageView.getHeight();

		Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromPath(
				mCurrentPhotoPath, reqWidth, reqHeight);

		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);

		/* show hidden image view */
		mImageView.setVisibility(View.VISIBLE);
	}

	/**
	 * add picture to media provider
	 */
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}

	/**
	 * take picture with native camera app and save it in external store
	 */
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (takePictureIntent.resolveActivity(this.getActivity()
				.getPackageManager()) != null) {

			File photoFile = null;
			try {
				/* create the file */
				photoFile = setUpPhotoFile();

			} catch (IOException e) {
				e.printStackTrace();
			}

			/* Check if the File was successfully created */
			if (photoFile != null) {
				mCurrentPhotoPath = photoFile.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));

				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_section_camera,
				container, false);

		/* set action to button camera */
		rootView.findViewById(R.id.camera).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						/* set dispatch take picture to onclick button */
						dispatchTakePictureIntent();

					}
				});

		/* set imageView from imageView */
		mImageView = (ImageView) rootView.findViewById(R.id.imageView);

		/* create album */
		mAlbumStorageDirFactory = new BaseAlbumDirFactory();

		/* get editor */
		SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		this.mEditor = sharedPref.edit();

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE
				&& resultCode == Activity.RESULT_OK
				&& mCurrentPhotoPath != null) {
			/* adjust screen size */
			setPic();

			/* add picture to native gallery from device */
			galleryAddPic();
		
			/*
			 * save path with only one from this App SharedPreferences to update
			 * imageAdapter on GalleryFragment onResume method
			 */
			mEditor.putString("IMAGE_PATH", this.mCurrentPhotoPath).commit();

			/* remove value */
			mCurrentPhotoPath = null;
		}
	}

}