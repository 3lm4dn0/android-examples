package com.example.android.photogallery.utils;

import java.io.File;
import java.io.IOException;	

import android.media.ExifInterface;

/**
 * A class with a bitmap image and its file name
 * 
 * @author d. albela
 * 
 */
public class ImageItem {

	/* Rotation state */
	public enum Rotation {
		PORTRAIT, LANDSCAPE;
	}

	private String name;
	private String absoluteName;
	private Rotation rotation;

	public ImageItem(String path) {
		this.name = path.substring(path.lastIndexOf(File.separator)+1, path.length());
		this.absoluteName = path;

		/* set rotation */
		this.rotation = getExifRotation(path);
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Set Rotation to image View according to the Exif meta data from file
	 * 
	 * @param imageView
	 * @param path
	 */
	private Rotation getExifRotation(String path) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String orientation = exif.getAttribute("Orientation");
		if (orientation.equals("3")) {
			return Rotation.LANDSCAPE;
			// imageView.setRotation(180.0f);
		} else if (orientation.equals("6")) {
			return Rotation.PORTRAIT;
			// imageView.setRotation(90.0f);
		}

		return null;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public String getAbsoluteName() {
		return absoluteName;
	}

}
