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
		inFileTilesCache   = new InFileTilesCache (inMemoryTilesCache, handler);
		remoteTileLoader   = new RemoteTileLoader (inFileTilesCache  , handler);
		resizedTilesCache  = new ResizedTilesCache(inFileTilesCache  , handler); 
	}

	public Bitmap getTileBitmap(Tile tile) 
	{
		if(inMemoryTilesCache.hasTile(tile.key))
		{
			return inMemoryTilesCache.getTileBitmap(tile.key);
		}
	
		if(inFileTilesCache.hasTile(tile.key))
		{
			inFileTilesCache.queueTileRequest(tile);
			return null;
		}
		
		remoteTileLoader.queueTileRequest(tile);

		if(resizedTilesCache.hasTile(tile)) 
		{
			return resizedTilesCache.getTileBitmap(tile);
		}
		
		if(inFileTilesCache.getCandidateForResize(tile.zoom, tile.mapX, tile.mapY) != null)
		{
			resizedTilesCache.queueResize(new ResizeTile(tile.key, tile.mapX, tile.mapY, tile.zoom));
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

	public void clearResizeCache() 
	{
		resizedTilesCache.clear();
	}

	public void setCachePath(String cachePath) 
	{
		inFileTilesCache.setCachePath(cachePath);
	}

	public void setTilePostfix(String tilePostfix) 
	{
		inFileTilesCache.setTilePostfix(tilePostfix);
	}
}
