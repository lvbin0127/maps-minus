package coderminus.maps;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Message;

public class ResizedTilesCache extends Thread 
{
	class TileScaler
	{
		public void scale(Tile minusZoomTile, Tile tile, LRUMap<String, Bitmap> extrapolatedBitmapCache) 
		{
			Bitmap minusZoomBitmap = inFileTilesCache.getTileBitmap(minusZoomTile);
			Bitmap closestBitmap = scaleUpAndChop(minusZoomBitmap, minusZoomTile, tile);
			extrapolatedBitmapCache.put(tile.key, closestBitmap);
		}
		
		private Bitmap scaleUpAndChop(Bitmap minusZoomBitmap, Tile minusZoomTile2,
				Tile tile) 
		{
			Bitmap bitmap = null;
			int scale = (tile.zoom - minusZoomTile2.zoom);
			if(scale > 1)
			{
				return null;
			}
			int xIncrement = 1;
			if(minusZoomTile2.mapX != 1)
			{
				xIncrement = tile.mapX % 2;
			}
			int yIncrement = 1;
			if(minusZoomTile2.mapY != 1)
			{
				yIncrement = tile.mapY % 2;
			}
			
			bitmap = BitmapScaler.scaleTo(minusZoomBitmap, 256*scale*2, 256*scale*2);
			bitmap = Bitmap.createBitmap(bitmap, (xIncrement*scale)*256, (yIncrement*scale)*256, 256, 256);
			return bitmap;
		}

	}

	class ResizeTile
	{
		public ResizeTile(Tile tile, Tile candidateForResize) 
		{
			this.tile = tile;
			this.candidateForResize = candidateForResize;
		}
		public Tile tile;
		public Tile candidateForResize;
	}
	
	private LRUMap<String, Bitmap> extrapolatedBitmapCache = new LRUMap<String, Bitmap>(5, 9);
	private InFileTilesCache inFileTilesCache;
	private Queue<ResizeTile> requests = new LinkedList<ResizeTile>();
	private ResizeTile resizeTile;
	private TileScaler tileScaler = new TileScaler();

	
	public ResizedTilesCache(InFileTilesCache inFileTilesCache) 
	{
		this.inFileTilesCache = inFileTilesCache;
	}

	public void queueResize(Tile tile, Tile candidateForResize) 
	{
		if(!requests.contains(tile)) 
		{
			requests.add(new ResizeTile(tile, candidateForResize));
		}
	}
	
	@Override
	public void run() 
	{
		while(true) 
		{
			resizeTile = requests.poll();
			if(resizeTile != null)
			{
				tileScaler.scale(resizeTile.candidateForResize, resizeTile.tile, extrapolatedBitmapCache);

				Message message = handler.obtainMessage();
				message.what = 0;
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

	public boolean hasTile(Tile tile) 
	{
		return extrapolatedBitmapCache.containsKey(tile.key);
	}

	public Bitmap getTileBitmap(Tile tile) 
	{
		return extrapolatedBitmapCache.get(tile.key);
	}
}
