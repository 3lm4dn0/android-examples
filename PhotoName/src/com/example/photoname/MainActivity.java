package com.example.photoname;	

import java.io.File;		
import java.io.IOException;

import com.example.photoname.utils.AlbumStorageDirFactory;
import com.example.photoname.utils.BaseAlbumDirFactory;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final String SHARED_PREFERENCES = "com.example.photoname.SHARED_PREFERENCES";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String APP_PATH = "PHOTO_NAME";
	
	/* path from current picture */
	private String mCurrentPhotoPath = null;
	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;		
	
    /* Handle to SharedPreferences for this app */
    SharedPreferences mPrefs;
    /* Handle to a SharedPreferences editor */
    SharedPreferences.Editor mEditor;
	
	/* view holder patter */
	private ViewHolder mViewHolder;		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Get EditTexts and ImageView */
		this.mViewHolder = new ViewHolder();
		this.mViewHolder.prefixName = (EditText) findViewById(R.id.prefixName);
		this.mViewHolder.mainName = (EditText) findViewById(R.id.mainName);
		this.mViewHolder.sufixName = (EditText) findViewById(R.id.sufixName);
		this.mViewHolder.imageView = (ImageView) findViewById(R.id.imageView);	
		
		/* create album */
        mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		
        /* disable errors onclick event from prefix text*/
        this.mViewHolder.prefixName.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {mViewHolder.prefixName.setError(null);}
		});
        
        /* disable errors onclick event from sufix text*/
        this.mViewHolder.sufixName.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {mViewHolder.sufixName.setError(null);}
		});
        
        /* disable errors onclick event from main text*/
        this.mViewHolder.mainName.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {mViewHolder.mainName.setError(null);}
		});     
        
        
        /* Open Shared Preferences */ 
        //mPrefs = getPreferences(Context.MODE_PRIVATE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //mEditor = mPrefs.edit();        
        
        /* Get default settings */
		this.mViewHolder.prefixName.setText(mPrefs.getString("prefix_preference", getString(R.string.prefix_default)));
		this.mViewHolder.sufixName.setText(mPrefs.getString("sufix_preference", getString(R.string.sufix_default)));
        
        /* set onclick listener on Camera Button */
		findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    		dispatchTakePictureIntent();					
			}
		});
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);	    	    		
		return super.onCreateOptionsMenu(menu);
	}   	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
	
	
	/**
	 * Setup a file image. Create new file image and store in private attribute
	 * @return
	 * @throws IOException
	 */
	public File setUpPhotoFile(String imageFileName) throws IOException {	
		
		// Create an image file name
		File albumFile = ((BaseAlbumDirFactory) mAlbumStorageDirFactory).getAlbumDir(APP_PATH, getString(R.string.app_name));
		File imageFile = new File(albumFile, imageFileName);
		
		if(imageFile.exists()){
			return null;
		}
		
		mCurrentPhotoPath = imageFile.getAbsolutePath();
		
		return imageFile;
	}	
	
	/**
	 * take picture with native camera app and save it in external store 
	 */
	private void dispatchTakePictureIntent()
	{	
		String imageFileName = 
				mViewHolder.prefixName.getText().toString() 
				+ mViewHolder.mainName.getText().toString() 
				+ mViewHolder.sufixName.getText().toString()
				+JPEG_FILE_SUFFIX;
		
		/* check errors */
		/* check empty edit texts */
    	if(mViewHolder.prefixName.getText().toString().trim().equals("")){
    		mViewHolder.prefixName.requestFocus();
    		mViewHolder.prefixName.setError(getString(R.string.prefixNameRequired));
    	}else if(mViewHolder.mainName.getText().toString().trim().equals("")){
    		mViewHolder.mainName.requestFocus();
    		mViewHolder.mainName.setError(getString(R.string.mainNameRequired));
    	}else if(mViewHolder.sufixName.getText().toString().trim().equals("")){
    		mViewHolder.sufixName.requestFocus();
    		mViewHolder.sufixName.setError(getString(R.string.sufixNameRequired));
    	}else{    
    		/* disable error */
    		mViewHolder.prefixName.setError(null);
    		mViewHolder.mainName.setError(null);
    		mViewHolder.sufixName.setError(null);
		    		    		
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			
			if (takePictureIntent.resolveActivity(this
					.getPackageManager()) != null) {
				
				File photoFile = null;
				try {
					/* create file image */
					photoFile = setUpPhotoFile(imageFileName);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
							
				if (photoFile != null) { // Check if the File was successfully
					
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					
					startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
				}else{
					mViewHolder.mainName.requestFocus();
					Toast.makeText(this, R.string.imageFileExists, Toast.LENGTH_LONG).show();
					mViewHolder.mainName.setError(getString(R.string.imageFileExists));
				}
			}				
    	}
	}		   

	/**
	 * Scale size of a new picture to the object ImageView from Fragment  
	 */
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mViewHolder.imageView.getWidth();
		int targetH = mViewHolder.imageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mViewHolder.imageView.setImageBitmap(bitmap);

		/* show hidden image view */
		mViewHolder.imageView.setVisibility(View.VISIBLE);
	}    

	/**
	 * add picture to media provider
	 */
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && mCurrentPhotoPath != null)
		{			
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}
	}
	
	
	/**
	 * View holder pattern for EditText
	 *
	 */
	private class ViewHolder {
		EditText prefixName;
		EditText mainName;
		EditText sufixName;
		ImageView imageView;
	}	

}
