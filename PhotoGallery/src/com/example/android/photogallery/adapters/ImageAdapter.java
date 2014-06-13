package com.example.android.photogallery.adapters;
		
import java.util.Vector;		

import com.example.android.photogallery.FullScreenFragment;
import com.example.android.photogallery.R;
import com.example.android.photogallery.utils.BitmapUtils;
import com.example.android.photogallery.utils.ConvertPixels;
import com.example.android.photogallery.utils.ImageItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ImageAdapter extends BaseAdapter {

	private static final int MAX_IMAGE_SIZE_PX = 512;
	private Context mContext;
	
	/* view holder pattern */
    private class ViewHolder {ImageView mImageView;TextView mTextView;int position;}	

    /* list with the path from all images */
    private Vector<String> itemList = new Vector<String>();
        
	@SuppressWarnings("unused")
	/**
	 * use this if you want to try BitmapUtils.decodeSampledBitmapFromPath(path, width, heigth) 
	 */
	private int width;
	

	public ImageAdapter(Context c, int width) {
		// TODO remove width
		this.mContext = c;
		this.width = width;
	}

	public void setItemList(Vector<String> itemList){
		this.itemList = itemList;
	}
	
	public void add(String item) {
		itemList.add(item);
	}

	public void clear() {
		itemList.clear();
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (long) position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null)
		{
			holder = new ViewHolder();
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);  
			
			/* get convertView from */
			convertView = layoutInflater.inflate(R.layout.custom_image_view, parent, false);
			
			/* set values to ViewHolder */
            holder.mImageView = (ImageView) convertView.findViewById(R.id.image_view_gallery);
            holder.mTextView = (TextView) convertView.findViewById(R.id.image_text_view);
            
			/* save viewHolder */
			convertView.setTag(holder);
		} else {
			/* load viewHolder */
			holder = (ViewHolder) convertView.getTag();
		}		
		
		/* set max height to viewImage in dp */
		/**
		 * set max height to viewImage item expressed in dp.
		 * If you want a GridView with different height on items take a show to
		 * StaggeredGridView http://www.androidviews.net/2013/01/pinterest-like-adapterview/ 
		 */
		holder.mImageView.setMaxHeight((int) ConvertPixels.convertPixelsToDp(MAX_IMAGE_SIZE_PX, mContext));
		
		/* set position to holder to use in AsynTask */
        holder.position = position;
		
		/*
		 *  Using an AsyncTask to load the slow images in a background thread
		 *  @link http://developer.android.com/training/improving-layouts/smooth-scrolling.html
		 */
		new AsyncTask<ViewHolder, Void, Bitmap>() {
		    private ViewHolder v;

		    @Override
		    protected Bitmap doInBackground(ViewHolder... params) {
		        v = params[0];		        
		 
				/* get ImageItem */		
		        ImageItem item = new ImageItem(itemList.get(v.position));
		        
		        return BitmapUtils.getThumbnail(mContext, item.getAbsoluteName());
		    }

		    @Override
		    protected void onPostExecute(Bitmap result) {
		        super.onPostExecute(result);
		        if (v.position == position) {
		        	ImageItem item = new ImageItem(itemList.get(position));
		        	
		            // If this item hasn't been recycled already, hide the
		            // progress and set and show the image       	
		        	v.mImageView.setVisibility(View.VISIBLE);
		        	v.mTextView.setVisibility(View.VISIBLE);		        			        	
		        	
		        	/* set values to item */
		        	v.mImageView.setImageBitmap(result);
			        
		        	v.mTextView.setText(item.getName());
		    		
		    		ImageItem.Rotation rotation = item.getRotation();
		    		if(rotation==ImageItem.Rotation.PORTRAIT){			
		    			v.mImageView.setRotation(90.0f);
		    		}else if(rotation ==ImageItem.Rotation.LANDSCAPE){
		    			v.mImageView.setRotation(180.0f);
		    		}		
		        }
		    }
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, holder);

		/* set onclick listener to ImageView */
		convertView.setOnClickListener(new OnImageClickListener(position));
 		
		return convertView;
	}
	
	/**
	 * Adds onClick listener to Item to show FullScreen Activity 
	 */
	class OnImageClickListener implements OnClickListener{
		
		int _position;
		 
        // constructor
        public OnImageClickListener(int position) {
            this._position = position;
        }
 
        @Override
        public void onClick(View v) {
 
        	ImageItem imageItem = new ImageItem(itemList.get(_position));
            Intent intent = new Intent(mContext, FullScreenFragment.class);            
            intent.putExtra(mContext.getString(R.string.EXTRA_IMAGE_PATH), imageItem.getAbsoluteName());

            
            mContext.startActivity(intent);
        }		
	}
}