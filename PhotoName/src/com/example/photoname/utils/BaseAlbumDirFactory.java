package com.example.photoname.utils;

import java.io.File;


import android.os.Environment;
import android.util.Log;

public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

	// Standard storage location for digital camera files
	private static final String CAMERA_DIR = "/dcim/";

	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR
				+ albumName);
	}

	/**
	 * Get the album path
	 * 
	 * @return File album path
	 */
	public File getAlbumDir(String albumName, String appName) {
		File storageDir = null;

		if (isExternalStorageWritable()) {

			storageDir = getAlbumStorageDir(albumName);

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d(appName, "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(appName, "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

}
