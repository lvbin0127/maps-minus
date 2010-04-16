package coderminus.maps;

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
			Preference preference) {
		if(preference.getKey() != null) {
			if(preference.getKey().equals("KEY_AUTOFOLLOW_LOCATION")) {
//				Intent intent = new Intent(this, EditGroupsListActivity.class);
//			    startActivityForResult(intent, 0);
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

}
