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
    private TextView tv_date, tv_main_state;
    private TabLayout tabLayout;
    private BroadcastReceiver mainBroadcastReceiver;
    Contract.PresenterMain presenterMain;
    private ArrayList<String> d_interval;

    private int theme_type;
    private int page = 0;
    private RecordVisibility recordVisibility;
    // for Calendar
    private HashMap<String, Integer> cal_hashmap;
    private HashMap <String, Boolean> holiday_hashmap;
    private HashMap <String, Integer> note_hashmap;
    private HashMap <String, Integer> calendar_colors;
    private Fragment calendarFragment = new CalendarFragment();
    private Contract.MainActivityToCalendarFragment callbackToCalendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initMainViewLayout();
        initSharedPreferences();
    }

    private void initMainViewLayout() {
        // Main ViewLayout
        tv_date = findViewById(R.id.tv_date);
        tv_main_state = findViewById(R.id.tv_main_state);
        float new_size = (float) (tv_date.getTextSize() * 0.5);
        tv_date.setTextSize(new_size);
        bt_restart = findViewById(R.id.bt_restart);
        bt_show_hide_free_rs = findViewById(R.id.bt_show_hide_free_rs);
        bt_exit = findViewById(R.id.bt_exit);
        // Set visibility and onclick method of buttons
        buttonsSetOnClickListener();
    }

    private void initPermissionsAndBR () {
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
            throw new ClassCastException(this + " must implement Contract.RecordActivity.ToRecordFragment");
        }
    }

    private void initSharedPreferences() {
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
        calendar_colors = new HashMap<>();

        if (sharedPrefs.contains(CALENDAR_BACKGROUND_SELECT_DAY)) {
            initCalendarColors(CALENDAR_BACKGROUND_HOLIDAY);
            initCalendarColors(CALENDAR_TEXT_HOLIDAY);
            initCalendarColors(CALENDAR_BACKGROUND_WORKDAY);
            initCalendarColors(CALENDAR_TEXT_WORKDAY);
            initCalendarColors(CALENDAR_BACKGROUND_TODAY);
            initCalendarColors(CALENDAR_TEXT_TODAY);
            initCalendarColors(CALENDAR_BACKGROUND_SELECT_DAY);
            initCalendarColors(CALENDAR_TEXT_SELECT_DAY);

            prefChangeListener = (sharedPreferences, key) -> {
                switch (key) {
                    case THEME:
                        onChangeThemeAppearance(true);
                        break;
                    case DAYS_BEFORE_NOW:
                        presenterMain.onChangeDaysBefore(getDaysBefore());
                        break;
                    case CALENDAR_BACKGROUND_HOLIDAY:
                    case CALENDAR_BACKGROUND_WORKDAY:
                    case CALENDAR_BACKGROUND_TODAY:
                    case CALENDAR_TEXT_HOLIDAY:
                    case CALENDAR_TEXT_WORKDAY:
                    case CALENDAR_TEXT_TODAY:
                    case CALENDAR_TEXT_SELECT_DAY:
                    case CALENDAR_BACKGROUND_SELECT_DAY:
                        calendar_colors.put(key, sharedPrefs.getInt(key, 0));
                        callbackToCalendarFragment.setCalendarHashMap(cal_hashmap, holiday_hashmap, note_hashmap, calendar_colors);

                        break;
                    default:
                }
            };

            sharedPrefs.registerOnSharedPreferenceChangeListener(prefChangeListener);
            theme_type = onChangeThemeAppearance(false);
            setTheme(theme_type);
            //sharedPrefs.edit().clear().commit();

            initPermissionsAndBR();
        } else {
            runPreferenceActivity(CALENDAR_SETTINGS, true);
            runPreferenceActivity(GENERAL_SETTINGS, true);
        }
    }

    private void initCalendarColors (String key) {
        if (sharedPrefs.contains(key)) {
            calendar_colors.put(key, sharedPrefs.getInt(key, 0));
        }
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "onRequestPermissionsResult: " + grantResults[0] );
            presenterMain.onPermissionsGranted();
        } else {
            Log.d(LOG_TAG, "onRequestPermissionsResult: failed" );
        }
        /* switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
            }
        } */
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
    }

    private void showTvDate(String date) {
        tv_date.setText(date);
    }

    // Fill In Permits User Made List
    @Override
    public void fillInRecordsList(ArrayList<ArrayList<MainScreenData>> new_data, ArrayList<String> days_interval ) {

        d_interval = days_interval;
        boolean holiday_value;

        ViewPager2 vpPager = findViewById(R.id.vpPager);
        MainPagerAdapter fragmentStateAdapter = new MainPagerAdapter(this );

        for (int i = 0; i < d_interval.size(); i++) {
            holiday_value = null != holiday_hashmap && holiday_hashmap.containsKey(d_interval.get(i));
            fragmentStateAdapter.addFragment( new MainFragment(new_data.get(i), theme_type, holiday_value ));
        }

        vpPager.setAdapter(fragmentStateAdapter);
        vpPager.setOffscreenPageLimit(2);

        tabLayout = findViewById(R.id.tabLayout);
        int theme_pos = Integer.parseInt(sharedPrefs.getString(THEME, "0"));

        switch (theme_pos) {
            case 3,4,5:
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
            int day_recs = getNumberOfDayRecords(d_interval.get(position));
            int day_notes = getNumberOfDayNotes(d_interval.get(position));
            String body;
            if (day_recs == 0 && day_notes == 0) {
                body = "";
            } else {
                body = "(" + (day_recs + day_notes) + ")";
            }

            tab.setText(WEEKDAYS[position] + '\n' + body);

        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                int day_recs = getNumberOfDayRecords(d_interval.get(position));
                int day_notes = getNumberOfDayNotes(d_interval.get(position));
                showTvDate(d_interval.get(position));
                int day = Integer.parseInt(d_interval.get(position).substring(0,2));

                callbackToCalendarFragment.syncCalendarDayToPage(day);

                String add_start = "В этот день ", add_recs ="", add_notes = "", add_status = "";

                switch (day_recs) {
                    case 1:
                        add_recs = day_recs + " запись";
                        break;
                    case 2, 3, 4:
                        add_recs = day_recs + " записи";
                        break;
                    case 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20:
                        add_recs = day_recs + " записей";
                        break;
                    case 0:
                    default:
                }

                switch (day_notes) {
                    case 1:
                        add_notes = day_notes + " заметка";
                        break;
                    case 2, 3, 4:
                        add_notes = day_notes + " заметки";
                        break;
                    case 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20:
                        add_notes = day_notes + " заметок";
                        break;
                    case 0:
                    default:
                }

                if (day_recs == 0 && day_notes ==0) {
                    add_status = add_start + "нет записей и заметок";

                } else if (day_recs != 0 && day_notes !=0) {
                    add_status = add_start + add_recs + " и " + add_notes;

                } else {
                    add_status = add_start + add_recs + add_notes;
                }
                refreshMainStatus(add_status);
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

    private int getNumberOfDayNotes (String day) {
        int number;
        try {
            number = note_hashmap.get(day);
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
    public void passDataToCalendar(HashMap<String, Integer> cal_hashmap, HashMap <String, Boolean> holiday_hashmap, HashMap <String, Integer> note_hashmap ) {
        this.cal_hashmap = cal_hashmap;
        this.holiday_hashmap = holiday_hashmap;
        this.note_hashmap = note_hashmap;
        callbackToCalendarFragment.setCalendarHashMap(cal_hashmap, holiday_hashmap, note_hashmap, calendar_colors);
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
                runPreferenceActivity( GENERAL_SETTINGS, false);
                break;

            case R.id.calendar_item:
                runPreferenceActivity( CALENDAR_SETTINGS, false);
                break;

            case R.id.serv_config_item:
                presenterMain.onChangeServerPreferences( getMaxRecordsNumber(), getDaysBefore() );
                break;

            case R.id.delete_archive_item:

                presenterMain.onDeleteArchiveRecords();
                break;

            case R.id.version:
                Toast.makeText(this, getResources().getString(R.string.version), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runPreferenceActivity (String sender, boolean first_start) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SENDER, sender);
        intent.putExtra(COMMAND, first_start);
        startActivity(intent);
    }

    @Override
    public void refreshMainStatus(String status) {
        String state = (recordVisibility == RecordVisibility.ARCHIVE) ? ARCHIVE_DATA + status :  status;
        tv_main_state.setText(state);
        //((TextView) findViewById(R.id.tv_main_state)).setText(state);
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

        if (sharedPrefs.contains(CHILD_ACTIVITY)) {
            if (sharedPrefs.getString(CHILD_ACTIVITY, GENERAL_SETTINGS).equals(RECORD_ACTIVITY)) {
                presenterMain.onMainActivityResume();
                Log.d(LOG_TAG, "MainActivity: onResume ");
            }

            /* if (sharedPrefs.getString(CHILD_ACTIVITY, GENERAL_SETTINGS).equals(GENERAL_SETTINGS)) {
                //initSharedPreferences();
            } */
        }
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