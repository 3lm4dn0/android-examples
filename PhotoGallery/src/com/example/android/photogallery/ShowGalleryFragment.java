package com.example.android.photogallery;

import java.io.File;			

import com.example.android.photogallery.R;
import com.example.android.photogallery.adapters.ImageAdapter;
import com.example.android.photogallery.utils.AlbumStorageDirFactory;
import com.example.android.photogallery.utils.BaseAlbumDirFactory;
import com.example.android.photogallery.utils.ScreenSizeUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

public class ShowGalleryFragment extends Fragment {
	
	private GridView mGridView;
	private ProgressBar mProgressBar;
	private LoadFilesAsyncTask mLoadFilesAsyncTask;

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private ImageAdapter mImageAdapter;
	
	SharedPreferences mSharedPref;
	SharedPreferences.Editor mEditor;

	/**
	 *  Photo album path
	 **/
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_section_gallery,
				container, false);	
		
		/* Get gridView */
		mGridView = (GridView) rootView.findViewById(R.id.gridview);
		
		/* Get progressBar */
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
	    		
		/* Get path from gallery */
		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		
		/* Get Shared preferences */
		mSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		mEditor = mSharedPref.edit();

		/* create and set adapter to grid view */
		ScreenSizeUtils screenSize = new ScreenSizeUtils(this.getActivity());		
		mImageAdapter = new ImageAdapter(getActivity(), screenSize.getWidth());
		mGridView.setAdapter(mImageAdapter);
		
        /* get album */
        File albumPath = ((BaseAlbumDirFactory) mAlbumStorageDirFactory).getAlbumDir(getAlbumName(), getString(R.string.app_name));
		
		if(!albumPath.isDirectory()){
			Log.e(getString(R.string.app_name), albumPath.getAbsolutePath() + " is not a dir.");
		}
		
		/* Show grid view on second thread */
		mLoadFilesAsyncTask = new LoadFilesAsyncTask(mImageAdapter, albumPath, mGridView, mProgressBar);
		mLoadFilesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		return rootView;
	}
	
	@Override
	public void onResume(){
		
		/* get taken photo from SharedPreferences */
		String path = mSharedPref.getString("IMAGE_PATH", null);
		
		if(path != null){			
			mImageAdapter.add(path);
			
			mImageAdapter.notifyDataSetChanged();
		
			/* remove key */
			mEditor.remove("IMAGE_PATH").commit();
		}
		
		super.onResume();
	}
	
	@Override
    public void onDestroy() {
		mLoadFilesAsyncTask.cancel(true);
		
        super.onPause();
    }	
}