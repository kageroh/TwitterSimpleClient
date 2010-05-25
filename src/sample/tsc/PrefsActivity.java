package sample.tsc;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class PrefsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
}
