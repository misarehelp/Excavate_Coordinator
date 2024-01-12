package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class RecordFragment extends Fragment implements Constants, Enums, AdapterView.OnItemSelectedListener, Contract.ViewRecord,
                 Contract.RecordActivityToRecordFragment, Contract.RecordActivityToFragmentBroadcast, Contract.RecordActivityToSomeFragment {

    private static String INPUT_CORRECT_NUMBER = "Нужно ввести корректный номер";
    private static String INPUT_CORRECT_NAME_NUMBER = "Нужно ввести корректные имя и номер телефона";
    private static String INPUT_CORRECT_DATE = "Дата должна быть в формате ДД.ММ.ГГГГ, например: 06.01.2024 ";
    private static String DOUBTABLE_STAMP = "Поставлена метка ненадежного клиента, ";
    private static String NOT_DOUBTABLE_STAMP = "Снята метка ненадежного клиента, ";
    private static String NEED_TO_SAVE = "для внесения изменений необходимо нажать <Изменить>";
    private static String NEED_TO_WAIT = "...необходимо дождаться сохранения данных на сервере...";
    private static String FIRST_SAVE_RECORD = "Сначала сохраните запись, затем, после повторного входа, можно добавить фото";
    private Contract.RecordFragmentToRecordActivity callbackToActivity;

    Contract.PresenterRecord presenterRecord;
    private Context context;
    private String date, time, index, type, filename;

    private LinearLayout ll_client_phone, ll_job, ll_price, ll_comment, ll_text_sms;
    private ImageView img_last_call, img_send_sms, img_call_client, img_check_client;
    private EditText et_client_name, et_client_phone, et_date, et_time, et_price, et_record_comment, et_sms;
    private Button bt_save, bt_change, bt_del_rec, bt_add_cam_photo, bt_add_file_photo, bt_delete_photo, bt_exit, bt_sms, bt_put_data_to_base;
    private TextView tv_client_name, tv_sms, tv_record_state;
    private CheckBox cb_photo;
    private Spinner job_spinner, duration_spinner;
    private String job_type = "";
    private String duration_value = "";
    private int client_id = NOT_IN_CLIENT_BASE;
    private byte byte_id;

    public RecordFragment ( String index, String date, String time, String type ) {
        this.index = index;
        this.date = date;
        this.time = time;
        this.type = type;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            callbackToActivity = (Contract.RecordFragmentToRecordActivity) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement Contract.RecordFragmentToRecordActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.record_holder, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_sms = view.findViewById(R.id.tv_sms);
        tv_record_state = view.findViewById(R.id.tv_record_state);

        job_spinner = view.findViewById(R.id.job_spinner);
        duration_spinner = view.findViewById(R.id.duration_spinner);

        et_client_name = view.findViewById(R.id.et_client_name);
        et_client_phone = view.findViewById(R.id.et_client_phone);
        et_price = view.findViewById(R.id.et_price);
        et_date = view.findViewById(R.id.et_date);
        et_sms = view.findViewById(R.id.et_sms);
        et_date.setText(date);
        et_time = view.findViewById(R.id.et_time);
        et_time.setText(time);
        et_record_comment = view.findViewById(R.id.et_record_comment);

        bt_save = view.findViewById(R.id.bt_save);
        bt_change = view.findViewById(R.id.bt_change);
        bt_del_rec = view.findViewById(R.id.bt_del_rec);
        bt_add_cam_photo = view.findViewById(R.id.bt_add_cam_photo);
        bt_add_file_photo = view.findViewById(R.id.bt_add_file_photo);
        bt_delete_photo = view.findViewById(R.id.bt_delete_photo);
        bt_exit = view.findViewById(R.id.bt_exit);
        bt_sms = view.findViewById(R.id.bt_sms);
        bt_put_data_to_base = view.findViewById(R.id.bt_put_data_to_base);
        bt_put_data_to_base.setVisibility(View.GONE);

        cb_photo = view.findViewById(R.id.cb_photo);

        img_last_call = view.findViewById(R.id.img_last_call);
        img_call_client = view.findViewById(R.id.img_call_client);
        img_check_client = view.findViewById(R.id.img_check_client);
        img_send_sms = view.findViewById(R.id.img_send_sms);

        ll_comment = view.findViewById(R.id.ll_comment);
        ll_client_phone = view.findViewById(R.id.ll_client_phone);
        ll_job = view.findViewById(R.id.ll_job);
        ll_price = view.findViewById(R.id.ll_price);
        ll_text_sms = view.findViewById(R.id.ll_text_sms);

        initFullRecordViewLayout();

        if (type != null && type.equals(INDEX_NOTE)) {
            initNoteRecordViewLayout();
            et_client_phone.setText(INDEX_NOTE);
            setRecordButtonsVisibility(INDEX_NOTE);
        }

        if ( type != null && type.equals(INDEX_FREE_RECORD) ) {
            setClientHasPictureField(false);
        }

        presenterRecord = new PresenterRecord( context, this);

        if (index.equals(INDEX_SET_ON_HOLIDAY)) {
            RecordData rd = new RecordData();
            byte_id = changeIndexBit(byte_id, BIT_HOLIDAY, true);
            rd.setBitsIndex(byte_id);
            rd.setDate(date);
            rd.setTime(time);
            rd.setId(NOT_IN_CLIENT_BASE);
            rd.setName("");
            rd.setPhone("");
            rd.setDuration("30");
            rd.setJob("");
            rd.setPrice("");
            rd.setComment("");
            presenterRecord.onButtonChangeRecord( rd, SERVER_MARK_HOLIDAY );
        }

        if (index.equals(INDEX_SET_OFF_HOLIDAY)) {
            RecordData rd = new RecordData();
            rd.setDate(date);
            rd.setTime(time);
            //byte_id = changeIndexBit(byte_id, BIT_HOLIDAY, true);
            presenterRecord.onButtonChangeRecord( rd, SERVER_UNMARK_HOLIDAY );
        }

        buttonsSetOnClickListener();
    }

    @Override
    public void onPhotoFragmentViewCreatedToRecord() {
        if (cb_photo.isChecked()) {
            // to load Saved Picture of a client
            checkCorrectNamePhoneInput(PhotoType.REPOSITORY);
        }
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        presenterRecord.onBroadcastReceive(intent);
    }

    @Override
    public void onGetClientDataToFragment( ClientData value) {
        et_client_name.setText( value.getName() );
        et_client_phone.setText( value.getPhone());
        client_id = value.getId();
    }

    @Override
    public void onLoadPictureResult( boolean done ) {
        String text = done ? (getResources().getString(R.string.photo_yes)) : (getResources().getString(R.string.photo_no));
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

        setClientHasPictureField(done);
    }

    public void setClientHasPictureField(boolean has_picture) {
        cb_photo.setChecked(has_picture);
        String text;
        if (has_picture) {
            text = getResources().getString(R.string.photo_yes);
            bt_delete_photo.setVisibility(View.VISIBLE);
        } else {
            text = getResources().getString(R.string.photo_no);
            bt_delete_photo.setVisibility(View.GONE);
        }

        cb_photo.setText(text);
    }


    private void initFullRecordViewLayout() {
        // Full Record Layout
        ArrayAdapter<String> job_spin_adapter = new ArrayAdapter<>( context, R.layout.spinner_item, getResources().getStringArray(R.array.job_type_values));
        job_spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        job_spinner.setAdapter(job_spin_adapter);
        job_spinner.setOnItemSelectedListener( this );
        job_spinner.setSelection(0);

        ArrayAdapter<String> duration_spin_adapter = new ArrayAdapter<>(context, R.layout.spinner_item, getResources().getStringArray(R.array.duration_entries));
        duration_spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        duration_spinner.setAdapter(duration_spin_adapter);
        duration_spinner.setOnItemSelectedListener(this);
        duration_spinner.setSelection(0);
        ll_text_sms.setVisibility(View.GONE);
    }

    private void initNoteRecordViewLayout() {
        //Note Record Layout
        tv_client_name.setText(getResources().getString(R.string.tv_note_to_do));
        et_client_name.setLines(3);
        job_type = INDEX_NOTE;

        ll_comment.setVisibility(View.GONE);
        ll_client_phone.setVisibility(View.GONE);
        ll_job.setVisibility(View.GONE);
        ll_price.setVisibility(View.GONE);
        cb_photo.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (parent.getId()) {
            case R.id.job_spinner:
                job_type = Integer.toString(position);
                break;
            case R.id.duration_spinner:
                String [] duration_array = getResources().getStringArray(R.array.duration_values);
                duration_value = duration_array[position];
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //
    }

    // Fill In Records fields
    @Override
    public void fillRecordInfoFields ( RecordData value ) {

        et_date.setText(value.getDate());
        et_time.setText(value.getTime());
        et_client_name.setText(value.getName());

        String duration_value = value.getDuration();
        int pos = Arrays.asList(getResources().getStringArray(R.array.duration_values)).indexOf(duration_value);
        duration_spinner.setSelection(pos);

        if (type.equals(INDEX_NOTE)) {
            initNoteRecordViewLayout();

        } else {

            et_client_phone.setText( value.getPhone());
            job_spinner.setSelection(Integer.parseInt(value.getJob()));
            et_price.setText(value.getPrice());
            et_record_comment.setText(value.getComment());

            byte_id = value.getBitsIndex();
            boolean state = value.getIndexBit(byte_id, BIT_HAS_PIC);

            setClientHasPictureField(state);

            if (value.getIndexBit(byte_id,BIT_REMIND_SENT)) {
                img_send_sms.setImageResource(R.drawable.sms_sent);
                tv_sms.setText(R.string.tv_sms_was_sent);
            }

            client_id = value.getId();

            if ( value.getIndexBit(byte_id, BIT_QUESTION) ) {
                img_check_client.setImageResource(R.drawable.bad_client);
            }
        }
    }

    @Override
    public void setRecordButtonsVisibility (String code) {
        //
        tv_record_state.setText("");
        switch ( code) {
            case SERVER_ADD_RECORD:
                bt_save.setVisibility(View.VISIBLE);
                bt_change.setVisibility(View.INVISIBLE);
                bt_del_rec.setVisibility(View.INVISIBLE);
                img_call_client.setVisibility(View.GONE);
                img_send_sms.setVisibility(View.GONE);
                img_check_client.setVisibility(View.GONE);
                break;

            case SERVER_CHANGE_RECORD:
            case SERVER_DELETE_RECORD:

                bt_change.setVisibility(View.VISIBLE);
                bt_del_rec.setVisibility(View.VISIBLE);
                bt_save.setVisibility(View.INVISIBLE);
                break;

            case INDEX_NOTE:
                img_last_call.setVisibility(View.GONE);
                bt_add_cam_photo.setVisibility(View.GONE);
                bt_add_file_photo.setVisibility(View.GONE);
                bt_delete_photo.setVisibility(View.GONE);
                img_check_client.setVisibility(View.GONE);
                break;

            case SERVER_WAIT_FOR_ANSWER:
                bt_exit.setVisibility(View.INVISIBLE);
                tv_record_state.setText(NEED_TO_WAIT);

            case SERVER_SHOW_RECORD:
            default:
                bt_exit.setVisibility(View.VISIBLE);
                bt_save.setVisibility(View.INVISIBLE);
                bt_change.setVisibility(View.INVISIBLE);
                bt_del_rec.setVisibility(View.INVISIBLE);
                img_last_call.setVisibility(View.GONE);
                img_call_client.setVisibility(View.GONE);
                img_send_sms.setVisibility(View.GONE);
                img_check_client.setVisibility(View.GONE);
                bt_add_cam_photo.setVisibility(View.GONE);
                bt_add_file_photo.setVisibility(View.GONE);
                bt_delete_photo.setVisibility(View.GONE);
                break;
        }
    }

    private void buttonsSetOnClickListener() {
        // save new record
        bt_save.setOnClickListener(v -> {
            RecordData  rec_data  = getInputFields(false);
            if (checkCorrectDateInput(rec_data.getDate()))
                presenterRecord.onButtonChangeRecord( rec_data, SERVER_ADD_RECORD );
        });

        // change record
        bt_change.setOnClickListener(v -> {
            RecordData  rec_data  = getInputFields(false);
            if (checkCorrectDateInput(rec_data.getDate()))
                presenterRecord.onButtonChangeRecord( rec_data, SERVER_CHANGE_RECORD );
        });

        // delete record
        bt_del_rec.setOnClickListener(v -> {
            presenterRecord.onButtonChangeRecord( null, SERVER_DELETE_RECORD );
        });

        // add photo from cam to storage
        bt_add_cam_photo.setOnClickListener(v -> {
            checkCorrectNamePhoneInput(PhotoType.CAMERA);
        });

        // add photo from file to storage
        bt_add_file_photo.setOnClickListener(v -> {
            checkCorrectNamePhoneInput(PhotoType.GALLERY);
        });

        // delete photo from storage
        bt_delete_photo.setOnClickListener(v -> {
            if (cb_photo.isChecked()) {
                callbackToActivity.deletePictureFile(filename);
            } else
                showToast( "No picture saved for the client " );
            //
        });

        // to exit from Record Activity
        bt_exit.setOnClickListener(v -> {
            presenterRecord.onButtonExit();
        });

        // add a client number from a phone book
        img_last_call.setOnClickListener(v -> {
            presenterRecord.onButtonAddLastCall();
        });

        // add a client number from a phone book
        img_check_client.setOnClickListener(v -> {
            if ( new RecordData().getIndexBit(byte_id, BIT_QUESTION) ) {

                byte_id = changeIndexBit(byte_id, BIT_QUESTION, false);
                img_check_client.setImageResource(R.drawable.good_client);
                showToast(NOT_DOUBTABLE_STAMP + NEED_TO_SAVE);

            } else {
                byte_id = changeIndexBit(byte_id, BIT_QUESTION, true);
                img_check_client.setImageResource(R.drawable.bad_client);
                showToast(DOUBTABLE_STAMP + NEED_TO_SAVE );
            }
        });

        // add a client number from a phone book
        img_call_client.setOnClickListener(v -> {
            presenterRecord.onButtonMakeCall(et_client_phone.getText().toString());
        });

        // send a notification sms to a client number
        img_send_sms.setOnClickListener(v -> {

            if ( !index.equals(INDEX_FREE_RECORD) ) {
                et_sms.setText(getResources().getString(R.string.default_sms) + " " + et_date.getText() + ", в " + et_time.getText());
                int ll_sms_visible = ll_text_sms.getVisibility();
                if ( ll_sms_visible == View.GONE ) ll_text_sms.setVisibility(View.VISIBLE);
                    else ll_text_sms.setVisibility(View.GONE);
            }
        });

        // send SMS to a client
        bt_sms.setOnClickListener(v -> {
            String number = et_client_phone.getText().toString();
            if ( number.trim().length() == 0 ) {
                showToast(INPUT_CORRECT_NUMBER );
            } else {
                presenterRecord.onButtonSendSms(getInputFields(true), et_sms.getText().toString());
            }
        });

        // show visibility of Button "Put Data to Client Base"
        tv_client_name.setOnClickListener(v -> {
            if (type != null && !type.equals(INDEX_NOTE)) {
                int vis = (bt_put_data_to_base.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                bt_put_data_to_base.setVisibility(vis);
            }
        });

        // Put Data to Client Base
        bt_put_data_to_base.setOnClickListener(v -> {
            if ( isNamePhoneFull ()) {
                callbackToActivity.passClientDataToActivity( et_client_name.getText().toString(), et_client_phone.getText().toString() );
            }
        });
    }

    private void checkCorrectNamePhoneInput( Enums.PhotoType picture_type) {
        if (index.equals(INDEX_FREE_RECORD)) {
            showToast(FIRST_SAVE_RECORD );
            return;
        }

        if ( isNamePhoneFull ()) {
            filename = et_client_name.getText().toString() + "-" + et_client_phone.getText().toString() + "-" + date;
            callbackToActivity.doPictureAction(picture_type, filename);
        }
    }

    private boolean checkCorrectDateInput( String date) {
        if (date.contains(".") && date.length() == 10) {
            return true;
        } else {
            showToast(INPUT_CORRECT_DATE );
            return false;
        }
    }

    private boolean isNamePhoneFull () {
        if ( et_client_phone.getText().toString().trim().length() == 0 || et_client_name.getText().toString().trim().length() == 0 ) {
            showToast(INPUT_CORRECT_NAME_NUMBER );
            return false;
        } else
            return true;
    }

    // Read data of Input fields
    private RecordData  getInputFields( boolean remind) {

        RecordData rd = new RecordData();
        rd.setId(client_id);

        rd.setName(et_client_name.getText().toString());
        rd.setPhone(et_client_phone.getText().toString());
        rd.setDate(et_date.getText().toString());
        rd.setTime(et_time.getText().toString());
        rd.setDuration(duration_value);
        rd.setJob(job_type);
        rd.setPrice(et_price.getText().toString());
        rd.setComment(et_record_comment.getText().toString());

        byte_id = changeIndexBit(byte_id, BIT_REMIND_SENT, remind);
        byte_id = changeIndexBit(byte_id, BIT_HAS_PIC, cb_photo.isChecked());
        rd.setBitsIndex(byte_id);

        return rd;
    }

    @Override
    public void showToast(String status) {
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLastCallText(String status) {
        et_client_phone.setText( status );
    }

    public byte changeIndexBit (byte b_id, int pos, boolean value) {
        if (value) {
            b_id |= 1 << pos;
        } else {
            b_id &= ~(1 << pos);
        }
        return b_id;
    }

    @Override
    public void finishRecordActivity() {
        callbackToActivity.finishRecordActivity();
    }

    @Override
    public void onDetach() {
        callbackToActivity = null;
        presenterRecord.onDestroy();
        super.onDetach();
    }
}