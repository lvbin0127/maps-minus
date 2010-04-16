package coderminus.maps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.os.Handler;
import android.os.Message;

public class RemoteTileLoader extends Thread
{
	public static String urlBaseOSM_a = "http://a.tile.openstreetmap.org/";
	public static String urlBaseOSM_b = "http://b.tile.openstreetmap.org/";
	public static String urlBaseOSM_c = "http://c.tile.openstreetmap.org/";
	
	//private String urlBaseGoogle            = "http://mt1.google.com/vt/x=1&y=0&z=1";

	private static final int IO_BUFFER_SIZE = 8192;
	byte[] buffer                           = new byte[8192];

	private RequestsQueue requestsQueue = new RequestsQueue(1);
	private Handler handler;
	private Tile currentTile;

	public RemoteTileLoader(TilesCache tilesCache, Handler handler) 
	{
		this.handler       = handler;
		start();
	}

	public void queueTileRequest(Tile tile) 
	{
		requestsQueue.queue(tile);
	}
	
	@Override
	public void run() 
	{
		while(true) 
		{
			currentTile = requestsQueue.dequeue();
			if(currentTile != null)
			{
				if(loadTile(currentTile.key)) 
				{
					Message message = handler.obtainMessage();
					message.arg1 = requestsQueue.size();
					message.arg2 = requestsQueue.id;
					message.what = 1;
					handler.sendMessage(message);
				}
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

	private boolean loadTile(String imageKey) 
	{
		if(imageKey == null) 
		{
			return false;
		}
		try
		{
			byte[] bitmapData = loadBitmap(imageKey);
			if(bitmapData == null) 
			{
				return false;
			}
			addTile(currentTile.key, bitmapData);
			return true;
		}
		catch(Exception e)
		{
			
		}
		return false;
	}
	
	private byte[] loadBitmap(String imageKey) throws InterruptedException 
	{
		String key = urlBaseOSM_a + imageKey;
		
		InputStream in = null;
		OutputStream out = null;
		ByteArrayOutputStream dataStream = null;
		
		try 
		{
			URL urL = new URL(key);
			InputStream inStream = urL.openStream();
			in = new BufferedInputStream(inStream, IO_BUFFER_SIZE);

			dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
			copy(in, out);
		
			out.flush();
			out.close();
			return dataStream.toByteArray();
		} 
		catch (IOException e) 
		{
			Thread.sleep(3000);//failedTiles.add(imageKey);
		}
		return null;
	}

	private void copy(InputStream in, OutputStream out) 
	{
		int read;
		try 
		{
			while ((read = in.read(buffer)) != -1) 
			{
				out.write(buffer, 0, read);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void addTile(String imageKey, final byte[] bitmapData) 
	{
		if(bitmapData == null || bitmapData.length == 0) 
		{
			return;
		}
		saveBufferToFile(bitmapData, LocalTileLoader.getBaseDir() + imageKey + ".tile");

		//cacheBitmap(imageKey, BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length));
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

}
