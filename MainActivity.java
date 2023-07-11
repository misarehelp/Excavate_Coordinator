package ru.volganap.nikolay.haircut_schedule;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity implements KM_Constants, Enums, Contract.ViewMain, Contract.ActivityReciever,
        MainFragmentInterface, DatePickerDialog.OnDateSetListener {

    private static final String DEFAULT_MAX_RECORDS = "25";

    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    // Main activity layout
    private Button bt_prev_week, bt_next_week, bt_add_rec, bt_add_client, bt_exit, bt_show_clients;
    private TextView tv_date;
    private BroadcastReceiver mainBroadcastReceiver;
    Contract.PresenterMain presenterMain;

    private ArrayList<String> d_interval;
    private ArrayList<String> d_of_week ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_date = findViewById(R.id.tv_date);

        Log.d(LOG_TAG, "Main - onCreate ");
        // Init Main activity layout
        initMainViewLayout();
        // instantiating object of Presenter Interface
        presenterMain = new PresenterMain(this,this);
        // Init Settings Preferences
        initSharedPreferences();
        // Init BroadcastReceiver
        initBroadcastReceiver();

    }

    private void initMainViewLayout() {
        // Main ViewLayout
        bt_prev_week = findViewById(R.id.bt_prev_week);
        bt_next_week = findViewById(R.id.bt_next_week);
        bt_add_rec = findViewById(R.id.bt_add_rec);
        bt_add_client = findViewById(R.id.bt_add_client);
        bt_exit = findViewById(R.id.bt_exit);
        bt_show_clients = findViewById(R.id.bt_show_clients);

    }

    private void initSharedPreferences() {
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);

        prefChangeListener = (sharedPreferences, key) -> {
            Log.d(LOG_TAG, "Main - prefChangeListener triggered on: " +key);

            presenterMain.onChangeSharedPrefs( key );
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefChangeListener);
        //sharedPrefs.edit().clear().commit();

        // Check for location and SMS permissions
        PermittedTask scanPermissionsTask = new PermittedTask(this, Manifest.permission.ACCESS_FINE_LOCATION) {
            @Override
            protected void granted() {

                presenterMain.onPermissionsGranted();
            }
        };
        scanPermissionsTask.run();
    }

    @Override
    public void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        // Set filter by Class name
        filter.addAction(getClass().getSimpleName());
        //filter.addAction(PREF_ACTIVITY);
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
    public void fillInRecordsList(ArrayList<ArrayList<Map<String, String>>> new_data, ArrayList<String> days_interval, ArrayList<String> days_of_week) {

        d_interval = days_interval;
        d_of_week = days_of_week;

        ViewPager2 vpPager = findViewById(R.id.vpPager);
        MainPagerAdapter fragmentStateAdapter = new MainPagerAdapter(this, getLifecycle(),this);

        for (int i = 0; i < d_interval.size(); i++) {
            if (new_data.get(i).size() == 0) {
                //fragmentStateAdapter.addFragment(EmptyFragment.newInstance(), days_interval.get(i));
                fragmentStateAdapter.addFragment(EmptyFragment.newInstance());
            } else {
                //fragmentStateAdapter.addFragment(new MainFragment(new_data.get(i)), days_interval.get(i));
                fragmentStateAdapter.addFragment(new MainFragment(new_data.get(i)));
            }
        }

        vpPager.setAdapter(fragmentStateAdapter);
        vpPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, vpPager, false, (tab, position) -> {

            if (position == 0) {
                tab.view.setBackgroundColor(Color.WHITE);
            } else {
                tab.view.setBackgroundColor(Color.GREEN);
            }
             if (d_of_week == null || d_of_week.size() == 0) {
                tab.setText("n/d");
                showTvDate("n/d");
            } else {
                tab.setText(d_of_week.get(position));
            }

        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int pos = tab.getPosition();
                if (d_of_week != null && d_of_week.size() != 0) {

                    tab.view.setBackgroundColor(Color.WHITE);
                    tab.setText(d_of_week.get(pos));
                    showTvDate(d_interval.get(pos));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.view.setBackgroundColor(Color.GREEN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
        showTvDate(d_interval.get(0));

        Log.d(LOG_TAG, "Main - fillInRecordsList - after");
        /* mainVp2Adapter.setValues(new_data);
        vpPager.setCurrentItem(0);
        mainVp2Adapter.notifyDataSetChanged(); */
        //mainVp2Adapter.notifyItemChanged(0);
    }

    @Override
    public void getDatafromMainFragment( int value) {
        presenterMain.onMainListViewItemClick( value );
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

        // add a record
        bt_add_rec.setOnClickListener(v -> {
            presenterMain.onButtonAddRecordClick(tv_date.getText().toString());
        });

        // add a client
        bt_add_client.setOnClickListener(v -> {
            presenterMain.onButtonAddClientClick();
        });

        // Exit a programm
        bt_exit.setOnClickListener(v -> {
            presenterMain.onButtonExitClick();
        });

        // Show all clients
        bt_show_clients.setOnClickListener(v -> {
            presenterMain.onButtonShowClientsClick();
        });

        // Show DatePicker dialog
        tv_date.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            DatePickerDialog dialog = new DatePickerDialog( this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();

        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        presenterMain.onTextViewDateClick( year, monthOfYear, dayOfMonth);
    }

    @Override
    public void setViewButtonsFieldsVisibility (String permit_code) {
        // Check for if a new permit
        switch (permit_code) {
            case ADD_CODE:

                bt_next_week.setVisibility(View.GONE);
                bt_add_rec.setVisibility(View.GONE);
                bt_add_client.setVisibility(View.VISIBLE);
                bt_show_clients.setVisibility(View.INVISIBLE);
                bt_exit.setVisibility(View.INVISIBLE);
                break;

            case CHANGE_CODE:

                bt_next_week.setVisibility(View.GONE);
                bt_add_rec.setVisibility(View.GONE);
                bt_add_client.setVisibility(View.VISIBLE);
                bt_show_clients.setVisibility(View.INVISIBLE);
                bt_exit.setVisibility(View.INVISIBLE);
                break;

            case EMPTY_STORAGE_STATE:
            case DATA_IS_READY:
            case DATA_WAS_NOT_CHANGED:
            case DATA_WAS_SAVED:
            case DATA_WAS_DELETED:
            default:
                bt_next_week.setVisibility(View.VISIBLE);
                bt_add_rec.setVisibility(View.VISIBLE);
                bt_add_client.setVisibility(View.VISIBLE);
                bt_show_clients.setVisibility(View.VISIBLE);
                bt_exit.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        Log.d(LOG_TAG, "onCreateOptionsMenu: "+super.onCreateOptionsMenu(menu));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.set_item:
                startActivity(new Intent(this, PrefActivity.class));
                break;

            case R.id.serv_config_item:
                presenterMain.onChangeServerPreferences(getMaxRecordsNumber());
                break;

            case R.id.show_archive:
                presenterMain.onShowArchiveClick();
                break;

            case R.id.version:
                Toast.makeText(this, getResources().getString(R.string.version), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshMainStatus(String status) {
        ((TextView)findViewById(R.id.tv_main_state)).setText(status);
    }

    @Override
    public void showToast(String status) {
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
    }

    @Override
    public String getMaxRecordsNumber(){
        return sharedPrefs.getString(RECORDS_MAX_NUMBER, DEFAULT_MAX_RECORDS);
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

    /*@Override
    public void setViewPermitBlockParams( PermitBlock state ) {

        LinearLayout ll_permit_block = findViewById(R.id.ll_permit_block);
        LinearLayout ll_main_block = findViewById(R.id.ll_main_block);
        ViewGroup.LayoutParams permit_params = ll_permit_block.getLayoutParams();
        ViewGroup.LayoutParams main_params = ll_main_block.getLayoutParams();

        if (state.equals(PermitBlock.INVISIBLE)) {

            permit_params.height = 0;
            main_params.height = WRAP_CONTENT;
        } else {

            permit_params.height = WRAP_CONTENT;
            main_params.height = 0;
        }

        ll_permit_block.setLayoutParams(permit_params);
        ll_main_block.setLayoutParams(main_params);
    } */

    /*@Override
    public void setViewUserMadeBlockVisibility( boolean dispatcher_on ) {
        LinearLayout ll_permits_user_made_block = findViewById(R.id.ll_permits_user_made_block);
        if (dispatcher_on) {
            ll_permits_user_made_block.setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tv_permits_awaiting)).setText(getResources().getString(R.string.tv_permits_all));
        } else {
            ll_permits_user_made_block.setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tv_permits_awaiting)).setText(getResources().getString(R.string.tv_permits_awaiting));
        }
    } */


    /*@Override
    public String getViewModeUser(){
        //return sharedPrefs.getString(MODE_USER, getDispatcherDefault());
        //return sharedPrefs.getString(MODE_USER, getDepartmentEntriesArray()[0]);
    } */

    /*

    @Override
    public String getViewDepartmentUser(){
        return sharedPrefs.getString(DEPARTMENT_USER, getDepartmentEntriesArray()[0]);
    }

    @Override
    public String [] getDepartmentValuesArray() {
        return getResources().getStringArray(R.array.department_values);
    }

    @Override
    public String [] getDepartmentEntriesArray() {
        return getResources().getStringArray(R.array.department_entries);
    }

    @Override
    public String getDispatcherDefault(){
        return  getResources().getStringArray(R.array.mode_user_type_values)[1];
    }
    */

    /*
    public String getAdminPass(){
        return sharedPrefs.getString(ADMIN_PASS, null);
    } */


    /*
    @Override
    public void setDepartmentBlockVisible () {

        bt_deps_choose.setText(getResources().getString(R.string.bt_deps_choose_end));
        bt_deps_choose.setBackground(ContextCompat.getDrawable(this, R.drawable.bt_bg_purple));
        bt_deps_choose.setTextColor(Color.WHITE);

        params = lv_deps_choose.getLayoutParams();
        params.height = WRAP_CONTENT;
        lv_deps_choose.setLayoutParams(params);

        lv_deps_choose.setVisibility(View.VISIBLE);
        ll_permit_data.setVisibility(View.GONE);
    }

    @Override
    public void setDepartmentBlockInvisible () {

        bt_deps_choose.setText(getResources().getString(R.string.bt_deps_choose_start));
        bt_deps_choose.setBackground(ContextCompat.getDrawable(this, R.drawable.bt_bg_green));
        bt_deps_choose.setTextColor(Color.BLACK);

        lv_deps_choose.setVisibility(View.GONE);
        ll_permit_data.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRequiredDepsVisibility (int state) {

        LinearLayout ll_deps_approve = findViewById(R.id.ll_deps_approve);
        ll_deps_approve.setVisibility(state);
    }
    */

    /*
    @Override
    public void setPermitIdTextView( String id ) {
        //put in Department  and ID
        TextView tv_permit_id = findViewById(R.id.tv_permit_id);
        tv_permit_id.setText(id);
    } */

    /*private boolean checkCorrectInputPlaceDate() {
        final String FILL_IN_FIELDS ="Необходмо ";
        final String REQUIRED_ID_PLACE_DATE = "заполнить поля: '" + getResources().getString(R.string.et_place)
                + "', '" + getResources().getString(R.string.et_date_start);

        if ( et_permit_place.getText().toString().isEmpty() || et_permit_date_start.getText().toString().isEmpty() ||
                et_permit_date_end.getText().toString().isEmpty() ) {
            Toast.makeText(this, FILL_IN_FIELDS + REQUIRED_ID_PLACE_DATE, Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }
    */

    /*@Override
    public void setPermitAdapterAndItemClickListener( ArrayList<Map<String, String>> data ) {

        int[] to = { R.id.li_permit_depart, R.id.li_permit_required, R.id.li_permit_commun, R.id.li_permit_date_approve };
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.permit_list_item, FROM, to);

        ListView lv = findViewById(R.id.lv_deps_approve);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //int lv_pos = ((ListView) parent).getCheckedItemPosition();
            }
        });
    }*/
}

// a change for a depricated onActivityResult()
    /*private ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // get back with Maps Activity
                    presenterMain.onMainActivityResult(result);
                }
            }); */

//private String code_after_map = DATA_WAS_NOT_CHANGED;

/* <LinearLayout
        android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:layout_gravity="center">
<TextView
            android:id="@+id/courseName"
                    android:text="courseName"
                    android:textStyle="bold"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"/>
</LinearLayout> */

//TabLayout tabLayout = findViewById(R.id.tabLayout);
//vpPager = findViewById(R.id.vpPager);
//
//
        /*
        ArrayList<ArrayList<Map<String, String>>> data = new ArrayList<> ();
        ArrayList<Map<String, String>> day_data = new ArrayList<>();
        day_data.add(new HashMap<>());
        data.add(day_data);
        mainVp2Adapter = new MainVp2Adapter(this, data);
        vpPager.setAdapter(mainVp2Adapter); */

//vpPager.setClipToPadding(false);
//vpPager.setPadding(40, 0, 70, 0);
//vpPager.setPageMargin(20);

		/* TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, new TabLayoutMediator.TabConfigurationStrategy()
		{
			@Override
			public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
			{
				// When a tab is created this is called, then we can set tab properties, in this case the text
				tab.setText(mainToolsAdapter.getItemTitle(position));
			}
		});
		tabLayoutMediator.attach(); */


       /* new TabLayoutMediator(tabLayout, vpPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                if (days_of_week == null || days_of_week.size() == 0) {
                    //tab.setText("no date");
                    //showTvDate("no date");
                } else {
                    //tab.setText(days_of_week.get(position));
                    //showTvDate(days_interval.get(position));
                }
            }
        }).attach();

        new TabLayoutMediator(tabLayout, vpPager, (tab, position) -> {

            tab.view.setBackgroundColor(Color.CYAN);
            if (days_of_week == null || days_of_week.size() == 0) {
                tab.setText("n/d");
                showTvDate("n/d");
            } else {
                tab.setText(days_of_week.get(position));
                showTvDate(days_interval.get(position));
            }

        }).attach();



         tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int pos = tab.getPosition();
                Log.d(LOG_TAG, "Main_TabSelectedListener, position: " + pos );
                if (days_of_week == null || days_of_week.size() == 0) {
                    tab.view.setBackgroundColor(Color.RED);
                    tab.setText("n/d");
                    showTvDate("n/d");
                } else {

                    //vpPager.setCurrentItem(pos);
                    //mainVp2Adapter.notifyDataSetChanged();
                    //mainVp2Adapter.notifyItemChanged(pos);

                    tab.view.setBackgroundColor(Color.WHITE);
                    tab.setText(days_of_week.get(pos));
                    showTvDate(days_interval.get(pos));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.view.setBackgroundColor(Color.CYAN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        }); */

        /* vpPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                //Log.d(LOG_TAG, "Main_onPageScrolled: " + String.valueOf(position));
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (days_of_week == null || days_of_week.size() == 0) {
                    showTvDate("no date");
                } else {
                    showTvDate(days_interval.get(position));
                }
                Log.d(LOG_TAG, "Main_onPageSelected: " + String.valueOf(position));
            }

            // triggered when there is
            // scroll state will be changed
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                //Log.d(LOG_TAG, "Main_onPageScrollStateChanged: " + String.valueOf(state));
            }
        }); */

//FragmentStateAdapter pagerAdapter = new MainPagerAdapter(this);
//vpPager.setAdapter(pagerAdapter);