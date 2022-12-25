package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.*;
import android.os.Bundle;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity implements KM_Constants {

    public static final int MODE_USER_DEFAULT_POSITION = 0;
    public static final int DEFAULT_DEPARTMENT_POSITION = 0;
    public static final int MAP_TYPE_DEFAULT_POSITION = 1;
    private static final String DEFINED_ADMIN_PASS = "5987";
    private static final String DEFINED_DISPATCHER_PASS = "3028";
    private static final String WRONG_PASS = "Для переключения в режим диспетчера/администратора необходимо ввести корректный пароль";


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

            String [] mode_user_type = getResources().getStringArray(R.array.mode_user_type_values);
            int admin_pos = mode_user_type.length-1;
            admin_value = mode_user_type[admin_pos];
            dispatcher_value = mode_user_type[admin_pos-1];

            Preference.OnPreferenceChangeListener listener = (preference, object) -> {
                String new_value = object.toString();

                if ( new_value.equals(admin_value) || new_value.equals(dispatcher_value) ) {
                    String current_pass = sharedPrefs.getString(ADMIN_PASS, null);
                    if (current_pass != null && current_pass.equals(getPass(DEFINED_ADMIN_PASS))) {
                        return true;
                    } else {
                        Toast.makeText(getActivity(), WRONG_PASS, Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    return true;
                }
            };

            ListPreference lp = (ListPreference) findPreference(MODE_USER);
            lp.setOnPreferenceChangeListener(listener);

            initPrefSetup();
        }

        String getPass (String value) {
            StringBuilder p = new StringBuilder();
            char[] old_arr = value.toCharArray();
            for (int i = 0; i < value.length(); i++ ) {
                p.append (String.valueOf(10 - Character.getNumericValue(old_arr[i])));
            }
            return p.toString();
        }

        private void initPrefSetup() {
            //initCheckBoxPreference(BROWSER_MODE);
            initListPreference(DEPARTMENT_USER, DEFAULT_DEPARTMENT_POSITION);
            //initListPreference(MAP_TYPE, MAP_TYPE_DEFAULT_POSITION);
            initListPreference(MODE_USER, MODE_USER_DEFAULT_POSITION);
            //updateEditPreference(MAP_SCALE);
            updateEditPreference(RECORDS_MAX_NUMBER);
            updateEditPreference(ADMIN_PASS);
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
                    /*case DEPARTMENT_USER:
                        lp.setValue(getResources().getStringArray(R.array.department_values)[default_pos]);
                        break;
                    //case MAP_TYPE:
                        //lp.setValue(getResources().getStringArray(R.array.map_type_values)[default_pos]);
                        break; */
                    case MODE_USER:
                        lp.setValue(getResources().getStringArray(R.array.mode_user_type_values)[default_pos]);
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
                /* case DEPARTMENT_USER:
                case MAP_TYPE:
                    updateUserListPreference(key);
                    break;
                case RECORDS_MAX_NUMBER:
                case MAP_SCALE:
                    updateEditPreference(key);
                    break; */
                case ADMIN_PASS:
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
            CharSequence entry = preference.getEntry();

            if (key.equals(MODE_USER)) {
                PreferenceScreen preference_server_setup = (PreferenceScreen) findPreference("server_setup");
                ListPreference preference_user = (ListPreference) findPreference(DEPARTMENT_USER);

                if (value.equals(admin_value)) {

                    preference_server_setup.setEnabled(true);
                    preference_user.setEnabled(true);

                } else if (value.equals(dispatcher_value)) {

                    preference_server_setup.setEnabled(false);
                    preference_user.setEnabled(false);

                } else {    // just some other user

                    preference_server_setup.setEnabled(false);
                    preference_user.setEnabled(true);
                }
            }

            /* if (key.equals(MAP_TYPE)) {
                storeSharedPreferenceValue(key, value);
            } else {
                storeSharedPreferenceValue(key, entry.toString());
            } */
            preference.setSummary(entry);
        }

        private void updateEditPreference(String key) {
            EditTextPreference preference = (EditTextPreference) findPreference(key);
            String value = preference.getText();
            if (key.equals(ADMIN_PASS)) {
                preference.setSummary("******");
            } else {
                preference.setSummary(value);
            }
            storeSharedPreferenceValue(key, value);
        }

        private void storeSharedPreferenceValue(String key, String value) {
            ed = sharedPrefs.edit();
            ed.putString(key, value);
            ed.apply();
        }
    }
}
