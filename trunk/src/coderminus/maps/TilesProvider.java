package coderminus.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

public class TilesProvider 
{
	private TilesCache        inMemoryTilesCache;
	private ResizedTilesCache resizedTilesCache = null;
	private InFileTilesCache  inFileTilesCache  = null;
	private RemoteTileLoader  remoteTileLoader  = null;
	
	public TilesProvider(Context context, Handler handler, TileQueueSizeWatcher sizeWatcher)
	{
		inMemoryTilesCache = new TilesCache       (context, handler, sizeWatcher);
		remoteTileLoader   = new RemoteTileLoader (inMemoryTilesCache, handler);
		inFileTilesCache   = new InFileTilesCache (inMemoryTilesCache, handler);
		resizedTilesCache  = new ResizedTilesCache(inFileTilesCache); 
	}

	public Bitmap getTileBitmap(Tile tile) 
	{
		if(inMemoryTilesCache.hasTile(tile))
		{
			return inMemoryTilesCache.getTileBitmap(tile);
		}
	
		if(inFileTilesCache.hasTile(tile))
		{
			inFileTilesCache.queueTileRequest(tile);
			return null;
		}
		
		remoteTileLoader.queueTileRequest(tile);

		if(resizedTilesCache.hasTile(tile)) 
		{
			return resizedTilesCache.getTileBitmap(tile);
		}
		
		if(inFileTilesCache.hasCandidateForResize(tile))
		{
			resizedTilesCache.queueResize(tile, inFileTilesCache.getCandidateForResize(tile));
		}
		
		return null;
	}
	
	public void clearCache() 
	{
		inMemoryTilesCache.clean();
	}

	public void removeTile(Tile tile) 
	{
		inFileTilesCache.removeCachedTile(tile);
	}

	public Object getTilesCache() 
	{
		return inMemoryTilesCache;
	}

}
