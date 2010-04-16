package coderminus.maps;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class ResizedTilesCache extends Thread 
{
	class TileScaler
	{
		public Bitmap scale(Bitmap minusZoomBitmap, Tile minusZoomTile, int mapX, int mapY)
		{
			Bitmap closestBitmap = scaleUpAndChop(minusZoomBitmap, minusZoomTile.mapX, minusZoomTile.mapY, mapX, mapY);
			return closestBitmap;
		}
		
		private Bitmap scaleUpAndChop(Bitmap minusZoomBitmap, int minusMapX, int minusMapY, int mapX, int mapY) 
		{
			Bitmap bitmap = null;
			int xIncrement = 1;
			if(minusMapX != 1)
			{
				xIncrement = mapX % 2;
			}
			int yIncrement = 1;
			if(minusMapY != 1)
			{
				yIncrement = mapY % 2;
			}
			
			bitmap = BitmapScaler.scaleTo(minusZoomBitmap, 256*2, 256*2);
			bitmap = Bitmap.createBitmap(bitmap, (xIncrement)*256, (yIncrement)*256, 256, 256);
			return bitmap;
		}

	}

	private LRUMap<String, Bitmap> extrapolatedBitmapCache = new LRUMap<String, Bitmap>(5, 9);
	private InFileTilesCache inFileTilesCache;
	private Queue<ResizeTile> requests = new LinkedList<ResizeTile>();
	private ResizeTile resizeTile;
	private TileScaler tileScaler = new TileScaler();
	private Object lock = new Object();
	private Handler handler;

	
	public ResizedTilesCache(InFileTilesCache inFileTilesCache, Handler handler) 
	{
		this.inFileTilesCache = inFileTilesCache;
		this.handler = handler;
		start();
	}

	public void queueResize(ResizeTile resizeRequest) 
	{
		synchronized (lock) 
		{
			if(!hasRequest(resizeRequest) && !extrapolatedBitmapCache.containsKey(resizeRequest.key))
			{
				requests.add(resizeRequest);
			}
		}
		synchronized (this) 
		{
			this.notify();
		}
	}
	
	private boolean hasRequest(ResizeTile resizeRequest) 
	{
		synchronized (lock) 
		{
    		for(ResizeTile request : requests)
    		{
    			if(request.key == resizeRequest.key)
    			{
    				return true;
    			}
    		}
		}
		return false;
	}

	@Override
	public void run() 
	{
		while(true) 
		{
			synchronized (lock) 
			{
				resizeTile = requests.poll();
			}
			
			if(resizeTile != null)
			{
				Tile minusZoomTile = inFileTilesCache.getCandidateForResize(resizeTile.zoom, resizeTile.mapX, resizeTile.mapY);
				if(minusZoomTile != null)
				{
    				Bitmap minusZoomBitmap = inFileTilesCache.getTileBitmap(minusZoomTile.key);
    
    				Bitmap closestBitmap = tileScaler.scale(minusZoomBitmap, minusZoomTile, resizeTile.mapX, resizeTile.mapY);
    				synchronized (lock) 
    				{
    					extrapolatedBitmapCache.put(resizeTile.key, closestBitmap);
    				}
				}

				Message message = handler.obtainMessage();
				message.arg1 = requests.size();
				message.arg2 = 2;
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
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				break;
			}
		}
	}

	public boolean hasTile(Tile tile) 
	{
		return extrapolatedBitmapCache.containsKey(tile.key);
	}

	public Bitmap getTileBitmap(Tile tile) 
	{
		synchronized (lock) 
		{
			return extrapolatedBitmapCache.get(tile.key);
		}
	}

	public void clear() 
	{
		synchronized (lock) 
		{
			extrapolatedBitmapCache.clear();
		}		
	}
}
