package ru.volganap.nikolay.excavate_coordinator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity implements KM_Constants, Enums, Contract.ViewMain{

    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    private String department_array [];

    // Main activity layout
    private EditText et_permit_place, et_permit_date_start, et_permit_comment;
    private Button bt_fill_permit, bt_check_request, bt_put_show_comm, bt_permit_exit, bt_deps_choose, bt_delete;
    private ListView lv_deps_choose;

    LinearLayout ll_permit_data;
    ViewGroup.LayoutParams params;

    private BroadcastReceiver mainBroadcastReceiver;
    Contract.PresenterMain presenterMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init Main activity layout
        initMainViewLayout();
        // instantiating object of Presenter Interface
        presenterMain = new PresenterMain(this,this);
        // Init Settings Preferences

        department_array = getResources().getStringArray(R.array.department_entries);
        initSharedPreferences();
        // Init BroadcastReceiver
        initBroadcastReceiver();
    }

    private void initMainViewLayout() {
        // Main ViewLayout
        bt_fill_permit = findViewById(R.id.bt_fill_permit);
        bt_check_request = findViewById(R.id.bt_check_request);

        et_permit_place = findViewById(R.id.et_permit_place);
        et_permit_date_start = findViewById(R.id.et_permit_date_start);
        et_permit_comment = findViewById(R.id.et_permit_comment);

        bt_deps_choose = findViewById(R.id.bt_deps_choose);
        bt_delete = findViewById(R.id.bt_delete);
        bt_put_show_comm = findViewById(R.id.bt_put_show_comm);
        bt_permit_exit = findViewById(R.id.bt_permit_exit);

        lv_deps_choose = findViewById(R.id.lv_deps_choose);
        lv_deps_choose.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ll_permit_data = findViewById(R.id.ll_permit_data);
    }

    @Override
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
    }

    private void initSharedPreferences() {
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);

        int disp_pos = getResources().getStringArray(R.array.mode_user_type_entries).length-2;
        String disp_value = getResources().getStringArray(R.array.mode_user_type_entries)[disp_pos];

        prefChangeListener = (sharedPreferences, key) -> {
            Log.d(LOG_TAG, "Main - prefChangeListener triggered on: " +key);
            if (key.equals(DEPARTMENT_USER)) {
                // trigger when Department user is changed
                presenterMain.onChangeSharedPrefs(getViewDepartmentUser(), false);
            }
            if (key.equals(MODE_USER)) {
                // trigger when Mode user is changed
                String value = sharedPrefs.getString(MODE_USER, disp_value);
                if (value.equals(disp_value)) {
                    presenterMain.onChangeSharedPrefs(disp_value, true);
                } else {
                    presenterMain.onChangeSharedPrefs(getViewDepartmentUser(), false);
                }
            }
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefChangeListener);
        //sharedPrefs.edit().clear().commit();

        // Check for location and SMS permissions
        PermittedTask scanPermissionsTask = new PermittedTask(this, Manifest.permission.ACCESS_FINE_LOCATION) {
            @Override
            protected void granted() {
                presenterMain.onChangeSharedPrefs(getViewDepartmentUser(), false);
                //presenterMain.onPermissionsGranted( getDepartmentArray() );
                presenterMain.onPermissionsGranted( department_array );
            }
        };
        scanPermissionsTask.run();
    }

    /* public  String[] getDepartmentArray (){
        return getResources().getStringArray(R.array.department_entries);
    } */

    public String getViewDepartmentUser(){
        //String[] department_array = getDepartmentArray ();
        return sharedPrefs.getString(DEPARTMENT_USER, department_array[0]);
        //return department_array[0];
    }

    @Override
    public void setViewDepartmentUser(String department_user) {
        ((TextView)findViewById(R.id.tv_department_user)).setText(department_user);
    }

    private void initBroadcastReceiver() {
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

    // Fill In Permits User Made List
    public void fillInPermitsUserMadeList(ArrayList<Map<String, String>> data) {

        int[] to = { R.id.li_number, R.id.li_place, R.id.li_date_reg, R.id.li_approved };

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.main_list_item_1, FROM, to);
        setAdapterAndItemClickListener(adapter, R.id.lv_permits_user_made);
    }

    // Fill In Permits Awaiting List
    public void fillInPermitsAwaitingList(ArrayList<Map<String, String>> data) {

        int[] to = { R.id.li_number2, R.id.li_depart2, R.id.li_place2, R.id.li_date_req2 };

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.main_list_item_2, FROM, to);
        setAdapterAndItemClickListener(adapter, R.id.lv_permits_awaiting);
    }

    private void setAdapterAndItemClickListener(SimpleAdapter adapter, int lv_id) {
        ListView lv = findViewById(lv_id);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                presenterMain.onMainListViewItemClick( position, lv_id, R.id.lv_permits_user_made );
            }
        });
    }

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

    private void buttonsSetOnClickListener() {
        // Fill out a work permit form
        bt_fill_permit.setOnClickListener(v -> {
            presenterMain.onButtonNewClick();
        });

        // Fill out a work permit form
        bt_check_request.setOnClickListener(v -> {
            presenterMain.onButtonCheckClick();
        });

        // Show or put the communication lines on the map
        bt_put_show_comm.setOnClickListener(v -> {
            //Start MapsActivity
            presenterMain.onButtonShowMapClick( this );
        });

        // Edit required departments
        bt_deps_choose.setOnClickListener(v -> {
            // Check if Place and Date data are put in
            if (checkCorrectInputPlaceDate()) {

                presenterMain.onButtonDepsChooseClick(bt_deps_choose.getText().toString(),
                        getResources().getString(R.string.bt_deps_choose_start),
                        et_permit_place.getText().toString(), et_permit_date_start.getText().toString(),
                        et_permit_comment.getText().toString());
            }
        });

        // Delete permit
        bt_delete.setOnClickListener(v -> {
            // Delete Depline Data Item
            presenterMain.onButtonDeleteClick();
            setDepartmentBlockInvisible ();
        });

        // Save the changes and exit
        bt_permit_exit.setOnClickListener(v -> {
            // Check if Lines of a new permit are put in
            presenterMain.onButtonExitClick();
        });
    }

    @Override
    public void setPermitIdTextView( String id ) {
        //put in Department  and ID
        TextView tv_permit_id = findViewById(R.id.tv_permit_id);
        tv_permit_id.setText(id);
    }

    @Override
    public void setPlaceDateComment (String place, String date_start, String comment ) {

        et_permit_place.setText(place);
        et_permit_date_start.setText(date_start);
        et_permit_comment.setText(comment);
    }

    @Override
    public void setViewButtonsFieldsVisibility (String permit_code) {
        // Check for if a new permit
        switch (permit_code) {
            case NEW_PERMIT_CODE:

                bt_fill_permit.setVisibility(View.GONE);
                bt_check_request.setVisibility(View.GONE);

                bt_deps_choose.setVisibility(View.VISIBLE);
                bt_put_show_comm.setVisibility(View.INVISIBLE);
                bt_delete.setVisibility(View.VISIBLE);
                bt_permit_exit.setVisibility(View.INVISIBLE);
                break;

            case FILLED_PERMIT_CODE:

                bt_fill_permit.setVisibility(View.GONE);
                bt_check_request.setVisibility(View.GONE);

                bt_deps_choose.setVisibility(View.GONE);
                bt_put_show_comm.setVisibility(View.VISIBLE);
                bt_delete.setVisibility(View.VISIBLE);
                bt_permit_exit.setVisibility(View.INVISIBLE);
                break;


            case EDIT_PERMIT_CODE:

                bt_fill_permit.setVisibility(View.GONE);
                bt_check_request.setVisibility(View.GONE);

                bt_deps_choose.setVisibility(View.GONE);
                bt_put_show_comm.setVisibility(View.VISIBLE);
                bt_delete.setVisibility(View.VISIBLE);
                bt_permit_exit.setVisibility(View.VISIBLE);
                break;

            case ADD_PERMIT_CODE:

                bt_fill_permit.setVisibility(View.GONE);
                bt_check_request.setVisibility(View.GONE);

                bt_deps_choose.setVisibility(View.GONE);
                bt_put_show_comm.setVisibility(View.VISIBLE);
                bt_delete.setVisibility(View.INVISIBLE);
                bt_permit_exit.setVisibility(View.VISIBLE);
                break;
            case SHOW_PERMIT_CODE:

                bt_fill_permit.setVisibility(View.GONE);
                bt_check_request.setVisibility(View.GONE);

                bt_deps_choose.setVisibility(View.GONE);
                bt_put_show_comm.setVisibility(View.VISIBLE);
                bt_delete.setVisibility(View.INVISIBLE);
                bt_permit_exit.setVisibility(View.VISIBLE);
                break;

            case NET_ERROR_STATE:
            case URL_WAS_NOT_FOUND:
                bt_fill_permit.setVisibility(View.GONE);
                bt_check_request.setVisibility(View.GONE);

                bt_deps_choose.setVisibility(View.GONE);
                bt_put_show_comm.setVisibility(View.INVISIBLE);
                bt_delete.setVisibility(View.INVISIBLE);
                bt_permit_exit.setVisibility(View.INVISIBLE);
                break;

            case EMPTY_STORAGE_STATE:
            case DATA_IS_READY:
            case DATA_WAS_NOT_CHANGED:
            case DATA_WAS_SAVED:
            case DATA_WAS_DELETED:
            default:
                bt_fill_permit.setVisibility(View.VISIBLE);
                bt_check_request.setVisibility(View.VISIBLE);

                bt_deps_choose.setVisibility(View.GONE);
                bt_put_show_comm.setVisibility(View.INVISIBLE);
                bt_delete.setVisibility(View.INVISIBLE);
                bt_permit_exit.setVisibility(View.VISIBLE);
                break;
        }
    }

    //Show Department Line Data from server
    @Override
    public void defineListOfRequiredDeps() {

        ArrayAdapter<String> depart_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, department_array);

                                                                //getDepartmentArray());
        //depart_adapter = new MyArrayAdapter(this, R.layout.custom_list, android.R.id.text2, DEPARTMENT_ARRAY);
        lv_deps_choose.setAdapter(depart_adapter);
        lv_deps_choose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                presenterMain.onPermitListViewItemClick(parent);
            }
        });
    }

    private boolean checkCorrectInputPlaceDate() {
        final String FILL_IN_FIELDS ="Необходмо ";
        final String REQUIRED_ID_PLACE_DATE = "заполнить поля: '" + getResources().getString(R.string.et_place)
                + "', '" + getResources().getString(R.string.et_date_start);

        if ( et_permit_place.getText().toString().isEmpty() || et_permit_date_start.getText().toString().isEmpty() ) {
            Toast.makeText(this, FILL_IN_FIELDS + REQUIRED_ID_PLACE_DATE, Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }

    @Override
    public void setPermitAdapterAndItemClickListener( ArrayList<Map<String, String>> data ) {

        int[] to = { R.id.li_permit_depart, R.id.li_permit_required, R.id.li_permit_commun, R.id.li_permit_date_approve };
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.permit_list_item, FROM, to);

        ListView lv = findViewById(R.id.lv_deps_approve);
        lv.setAdapter(adapter);
        /* lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //int lv_pos = ((ListView) parent).getCheckedItemPosition();
            }
        }); */
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        Log.d(LOG_TAG, "onCreateOptionsMenu: "+super.onCreateOptionsMenu(menu));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_item:
                startActivity(new Intent(this, PrefActivity.class));
                break;
            case R.id.serv_config_item:
                //getBackWithServer(CHANGE_CONFIG_SERVER, sharedPrefs.getString(MARKER_MAX_NUMBER, ""));
                //presenter.updateViewServerData(getBaseContext(), CHANGE_CONFIG_SERVER, sharedPrefs.getString(MARKER_MAX_NUMBER, ""), "");
                break;

            case R.id.version:
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.version), Toast.LENGTH_LONG).show();
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

    /* @Override
    public void runMapsActivity( Intent intent ) {

        String dep_line_data_json = intent.getStringExtra(DEP_LINE_DATA);
        String permit_code = intent.getStringExtra(DATA_TYPE);

        Intent maps_activity = new Intent(this, MapsActivity.class);
        maps_activity.putExtra(DEP_LINE_DATA, dep_line_data_json);
        maps_activity.putExtra(DATA_TYPE, permit_code);
        activityResultLaunch.launch(maps_activity);
    } */
