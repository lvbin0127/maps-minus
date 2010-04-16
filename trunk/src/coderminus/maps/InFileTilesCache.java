package coderminus.maps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
		final File sddir = new File(tileLoader.getBaseDir() + tileKey + tileLoader.getTilePostfix());
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
		deleteFile(tileLoader.getBaseDir() + tile.key + tileLoader.getTilePostfix());
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

	public void addBitmap(String imageKey, byte[] bitmapData) 
	{
		saveBufferToFile(bitmapData, tileLoader.getBaseDir() + imageKey + tileLoader.getTilePostfix());
	}
	
	private void saveBufferToFile(byte[] bitmapData, String fileName) 
	{
		ensureFolderExists(fileName.substring(0, fileName.lastIndexOf('/')));

        FileOutputStream fos = null;
		try 
		{
			fos = new FileOutputStream(fileName);
			final BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);
		
			bos.write(bitmapData);
        	bos.flush();
	        bos.close();
		} 
		catch (FileNotFoundException e) 
		{
			//Log(e);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void ensureFolderExists(String path) 
	{
        final File folder = new File(path);
		if (!folder.mkdirs()) 
		{
			//throw new Exception();
		}
	}

	public void setCachePath(String cachePath) 
	{
		tileLoader.setBaseDir(cachePath);
	}

	public void setTilePostfix(String tilePostfix) 
	{
		tileLoader.setTilePostfix(tilePostfix);
	}

}
