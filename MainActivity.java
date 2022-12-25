package ru.volganap.nikolay.excavate_coordinator;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity implements KM_Constants{
    public static final String SERVER_GET_ALL ="server_get_all";
    public static final String SERVER_PUT_ALL ="server_put_all";
    public static final String SERVER_GET_BY_DEPART ="server_get_by_depart";
    public static final String SERVER_GET_BY_ID ="server_get_by_id";
    public static final String SERVER_NEW_BY_ID ="server_new_by_id";
    public static final String SERVER_CHANGE_BY_ID ="server_change_by_id";
    final String FILL_IN_FIELDS ="Необходмо ";
    final String COMMUNICATIONS_ARE_DEFINED ="Коммуникации внесены в базу данных наряда, сохраните изменения.";

    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    // Main activity layout
    private TextView tv_main_state, tv_department_user;
    private Button bt_fill_permit, bt_check_request;

    // Permit activity layout
    private TextView tv_permit_state, tv_permit_id;
    private EditText et_permit_place, et_permit_date_start, et_permit_comment;
    private Button bt_put_show_comm, bt_permit_save, bt_deps_choose, bt_delete;
    private LinearLayout ll_permit_block, ll_required_department_edit;
    private ListView lv_deps_choose, lv_deps_approve;

    private BroadcastReceiver mainBroadcastReceiver;
    private String department_user;
    private String[] department_array;
    boolean [] required_array;

    // a change for a depricated onActivityResult()
    private ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == MAPS_ACTIVITY_REQUEST_CODE) {
                        Log.d(LOG_TAG, "PermitActivity: ActivityResultCallback ");

                        Intent intent = result.getData();
                        code_after_map = intent.getStringExtra(DATA_TYPE);
                        String dep_line_data_json = intent.getStringExtra(DEP_LINE_DATA);
                        dep_line_data = new Gson().fromJson(dep_line_data_json, DepLinesData.class);
                        //String status = COMMUNICATIONS_ARE_DEFINED;

                        switch (code_after_map) {

                            case ADD_PERMIT_CODE:
                            case EDIT_PERMIT_CODE:
                                fillInDepsApproveList(code_after_map);
                            default:
                                //status = code_after_map;
                                break;
                        }

                        refreshMainStatus( code_after_map );
                    }
                }
                /* public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == PERMIT_ACTIVITY_REQUEST_CODE) {
                        refreshMainStatus(DATA_IS_READY);
                        Intent intent = result.getData();
                        handleActivityResult(intent);
                    }
                } */
            });
    //private ListView lv_permits_user_made, lv_permits_awaiting;
    private ArrayList<DepLinesData> dep_lines_data_array  = new ArrayList<>();
    private DepLinesData dep_line_data;
    private int position_to_edit;
    private String[] from = {"0", "1", "2", "3", "4"};
    private String code_after_map = DATA_WAS_NOT_CHANGED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init Main activity layout
        initMainViewLayout();

        // Init Settings Preferences
        initSharedPreferences();

        // set Departtment User;
        setDepartmentUser();

        required_array  = new boolean[department_array.length];

        // Init BroadcastReceiver
        initBroadcastReceiver();
    }

    private void setDepartmentUser() {
        department_array = getResources().getStringArray(R.array.department_values);
        department_user = sharedPrefs.getString(DEPARTMENT_USER, department_array[0]);
        tv_department_user.setText(department_user);
    }

    private void initMainViewLayout() {
        tv_main_state = findViewById(R.id.tv_main_state);
        tv_department_user = findViewById(R.id.tv_department_user);

        ll_permit_block = findViewById(R.id.ll_permit_block);
        ViewGroup.LayoutParams params = ll_permit_block.getLayoutParams();
        params.height = 0;
        ll_permit_block.setLayoutParams(params);

        //tv_permits_user_made = findViewById(R.id.tv_permits_user_made);
        //tv_permits_awaiting = findViewById(R.id.tv_permits_awaiting);
        bt_fill_permit = findViewById(R.id.bt_fill_permit);
        bt_check_request = findViewById(R.id.bt_check_request);
        setButtonsInterface(URL_WAS_NOT_FOUND);
    }

    private void initSharedPreferences() {
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
        prefChangeListener = (sharedPreferences, key) -> {
            Log.d(LOG_TAG, "Main - prefChangeListener triggered on: " +key);
            if (key.equals(DEPARTMENT_USER)) {
                setDepartmentUser();
                bt_check_request.performClick();
            }
            refreshMainStatus("");
        };
        sharedPrefs.registerOnSharedPreferenceChangeListener(prefChangeListener);

        // Check for location and SMS permissions
        PermittedTask scanPermissionsTask = new PermittedTask(this, Manifest.permission.ACCESS_FINE_LOCATION) {
            @Override
            protected void granted() {
                //Get the permit data having been required user's approvement  from server
                getBackWithServer(SERVER_GET_ALL, "", "");
            }
        };
        scanPermissionsTask.run();
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        // Set filter by Class name
        filter.addAction(getClass().getSimpleName());
        mainBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String sender = intent.getStringExtra(SENDER);
                    String message = intent.getStringExtra(MESSAGE);
                    //String action = intent.getAction();
                    Log.d(LOG_TAG, "Main Get back with Sender: " + sender + ", data: " + message);
                    setButtonsInterface(message);
                    switch (message) {
                        case NET_ERROR_STATE:
                        case URL_WAS_NOT_FOUND:
                        case DATA_WAS_NOT_SAVED:
                        case DATA_WAS_SAVED:
                            refreshMainStatus(message);
                            break;
                        case EMPTY_STORAGE_STATE:
                        case REQUEST_IS_EMPTY:
                            refreshMainStatus(EMPTY_STORAGE_STATE);
                            break;
                        default:
                            refreshMainStatus(DATA_IS_READY);

                            ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
                            for (String item: array_level_json) {
                                dep_lines_data_array.add(new Gson().fromJson(item, DepLinesData.class));
                            }

                            fillInPermitsUserMadeList();
                            fillInPermitsAwaitingList();
                    }
                }
            }
        };
        registerReceiver(mainBroadcastReceiver, filter);
        // Set visibility and onclick method of buttons
        buttonsMainSetOnClickListener();
    }

    // Handle the result got from another Activity
    private void getBackWithPermitBlock(String data_type) {

        ViewGroup.LayoutParams params = ll_permit_block.getLayoutParams();
        params.height = 0;
        ll_permit_block.setLayoutParams(params);

        Log.d(LOG_TAG, "MainActivity: getBackWithPermitBlock ");

        switch (data_type) {
            // add a new record
            case NEW_PERMIT_CODE:
                dep_lines_data_array.add( dep_line_data );
                break;
            // change the record
            case EDIT_PERMIT_CODE:
            case ADD_PERMIT_CODE:
                dep_lines_data_array.set( position_to_edit, dep_line_data );
                break;
            // delete the record
            case DATA_WAS_DELETED:
                dep_lines_data_array.remove( position_to_edit);
                break;
            // no change was made
            case DATA_WAS_NOT_CHANGED:
            case SHOW_PERMIT_CODE:
            default:
                refreshMainStatus(data_type);
                return;
        }
        //send changed data to server
        changeDataAndSendToServer();
    }

    private void setButtonsInterface (String button_visibility) {
        //Boolean mode = sharedPrefs.getBoolean(PREF_USER, false);
        switch (button_visibility) {
            case NET_ERROR_STATE:
            case URL_WAS_NOT_FOUND:
                bt_fill_permit.setVisibility(View.INVISIBLE);
                break;
            case EMPTY_STORAGE_STATE:
            default:
                bt_fill_permit.setVisibility(View.VISIBLE);
                break;
        }
    }

    // Change DepLinesData Array and send it to Server
    private void changeDataAndSendToServer() {

        ArrayList<String> dld_array_list = new ArrayList<>();
        //DepLinesData item_help;
        for (DepLinesData item: dep_lines_data_array) {
            String item_json = new Gson().toJson(item);
            dld_array_list.add(item_json);
        }

        String dld_array_json = new Gson().toJson(dld_array_list);
        getBackWithServer(SERVER_PUT_ALL, "", dld_array_json);

        // renew the lists of permits
        fillInPermitsUserMadeList();
        fillInPermitsAwaitingList();
    }

    // Fill In Permits User Made List
    private void fillInPermitsUserMadeList() {
        int[] to = { R.id.li_number, R.id.li_place, R.id.li_date_start, R.id.li_date_reg, R.id.li_approved };
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ArrayList<Integer> permit_array = new ArrayList();
        //data = new PermitData().getUserMadeListData(to, dep_lines_data_array, department_user);
        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dep_lines_data_array.get(i).getDepartMaster().equals(department_user)) {
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(from[0], dep_lines_data_array.get(i).getId());
                hashmap.put(from[1], dep_lines_data_array.get(i).getPlace());
                hashmap.put(from[2], dep_lines_data_array.get(i).getStringDateStart());
                hashmap.put(from[3], dep_lines_data_array.get(i).getStringDateReg());
                hashmap.put(from[4], dep_lines_data_array.get(i).getPermitApproved());
                data.add(hashmap);
                permit_array.add(i);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.main_list_item_1, from, to);
        setAdapterAndItemClickListener(adapter, R.id.lv_permits_user_made, permit_array);
    }

    // Fill In Permits Awaiting List
    private void fillInPermitsAwaitingList() {
        int[] to = { R.id.li_number2, R.id.li_depart2, R.id.li_place2, R.id.li_date_start2, R.id.li_comment2 };
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ArrayList<Integer> permit_array = new ArrayList();

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user department is required
            if (dep_lines_data_array.get(i).getHashmapRequired().get(department_user)) {
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(from[0], dep_lines_data_array.get(i).getId());
                hashmap.put(from[1], dep_lines_data_array.get(i).getDepartMaster());
                hashmap.put(from[2], dep_lines_data_array.get(i).getPlace());
                hashmap.put(from[3], dep_lines_data_array.get(i).getStringDateStart());
                hashmap.put(from[4], dep_lines_data_array.get(i).getComment());
                data.add(hashmap);
                permit_array.add(i);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.main_list_item_2, from, to);
        setAdapterAndItemClickListener(adapter, R.id.lv_permits_awaiting, permit_array);
    }

    private void setAdapterAndItemClickListener(SimpleAdapter adapter, int lv_id, ArrayList<Integer> permit_array) {
        ListView lv = findViewById(lv_id);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                position_to_edit = position;
                dep_line_data = dep_lines_data_array.get(permit_array.get(position));

                // Get info about chosen work permit
                String permit_code;

                if (lv_id == R.id.lv_permits_user_made) {
                    permit_code = EDIT_PERMIT_CODE;
                } else if (dep_line_data.getHashmapCommExist().get(department_user).equals(Approvement.UNKNOWN)) {
                    permit_code = ADD_PERMIT_CODE;
                } else {
                    permit_code = SHOW_PERMIT_CODE;
                }

                //launchPermitActivityForResult(dep_line_data, permit_code);
                runPermitBlock(permit_code);
            }
        });
    }

    private void buttonsMainSetOnClickListener() {
        // Fill out a work permit form
        bt_fill_permit.setOnClickListener(v -> {
            //launchPermitActivityForResult(getDepLinesDataItem(), NEW_PERMIT_CODE);
            dep_line_data = getDepLinesDataItem();
            runPermitBlock(NEW_PERMIT_CODE);
        });

        // Fill out a work permit form
        bt_check_request.setOnClickListener(v -> {
            dep_lines_data_array = new ArrayList<>();
            getBackWithServer(SERVER_GET_ALL, "", "");
        });
    }

    // Get New Department Item
    private DepLinesData getDepLinesDataItem() {
        int number;
        if (dep_lines_data_array.size() == 0) {
            number = 1;
        } else {
            String help_number = dep_lines_data_array.get(dep_lines_data_array.size()-1).getId();
            number = Integer.parseInt(help_number) + 1;
        }
        String et_number =Integer.toString(number);

        DepLinesData dep_line_data = new DepLinesData(
                et_number, null, department_user,  null, null,true, null,
                null, null, null,null
        );
        return dep_line_data;
    }

    /*private void launchPermitActivityForResult(DepLinesData dep_line_data_orig, String permit_code) {
        // a change for a depricated onActivityResult()
        String dep_line_data_json = new Gson().toJson(dep_line_data_orig);
        Intent permit_activity = new Intent(this, PermitActivity.class);
        permit_activity.putExtra(DEP_LINE_DATA, dep_line_data_json);
        permit_activity.putExtra(DATA_TYPE, permit_code);
        //startActivity(permit_activity);
        activityResultLaunch.launch(permit_activity);
    } */

    // ************* Start Permit Block ******************************
    private void runPermitBlock(String permit_code) {

        initPermitViewLayout(permit_code);

        setPermitFieldsVisibility(permit_code);
        if (!permit_code.equals(NEW_PERMIT_CODE)) {
            setPlaceDateComment ();
        }

        //Set visibility and onclick method of buttons
        buttonsPermitSetOnClickListener(permit_code);

        // check what type of permit is passed
        switch (permit_code) {
            case NEW_PERMIT_CODE:
                //set Adapter for defining list of required departments
                defineListOfRequiredDeps(permit_code);
                break;

            case EDIT_PERMIT_CODE:
                //show-edit data from a chosen permit number
                fillInDepsApproveList(permit_code);

            case ADD_PERMIT_CODE:
            case SHOW_PERMIT_CODE:
                refreshMainStatus(DATA_IS_READY);
                fillInDepsApproveList(permit_code);
                break;

            default:
                break;
        }
    }


    private void initPermitViewLayout(String permit_code) {
        ViewGroup.LayoutParams params = ll_permit_block.getLayoutParams();
        params.height = WRAP_CONTENT;
        ll_permit_block.setLayoutParams(params);

        tv_permit_state = findViewById(R.id.tv_permit_state);
        //tv_deps_approve_title = findViewById(R.id.tv_deps_approve_title);
        //tv_permit_depart = findViewById(R.id.tv_permit_depart);
        tv_permit_id = findViewById(R.id.tv_permit_id);
        et_permit_place = findViewById(R.id.et_permit_place);
        et_permit_date_start = findViewById(R.id.et_permit_date_start);
        et_permit_comment = findViewById(R.id.et_permit_comment);
        bt_put_show_comm = findViewById(R.id.bt_put_show_comm);
        bt_permit_save = findViewById(R.id.bt_permit_save);
        bt_deps_choose = findViewById(R.id.bt_deps_choose);
        bt_delete = findViewById(R.id.bt_delete);
        lv_deps_choose = findViewById(R.id.lv_deps_choose);
        lv_deps_approve = findViewById(R.id.lv_deps_approve);
        lv_deps_choose.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //put in Department  and ID
        tv_permit_id.setText(dep_line_data.getId());
    }

    private void setPlaceDateComment () {

        et_permit_place.setText(dep_line_data.getPlace());
        et_permit_date_start.setText(dep_line_data.getStringDateStart());
        et_permit_comment.setText(dep_line_data.getComment());
    }

    private void setPermitFieldsVisibility (String permit_code) {
        // Check for if a new permit
        switch (permit_code) {
            case NEW_PERMIT_CODE:
                bt_put_show_comm.setVisibility(View.INVISIBLE);
                bt_delete.setVisibility(View.INVISIBLE);
                break;

            case EDIT_PERMIT_CODE:
                bt_put_show_comm.setVisibility(View.VISIBLE);
                bt_deps_choose.setVisibility(View.INVISIBLE);
                break;

            case SHOW_PERMIT_CODE:
            case ADD_PERMIT_CODE:
                bt_delete.setVisibility(View.INVISIBLE);
                bt_deps_choose.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }
    }

    private void buttonsPermitSetOnClickListener(String permit_code) {

        // Show or put the communication lines on the map
        bt_put_show_comm.setOnClickListener(v -> {
            //checking for correct input in all the fields and check boxes
            if (!checkCorrectInputPlaceDate()) {
                return;
            } else {
                //Start MapsActivity
                String dep_line_data_json = new Gson().toJson(dep_line_data);
                Intent maps_activity = new Intent(this, MapsActivity.class);
                maps_activity.putExtra(DEP_LINE_DATA, dep_line_data_json);
                maps_activity.putExtra(DATA_TYPE, permit_code);
                activityResultLaunch.launch(maps_activity);
            }
        });

        // Edit required departments
        bt_deps_choose.setOnClickListener(v -> {
            // Check if Place and Date data are put in
            if (!checkCorrectInputPlaceDate()) {
                return;
            }

            LinearLayout ll_deps_choose_title = findViewById(R.id.ll_deps_choose_title);
            LinearLayout ll_permit_data = findViewById(R.id.ll_permit_data);
            ViewGroup.LayoutParams params = ll_deps_choose_title.getLayoutParams();
            String button_text;

            //Show listview
            if (bt_deps_choose.getText().equals(getResources().getString(R.string.bt_deps_choose_start))){
                button_text = getResources().getString(R.string.bt_deps_choose_end);
                params.height = WRAP_CONTENT;
                ll_deps_choose_title.setBackgroundColor(Color.parseColor("#e2e6e4"));
                lv_deps_choose.setVisibility(View.VISIBLE);
                ll_permit_data.setVisibility(View.INVISIBLE);
                bt_deps_choose.setBackground(ContextCompat.getDrawable(this, R.drawable.bt_bg_purple));
                bt_deps_choose.setTextColor(Color.WHITE);
            } else {//Hide listview
                // Check if correct departments are put in
                if (!checkCorrectInputChosenDeps()) {
                    return;
                }

                button_text = getResources().getString(R.string.bt_deps_choose_start);
                params.height = MATCH_PARENT;
                ll_deps_choose_title.setBackgroundColor(Color.BLACK);
                lv_deps_choose.setVisibility(View.INVISIBLE);
                ll_permit_data.setVisibility(View.VISIBLE);
                bt_deps_choose.setBackground(ContextCompat.getDrawable(this, R.drawable.bt_bg_green));
                bt_deps_choose.setTextColor(Color.BLACK);

                // Set "bt_put_show_comm" button visible
                setPermitFieldsVisibility(EDIT_PERMIT_CODE);
                // Fill in Department approvement List
                fillInDepsApproveList(permit_code);
            }
            ll_deps_choose_title.setLayoutParams(params);
            bt_deps_choose.setText(button_text);
        });

        // Delete permit
        bt_delete.setOnClickListener(v -> {
            dep_line_data = null;
            getBackWithPermitBlock(DATA_WAS_DELETED);
        });

        // Save the changes and exit
        bt_permit_save.setOnClickListener(v -> {
            // Check if Lines of a new permit are put in
            if ( code_after_map.equals(DATA_WAS_NOT_CHANGED) || code_after_map.equals(SHOW_PERMIT_CODE) ) {
                Toast.makeText(this, code_after_map, Toast.LENGTH_LONG).show();
            } else {
                getBackWithPermitBlock(permit_code);
            }
        });
    }

    //Show Department Line Data from server
    private void defineListOfRequiredDeps(String permit_code) {

        ArrayAdapter<String> depart_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, department_array);
        //depart_adapter = new MyArrayAdapter(this, R.layout.custom_list, android.R.id.text2, DEPARTMENT_ARRAY);
        lv_deps_choose.setAdapter(depart_adapter);
        lv_deps_choose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
                for (int i = 0; i < chosen.size(); i++) {
                    required_array[chosen.keyAt(i)] = chosen.valueAt(i);
                }
                //Log.d(LOG_TAG, "PermitActivity - onItemClick - required_array2: "+ required_array);

            }
        });
    }

    private boolean checkCorrectInputPlaceDate() {
        final String REQUIRED_ID_PLACE_DATE = "заполнить поля: '" + getResources().getString(R.string.et_place)
                + "', '" + getResources().getString(R.string.et_date_start);

        if ( et_permit_place.getText().toString().isEmpty() || et_permit_date_start.getText().toString().isEmpty() ) {
            Toast.makeText(this, FILL_IN_FIELDS + REQUIRED_ID_PLACE_DATE, Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }

    private boolean checkCorrectInputChosenDeps() {
        final String REQUIRED_DEPS_CHOSEN = "отметить подразделения для согласования в списке " +
                getResources().getString(R.string.tv_deps_approve_title);
        final String AUTHOR_MUST_NOT_BE_CHOSEN = "Подразделение(автор) наряда не должно быть отмечено";
        Boolean check_deps = false;
        // check if the fields departments are empty or inappropriate
        for (int i = 0; i < required_array.length; i++) {
            if  (required_array[i]) {
                //check for if department of user-author is unchecked
                if (department_array[i].equals(dep_line_data.getDepartMaster())) {
                    Toast.makeText(this, AUTHOR_MUST_NOT_BE_CHOSEN, Toast.LENGTH_LONG).show();
                    return false;
                } else
                    check_deps = true;
            }
        }
        if (!check_deps) {
            Toast.makeText(this, FILL_IN_FIELDS + REQUIRED_DEPS_CHOSEN, Toast.LENGTH_LONG).show();
        }
        return check_deps;
    }

    // Fill In Permits User Made List
    private void fillInDepsApproveList(String permit_code) {

        String[] from = {"0", "1", "2", "3", "4"};
        int[] to = { R.id.li_permit_depart, R.id.li_permit_required, R.id.li_permit_commun, R.id.li_permit_approvement, R.id.li_permit_date_approve };
        ArrayList<Map<String, String>> data = new ArrayList<>();
        Map<String, String> hashmap_adapter;

        // fill in fields for a new permit or edited permit
        if (permit_code.equals(NEW_PERMIT_CODE)) {
            initPermitFields(permit_code);
        } else {
            setPlaceDateComment();
        }

        // fill in permit hash map fields
        //for (int i = 0; i < department_array.length; i++) {
        for (String department: department_array) {

            //boolean required_depart = dep_line_data.getHashmapRequired().get(department_array[i]);
            boolean required_depart = dep_line_data.getHashmapRequired().get(department);

            // Check if the departments are chosen
            if (required_depart) {

                hashmap_adapter = new HashMap<>();

                hashmap_adapter.put(from[0], department);
                hashmap_adapter.put(from[1], String.valueOf(dep_line_data.getHashmapRequired().get(department)));
                hashmap_adapter.put(from[2], dep_line_data.getHashmapCommExist().get(department).getValue());
                hashmap_adapter.put(from[3], "n/a");
                hashmap_adapter.put(from[4], dep_line_data.getDateApproveHashmap().get(department));

                data.add(hashmap_adapter);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.permit_list_item, from, to);
        setAdapterAndItemClickListener(adapter);
    }

    private void initPermitFields( String permit_code ) {

        HashMap<String, Boolean> hashmap_required = new HashMap<>();
        HashMap<String, Approvement> hashmap_communication = new HashMap<>();
        HashMap<String, String> hashmap_date_approve = new HashMap<>();

        for (int i = 0; i < department_array.length; i++) {

            hashmap_required.put(department_array[i], required_array[i]);
            hashmap_communication.put(department_array[i], Approvement.UNKNOWN);
            hashmap_date_approve.put(department_array[i], Approvement.UNKNOWN.getValue());
        }

        dep_line_data.setPlace(et_permit_place.getText().toString());
        dep_line_data.setStringDateStart(et_permit_date_start.getText().toString());
        dep_line_data.setComment(et_permit_comment.getText().toString());

        dep_line_data.setHashmapRequired(hashmap_required);
        dep_line_data.setHashmapCommExist(hashmap_communication);
        dep_line_data.setDateApproveHashmap(hashmap_date_approve);

    }

    private void setAdapterAndItemClickListener( SimpleAdapter adapter ) {
        int lv_id = R.id.lv_deps_approve;
        ListView lv = findViewById(lv_id);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int lv_pos = ((ListView) parent).getCheckedItemPosition();
            }
        });
    }

    // ************* End Permit Block ******************************

    private void getBackWithServer(String command, String depID, String value) {
        new OkHttpRequest().serverGetback(this, command, depID, value);
        refreshMainStatus(DATA_REQUEST_PROCESSING);
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
                break;
            case R.id.version:
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.version), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshMainStatus(String status) {
        tv_main_state.setText(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "MainActivity: onResume ");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mainBroadcastReceiver);
        Log.d(LOG_TAG, "MainActivity: onDestroy ");
        super.onDestroy();
    }
}

        /* String[] from = {
                getResources().getString(R.string.lv_permit_number),
                getResources().getString(R.string.lv_place),
                getResources().getString(R.string.lv_date_start),
                getResources().getString(R.string.lv_date_reg),
                getResources().getString(R.string.lv_approved)
        }; */

        /*try {
            //mqttHelper.mqttAndroidClient.unsubscribe(PARENT_PHONE);
        } catch (MqttException ex) {
            System.err.println("Exception whilst UNsubscribing");
            ex.printStackTrace();
        } */

    /* protected void sendSMSMessage (String phoneNo, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "MainActivity: SMS sent to number: " + phoneNo + " with message: " + message);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Ошибка отправки запроса через СМС", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.d(LOG_TAG, "MainActivity: SMS failed. SMS Exception: " + e.toString());
        }
    } */

 /*    // Send a command to the Kid
    private void sendCommandToKid(String rq_command) {
        String kid_phone = sharedPrefs.getString(KID_PHONE, "" );
        if (kid_phone == null) {
            Toast.makeText(getApplicationContext(), "Сначала выберите в настройках номер телефона ребенка", Toast.LENGTH_LONG).show();
            return;
        }
        String parent_phone = sharedPrefs.getString(PARENT_PHONE, "" );
        String rq_message = COMMAND_BASE + STA_SIGN + rq_command + STA_SIGN + parent_phone + STA_SIGN + kid_phone;
        String status_mes = DATA_REQUEST_PROCESSING + ". Команда: " + rq_message;
        refreshStatus(status_mes);
        String rq_mode = sharedPrefs.getString(PREF_REQUEST, "");
        switch (rq_mode) {
            case REQUEST_NET:      // MQTT PUBLISH a message
                try {
                    MqttMessage message = new MqttMessage();
                    message.setPayload(rq_message.getBytes());
                    mqttHelper.mqttAndroidClient.publish(KID_PHONE, message);
                } catch (MqttException e) {
                    System.err.println("Error Publishing: " + e.getMessage());
                        Log.d(LOG_TAG,"Main - MQTT - onError: " + rq_command);
                    refreshStatus(NE_REQ_NOT_SENT);
                    e.printStackTrace();
                }
                break;
            case REQUEST_SERVER:    // SMS PUBLISH a message
            case REQUEST_SMS:
                 sendSMSMessage(kid_phone, rq_message);

                 int server_delay = Integer.parseInt(sharedPrefs.getString(SERVER_DELAY_TITLE, DEFAULT_SERVER_DATA_DELAY));
                 if (rq_mode.equals(REQUEST_SERVER)) {   // get the location from server in N seconds
                    Handler handler = new Handler();
                    handler.postDelayed(()-> getBackWithServer(SERVER_SINGLE_REQUEST, ""), server_delay * 1000);                 }

                break;
        }
    }*/

//Perform actions after reply came from Kid or Server
 /*   public void replyRecieved(String location_message){
        Log.d(LOG_TAG, "MainActivity: replyRecieved is worked, position is:  " + location_message);

        String [] complex_message = location_message.split(STA_SIGN);
        String status_state = complex_message[0];
        String source = SOURCE_SERVER;
        String loc_array []={""};
        showListLocations(null);
        switch (status_state) {
            // Data from Server
            case OK_STATE_PARENT:
                //Multiple records
                if (complex_message[1].startsWith("[")) {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    loc_array = gson.fromJson(complex_message[1], String[].class);
                // Single record
                } else {
                    source = ", источник: " + complex_message[complex_message.length-1];
                    loc_array[0] = complex_message[1];
                }
                showListLocations(loc_array);
                break;
            case EMPTY_STORAGE_STATE:
            case NET_ERROR_STATE:
                refreshStatus(status_state + source);
                break;
            case CONFIG_SERVER_STATE:
                refreshStatus(status_state + complex_message[1] + source);
                break;
            //Data from Kid
            case OK_STATE_KID:
                source = ", источник: " + complex_message[complex_message.length-1];
                refreshStatus(DATA_IS_READY + source);
                loc_array[0] = complex_message[1];
                showListLocations(loc_array);
                break;
            case NET_ERROR_GOT_LOCATION_STATE:
                source = ", источник: " + complex_message[complex_message.length-1];
                refreshStatus(DATA_IS_READY + NET_ERROR_STATE + source);
                loc_array[0] = complex_message[1];
                showListLocations(loc_array);
                break;
            case CONFIRM_CONNECTION:
                source = ", источник: " + complex_message[complex_message.length-1] + complex_message[complex_message.length-2];
                refreshStatus(status_state + source);
                break;
            case NO_CHANGE_STATE:
            case NO_LOCATION_FOUND_STATE:
            case LOCATION_IS_TURNED_OFF:
            default:
                source = ", источник: " + complex_message[complex_message.length-1];
                refreshStatus(status_state + source);
                break;
        }
    } */

    /*public void showListLocations(String [] loc_array) {
        ListView lvMain = findViewById(R.id.listView);
        if (loc_array == null) {
            lvMain.setVisibility(View.INVISIBLE);
            return;
        } else {
            lvMain.setVisibility(View.VISIBLE);
        }
        String[] from = { ATTRIBUTE_NAME_LAT, ATTRIBUTE_NAME_LONG, ATTRIBUTE_NAME_DATE,
                ATTRIBUTE_NAME_ACCU, ATTRIBUTE_NAME_BATT };
        int[] to = { R.id.list_lat, R.id.list_long, R.id.list_date, R.id.list_accu, R.id.list_batt };
        ArrayList<String []> record = new ArrayList<>();
        int loc_array_length = loc_array.length;
        for(int i=0; i < loc_array_length; i++) {
            String [] helper_mes = loc_array[i].split(REG_SIGN);
            //Check for if the Data of chosen Kid's phone
            record.add(helper_mes);
        }

        if (loc_array_length <2) {
            refreshStatus(DATA_IS_READY + record.get(0)[5]);
        } else {
            refreshStatus(DATA_IS_READY + SOURCE_SERVER);
        }

        ArrayList<Map<String, String>> data = new ArrayList<>(loc_array_length);
        Map<String, String> m;
        for (int i = 0; i < loc_array_length; i++) {
            m = new HashMap<>();
            m.put(ATTRIBUTE_NAME_LAT, record.get(i)[0]);
            m.put(ATTRIBUTE_NAME_LONG, record.get(i)[1]);
            m.put(ATTRIBUTE_NAME_DATE, record.get(i)[3]);
            m.put(ATTRIBUTE_NAME_ACCU, record.get(i)[2]);
            m.put(ATTRIBUTE_NAME_BATT, record.get(i)[4]);
            if (sharedPrefs.getBoolean(CHOSEN_KID_MARKERS, false)) {
                if (sharedPrefs.getString(KID_PHONE, "").equals(record.get(i)[5])) {
                    data.add(m);
                }
            } else {
                data.add(m);
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.list_item, from, to);
        lvMain.setAdapter(adapter);

        // Show markers on the map
        if (sharedPrefs.getBoolean(BROWSER_MODE, false)) {
            Intent maps_activity = new Intent(this, MapsActivity.class);
            String dataAsJson = new Gson().toJson(data);
            maps_activity.putExtra("loc_data", dataAsJson);
            startActivity(maps_activity);
        }
    } */


                        /*Handler handler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = () -> {
                            try {
                                replyRecieved (loc_mes);
                            } catch (Exception e) {
                                Log.d(LOG_TAG, "Main: Handler: Ably_message EXCEPTION: " + e.toString());
                            }
                        };
                        handler.post(myRunnable); */


//Init MQTT;
        /*

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

        String id_mqtt = sharedPrefs.getString(PARENT_PHONE, "" ) + System.currentTimeMillis();;
        Log.d(LOG_TAG,"Main: MQTT Id: " + id_mqtt);
        mqttHelper = new MqttHelper(getApplicationContext(), PARENT_PHONE, id_mqtt);
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.d(LOG_TAG,"Main: MQTT * " +"Connected: " + s);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.d(LOG_TAG,"Main: MQTT * "+"Connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.d(LOG_TAG,"Main: MQTT: messageArrived * "+ mqttMessage.toString());
                replyRecieved (mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        }); */