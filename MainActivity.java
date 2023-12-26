package ru.volganap.nikolay.haircut_schedule;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements Constants, Enums, Contract.ViewMain, Contract.ActivityReciever, Contract.CalendarFragmentToMainActivity,
        Contract.Recycle.MainInterface {

    private static final String DEFAULT_MAX_RECORDS = "360";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =101;
    private static final String ARCHIVE_DATA = "Архив: ";
    private static final String NEED_RESTART = "Это первый запуск приложения. Для корректной работы приложения его необходимо перезапустить";
    private static final String PASS_RECORDS_INEDITABLE = "Невозможно занести новую запись в прошлом";

    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    // Main activity layout
    private Button bt_restart, bt_show_hide_free_rs, bt_exit;
    private TextView tv_date;
    private TabLayout tabLayout;
    private BroadcastReceiver mainBroadcastReceiver;
    Contract.PresenterMain presenterMain;
    private ArrayList<String> d_interval;

    private int theme_type;
    private int page = 0;
    private RecordVisibility recordVisibility;
    // for Calendar
    private HashMap<String, Integer> cal_hashmap;
    private Fragment calendarFragment = new CalendarFragment();
    private Contract.MainActivityToCalendarFragment callbackToCalendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init Settings Preferences
        initSharedPreferences();

        setContentView(R.layout.activity_main);
        tv_date = findViewById(R.id.tv_date);
        float new_size = (float) (tv_date.getTextSize() * 0.5);
        tv_date.setTextSize(new_size);
        /* StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build()); */
        // Init Main activity layout
        initMainViewLayout();
        // instantiating object of Presenter Interface
        presenterMain = new PresenterMain(this, this, theme_type, getDaysBefore());

        autoRequestAllPermissions();
        // Init BroadcastReceiver
        initBroadcastReceiver();

        try {
            callbackToCalendarFragment = (Contract.MainActivityToCalendarFragment) calendarFragment;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.calendar_container, calendarFragment)
                    .commit();

        } catch (ClassCastException e) {
            throw new ClassCastException(this
                    + " must implement Contract.RecordActivity.ToRecordFragment");
        }
    }

    private void initMainViewLayout() {
        // Main ViewLayout
        bt_restart = findViewById(R.id.bt_restart);
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
    public void fillInRecordsList(ArrayList<ArrayList<MainScreenData>> new_data, ArrayList<String> days_interval ) {

        d_interval = days_interval;

        ViewPager2 vpPager = findViewById(R.id.vpPager);
        MainPagerAdapter fragmentStateAdapter = new MainPagerAdapter(this );

        for (int i = 0; i < d_interval.size(); i++) {
            fragmentStateAdapter.addFragment(new MainFragment(new_data.get(i), theme_type));
        }

        vpPager.setAdapter(fragmentStateAdapter);
        vpPager.setOffscreenPageLimit(2);

        tabLayout = findViewById(R.id.tabLayout);
        int theme_pos = Integer.parseInt(sharedPrefs.getString(THEME, "0"));

        switch (theme_pos) {
            case 3:
            case 4:
            case 5:
                tabLayout.setTabTextColors( getResources().getColor(R.color.tabTextColorDark), getResources().getColor(R.color.colorGenTextDark));
                tabLayout.setBackgroundColor(getResources().getColor(R.color.tabBackgroundDark));
                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tabIndicatorColorDark));
                break;
            default:
                tabLayout.setTabTextColors( getResources().getColor(R.color.colorTableBgroundDark_2), getResources().getColor(R.color.tabTextColorLight));
                tabLayout.setBackgroundColor(getResources().getColor(R.color.tabBackgroundLight));
                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tabIndicatorColorLight));
        }

        new TabLayoutMediator(tabLayout, vpPager, false, (tab, position) -> {
            //tab.view.setBackgroundColor(Color.WHITE);
            tab.setText(WEEKDAYS[position] + " (" + getNumberOfDayRecords(d_interval.get(position)) + ")");
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                int number = getNumberOfDayRecords(d_interval.get(position));
                showTvDate(d_interval.get(position));
                int day = Integer.parseInt(d_interval.get(position).substring(0,2));

                callbackToCalendarFragment.syncCalendarDayToPage(day);

                String add_status;
                switch (number) {
                    case 1:
                        add_status = number + " запись";
                        break;
                    case 2, 3, 4:
                        add_status = number + " записи";
                        break;
                    case 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27:
                        add_status = number + " записей";
                        break;
                    case 0:
                    default:
                        add_status = " нет записей";
                }

                refreshMainStatus("В этот день " + add_status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        TabLayout.Tab tab = tabLayout.getTabAt(page);
        tab.select();
        showTvDate(d_interval.get(page));
    }

    private int getNumberOfDayRecords (String day) {
        int number;
        try {
            number = cal_hashmap.get(day);
        } catch (Exception e) {
            number = 0;
        }
        return number;
    }

    @Override
    public void onItemClick(String index, String time, String type) {
        String command;
        int num_index = Integer.parseInt(index);

        if (recordVisibility == RecordVisibility.ARCHIVE) {
            if (index.equals(INDEX_FREE_RECORD) || index.equals(INDEX_NOTE)) {
                refreshMainStatus(PASS_RECORDS_INEDITABLE);
                return;
            } else {
                command = SERVER_SHOW_RECORD;
            }

        } else {
            if (num_index < 0) {
                command = SERVER_ADD_RECORD;
            } else {
                command = SERVER_CHANGE_RECORD;
            }
        }

        presenterMain.onChangeRecordClick(tv_date.getText().toString(), time, index, type, theme_type, command);
    }

    private void buttonsSetOnClickListener() {
        // restart load from server
        bt_restart.setOnClickListener(v -> presenterMain.onPermissionsGranted());
        // Show/hide free records
        bt_show_hide_free_rs.setOnClickListener(v -> presenterMain.onButtonShowHideFreeRecordsClick());
        // Exit a programm
        bt_exit.setOnClickListener(v -> finish());
        // Show DatePicker dialog
        tv_date.setOnClickListener(v -> changeCalendarState());
    }

    public void changeCalendarState() {
        if (calendarFragment.isHidden()) {
            showCalendarFragment();
        } else {
            hideCalendarFragment();
        }
    }

    public void showCalendarFragment() {
        getSupportFragmentManager().beginTransaction()
                .show(calendarFragment)
                .commit();
    }

    private void hideCalendarFragment() {
        getSupportFragmentManager().beginTransaction()
                .hide(calendarFragment)
                .commit();
    }

    @Override
    public void passDataToCalendar(HashMap<String, Integer> cal_hashmap) {
        this.cal_hashmap = cal_hashmap;
        callbackToCalendarFragment.setCalendarHashMap(cal_hashmap);
    }

    @Override
    public void onDateSet( Calendar calendar, int dayOfWeek, String date_str) {

        page = dayOfWeek;
        if (null != d_interval && d_interval.contains(date_str)) {
            TabLayout.Tab tab = tabLayout.getTabAt(page);
            tab.select();
        } else {
            presenterMain.onTextViewDateClick(calendar, dayOfWeek);
        }
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
        String state = (recordVisibility == RecordVisibility.ARCHIVE) ? ARCHIVE_DATA + status :  status;
        ((TextView) findViewById(R.id.tv_main_state)).setText(state);
    }

    @Override
    public void showToast(String status) {
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setArchiveStatus(RecordVisibility value) {
        recordVisibility = value;
        bt_show_hide_free_rs.setVisibility( (recordVisibility == RecordVisibility.ARCHIVE) ?  View.INVISIBLE : View.VISIBLE);
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

            /*
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            DatePickerDialog dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            ((TextView)((LinearLayout)((LinearLayout)((LinearLayout)((DatePicker)dialog.getDatePicker())
                    .getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0)).setText("My Date");
            dialog.show(); */