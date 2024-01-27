package ru.volganap.nikolay.haircut_schedule;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class ColorFragment extends PreferenceFragmentCompat implements Constants, SharedPreferences.OnSharedPreferenceChangeListener {
   private static final int DEFAULT_CALENDAR_BACKGROUND_HOLIDAY = Color.parseColor("#B2CCC7AE");
   private static final int DEFAULT_CALENDAR_TEXT_HOLIDAY = Color.BLACK;
   //private static final int DEFAULT_CALENDAR_BACKGROUND_WORKDAY = Color.LTGRAY;
   private static final int DEFAULT_CALENDAR_BACKGROUND_WORKDAY = Color.GREEN;
   private static final int DEFAULT_CALENDAR_TEXT_WORKDAY = Color.BLACK;
   private static final int DEFAULT_CALENDAR_BACKGROUND_TODAY = Color.YELLOW;
   private static final int DEFAULT_CALENDAR_TEXT_TODAY = Color.BLACK;
   private static final int DEFAULT_CALENDAR_BACKGROUND_SELECTED_DAY = Color.BLUE;
   private static final int DEFAULT_CALENDAR_TEXT_SELECTED_DAY = Color.WHITE;
   private static final String CALENDAR_SWITCH = "calendar_sw";
   boolean first_start, sw_default_colors;
   private SwitchPreference switchPreference;

   SharedPreferences.Editor ed;
   private SharedPreferences sharedPrefs;

   public ColorFragment (SharedPreferences sharedPrefs) {
      this.sharedPrefs = sharedPrefs;
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
      setPreferencesFromResource(R.xml.color_preferences, rootKey);
      first_start = getArguments().getBoolean(COMMAND, false);
      ed = sharedPrefs.edit();
      ed.putString(CHILD_ACTIVITY, CALENDAR_SETTINGS);
      ed.apply();
      initPrefSetup();
   }

   private void initPrefSetup() {

      switchPreference = findPreference("calendar_sw");
      sw_default_colors = switchPreference.isChecked();

      initColorPreference(CALENDAR_BACKGROUND_HOLIDAY, DEFAULT_CALENDAR_BACKGROUND_HOLIDAY);
      initColorPreference(CALENDAR_TEXT_HOLIDAY, DEFAULT_CALENDAR_TEXT_HOLIDAY);
      initColorPreference(CALENDAR_BACKGROUND_WORKDAY, DEFAULT_CALENDAR_BACKGROUND_WORKDAY);
      initColorPreference(CALENDAR_TEXT_WORKDAY, DEFAULT_CALENDAR_TEXT_WORKDAY);
      initColorPreference(CALENDAR_BACKGROUND_TODAY, DEFAULT_CALENDAR_BACKGROUND_TODAY);
      initColorPreference(CALENDAR_TEXT_TODAY, DEFAULT_CALENDAR_TEXT_TODAY);
      initColorPreference(CALENDAR_BACKGROUND_SELECT_DAY, DEFAULT_CALENDAR_BACKGROUND_SELECTED_DAY);
      initColorPreference(CALENDAR_TEXT_SELECT_DAY, DEFAULT_CALENDAR_TEXT_SELECTED_DAY);
   }

   public void initColorPreference(String key, int default_color) {

      ColorPreferenceCompat colorPreference = findPreference(key);
      //set Default vaulues
      if ( !sharedPrefs.contains(key) || !sw_default_colors ) {
         storeColorSharedPreferenceValue(key, default_color);
         colorPreference.saveValue(default_color);
      //set Manual vaulues
      } else {
         colorPreference.saveValue(sharedPrefs.getInt(key, DEFAULT_CALENDAR_TEXT_HOLIDAY));
      }
   }

   @Override
   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      switch (key) {
         case CALENDAR_SWITCH:
            initPrefSetup();
            break;
         case CALENDAR_BACKGROUND_HOLIDAY:
         case CALENDAR_BACKGROUND_WORKDAY:
         case CALENDAR_BACKGROUND_TODAY:
         case CALENDAR_TEXT_HOLIDAY:
         case CALENDAR_TEXT_WORKDAY:
         case CALENDAR_TEXT_TODAY:
         case CALENDAR_TEXT_SELECT_DAY:
         case CALENDAR_BACKGROUND_SELECT_DAY:
            updateColorPreference(key);
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

   private void updateColorPreference(String key) {
      ColorPreferenceCompat colorPreference = findPreference(key);
      colorPreference.setOnPreferenceChangeListener((preference, newValue) -> {
         if (key.equals(preference.getKey())) {
            Log.d(LOG_TAG, "key: " + key + ", New default color is: #" + (int) newValue);
            storeColorSharedPreferenceValue(key, (int) newValue);
         }
         return true;
      });
   }

   private void storeColorSharedPreferenceValue(String key, int value) {
      ed = sharedPrefs.edit();
      ed.putInt(key, value);
      ed.apply();
   }
}

