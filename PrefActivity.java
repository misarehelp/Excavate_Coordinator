package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.*;
import android.os.Bundle;

public class PrefActivity extends PreferenceActivity implements KM_Constants {

    public static final int DEFAULT_DEPARTMENT_POSITION = 0;
    public static final int MAP_TYPE_DEFAULT_POSITION = 1;
    public static final int MODE_USER_DEFAULT_POSITION = 0;


    private static SharedPreferences sharedPrefs;
    static SharedPreferences.Editor ed;
    static String admin_value, dispatcher_value;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, Context.MODE_PRIVATE);
    }

    public static class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);

            int admin_pos = getResources().getStringArray(R.array.mode_user_type_entries).length-1;
            admin_value = getResources().getStringArray(R.array.mode_user_type_entries)[admin_pos];
            dispatcher_value = getResources().getStringArray(R.array.mode_user_type_entries)[admin_pos-1];

            initPrefSetup();
        }

        private void initPrefSetup() {
            //initCheckBoxPreference(BROWSER_MODE);
            initListPreference(DEPARTMENT_USER, DEFAULT_DEPARTMENT_POSITION);
            initListPreference(MAP_TYPE, MAP_TYPE_DEFAULT_POSITION);
            initListPreference(MODE_USER, MODE_USER_DEFAULT_POSITION);
            updateEditPreference(MAP_SCALE);
            updateEditPreference(RECORDS_MAX_NUMBER);
        }

        /* public void initCheckBoxPreference(String key) {
            CheckBoxPreference cb = (CheckBoxPreference) findPreference(key);
            if (cb == null) cb.setChecked(false);
            updateUserCheckBox(key);
        } */

        public void initListPreference(String key, int default_pos) {

            ListPreference lp = (ListPreference) findPreference(key);
            if (lp.getValue() == null) {
                switch (key) {
                    case DEPARTMENT_USER:
                        lp.setValue(getResources().getStringArray(R.array.department_entries)[default_pos]);
                        break;
                    case MAP_TYPE:
                        lp.setValue(getResources().getStringArray(R.array.map_type_entries)[default_pos]);
                        break;
                    case MODE_USER:
                        lp.setValue(getResources().getStringArray(R.array.mode_user_type_entries)[default_pos]);
                        break;
                }
            //
            }
            updateUserListPreference(key);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case MODE_USER:
                    updateUserListPreference(key);
                    break;
                case DEPARTMENT_USER:
                case MAP_TYPE:
                    updateUserListPreference(key);
                    break;
                case RECORDS_MAX_NUMBER:
                case MAP_SCALE:
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

        /* private void updateUserCheckBox(String key) {
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
        } */

        private void updateUserListPreference(String key) {
            ListPreference preference = (ListPreference) findPreference(key);
            String value = preference.getValue();
            if (key.equals(MODE_USER)) {
                PreferenceScreen preference_server_setup = (PreferenceScreen) findPreference("server_setup");
                ListPreference preference_user = (ListPreference) findPreference(DEPARTMENT_USER);

                if (value.equals(admin_value)) {

                    preference_server_setup.setEnabled(true);
                    preference_user.setEnabled(true);

                } else if (value.equals(dispatcher_value)) {

                    preference_server_setup.setEnabled(false);
                    preference_user.setEnabled(false);

                } else {    // just some user

                    preference_server_setup.setEnabled(false);
                    preference_user.setEnabled(true);
                }
            }
            preference.setSummary(value);
            storeSharedPreferenceValue(key, value);
        }

        private void updateEditPreference(String key) {
            EditTextPreference preference = (EditTextPreference) findPreference(key);
            String value = preference.getText();
            preference.setSummary(value);
            storeSharedPreferenceValue(key, value);
        }

        private void storeSharedPreferenceValue(String key, String value) {
            ed = sharedPrefs.edit();
            ed.putString(key, value);
            ed.apply();
        }
    }
}
