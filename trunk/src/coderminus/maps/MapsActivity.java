package coderminus.maps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MapsActivity extends Activity {
    private static final int MENU_UPDATE_MAP_ID = Menu.FIRST;
	private static final int MENU_SAVE_MAP_ID   = Menu.FIRST + 1;
    
    private OsmMapView mapView;
    private SharedPreferences prefs;
	private int zoomLevel = 0;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        final RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativeLayout);
        
        
        mapView = new OsmMapView(this);
        
        prefs = getSharedPreferences("MapsMinus", Context.MODE_PRIVATE);
		mapView.setOffsetX(prefs.getInt("offsetX", 0));
		mapView.setOffsetY(prefs.getInt("offsetY", 0));
		zoomLevel = prefs.getInt("zoomLevel", 0);
		mapView.setZoom(zoomLevel);
        
        rl.addView(mapView, new RelativeLayout.LayoutParams(
        				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        ImageButton zoomOutButton = new ImageButton(this);
        zoomOutButton.setBackgroundColor(Color.TRANSPARENT);
        zoomOutButton.setImageResource(android.R.drawable.btn_minus);
        zoomOutButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					onZoomOut();
				}
        });
        
        final RelativeLayout.LayoutParams zoomOutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomOutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        zoomOutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl.addView(zoomOutButton, zoomOutParams);

        ImageButton zoomInButton = new ImageButton(this);
        zoomInButton.setBackgroundColor(Color.TRANSPARENT);
        zoomInButton.setImageResource(android.R.drawable.btn_plus);
        zoomInButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onZoomIn();
			}
        });
        
        
        final RelativeLayout.LayoutParams zoomInParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomInParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        zoomInParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl.addView(zoomInButton, zoomInParams);
    }
    
	protected void onZoomOut() {
		--zoomLevel;
		if(zoomLevel < 0) {
			zoomLevel = 0;
		}
		mapView.animateZoomOut(zoomLevel);
	}

	protected void onZoomIn() {
		++zoomLevel;
		if(zoomLevel > 18) {
			zoomLevel = 18;
		}
		mapView.animateZoomIn(zoomLevel);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_UPDATE_MAP_ID, 0, R.string.menu_update_map).setIcon(android.R.drawable.ic_menu_mapmode);
        menu.add(0, MENU_SAVE_MAP_ID  , 0, R.string.menu_save_map  ).setIcon(android.R.drawable.ic_menu_save   );

        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_UPDATE_MAP_ID: {
			onUpdateMap();
			break;
		}
		case MENU_SAVE_MAP_ID: {
			onSaveMap();
			break;
		}
	}			

		return super.onOptionsItemSelected(item);
	}

	private void onSaveMap() {
		mapView.cacheCurrentMap();
		
	}

	private void onUpdateMap() {
		mapView.clearCurrentCache();
		mapView.invalidate();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putInt("offsetX", mapView.getOffsetX());
	  savedInstanceState.putInt("offsetY", mapView.getOffsetY());
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  mapView.setOffsetX(savedInstanceState.getInt("offsetX"));
	  mapView.setOffsetY(savedInstanceState.getInt("offsetY"));
	}

    protected void onPause() {
		super.onPause();
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt("offsetX", mapView.getOffsetX());
		ed.putInt("offsetY", mapView.getOffsetY());
		ed.putInt("zoomLevel", zoomLevel);
		ed.commit();
    }

}