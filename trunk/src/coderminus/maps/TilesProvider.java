package coderminus.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

public class TilesProvider 
{
	class TileScaler extends AsyncTask<Object, Object, Object>
	{
		@Override
		protected Object doInBackground(Object... objs) {
			Bitmap minusZoomBitmap = (Bitmap)objs[0];
			Tile minusZoomTile     = (Tile  )objs[1];
			Tile tile              = (Tile  )objs[2];
			LRUMap<String, Bitmap> extrapolatedBitmapCache
			                       = (LRUMap<String, Bitmap>)objs[3];
			
			Bitmap closestBitmap = scaleUpAndChop(minusZoomBitmap, minusZoomTile, tile);
			extrapolatedBitmapCache.put(tile.key, closestBitmap);

			return null;
		}
	}
	
	private MapTilesCache tilesCache;
	private LRUMap<String, Bitmap> extrapolatedBitmapCache = new LRUMap<String, Bitmap>(5, 9);

	public TilesProvider(Context context, Handler handler, TileQueueSizeWatcher sizeWatcher)
	{
		tilesCache = new MapTilesCache(context, handler, sizeWatcher);
	}

	public Bitmap getTileBitmap(Tile tile) 
	{
		if(!tilesCache.hasTileBitmap(tile.key)) 
		{
			tilesCache.queueTileRequest(tile.key);

//			if(extrapolatedBitmapCache.containsKey(tile.key))
//			{
//				return extrapolatedBitmapCache.get(tile.key);
//			}
//			if(!tilesCache.isInFile(tile.key))
//			{
//				return getClosestResizedTile(tile);
//			}
			return null;
		}
		
		return tilesCache.getTileBitmap(tile.key);
	}
	
	private Bitmap getClosestResizedTile(Tile tile) 
	{
		Bitmap closestBitmap = null; 
		
		Tile minusZoomTile  = findClosestCachedMinusTile(tile);

		if(minusZoomTile != null)
		{
			if(tilesCache.hasTileBitmap(minusZoomTile.key)) 
			{
				Bitmap minusZoomBitmap = tilesCache.getTileBitmap(minusZoomTile.key);

				new TileScaler().execute(minusZoomBitmap, minusZoomTile, tile, extrapolatedBitmapCache);
			}
			else
			{
				tilesCache.queueTileRequest(minusZoomTile.key);
			}
		}
		//plusZoomTile = getBitmap(generatedPlusTile);
		return closestBitmap;
	}

	private Tile findClosestCachedMinusTile(Tile tile) 
	{
		//Bitmap closestBitmap  = null;
		Tile minusZoomTile = generateMinusZoomTile(tile);
		
		if((minusZoomTile != null) && tilesCache.isInFile(minusZoomTile.key))
		{
			return minusZoomTile;
		}
//		else
//		{
//			minusZoomTile = findClosestCachedMinusTile(minusZoomTile);
//		}
		return null;
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

	private Tile generateMinusZoomTile(Tile tile) 
	{
		if(tile.zoom == 0)
		{
			return null;
		}
		Tile minusZoomTile = new Tile();
		minusZoomTile.zoom = tile.zoom - 1;
		minusZoomTile.mapX = tile.mapX/2;
		minusZoomTile.mapY = tile.mapY/2;
		
		minusZoomTile.offsetX = tile.offsetX;
		minusZoomTile.offsetY = tile.offsetY;
		minusZoomTile.key = 
			minusZoomTile.zoom + "/" + minusZoomTile.mapX + "/" + minusZoomTile.mapY + ".png";
		
		return minusZoomTile;
	}

	public void clearCache() 
	{
		tilesCache.clean();
	}

	public void removeTile(String tileKey) 
	{
		tilesCache.removeCachedTile(tileKey);
	}

	public void clearExtrapolatedCache() 
	{
		extrapolatedBitmapCache.clear();
	}

	public Object getTilesCache() 
	{
		return tilesCache;
	}

}
