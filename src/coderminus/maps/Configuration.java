package coderminus.maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Configuration {
	private static SharedPreferences preferences = null;

	private static SharedPreferences getPreferences(Context activity) 
	{
		if(preferences == null) 
		{
			preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		}
		return preferences;
	}

	public static String getCachePath(Context activity) 
	{	
		return getPreferences(activity).getString("KEY_CACHE_PATH", "/sdcard/mapsminus/osm");
	}
}
