package com.example.android.photogallery;

import java.io.File;

import com.example.android.photogallery.adapters.ImageAdapter;

import android.os.AsyncTask;
import android.support.v4.media.TransportMediator;
import android.widget.GridView;
import android.widget.ProgressBar;

public class LoadFilesAsyncTask extends AsyncTask<Void, String, Void> {

	private static final String VALID_FILENAME_TYPE = ".jpg";
	private File mAlbumDir;
	private ImageAdapter mImageAdapter;
	private GridView mGridView = null;
	private ProgressBar mProgressBar = null;

	public LoadFilesAsyncTask(ImageAdapter imageAdapter, File albumDir, 
			GridView gridView, ProgressBar progressBar) {
		
		this.mImageAdapter = imageAdapter;
		this.mAlbumDir = albumDir;
		this.mGridView = gridView;
		this.mProgressBar = progressBar;

	}

	@Override
	protected void onPreExecute() {
		mProgressBar.setVisibility(0);
		mGridView
				.setVisibility(TransportMediator.FLAG_KEY_MEDIA_PLAY_PAUSE);

		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {

		/*
		 * TODO Use Thumbnails instead pictures with
		 * android.provider.MediaStore.Images.Thumbnails.getThumbnail() example:
		 * 
		 * @link
		 * http://stackoverflow.com/questions/8676384/loading-images-from-sd
		 * -card-directory-in-gridview
		 */

		/* load images from albumDir */
		if (mAlbumDir != null && mAlbumDir.isDirectory()) {
			loadImages();
		}

		return null;
	}

	/**
	 * onProgressUpdate received String path for each file called in
	 * doInBackground with publishProgress(file.getAbsolutePath());
	 */
	@Override
	protected void onProgressUpdate(String... values) {
		mImageAdapter.add(values[0]);

		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		mProgressBar
				.setVisibility(TransportMediator.FLAG_KEY_MEDIA_PLAY_PAUSE);
		mGridView.setVisibility(0);

		/* notify to adapter was changed */
		mImageAdapter.notifyDataSetChanged();

		super.onPostExecute(result);
	}

	/**
	 * Load images
	 */
	private void loadImages() {
		File[] files = mAlbumDir.listFiles();
		for (File file : files) {
			/* add on progress update */
			String filename;
			String extension;
			if (file.isFile() && file.length() > 0) {
				filename = file.getName();

				extension = filename.substring(filename.lastIndexOf("."),
						filename.length());
				if (extension.equalsIgnoreCase(VALID_FILENAME_TYPE)) {
					/*
					 * Load external images with several bytes one by one it
					 * takes some time
					 */
					publishProgress(file.getAbsolutePath());
				}
			}

			/* exit if cancel thread */
			if (isCancelled()) {
				break;
			}
		}
	}
}