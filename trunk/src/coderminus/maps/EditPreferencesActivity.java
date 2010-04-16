package coderminus.maps;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class EditPreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) 
	{
		if(preference.getKey() != null) 
		{
			if(preference.getKey().equals("KEY_AUTOFOLLOW_LOCATION")) 
			{
				onAutofollowLocation();
			}
			if(preference.getKey().equals("KEY_SCREEN_ON")) 
			{
				onScreenOn();
			}
			if(preference.getKey().equals("KEY_CONFIGURE_MAPS"))
			{
				//onConfigureMaps();
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	private void onConfigureMaps() 
	{
        Intent intent = new Intent(this, ConfigureMapsActivity.class);
        
        this.startActivityForResult(intent, 0);
	}

	private void onScreenOn() 
	{
		
	}

	private void onAutofollowLocation() 
	{
		
	}

}
