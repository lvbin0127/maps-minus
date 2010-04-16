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

	public boolean hasTile(Tile tile) 
	{
		final File sddir = new File(LocalTileLoader.getBaseDir() + tile.key + ".tile");
		return sddir.exists(); 
	}

	public void queueTileRequest(Tile tile) 
	{
		requests.queue(tile);
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

	public boolean hasCandidateForResize(Tile tile) 
	{
		return findClosestCachedMinusTile(tile) != null;
	}

	public Tile getCandidateForResize(Tile tile) 
	{
		return findClosestCachedMinusTile(tile);
	}

	private Tile findClosestCachedMinusTile(Tile tile) 
	{
		//Bitmap closestBitmap  = null;
		Tile minusZoomTile = generateMinusZoomTile(tile);
		
		if((minusZoomTile != null) && hasTile(minusZoomTile))
		{
			return minusZoomTile;
		}
//		else
//		{
//			minusZoomTile = findClosestCachedMinusTile(minusZoomTile);
//		}
		return null;
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

	public Bitmap getTileBitmap(Tile tile) 
	{
		return tileLoader.loadFromFile(tile);
	}

}
