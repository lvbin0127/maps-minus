package coderminus.maps;

import android.os.AsyncTask;


public class TilesCacher extends AsyncTask<Object, Object, Object>  
{
	private Tile nextTile = new Tile();
	private TilesCache tilesCache;
	
	@Override
	protected Object doInBackground(Object... objs) 
	{
		Tile[] tiles  = (Tile[])       objs[0];
		tilesCache    = (TilesCache)   objs[1];
		int zoomLevel = (Integer)      objs[2];
		int stopLevel = (Integer)      objs[3];

		for(Tile tile : tiles) 
		{
			queueNextZoomAheadRecursively(tile, zoomLevel, stopLevel);
		}

		return null;
	}
	private void queueNextZoomAheadRecursively(Tile tile, int queriedZoomLevel, int stopLevel) 
	{
		if(queriedZoomLevel >= stopLevel) return;
		int nextZoomLevel = queriedZoomLevel + 1;
		
		nextTile.mapX = tile.mapX * 2;
		nextTile.mapY = tile.mapY * 2;
		nextTile.key  = nextZoomLevel + "/" + nextTile.mapX + "/" + nextTile.mapY + ".png";
		if(!tilesCache.hasTile(nextTile))
		{
			getBitmap(nextTile);
		}
		queueNextZoomAheadRecursively(nextTile, nextZoomLevel, stopLevel);
		
		nextTile.mapX = tile.mapX * 2 + 1;
		nextTile.mapY = tile.mapY * 2;
		nextTile.key  = nextZoomLevel + "/" + nextTile.mapX + "/" + nextTile.mapY + ".png";
		if(!tilesCache.hasTile(nextTile))
		{
			getBitmap(nextTile);
		}
		queueNextZoomAheadRecursively(nextTile, nextZoomLevel, stopLevel);
		
		nextTile.mapX = tile.mapX * 2;
		nextTile.mapY = tile.mapY * 2 + 1;
		nextTile.key  = nextZoomLevel + "/" + nextTile.mapX + "/" + nextTile.mapY + ".png";
		if(!tilesCache.hasTile(nextTile))
		{
			getBitmap(nextTile);
		}
		queueNextZoomAheadRecursively(nextTile, nextZoomLevel, stopLevel);
		
		nextTile.mapX = tile.mapX * 2 + 1;
		nextTile.mapY = tile.mapY * 2 + 1;
		nextTile.key  = nextZoomLevel + "/" + nextTile.mapX + "/" + nextTile.mapY + ".png";
		if(!tilesCache.hasTile(nextTile))
		{
			getBitmap(nextTile);
		}
		queueNextZoomAheadRecursively(nextTile, nextZoomLevel, stopLevel);
	}
	
	private void getBitmap(Tile tile) 
	{
//		if(!tilesCache.hasTile(tile.key)) 
//		{
//			tilesCache.queueTileRequest(tile.key);
//		}
	}

}
