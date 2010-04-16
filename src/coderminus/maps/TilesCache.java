package coderminus.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

public class TilesCache 
{
	private LRUMap<String, Bitmap> bitmapCache = new LRUMap<String, Bitmap>(10, 40);
	private Object lock = new Object();

	public TilesCache(Context context, Handler handler,
			TileQueueSizeWatcher sizeWatcher) 
	{
		
	}

	public void add(String tileKey, Bitmap bitmap) 
	{
		synchronized (lock) 
		{
			bitmapCache.remove(tileKey);
			bitmapCache.put(tileKey, bitmap);
		}
	}
	
	public boolean hasTile(String tileKey) 
	{
		synchronized (lock) 
		{		
			return bitmapCache.containsKey(tileKey);
		}
	}

	public void clean() 
	{
		synchronized (lock) 
		{	
			bitmapCache.clear();
		}
	}

	public Bitmap getTileBitmap(String tileKey) 
	{
		synchronized (lock) 
		{
			return bitmapCache.get(tileKey);
		}
	}
}
