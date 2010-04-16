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

public class DownloaderThreadPool {

	public class RequestsQueue {
		private Queue<String> queue = new LinkedList<String>();
		private Object lock = new Object();

		public void queue(String imageKey) {
			synchronized (lock ) {
				if(!queue.contains(imageKey)) {
					queue.add(imageKey);
				}
			}
		}

		public String dequeue() {
			synchronized (lock) {
				if(queue.size() > 0) {
					return queue.remove();
				}
				else {
					return "";
				}
			}
		}

		public boolean hasRequest() {
			synchronized (lock) {
				return queue.size() != 0;
			}
		}

		public void clear() {
			synchronized (lock) {
				queue.clear();
			}
		}
	}

	class DownloaderThread extends Thread {

		private RequestsQueue requests;
		private MapTilesCache tilesCache;
		private Handler       handler;
		private static final int IO_BUFFER_SIZE = 8192;
		private String urlBase                  = "http://tile.openstreetmap.org/";
		byte[] buffer                           = new byte[8192];

		public DownloaderThread(RequestsQueue requests,
				MapTilesCache tilesCache, Handler handler) {
			this.requests    = requests;
			this.tilesCache  = tilesCache;
			this.handler     = handler;
			start();
		}

		@Override
		public void run() {
			while(!isStopped()) {
				if(loadTile(requests.dequeue())) {
					handler.sendEmptyMessage(0);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
		}

		private boolean isStopped() {
			return false;
		}

		private boolean loadTile(String imageKey) {
			if(imageKey.equals("")) {
				return false;
			}
			if(!tilesCache.hasTile(imageKey)) {
				tilesCache.loadFromFile(imageKey);
			}
			if(!tilesCache.hasTile(imageKey)) {
				byte[] bitmapData = loadBitmap(imageKey);
				if(bitmapData == null) {
					return false;
				}
				tilesCache.addTile(imageKey, bitmapData);
			}
			return true;
		}
		
		private byte[] loadBitmap(String imageKey) {
			String key = urlBase + imageKey;
			
			InputStream in = null;
			OutputStream out = null;
			ByteArrayOutputStream dataStream = null;
			
			try {
				URL urL = new URL(key);
				InputStream inStream = urL.openStream();
				in = new BufferedInputStream(inStream, IO_BUFFER_SIZE);

				dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
				copy(in, out);
			
				out.flush();
				out.close();
				return dataStream.toByteArray();
			} catch (IOException e) {
				//failedTiles.add(imageKey);
			}
			return null;
		}

		private void copy(InputStream in, OutputStream out) {
			int read;
			try {
				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private static final int POOL_SIZE = 6;

	private RequestsQueue requests = new RequestsQueue();
	private Vector<DownloaderThread> threads = new Vector<DownloaderThread>();

	public DownloaderThreadPool(MapTilesCache tilesCache, Context context, Handler handler) {
		for(int index = 0; index < POOL_SIZE; ++index) {
			threads.add(new DownloaderThread(requests, tilesCache, handler));
		}
	}

	public void addRequest(String imageKey) {
		requests.queue(imageKey);
	}

	public void clearRequests() {
		requests.clear();
	}
}
