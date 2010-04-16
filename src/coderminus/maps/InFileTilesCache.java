package coderminus.maps;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Handler;

public class InFileTilesCache 
{
	private RequestsQueue requests = new RequestsQueue(0);
	private LocalTileLoader tileLoader;

	public InFileTilesCache(TilesCache tilesCache, Handler handler) 
	{
		tileLoader = new LocalTileLoader(requests, tilesCache, handler);
	}

	public boolean hasTile(String tileKey) 
	{
		final File sddir = new File(LocalTileLoader.getBaseDir() + tileKey + ".tile");
		return sddir.exists(); 
	}

	public void queueTileRequest(Tile tile) 
	{
		requests.queue(tile.key);
		synchronized (tileLoader) 
		{
			tileLoader.notify();
		}
	}
	
	public void removeCachedTile(Tile tile) 
	{
		deleteFile(LocalTileLoader.getBaseDir() + tile.key + ".tile");
	}
	
	private void deleteFile(String fileName) 
	{
		final File file = new File(fileName);
		file.delete();
	}

	public Tile getCandidateForResize(int zoom, int mapX, int mapY) 
	{
		return findClosestCachedMinusTile(zoom, mapX, mapY);
	}

	private Tile findClosestCachedMinusTile(int zoom, int mapX, int mapY) 
	{
		Tile minusZoomTile = generateMinusZoomTile(zoom, mapX, mapY);
		
		if((minusZoomTile != null) && hasTile(minusZoomTile.key))
		{
			return minusZoomTile;
		}
//		else
//		{
//			minusZoomTile = findClosestCachedMinusTile(minusZoomTile);
//		}
		return null;
	}
	
	private Tile generateMinusZoomTile(int zoom, int mapX, int mapY) 
	{
		if(zoom == 0)
		{
			return null;
		}
		Tile minusZoomTile = new Tile();
		minusZoomTile.zoom = zoom - 1;
		minusZoomTile.mapX = mapX/2;
		minusZoomTile.mapY = mapY/2;
		
		//minusZoomTile.offsetX = tile.offsetX;
		//minusZoomTile.offsetY = tile.offsetY;
		minusZoomTile.key = 
			minusZoomTile.zoom + "/" + minusZoomTile.mapX + "/" + minusZoomTile.mapY + ".png";
		
		return minusZoomTile;
	}

	public Bitmap getTileBitmap(String tileKey) 
	{
		return tileLoader.loadFromFile(tileKey);
	}

}
