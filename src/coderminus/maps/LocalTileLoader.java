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
	private Tile          tile;
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
		while(true) 
		{
			tile = requests.dequeue();
			if(tile != null)
			{
				tilesCache.add(tile, loadFromFile(tile));
				Message message = handler.obtainMessage();
				message.arg1 = requests.size();
				message.arg2 = requests.id;
				message.what = 1;
				handler.sendMessage(message);
			}
			try 
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				break;
			}
		}
	}
	
	public Bitmap loadFromFile(Tile tile) 
	{
		try
		{
			return BitmapFactory.decodeFile(cacheBase + tile.key + ".tile");
		}
		catch(Exception e)
		{
			Log.d("Maps::MapTilesCache", "loadFromFile(" + tile.key + ") failed");
		}
		return null;
	}

	public static String getBaseDir() 
	{
		return cacheBase;
	}

}
