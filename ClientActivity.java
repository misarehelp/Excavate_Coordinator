package ru.volganap.nikolay.haircut_schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import static ru.volganap.nikolay.haircut_schedule.KM_Constants.LOG_TAG;

public class ClientActivity extends AppCompatActivity {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    // Client activity layout
    //private EditText et_permit_place, et_permit_date_start, et_permit_date_end, et_permit_comment;
    private Button bt_prev_day, bt_next_day, bt_add_rec, bt_add_client, bt_exit, bt_show_clients;
    private ListView lv_records_list;

    //LinearLayout ll_permit_data;
    //ViewGroup.LayoutParams params;

    private BroadcastReceiver mainBroadcastReceiver;
    Contract.PresenterMain presenterMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Log.d(LOG_TAG, "Client - onCreate ");
        // instantiating object of Presenter Interface
        //presenterMain = new PresenterMain(this,this);
    }
}