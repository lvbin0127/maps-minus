/**
 * 
 */
package coderminus.maps;

import java.util.LinkedList;
import java.util.Queue;

public class RequestsQueue 
{
	private Queue<Tile>          queue = new LinkedList<Tile>();
	int id;

	RequestsQueue(int id)
	{
		this.id = id;
	}
	
	public void queue(Tile tile) 
	{
		if(!queue.contains(tile)) 
		{
			queue.add(tile);
		}
	}

	public boolean contains(Tile tile)
	{
		return queue.contains(tile);
	}
	
	public Tile dequeue() 
	{
		return queue.poll(); 
	}

	public boolean hasRequest() 
	{
		return queue.size() != 0;
	}

	public void clear() 
	{
		queue.clear();
	}
	
	public int size() 
	{
		return queue.size();
	}
}