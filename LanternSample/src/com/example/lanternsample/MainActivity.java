package com.example.lanternsample;
	
import com.example.lanternsample.R;	

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    private static int sCameraId;
	private Camera mCamera;
    private Parameters mParams;
    private boolean mIsFlashOn;
    private boolean mHasFlash;
    private ImageButton mBtnSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* check if exists flash on mCamera */
		mHasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		
		if(mHasFlash)
		{		
			sCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			
			/* get button and add on click listener */
			mBtnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
			
			/* set mIsFlashOn to false on create activity */
			mIsFlashOn = false;
			
			mBtnSwitch.setOnClickListener(new View.OnClickListener() {						 
			    @Override
			    public void onClick(View v) {
			        if (mIsFlashOn) {
			            // turn OFF flash
			            turnOffFlash();
			        } else {
			            // turn ON flash
			            turnOnFlash();
			        }
			    }
			});		
		}
	}
	
	/**
	 * Safe camera open with an id
	 * @param id
	 * @return
	 */
	private boolean safeCameraOpen(int id) {
		boolean qOpened = false;
		try {
			/* liberate mCamera */
			releaseCameraAndPreview();
			
			/* open mCamera */
			mCamera = Camera.open(id);
			
			/* get parameters */
			mParams = mCamera.getParameters();
			
			qOpened = (mCamera != null);
		} catch (Exception e) {
			Log.e(getString(R.string.app_name), "failed to open Camera");
			e.printStackTrace();
		}
		
		return qOpened;
	}
	
	/**
	 * Left mCamera if it is opened by other application
	 */
	private void releaseCameraAndPreview() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	private void toggleButtonImage(){
	    if(mIsFlashOn){
	        mBtnSwitch.setImageResource(R.drawable.switch_on);
	    }else{
	        mBtnSwitch.setImageResource(R.drawable.switch_off);
	    }
	}
	
	/**
	 * Turn on mCamera flash
	 */
	private void turnOnFlash() {
        if (!mIsFlashOn) {
            if (mCamera == null || mParams == null) {
                return;
            }
           
            //playSound();
             
            mParams = mCamera.getParameters();
            mParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParams);
            mCamera.startPreview();
            mIsFlashOn = true;
                         
            toggleButtonImage();
        }        
	}
	
	/**
	 * Turn off mCamera flash
	 */
	private void turnOffFlash() {
        if (mIsFlashOn) {
            if (mCamera == null || mParams == null) {
                return;
            }

            //playSound();
             
            mParams = mCamera.getParameters();
            mParams.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParams);
            mCamera.stopPreview();
            mIsFlashOn = false;
             
            // changing button/switch image
            toggleButtonImage();
        }
	}
	
	@Override
	protected void onStart() {	     
	    // get id mCamera
		safeCameraOpen(sCameraId);
		
		// toggle to turn on if you want switch on the flash on start
		turnOnFlash();
		
	    super.onStart();
	}
	 
	@Override
	protected void onStop() {	     
	    // liberate the camera 
	    if (mCamera != null) {
	        mCamera.stopPreview();
	        mCamera.release();
	        mCamera = null;
	    }
	    
	    super.onStop();
	}	
	
	@Override
	protected void onPause() {	     
	    // turn off flash on pause 
	    turnOffFlash();
	    
	    super.onPause();
	}
}