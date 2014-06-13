package com.example.android.photogallery.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;	
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

public class BitmapUtils {
	
	public static Bitmap getThumbnail(Context context, String path){
		
		ContentResolver cr = context.getContentResolver();
		Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
	    if (ca != null && ca.moveToFirst()) {
	        int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
	        ca.close();
	        
	        /* Get minimum resolution 96x96 with MICRO_KIND 
	         * or medium resolution with MINI_KIND
	         * @link http://developer.android.com/reference/android/provider/MediaStore.Images.Thumbnails.html
	         * */
	        return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null );
	    }

	    ca.close();
	    return null;
	}

	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
			int reqHeight) {

		Bitmap bm = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		
    	options.inJustDecodeBounds = false;		
    	options.inPurgeable = true;

		// Decode bitmap with inSampleSize set		
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		
		int inSampleSize = 4;

		if ( (reqHeight > 0 && reqWidth > 0) && (height > reqHeight || width > reqWidth) ) {
			/* get the lowest size */
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

}
