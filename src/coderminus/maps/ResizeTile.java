/**
 * 
 */
package coderminus.maps;

class ResizeTile
{
	public String key;
	public int mapX;
	public int mapY;
	public int zoom;
	
	public ResizeTile(String key, int mapX, int mapY, int zoom) 
	{
		this.key = key;
		this.mapX = mapX;
		this.mapY = mapY;
		this.zoom = zoom;
	}
}