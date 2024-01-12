package ru.volganap.nikolay.haircut_schedule;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements Constants, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int DEFAULT_ROTATE_POSITION = 0;
    private static final int DEFAULT_ASPECT_POSITION = 0;
    private static final int DEFAULT_THEME_POSITION = 0;
    boolean first_start;
    SharedPreferences.Editor ed;
    private SharedPreferences sharedPrefs;

    public SettingsFragment (SharedPreferences sharedPrefs) {
       this.sharedPrefs = sharedPrefs;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        first_start = getArguments().getBoolean(COMMAND, false);
        ed = sharedPrefs.edit();
        ed.putString(CHILD_ACTIVITY, GENERAL_SETTINGS);
        ed.apply();

        initPrefSetup();
    }

    private void initPrefSetup() {
        initListPreference(ROTATE, DEFAULT_ROTATE_POSITION);
        initListPreference(ASPECT, DEFAULT_ASPECT_POSITION);
        initListPreference(THEME, DEFAULT_THEME_POSITION);
        updateEditPreference(RECORDS_MAX_NUMBER);
        updateEditPreference(DAYS_BEFORE_NOW);
        updateEditPreference(COMPRESS);
    }

    public void initListPreference(String key, int default_pos) {

        ListPreference lp = (ListPreference) findPreference(key);
        if (lp.getValue() == null) {
            switch (key) {
                case ROTATE:
                    lp.setValue(getResources().getStringArray(R.array.rotate_values)[default_pos]);
                    break;
                case ASPECT:
                    lp.setValue(getResources().getStringArray(R.array.aspect_values)[default_pos]);
                    break;
                case THEME:
                    lp.setValue(getResources().getStringArray(R.array.theme_values)[default_pos]);
                    break;
                default:
                    break;
            }
        }
        updateUserListPreference(key);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case ROTATE:
            case ASPECT:
            case THEME:
                updateUserListPreference(key);
                break;
            case RECORDS_MAX_NUMBER:
            case DAYS_BEFORE_NOW:
            case COMPRESS:
                updateEditPreference(key);
                break;
            default:
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

    private void updateUserListPreference(String key) {
        ListPreference lp = (ListPreference) findPreference(key);
        String value = lp.getValue();
        CharSequence entry = lp.getEntry();
        lp.setSummary(entry);
        storeSharedPreferenceValue(key, value);
    }

    private void updateEditPreference(String key) {
        EditTextPreference etp = (EditTextPreference) findPreference(key);
        String value = etp.getText();
        etp.setSummary(value);
        storeSharedPreferenceValue(key, value);
    }

    private void storeSharedPreferenceValue(String key, String value) {
        ed = sharedPrefs.edit();
        ed.putString(key, value);
        ed.apply();
        if (first_start && key.equals(COMPRESS)) {
            onDestroy();
        }
    }
}

//private static final String DEFINED_ADMIN_PASS = "5987";
//private static final String WRONG_PASS = "Для переключения в режим диспетчера/администратора необходимо ввести корректный пароль";
//String [] rotate = getResources().getStringArray(R.array.rotate_values);
//int rotate_pos = rotate.length-1;

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


        /* public void initCheckBoxPreference(String key) {
            CheckBoxPreference cb = (CheckBoxPreference) findPreference(key);
            if (cb == null) cb.setChecked(false);
            updateUserCheckBox(key);
        } */


        /* String getPass (String value) {
            StringBuilder p = new StringBuilder();
            char[] old_arr = value.toCharArray();
            for (int i = 0; i < value.length(); i++ ) {
                p.append (String.valueOf(10 - Character.getNumericValue(old_arr[i])));
            }
            return p.toString();
        } */

            /*
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
            */