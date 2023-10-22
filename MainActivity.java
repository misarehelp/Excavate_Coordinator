package ru.volganap.nikolay.haircut_schedule;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements Constants, Enums, Contract.ViewMain, Contract.ActivityReciever,
        DatePickerDialog.OnDateSetListener, Contract.Recycle.MainInterface {

    private static final String DEFAULT_MAX_RECORDS = "60";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =101;
    private static final String ARCHIVE_DATA = "Архив: ";
    private static final String NEED_RESTART = "Это первый запуск приложения. Для корректной работы приложения его необходимо перезапустить";

    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    // Main activity layout
    private Button bt_prev_week, bt_next_week, bt_show_hide_free_rs, bt_exit;
    private TextView tv_date;
    private TabLayout tabLayout;
    private BroadcastReceiver mainBroadcastReceiver;
    Contract.PresenterMain presenterMain;

    private ArrayList<String> d_interval;
    private ArrayList<String> d_of_week;
    private int current_page = 0;
    private int theme_type;
    private boolean future_recs = true;
    int[] sor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Settings Preferences
        initSharedPreferences();

        setContentView(R.layout.activity_main);
        tv_date = findViewById(R.id.tv_date);

        /* StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build()); */

        Log.d(LOG_TAG, "Main - onCreate ");
        // Init Main activity layout
        initMainViewLayout();

        // instantiating object of Presenter Interface
        presenterMain = new PresenterMain(this, this, theme_type, getDaysBefore());

        autoRequestAllPermissions();
        // Init BroadcastReceiver
        initBroadcastReceiver();
    }

    private void initMainViewLayout() {
        // Main ViewLayout
        bt_prev_week = findViewById(R.id.bt_prev_week);
        bt_next_week = findViewById(R.id.bt_next_week);
        bt_show_hide_free_rs = findViewById(R.id.bt_show_hide_free_rs);
        bt_exit = findViewById(R.id.bt_exit);
    }

    private void initSharedPreferences() {
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);

        prefChangeListener = (sharedPreferences, key) -> {
            if (key.equals(THEME)) {
                onChangeThemeAppearance(true);
            }
            if (key.equals(DAYS_BEFORE_NOW)) {
                presenterMain.onChangeDaysBefore(getDaysBefore());
                //presenterMain.onChangeSharedPrefs(key);
            }
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefChangeListener);
        theme_type = onChangeThemeAppearance(false);
        setTheme(theme_type);
        //sharedPrefs.edit().clear().commit();
    }

    private int onChangeThemeAppearance( boolean recreate) {

        int theme_pos = Integer.parseInt(sharedPrefs.getString(THEME, "0"));

        int theme;
        switch (theme_pos) {
            default:
            case 0:
                theme = THEME_LIGHT_SMALL;
                break;
            case 1:
                theme = THEME_LIGHT_MEDIUM;
                break;
            case 2:
                theme = THEME_LIGHT_BIG;
                break;
            case 3:
                theme = THEME_DARK_SMALL;
                break;
            case 4:
                theme = THEME_DARK_MEDIUM;
                break;
            case 5:
                theme = THEME_DARK_BIG;
                break;
            case 6:
                theme = THEME_NEUTRAL_SMALL;
                break;
            case 7:
                theme = THEME_NEUTRAL_MEDIUM;
                break;
            case 8:
                theme = THEME_NEUTRAL_BIG;
                break;
        }

        if (recreate) showToast(getResources().getString(R.string.need_to_restart));
        return theme;
    }

    void autoRequestAllPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){return;}
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info==null){return;}
        String[] permissions = info.requestedPermissions;
        boolean remained = false;
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remained = true;
            }
        }
        if (remained) {
            requestPermissions(permissions, 0);
        }

        PermittedTask scanPermissionsTask = new PermittedTask(this, Manifest.permission.CAMERA) {
            @Override
            protected void granted() {
                presenterMain.onPermissionsGranted();
            }

            @Override
            protected void denied() {
                runPrefActivity(true);
                Toast.makeText(getApplicationContext(), NEED_RESTART, Toast.LENGTH_LONG).show();
            }
        };
        scanPermissionsTask.run();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "onRequestPermissionsResult: " + grantResults[0] );
                } else {
                    Log.d(LOG_TAG, "onRequestPermissionsResult: failed" );
                }
            }
        }

    }

    @Override
    public void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        // Set filter by Class name
        filter.addAction(getClass().getSimpleName());

        mainBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    presenterMain.onBroadcastReceive(intent);
                }
            }
        };
        registerReceiver(mainBroadcastReceiver, filter);
        // Set visibility and onclick method of buttons
        buttonsSetOnClickListener();
    }

    private void showTvDate(String date) {
        tv_date.setText(date);
    }

    // Fill In Permits User Made List
    @Override
    public void fillInRecordsList(ArrayList<ArrayList<MainScreenData>> new_data, ArrayList<String> days_interval, ArrayList<String> days_of_week, int[] sum_of_rec) {

        d_interval = days_interval;
        d_of_week = days_of_week;
        sor = sum_of_rec;

        ViewPager2 vpPager = findViewById(R.id.vpPager);
        MainPagerAdapter fragmentStateAdapter = new MainPagerAdapter(this );

        for (int i = 0; i < d_interval.size(); i++) {
            fragmentStateAdapter.addFragment(new MainFragment(new_data.get(i), d_interval.get(i), theme_type));
        }

        vpPager.setAdapter(fragmentStateAdapter);
        vpPager.setOffscreenPageLimit(2);

        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, vpPager, false, (tab, position) -> {
            //tab.view.setBackgroundColor(Color.WHITE);
            if (d_of_week != null && d_of_week.size() != 0) {
                tab.setText(d_of_week.get(position) + " (" + sor[position] + ")");
            }

        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int pos = tab.getPosition();
                if (d_of_week != null && d_of_week.size() != 0) {

                    tab.setText(d_of_week.get(pos) + " (" + sor[pos] + ")");
                    showTvDate(d_interval.get(pos));
                    String status = "В этот день нет записей";
                    if (sor[pos] != 0)  status = "В этот день " + sor[pos] + " записей";
                    refreshMainStatus(status);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //int pos = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //int pos = tab.getPosition();
            }
        });

        //TabLayout.Tab tab = tabLayout.getTabAt(current_page);
        TabLayout.Tab tab = tabLayout.getTabAt(current_page);
        tab.select();
        showTvDate(d_interval.get(current_page));

        /* mainVp2Adapter.setValues(new_data);
        vpPager.setCurrentItem(0);
        mainVp2Adapter.notifyDataSetChanged(); */
        //mainVp2Adapter.notifyItemChanged(0);
    }

    @Override
    public void onItemClick(String index, String time, String type) {
        setCurrentPage();
        String command;
        int num_index = Integer.parseInt(index);

        if (num_index < 0) {
            command = SERVER_ADD_RECORD;
        } else {
            command = SERVER_CHANGE_RECORD;
        }

        presenterMain.onChangeRecordClick(tv_date.getText().toString(), time, index, type, theme_type, command);
    }

    private void setCurrentPage() {
        current_page = tabLayout.getSelectedTabPosition();
    }

    private void buttonsSetOnClickListener() {

        // show previous day record
        bt_prev_week.setOnClickListener(v -> {
            presenterMain.onButtonPreviousWeekClick();
        });

        // show next day record
        bt_next_week.setOnClickListener(v -> {
            presenterMain.onButtonNextWeekClick();
        });

        // add a client
        bt_show_hide_free_rs.setOnClickListener(v -> {
            setCurrentPage();
            presenterMain.onButtonShowHideFreeRecordsClick();
        });

        // Exit a programm
        bt_exit.setOnClickListener(v -> {
            finish();
        });

        // Show DatePicker dialog
        tv_date.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            DatePickerDialog dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        presenterMain.onTextViewDateClick(year, monthOfYear, dayOfMonth);
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.set_item:
                runPrefActivity(false);
                //startActivity(new Intent(this, PrefActivity.class));
                break;

            case R.id.serv_config_item:
                presenterMain.onChangeServerPreferences( getMaxRecordsNumber(), getDaysBefore() );
                break;

            case R.id.version:
                Toast.makeText(this, getResources().getString(R.string.version), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runPrefActivity (boolean first_start) {
        Intent intent = new Intent(this, PrefActivity.class);
        intent.putExtra(COMMAND, first_start);
        startActivity(intent);
    }

    @Override
    public void refreshMainStatus(String status) {
        String state = (future_recs) ? status : ARCHIVE_DATA + status;
        ((TextView) findViewById(R.id.tv_main_state)).setText(state);
    }

    @Override
    public void showToast(String status) {
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setArchiveStatus(boolean value) {
        future_recs = value;
    }

    @Override
    public void setShowHideButtonVisibility(boolean value) {
        bt_show_hide_free_rs.setVisibility( value ?  View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public String getMaxRecordsNumber() {
        return sharedPrefs.getString(RECORDS_MAX_NUMBER, DEFAULT_MAX_RECORDS);
    }

    private int getDaysBefore() {
        return Integer.parseInt(sharedPrefs.getString(DAYS_BEFORE_NOW, "0"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenterMain.onMainActivityResume();
        Log.d(LOG_TAG, "MainActivity: onResume ");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mainBroadcastReceiver);
        Log.d(LOG_TAG, "MainActivity: onDestroy ");
        super.onDestroy();
        presenterMain.onDestroy();
    }
}

        /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_CAMERA);
        } */

        /*String admin = getResources().getStringArray(R.array.mode_user_type_values)[2];
        String dispatcher = getResources().getStringArray(R.array.mode_user_type_values)[1];
        boolean is_admin = admin.equals(getViewModeUser());
        boolean is_disp = dispatcher.equals(getViewModeUser());
        boolean cond = is_admin || is_disp;
        // allow additional options of Menu
        menu.findItem(R.id.serv_config_item).setEnabled(cond);
        menu.findItem(R.id.show_archive).setEnabled(cond);
        menu.findItem(R.id.clear_id_counter).setEnabled(cond);
        menu.findItem(R.id.delete_all_permits).setEnabled(cond); */