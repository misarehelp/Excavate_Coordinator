package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements Constants {

    private static final String NEED_FILL_IN_COLORS = "Рекомендуется определить цветовую схему календаря";
    private static SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        boolean first_start = intent.getBooleanExtra(COMMAND, false);
        String sender = intent.getStringExtra(SENDER);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putBoolean(COMMAND, first_start);

            if (sender.equals(GENERAL_SETTINGS)) {
                SettingsFragment setFragment = new SettingsFragment(sharedPrefs);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.settings, setFragment )
                        .commit();
                setFragment.setArguments(args);

            } else {
                if (first_start) Toast.makeText(this, NEED_FILL_IN_COLORS, Toast.LENGTH_SHORT).show();

                ColorFragment setFragment = new ColorFragment(sharedPrefs);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.settings, setFragment )
                        .commit();
                setFragment.setArguments(args);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
    }
}
