/**
 * 
 */
package coderminus.maps;

import java.util.LinkedList;
import java.util.Queue;

public class RequestsQueue 
{
	private Queue<String> queue = new LinkedList<String>();
	private Object lock = new Object();
	int id;

	RequestsQueue(int id)
	{
		this.id = id;
	}
	
	public void queue(String tileKey) 
	{
		synchronized (lock) 
		{
			if(!queue.contains(tileKey)) 
			{
				queue.add(tileKey);
			}
		}
	}

	public boolean contains(String tileKey)
	{
		synchronized (lock) 
		{
			return queue.contains(tileKey);
		}
	}
	
	public String dequeue() 
	{
		synchronized (lock) 
		{
			return queue.poll();
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
		}
	}
	
	public int size() 
	{
		synchronized (lock) 
		{
			return queue.size();
		}
	}
}