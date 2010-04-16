package coderminus.maps;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class OsmMapView extends View {

	public class Tiles extends Vector<Tile> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6468659912600523042L;
	}

	public class Tile {

		public int mapX;
		public int mapY;
		public int offsetX;
		public int offsetY;
		public int zoom;
		public String key;

	}

	private Paint paint = new Paint();
	private int offsetX = 0;
	private int offsetY = 0;
	private int touchDownX = 0;
	private int touchDownY = 0;
	private int zoomLevel = 0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			invalidate();
		}
	};
	
	private MapTilesCache tilesCache = new MapTilesCache(this.getContext(), handler );
	private int touchOffsetX;
	private int touchOffsetY;
	private Tile[] tiles = new Tile[9];
	private int incrementsX[] = new int[] {0, 1, 2, 0, 1, 2, 0, 1, 2};
	private int incrementsY[] = new int[] {0, 0, 0, 1, 1, 1, 2, 2, 2};
	
	private static final int TILE_SIZE = 256;
	private Animation zoomInAnimation;
	private Animation zoomOutAnimation;
	private int pendingZoomLevel;

	public OsmMapView(Context context) {
		super(context);
        
		zoomInAnimation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		zoomInAnimation.setDuration(500L);

		zoomOutAnimation = new ScaleAnimation(1, 0.5f, 1, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		zoomOutAnimation.setDuration(500L);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Tile[] tiles = getTiles(zoomLevel, this.offsetX, this.offsetY, 9);
		
		for(Tile tile : tiles) {
			if(isOnScreen(tile)) {
				Bitmap bitmap = getBitmap(tile);
				if(bitmap != null) {
					canvas.drawBitmap(bitmap, this.offsetX + tile.offsetX, this.offsetY + tile.offsetY, this.paint);
				}
			}
		}
	}

	private boolean isOnScreen(Tile tile) {
		int upperLeftX = tile.offsetX + this.offsetX;
		int upperLeftY = tile.offsetY + this.offsetY;
		int width = this.getWidth();
		int height = this.getHeight();
		
		if(((upperLeftX + TILE_SIZE) >= 0) && (upperLeftX  < width) &&
		   ((upperLeftY + TILE_SIZE) >= 0) && (upperLeftY  < height)) {
			return true;
		}
		return false;
	}

	private Tile[] getTiles(int zoomLevel, int offsetX, int offsetY, int size) {
		int mapX = (0 - offsetX)/TILE_SIZE;
		int mapY = (0 - offsetY)/TILE_SIZE;
		
		for(int index = 0; index < size; ++index) {
			if(tiles[index] == null) {
				tiles[index] = new Tile();
			}
			tiles[index].mapX    = mapX + incrementsX[index];
			tiles[index].mapY    = mapY + incrementsY[index];
			tiles[index].offsetX = tiles[index].mapX * TILE_SIZE;
			tiles[index].offsetY = tiles[index].mapY * TILE_SIZE;
			tiles[index].zoom    = zoomLevel;
			tiles[index].key     = zoomLevel + "/" + tiles[index].mapX + "/" + tiles[index].mapY + ".png";
		}		
		return tiles;		
	}

	private Bitmap getBitmap(Tile tile) {
		if(!tilesCache.hasTile(tile.key)) {
			tilesCache.queueTileRequest(tile.key);
			return getClosestResizedTile(tile);
		}
		
		return tilesCache.getTile(tile.key);
	}


	private Bitmap getClosestResizedTile(Tile tile) {
		Bitmap closestBitmap = null; 
		//plusZoomTile = getBitmap(generatedPlusTile);
		//minusZoomTile = getBitmap(generatedMunisTile);
		return closestBitmap;
	}

	public void setZoom(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}	
	
	@Override
	protected void onAnimationEnd() {
		if(this.zoomLevel > pendingZoomLevel) {
			zoomOut();
		}
		else {
			zoomIn();
		}
		this.zoomLevel = pendingZoomLevel;

		invalidate();
		super.onAnimationEnd();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.touchDownX = (int) event.getX();
				this.touchDownY = (int) event.getY();
				this.touchOffsetX = this.offsetX;
				this.touchOffsetY = this.offsetY;
				invalidate();
				return true;
			case MotionEvent.ACTION_MOVE:
				this.offsetX = this.touchOffsetX + (int) event.getX() - this.touchDownX;
				this.offsetY = this.touchOffsetY + (int) event.getY() - this.touchDownY;
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				this.offsetX = this.touchOffsetX + (int) event.getX() - this.touchDownX;
				this.offsetY = this.touchOffsetY + (int) event.getY() - this.touchDownY;
				invalidate();
		}

		return super.onTouchEvent(event);
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void clearCurrentCache() {
		tilesCache.clean();
		Tile[] tiles = getTiles(zoomLevel, this.offsetX, this.offsetY, 9);
		
		for(Tile tile : tiles) {
			if(isOnScreen(tile)) {
				tilesCache.removeCachedTile(tile.zoom + "/" + tile.mapX + "/" + tile.mapY + ".png");
			}
		}
	}

	public void zoomOut() {
		setOffsetX(getOffsetX() / 2 + (getWidth ()/4));
		setOffsetY(getOffsetY() / 2 + (getHeight()/4));
	}

	public void zoomIn() {
		setOffsetX((getOffsetX()) * 2 - (getWidth ()/2));
		setOffsetY((getOffsetY()) * 2 - (getHeight()/2));
	}

	public void animateZoomOut(int zoomLevel) {
		startAnimation(zoomOutAnimation);
		this.pendingZoomLevel = zoomLevel;
	}

	public void animateZoomIn(int zoomLevel) {
		startAnimation(zoomInAnimation);
		this.pendingZoomLevel = zoomLevel;
	}

	public void cacheCurrentMap() {
		int tempOffsetX = getOffsetX() * 2;
		int tempOffsetY = getOffsetY() * 2;
		
		Tile[] tiles = getTiles(zoomLevel + 1, tempOffsetX, tempOffsetY, 9);
		
		for(Tile tile : tiles) {
			getBitmap(tile);
		}
	}
}
