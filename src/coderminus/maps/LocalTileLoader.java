package coderminus.maps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LocalTileLoader extends Thread 
{
	private static String cacheBase = "/sdcard/Maps/";

	private RequestsQueue requests;
	private TilesCache    tilesCache;
	private Handler       handler;
	
	public LocalTileLoader(RequestsQueue requests,	TilesCache tilesCache, Handler handler) 
	{
		this.requests     = requests;
		this.tilesCache   = tilesCache;
		this.handler      = handler;
		start();
	}

	@Override
	public void run() 
	{
		String tileKey;
		while(true) 
		{
			tileKey = requests.dequeue();
			if(tileKey != null)
			{
				tilesCache.add(tileKey, loadFromFile(tileKey));
				Message message = handler.obtainMessage();
				message.arg1 = requests.size();
				message.arg2 = requests.id;
				message.what = 1;
				handler.sendMessage(message);
			}
			try 
			{
				synchronized (this) 
				{
					if(requests.size() == 0)
					{
						this.wait();
					}
				}
				Thread.sleep(150);
			} 
			catch (InterruptedException e) 
			{
				break;
			}
		}
	}
	
	public Bitmap loadFromFile(String tileKey) 
	{
		try
		{
			return BitmapFactory.decodeFile(cacheBase + tileKey + ".tile");
		}
		catch(Exception e)
		{
			Log.d("Maps::MapTilesCache", "loadFromFile(" + tileKey + ") failed");
		}
		return null;
	}

	public static String getBaseDir() 
	{
		return cacheBase;
	}

}
