package coderminus.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

public class TilesCache 
{
	private LRUMap<String, Bitmap> bitmapCache = new LRUMap<String, Bitmap>(10, 60);
	private Object lock = new Object();

	public TilesCache(Context context, Handler handler,
			TileQueueSizeWatcher sizeWatcher) 
	{
		
	}

	public void add(Tile tile, Bitmap bitmap) 
	{
		synchronized (lock) 
		{
			bitmapCache.remove(tile.key);
			bitmapCache.put(tile.key, bitmap);
		}
	}
	
	public boolean hasTile(Tile tile) 
	{
		synchronized (lock) 
		{		
			return bitmapCache.containsKey(tile.key);
		}
	}

	public void clean() 
	{
		synchronized (lock) 
		{	
			bitmapCache.clear();
		}
	}

	public Bitmap getTileBitmap(Tile tile) 
	{
		synchronized (lock) 
		{
			return bitmapCache.get(tile.key);
		}
	}
}
