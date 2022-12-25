package ru.volganap.nikolay.excavate_coordinator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PermitActivity extends AppCompatActivity implements KM_Constants {
    final String FILL_IN_FIELDS ="Необходмо ";
    final String COMMUNICATIONS_ARE_DEFINED ="Коммуникации внесены в базу данных наряда, сохраните изменения.";
    final String COMMUNICATIONS_ARE_NOT_DEFINED ="Не отмечены коммуникации. Нажмите 'Указать коммуникации'";
    final String START_COMM_MESSAGE ="Наличие коммуникаций подразделения: ";
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

                        switch (code_after_map) {

                            case ADD_PERMIT_CODE:
                            case EDIT_PERMIT_CODE:
                                fillInDepsApproveList(code_after_map);
                                refreshPermitStatus(COMMUNICATIONS_ARE_DEFINED);

                            case NEW_PERMIT_CODE:
                                refreshPermitStatus(COMMUNICATIONS_ARE_DEFINED);

                            case SHOW_PERMIT_CODE:
                            case DATA_WAS_NOT_CHANGED:
                            default:
                                refreshPermitStatus(code_after_map);
                                break;
                        }

                    }
                }
            });

    private Button bt_put_show_comm, bt_permit_save, bt_deps_choose, bt_delete;
    private SharedPreferences sharedPrefs;
    private String[] department_array;
    private String code_after_map = DATA_WAS_NOT_CHANGED;
    private DepLinesData dep_line_data;
    // Permit activity layout
    private TextView tv_permit_state, tv_permit_depart, tv_permit_id;
    private EditText et_permit_place, et_permit_date_start, et_permit_comment;
    private LinearLayout ll_required_department_edit;
    private ListView lv_deps_choose, lv_deps_approve;
    //private MyArrayAdapter depart_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permit);
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
        department_array = getResources().getStringArray(R.array.department_values);

        Log.d(LOG_TAG, "PermitActivity: onCreate ");
        // Init Permit activity layout

        String dep_line_data_json = getIntent().getStringExtra(DEP_LINE_DATA);
        //Log.d(LOG_TAG, "PermitActivity: onCreate - dld_json = " + dep_line_data_json);

        String permit_code = getIntent().getStringExtra(DATA_TYPE);
        dep_line_data = new Gson().fromJson(dep_line_data_json, DepLinesData.class);

        required_array  = new boolean[department_array.length];

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
                //defineListOfRequiredDeps(permit_code);
                fillInDepsApproveList(permit_code);

            case ADD_PERMIT_CODE:
            case SHOW_PERMIT_CODE:
                refreshPermitStatus(DATA_IS_READY);
                fillInDepsApproveList(permit_code);
                break;

            default:
                break;
        }
    }

    private void initPermitViewLayout(String permit_code) {
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
        tv_permit_depart.setText(dep_line_data.getDepartMaster());
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
            sendResultToMainActivity(DATA_WAS_DELETED);
        });

        // Save the changes and exit
        bt_permit_save.setOnClickListener(v -> {
            // Check if Lines of a new permit are put in
           if ( code_after_map.equals(DATA_WAS_NOT_CHANGED) || code_after_map.equals(SHOW_PERMIT_CODE) ) {
               Toast.makeText(this, code_after_map, Toast.LENGTH_LONG).show();
           } else {
                sendResultToMainActivity(permit_code);
           }
        });
    }

    //Send result about a permit to Main Activity
    private void sendResultToMainActivity(String code) {
        Intent back_intent = getIntent();
        back_intent.putExtra(DATA_TYPE, code);
        String dep_line_data_json = new Gson().toJson(dep_line_data);
        back_intent.putExtra(DEP_LINE_DATA, dep_line_data_json);
        setResult(PERMIT_ACTIVITY_REQUEST_CODE, back_intent);
        finish();
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
                    required_array[chosen.keyAt(i)]=chosen.valueAt(i);
                }
                //Log.d(LOG_TAG, "PermitActivity - onItemClick - required_array2: "+ required_array);

            }
        });

        /* if (permit_code.equals(EDIT_PERMIT_CODE)) {
            for (int i = 0; i < department_array.length; i++) {
                if (dep_line_data.getHashmapRequired().get(department_array[i])) {
                    //lv_deps_choose.setSelection(i);
                    lv_deps_choose.setItemChecked(i, true);
                    //required_array[i]=true;
                }
            }
        } */

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
                //String dld_id = dep_lines_data_array.get(lv_pos).getId();
                //DepLinesData dep_line_data = dep_lines_data_array.get(lv_pos);
                //required_array[chosen.keyAt(i)]=chosen.valueAt(i);

                // Get info about chosen work permit
                //launchPermitActivityForResult(dep_line_data);
            }
        });
    }

    public void refreshPermitStatus(String status) {
        tv_permit_state.setText(status);
    }

   @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "PermitActivity: onDestroy ");
        super.onDestroy();
    }
}

           /*ArrayAdapter<CharSequence> depart_adapter = ArrayAdapter.createFromResource(this, R.array.department_values,
                    android.R.layout.simple_list_item_multiple_choice);*/

    /* //convert TextView to Date
    public Date convertStringtoDate(String startDateString) {
        Date start_date = new Date();
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");  // same date format as your TextView supports
        try {
            start_date = simpleDateFormat.parse(startDateString); // parses the string date to get a date object
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return start_date;
    } */



/*class MyArrayAdapter extends ArrayAdapter<CharSequence> {

    private HashMap<Integer, Boolean> mCheckedMap = new HashMap<>();
    Context context;
    String[] objects;

    MyArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {

        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        for (int i = 0; i < objects.length; i++) {
            mCheckedMap.put(i, false);
        }
    }

     void toggleChecked(int position) {
        if (mCheckedMap.get(position)) {
            mCheckedMap.put(position, false);
        } else {
            mCheckedMap.put(position, true);
        }

        notifyDataSetChanged();
    }

    public List<Integer> getCheckedItemPositions() {
        List<Integer> checkedItemPositions = new ArrayList<>();

        for (int i = 0; i < mCheckedMap.size(); i++) {
            if (mCheckedMap.get(i)) {
                (checkedItemPositions).add(i);
            }
        }

        return checkedItemPositions;
    }

    List<String> getCheckedItems() {
        List<String> checkedItems = new ArrayList<>();

        for (int i = 0; i < mCheckedMap.size(); i++) {
            if (mCheckedMap.get(i)) {
                (checkedItems).add(objects[i]);
            }
        }

        return checkedItems;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_list, parent, false);
        }

        CheckedTextView checkedTextView = row.findViewById(R.id.ctv_custom);
        checkedTextView.setText(objects[position]);

        // perform on Click Event Listener on CheckedTextView
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    checkedTextView.setCheckMarkDrawable(R.drawable.unchecked_04);
                    checkedTextView.setChecked(false);
                } else {
                    checkedTextView.setCheckMarkDrawable(R.drawable.checked_04);
                    checkedTextView.setChecked(true);
                }
                toggleChecked(position);
            }
        });

        return row;
    }
}

*/