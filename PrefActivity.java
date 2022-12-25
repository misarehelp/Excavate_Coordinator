package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity implements KM_Constants {

    public static final String SHOW_BR_MODE = "Показывать местоположение на карте";
    public static final String HIDE_BR_MODE = "Не показывать местоположение на карте";
    public static final int DEFAULT_DEPARTMENT_POSITION = 0;
    public static final int MAP_TYPE_DEFAULT_POSITION = 1;

    private static SharedPreferences sharedPrefs;
    static SharedPreferences.Editor ed;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, Context.MODE_PRIVATE);
        ed = sharedPrefs.edit();
    }

    public static class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        ArrayMap<String, String> pref_entry = new ArrayMap<>(2);
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
            initPrefSetup();
        }

        private void initPrefSetup() {
            initCheckBoxPreference(BROWSER_MODE);
            initListPreference(DEPARTMENT_USER, DEFAULT_DEPARTMENT_POSITION);
            initListPreference(MAP_TYPE, MAP_TYPE_DEFAULT_POSITION);
            updateEditPreference(SERVER_DELAY_TITLE);
            updateEditPreference(MARKER_DELAY);
            updateEditPreference(MARKER_SCALE);
            updateEditPreference(MARKER_MAX_NUMBER);
            updateEditPreference(MAX_LOCATION_TIME);
        }

        public void initCheckBoxPreference(String key) {
            CheckBoxPreference cb = (CheckBoxPreference) findPreference(key);
            if (cb == null) cb.setChecked(false);
            updateUserCheckBox(key);
        }

        public void initListPreference(String key, int default_pos) {
            //int pos = -1;
            ListPreference lp = (ListPreference) findPreference(key);
            if (lp.getValue() == null) {
                switch (key) {
                    case DEPARTMENT_USER:
                        lp.setValue(getResources().getStringArray(R.array.department_values)[default_pos]);
                        break;
                    case MAP_TYPE:
                        lp.setValue(getResources().getStringArray(R.array.map_type_values)[default_pos]);
                        break;
                }
            }
            updateUserListPreference(key);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case BROWSER_MODE:
                case CHOSEN_KID_MARKERS:
                    updateUserCheckBox(key);
                    break;
                case DEPARTMENT_USER:
                case MAP_TYPE:
                    updateUserListPreference(key);
                    break;
                case MARKER_MAX_NUMBER:
                case MAX_LOCATION_TIME:
                    updateEditPreference(key);
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        private void updateUserCheckBox(String key) {
            CheckBoxPreference preference = (CheckBoxPreference) findPreference(key);
            String mode = "";
            switch (key) {
                case BROWSER_MODE:
                    mode = (preference.isChecked()) ? SHOW_BR_MODE : HIDE_BR_MODE;
                    break;
            }
            preference.setSummary(mode);
            ed.putBoolean(key, preference.isChecked());
            ed.apply();
        }

        private void updateUserListPreference(String key) {
            ListPreference preference = (ListPreference) findPreference(key);
            //CharSequence entry = preference.getEntry();
            String value = preference.getValue();
            preference.setSummary(value);
            pref_entry.put(key, value);
            ed.putString(key, value);
            ed.apply();
            /*
            if (key.equals(PREF_REQUEST) || key.equals(TIMER_DELAY) || key.equals(MAP_TYPE)) {
                preference.setSummary(entry);
            */
        }

        private void updateEditPreference(String key) {
            EditTextPreference preference = (EditTextPreference) findPreference(key);
            String value = preference.getText();
            preference.setSummary(value);
            pref_entry.put(key, value);
            ed.putString(key, value);
            ed.apply();
        }
    }
}
