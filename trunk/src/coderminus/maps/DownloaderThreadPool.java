package coderminus.maps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class DownloaderThreadPool 
{
	public class RequestsQueue 
	{
		private Queue<String> queue = new LinkedList<String>();
		private Object lock = new Object();
		private TileQueueSizeWatcher sizeWatcher;
		private int id;

		RequestsQueue(TileQueueSizeWatcher sizeWatcher, int id)
		{
			this.sizeWatcher = sizeWatcher;
			this.id = id;
		}
		public void queue(String imageKey) 
		{
			synchronized (lock ) 
			{
				if(!queue.contains(imageKey)) 
				{
					queue.add(imageKey);
				}
			}
		}

		public String dequeue() 
		{
			synchronized (lock) 
			{
				try
				{
    				if(queue.size() > 0) 
    				{
    					String text = queue.remove();
    					//sizeWatcher.onSizeChanged(queue.size());
    					return text; 
    				}
    				else 
    				{
    					return "";
    				}
				}
				finally
				{
				}
			}
		}

		public boolean hasRequest() 
		{
			synchronized (lock) 
			{
				return queue.size() != 0;
			}
		}

		public void clear() 
		{
			synchronized (lock) 
			{
				queue.clear();
				if(sizeWatcher != null)
				{
					sizeWatcher.onSizeChanged(queue.size(), id);
				}
			}
		}
		public int size() 
		{
			return queue.size();
		}
	}

	class DownloaderThread extends Thread 
	{
		private RequestsQueue requests;
		private MapTilesCache tilesCache;
		private Handler       handler;
		private static final int IO_BUFFER_SIZE = 8192;
		private String urlBase                  = "http://tile.openstreetmap.org/";
		byte[] buffer                           = new byte[8192];
		private String currentImageKey          = "";

		public DownloaderThread(RequestsQueue requests,
				MapTilesCache tilesCache, Handler handler) 
		{
			this.requests    = requests;
			this.tilesCache  = tilesCache;
			this.handler     = handler;
			start();
		}

		@Override
		public void run() 
		{
			while(!isStopped()) 
			{
				if(loadTile(requests.dequeue())) 
				{
					Message message = handler.obtainMessage();
					message.arg1 = requests.size();
					message.arg2 = requests.id;
					message.what = 1;
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

		private boolean isStopped() 
		{
			return false;
		}

		private boolean loadTile(String imageKey) 
		{
			this.currentImageKey = imageKey;
			
			if(imageKey.equals("")) 
			{
				return false;
			}
			if(!tilesCache.hasTileBitmap(imageKey)) 
			{
				tilesCache.loadFromFile(imageKey);
			}
			if(!tilesCache.hasTileBitmap(imageKey)) 
			{
				byte[] bitmapData = loadBitmap(imageKey);
				if(bitmapData == null) 
				{
					return false;
				}
				tilesCache.addTile(imageKey, bitmapData);
			}
			return true;
		}
		
		private byte[] loadBitmap(String imageKey) 
		{
			String key = urlBase + imageKey;
			
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
				//failedTiles.add(imageKey);
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

		public boolean isWorkingOn(String imageKey) 
		{	
			return currentImageKey.equals(imageKey);
		}
	}

	private static final int POOL_SIZE = 2;

	private RequestsQueue localFileRequests;
	private RequestsQueue remoteFileRequests;
	
	private Vector<DownloaderThread> threads = new Vector<DownloaderThread>();

	private MapTilesCache tilesCache;

	private Handler handler;

	public DownloaderThreadPool(
			MapTilesCache tilesCache, 
			Context context, 
			Handler handler, 
			TileQueueSizeWatcher sizeWatcher) 
	{
		localFileRequests = new RequestsQueue(sizeWatcher, 0);
		remoteFileRequests = new RequestsQueue(sizeWatcher, 1);
		this.tilesCache = tilesCache;
		this.handler = handler;
		
		for(int index = 0; index < POOL_SIZE; ++index) 
		{
			threads.add(new DownloaderThread(localFileRequests, tilesCache, handler));
		}

		for(int index = 0; index < POOL_SIZE; ++index) 
		{
			threads.add(new DownloaderThread(remoteFileRequests, tilesCache, handler));
		}
	}

	public void addRequest(String imageKey) 
	{
		if(tilesCache.isInFile(imageKey))
		{
			localFileRequests.queue(imageKey);
			Message message = handler.obtainMessage();
			message.arg1 = localFileRequests.size();
			message.arg2 = 0;
			message.what = 0;
			handler.sendMessage(message);
		}
		else
		{
			remoteFileRequests.queue(imageKey);
			Message message = handler.obtainMessage();
			message.arg1 = remoteFileRequests.size();
			message.arg2 = 1;
			message.what = 0;
			handler.sendMessage(message);
		}
	}

	public void clearRequests() 
	{
		localFileRequests.clear();
	}
}
