package coderminus.maps;

import java.util.Vector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import coderminus.maps.MapsActivity.CacheLevel;

public class CacheLevelAdapter extends BaseAdapter {
	private Vector<CacheLevel> levels = new Vector<CacheLevel>();
	private Context context;
	private int currentZoomLevel;

	public CacheLevelAdapter(Context context) 
	{
		this.context = context;
	}

	@Override
	public int getCount() 
	{
		return levels.size() - currentZoomLevel;
	}

	@Override
	public Object getItem(int position) 
	{
		return levels.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		CacheLevelItemView view;
		
		if(convertView == null) 
		{
			view = new CacheLevelItemView(context);
        } 
		else 
		{
			view = (CacheLevelItemView) convertView;
        }
		
		view.setData(
				levels.get(
						position + currentZoomLevel).text, 
						levels.get(position + currentZoomLevel).icon);

		return view;	
	}

	public void addCacheLevel(CacheLevel cacheLevel) 
	{
		levels.add(cacheLevel);
	}

	public void setCurrentZoomLevel(int zoomLevel) 
	{
		this.currentZoomLevel = zoomLevel;
	}	

}
