package coderminus.maps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

public class MapTilesCache 
{
	private Hashtable<String, Bitmap> bitmapCache = new Hashtable<String, Bitmap>();
	private static final String cacheBase = "/sdcard/Maps/";
	private Queue<String> keys = new LinkedList<String>();
	
	private DownloaderThreadPool threadPool;

	public MapTilesCache(Context context, Handler handler, TileQueueSizeWatcher sizeWatcher) 
	{
		threadPool = new DownloaderThreadPool(this, context, handler,sizeWatcher);
	}
	
	public boolean hasTileBitmap(String imageKey) 
	{
		return bitmapCache.containsKey(imageKey);
	}

	void loadFromFile(String imageKey) 
	{
		if(isInFile(imageKey))
		{
			try
			{
    			Bitmap bitmap = BitmapFactory.decodeFile(cacheBase + imageKey + ".tile");
    			if(bitmap != null) 
    			{
    				cacheBitmap(imageKey, bitmap);
    			}
			}
			catch(Exception e)
			{
				Log.d("Maps::MapTilesCache", "loadFromFile(" + imageKey + ") failed");
			}
		}
	}

	public boolean isInFile(String imageKey) 
	{
		final File sddir = new File(cacheBase + imageKey + ".tile");
		return sddir.exists(); 
	}

	private void clearOldestBitmap() 
	{
		bitmapCache.remove(keys.remove());
	}

	public void addTile(String imageKey, final byte[] bitmapData) 
	{
		if(bitmapData == null || bitmapData.length == 0) 
		{
			return;
		}
		saveBufferToFile(bitmapData, cacheBase + imageKey + ".tile");

		cacheBitmap(imageKey, BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length));
	}

	private void saveBufferToFile(byte[] bitmapData, String fileName) 
	{
		ensureFolderExists(fileName.substring(0, fileName.lastIndexOf('/')));

        FileOutputStream fos = null;
		try 
		{
			fos = new FileOutputStream(fileName);
			final BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);
		
			bos.write(bitmapData);
        	bos.flush();
	        bos.close();
		} 
		catch (FileNotFoundException e) 
		{
			//Log(e);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void ensureFolderExists(String path) 
	{
        final File folder = new File(path);
		if (!folder.mkdirs()) 
		{
			//throw new Exception();
		}
	}

	private void cacheBitmap(String imageKey, Bitmap bitmap) 
	{
		while(bitmapCache.size() > 60) 
		{
			clearOldestBitmap();
		}
		
		bitmapCache.put(imageKey, bitmap);
		keys.add(imageKey);
	}

	public Bitmap getTileBitmap(String key) 
	{
		return bitmapCache.get(key);
	}

	public void clean() 
	{
		bitmapCache.clear();
		keys.clear();
		threadPool.clearRequests();
	}

	public void queueTileRequest(String imageKey) 
	{
		threadPool.addRequest(imageKey);
	}

	public void removeCachedTile(String imageKey) 
	{
		deleteFile(cacheBase + imageKey + ".tile");
	}

	private void deleteFile(String fileName) 
	{
		final File file = new File(fileName);
		file.delete();
	}

}
