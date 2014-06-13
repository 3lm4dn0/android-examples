Photo Gallery
=============

Photo gallery sample with two swipe view tabs based on the sample from [Android developer web site](https://developer.android.com/training/implementing-navigation/lateral.html) and [GridView example](http://developer.android.com/guide/topics/ui/layout/gridview.html).

The gallery fragment call with an AsynTask (LoadFilesAsyncTask.java) to load ImageAdapter outside the main UI thread. Also, it uses an BaseAdapter (ImageAdapter.java) with pattern ViewHolder and other AsyncTask which loads each image path in a ImageView. 

To read the Bitmap from a file path, there is the class photogallery.utils.BitmapUtils.java with getThumbnail() and decodeSampledBitmapFromUri() methods. getThumbnail() gets the Thumbnail from the image with MINI_KIND size (512x364px).

The ImageAdapter also set the filename on a TextView and set the orientation from the image Exif metadata.

The items from GridView have a OnClickListener (OnImageClickListener) to load the FullScreenFragment.


