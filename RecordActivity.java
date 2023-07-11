package ru.volganap.nikolay.haircut_schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class RecordActivity extends AppCompatActivity implements KM_Constants, Enums, Contract.ViewRecord, Contract.ActivityReciever {

    private SharedPreferences sharedPrefs;

    private BroadcastReceiver recordBroadcastReceiver;

    private EditText et_client_name, et_client_phone, et_date, et_time, et_duration, et_job, et_price, et_record_comment;
    private Button bt_save, bt_change, bt_del_rec, bt_exit, bt_show_client_job, bt_add_from_book;

    private CheckBox cb_send_notif;
    private ListView lv_records_list;

    Contract.PresenterRecord presenterRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
        Intent intent = getIntent();
        String date = intent.getStringExtra(ADD_CODE);
        initRecordViewLayout(date);

        presenterRecord = new PresenterRecord(this, this);

        // Init BroadcastReceiver
        initBroadcastReceiver();
        //setRecordButtonsVisibility(DATA_WAS_NOT_CHANGED);
    }

    private void initRecordViewLayout(String date) {
        // Record Layout
        et_client_name = findViewById(R.id.et_client_name);
        et_client_phone = findViewById(R.id.et_client_phone);
        et_date = findViewById(R.id.et_date);
        et_date.setText(date);
        et_time = findViewById(R.id.et_time);
        et_duration = findViewById(R.id.et_duration);
        et_job = findViewById(R.id.et_job);
        et_price = findViewById(R.id.et_price);
        et_record_comment = findViewById(R.id.et_record_comment);

        bt_save = findViewById(R.id.bt_save);
        bt_change = findViewById(R.id.bt_change);
        bt_exit = findViewById(R.id.bt_exit);
        bt_del_rec = findViewById(R.id.bt_del_rec);
        bt_show_client_job = findViewById(R.id.bt_show_client_job);
        bt_add_from_book = findViewById(R.id.bt_add_from_book);

        cb_send_notif = findViewById(R.id.cb_send_notif);

        //lv_records_list = findViewById(R.id.lv_records_list);
        //lv_records_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    public void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        //Set filter by Class name
        filter.addAction(getClass().getSimpleName());
        //filter.addAction(PREF_ACTIVITY);
        recordBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    presenterRecord.onBroadcastReceive(intent);
                }
            }
        };
        registerReceiver(recordBroadcastReceiver, filter);
        // Set visibility and onclick method of buttons
        buttonsSetOnClickListener();
    }
/*
    @Override
    public void fillInJobsList(ArrayList<Map<String, String>> data) {

        int[] to = { R.id.li_rec_time, R.id.li_rec_job, R.id.li_rec_name, R.id.li_rec_comment };

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.main_list_item_1, FROM, to);
        setAdapterAndItemClickListener(adapter, R.id.lv_records_list);
    }
*/
    @Override
    public void fillRecordInfoFields ( ArrayList<String> value ) {

        et_client_name.setText(value.get(0));
        et_client_phone.setText(value.get(1));
        et_date.setText(value.get(2));
        et_time.setText(value.get(3));
        et_duration.setText(value.get(4));
        et_job.setText(value.get(5));
        et_price.setText(value.get(6));
        et_record_comment.setText(value.get(7));
        cb_send_notif.setChecked(Boolean.parseBoolean(value.get(8)));

    }
    private void setAdapterAndItemClickListener(SimpleAdapter adapter, int lv_id) {
        ListView lv = findViewById(lv_id);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                presenterRecord.onRecordListViewItemClick( position, lv_id, R.id.lv_records_list );
            }
        });
    }

    @Override
    public void setRecordButtonsVisibility (String permit_code) {
        // Check for if a new permit
        switch (permit_code) {
            case ADD_CODE:

                bt_save.setVisibility(View.VISIBLE);
                bt_change.setVisibility(View.INVISIBLE);
                bt_del_rec.setVisibility(View.INVISIBLE);
                bt_exit.setVisibility(View.VISIBLE);
                bt_show_client_job.setVisibility(View.VISIBLE);
                bt_add_from_book.setVisibility(View.VISIBLE);
                break;

            case CHANGE_CODE:
            case DELETE_CODE:

                bt_save.setVisibility(View.INVISIBLE);
                bt_change.setVisibility(View.VISIBLE);
                bt_del_rec.setVisibility(View.VISIBLE);
                bt_exit.setVisibility(View.VISIBLE);
                bt_show_client_job.setVisibility(View.VISIBLE);
                bt_add_from_book.setVisibility(View.VISIBLE);
                break;

            //case EMPTY_STORAGE_STATE:
            //case DATA_IS_READY:
            //case DATA_WAS_NOT_CHANGED:
            //case DATA_WAS_SAVED:
            //case DATA_WAS_DELETED:
            default:
                bt_save.setVisibility(View.INVISIBLE);
                bt_change.setVisibility(View.VISIBLE);
                bt_del_rec.setVisibility(View.VISIBLE);
                bt_exit.setVisibility(View.VISIBLE);
                bt_show_client_job.setVisibility(View.VISIBLE);
                bt_add_from_book.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void buttonsSetOnClickListener() {

        // save new record
        bt_save.setOnClickListener(v -> {
            ArrayList<String> rec_data  = checkCorrectInput();
            if (rec_data != null) {
                presenterRecord.onButtonSave( rec_data );
            }
        });

        // delete record
        bt_del_rec.setOnClickListener(v -> {
            presenterRecord.onButtonDeleteRecord();
        });

        // change record
        bt_change.setOnClickListener(v -> {
            ArrayList<String> rec_data  = checkCorrectInput();
            if (rec_data != null) {
                presenterRecord.onButtonChangeRecord( rec_data );
            }
        });

        // Exit a programm
        bt_exit.setOnClickListener(v -> {
            presenterRecord.onButtonExit();
        });

        // show a list of jobs for a client
        bt_show_client_job.setOnClickListener(v -> {
            presenterRecord.onButtonShowClientJob();

        });

        // add a client number from a phone book
        bt_add_from_book.setOnClickListener(v -> {
            presenterRecord.onButtonAddFromBook();
        });
    }

    private ArrayList<String> checkCorrectInput() {

        ArrayList<String> rec_data  = new ArrayList<>();
        boolean correct_input = true;

        rec_data.add(et_client_name.getText().toString());
        rec_data.add(et_client_phone.getText().toString());
        rec_data.add(et_date.getText().toString());
        rec_data.add(et_time.getText().toString());
        rec_data.add(et_duration.getText().toString());
        rec_data.add(et_job.getText().toString());
        rec_data.add(et_price.getText().toString());
        rec_data.add(et_record_comment.getText().toString());

        for (int i = 0; i < rec_data.size()-1; i++) {
            if (rec_data.get(i).isEmpty()) {
                correct_input = false;
            }
        }

        if ( correct_input ) {
            rec_data.add(Boolean.toString(cb_send_notif.isChecked()));
            return rec_data;
        } else {

            final String FILL_IN_FIELDS ="Необходимо заполнить поля: "
                    + "'" + getResources().getString(R.string.tv_client_name) + "', '"
                    + getResources().getString(R.string.tv_client_phone) + "', '"
                    + getResources().getString(R.string.tv_date) + "', '"
                    + getResources().getString(R.string.tv_time) + "', '"
                    + getResources().getString(R.string.tv_duration) + "', '"
                    + getResources().getString(R.string.tv_job) + "', '"
                    + getResources().getString(R.string.tv_job_price) + "'";
            Toast.makeText(this, FILL_IN_FIELDS, Toast.LENGTH_LONG).show();
            return null;
        }

        /*
        et_client_name.getText().toString().isEmpty() || et_client_phone.getText().toString().isEmpty() ||
                et_date.getText().toString().isEmpty() || et_time.getText().toString().isEmpty() ||
                et_duration.getText().toString().isEmpty() || et_job.getText().toString().isEmpty() ||
                et_price.getText().toString().isEmpty()
         */
    }

    @Override
    public void showToast(String status) {
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishRecordActivity() {
        finish();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "RecordActivity: onDestroy ");
        unregisterReceiver(recordBroadcastReceiver);
        super.onDestroy();
        presenterRecord.onDestroy();
    }
}