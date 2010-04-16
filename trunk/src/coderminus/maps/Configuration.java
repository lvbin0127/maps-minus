package coderminus.maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Configuration {
	private static SharedPreferences preferences = null;

	private static SharedPreferences getPreferences(Context context) 
	{
		if(preferences == null) 
		{
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return preferences;
	}

	public static String getCachePath(Context context) 
	{	
		return getPreferences(context).getString("KEY_CACHE_PATH", "/sdcard/mapsminus/osm/");
	}

	public static String getTilePostfix(Context context) 
	{	
		return getPreferences(context).getString("KEY_TILE_POSTFIX", ".tile");
	}
}
