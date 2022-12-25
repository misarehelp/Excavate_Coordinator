package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModelMain implements Contract.ModelMain, KM_Constants, Enums {

    private String  SERVER_ADD_PERMIT = "server_add_permit";
    private String  SERVER_CHANGE_PERMIT = "server_change_permit";
    private String  SERVER_DELETE_PERMIT = "server_delete_permit";
    //private long DAY_OFFSET = 1000 * 60 * 60 * 24;
    private ArrayList<RecordData> rec_data_array  = new ArrayList<>();
    //private DataParameters dataParameters;
    private Context context;
    DataParameters dataParameters;

    public Date record_day;

    // initiating the objects of Model
    public ModelMain(Context context, DataParameters dataParameters ) {
        this.context = context;
        record_day = new Date();
        this.dataParameters = dataParameters;
    }

    // Do  Model Logic to get Date
    @Override
    public void getDateFromModelMain( Contract.ModelMain.OnPresenterMainCallBack main_listener, int value ) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Define List of Required Departments in View Adapter
                Calendar calendar = Calendar.getInstance();
                Date now = new Date();

                if ( value == 0 ) {
                    calendar.setTime(new Date());
                } else {
                    calendar.setTime(record_day);
                    calendar.add(Calendar.DAY_OF_MONTH, value);
                }

                Date saved_time = calendar.getTime();

                if (now.getTime() < saved_time.getTime()) {
                    record_day = calendar.getTime();
                } else {
                    record_day = now;
                }

                String date = new SimpleDateFormat("dd.MM.yyyy").format(record_day);
                main_listener.onFinishedGetDate(date);

            }
        }, 0);
    }

    // Convert Record into Json
    private String getFromRecordDataToJson(RecordData record_data) {
        return new Gson().toJson(record_data);
    }

    // Convert Json  into Record Data
    private RecordData getFromJsonToRecordData(String record_data) {
        return new Gson().fromJson(record_data, RecordData.class);
    }

    // this method will invoke when BroadcastReceiver trigger
    @Override
    public void getFromModelBroadcastReceiver(Contract.ModelMain.OnPresenterMainCallBack main_listener,
                                          Contract.ViewMainLayout view_listener, Intent intent) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String message = intent.getStringExtra(MESSAGE);
                String status = intent.getStringExtra(SENDER);
                RecordData rd =  new RecordData();

                switch ( status ) {
                    // Record Data was changed on the server
                    case DATA_WAS_SAVED:
                    case DATA_IS_NOT_READY:
                        rec_data_array  = new ArrayList<>();
                        status = message;
                        main_listener.onFinishedBrUserMadeList(getListRecordData());
                        break;

                    // Config answer was got from the server
                    case SERVER_ANSWER_CONFIG:

                        status = message;
                        //main_listener.onFinishedBrUserMadeList(getPermitsUserMadeData());
                        break;

                    // Got some Record Data
                    case DATA_IS_READY:
                        // Getting Record Data after Command GET_BY_DATE or GET_ALL
                        rec_data_array  = new ArrayList<>();
                        ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
                        for (String item: array_level_json) {
                            rd = getFromJsonToRecordData(item); // get original (LATIN) Record Data
                            if (!( null == rd)) {
                                rec_data_array.add( rd );
                            }
                        }

                        dataParameters.setRecordDataArray(rec_data_array);

                        main_listener.onFinishedBrUserMadeList(getListRecordData());
                        break;
                    //Config Data has got or confirmation of saving Depline Data to Server
                    default:
                        status = message;
                        break;
                }

                view_listener.OnFinishedRefreshViewStatus( status );
                view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( status );

            }
        }, 0);

    }
    // Fill In Records of Haicut schedule List
     private ArrayList<Map<String, String>> getListRecordData() {

        ArrayList<Map<String, String>> data = new ArrayList<>();

        for (int i = 0; i < rec_data_array.size(); i++) {

                Map<String, String> hashmap = new HashMap<>();

                hashmap.put(FROM[0], rec_data_array.get(i).getTime());;
                hashmap.put(FROM[1], rec_data_array.get(i).getJob());
                hashmap.put(FROM[2], rec_data_array.get(i).getName());
                hashmap.put(FROM[3], rec_data_array.get(i).getComment());

                data.add(hashmap);
        }

        return data;
    }

    // send Command and/or Data to Server
    @Override
    public void sendModelDataToServer ( Contract.ViewMainLayout view_listener, String command, String dateID, String value) {

        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility(DATA_WAS_NOT_CHANGED);
        view_listener.OnFinishedRefreshViewStatus(DATA_REQUEST_PROCESSING);

        new OkHttpRequest().serverGetback( context, command, dateID, value );
    }

   /*
    @Override
    // method to Update DepLineData Array after Delete
    public void updateDataArrayAfterDelete (Contract.ViewMainLayout view_listener ) {
        // Check if Button Save clicked
                 String code = dataParameters.getStateCode();

                switch ( code ) {

                    case EDIT_MASTER_PERMIT_CODE:
                        getBackWithServer ( SERVER_DELETE_PERMIT, dataParameters.getDepLineData().getId(), "");
                        break;

                    case NEW_PERMIT_CODE:
                        dataParameters.setDepLineData(null);

                    // no change was made
                    default:
                        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( DATA_WAS_NOT_CHANGED );
                        view_listener.OnFinishedRefreshViewStatus( DATA_WAS_NOT_CHANGED );
                        break;
                }

            //view_listener.OnFinishedSetPermitBlockState( PermitBlock.INVISIBLE );
    }

    @Override
    // method to Update DepLineData Array after Save
    public void updateDataArrayAfterSave (Contract.ViewMainLayout view_listener ) {

        String status = DATA_WAS_NOT_CHANGED;
        //view_listener.OnFinishedSetPermitBlockState( PermitBlock.INVISIBLE );
        DepLinesData dl_data;

        switch ( dataParameters.getStateCode() ) {
            // add a new record
            case NEW_PERMIT_CODE:
            // Send to Server New Depline Data Permit with ID and Modified Department User        *****************************************
                dl_data = getModifiedData( dataParameters.getDepLineData(), departServerHashMap );
                getBackWithServer(SERVER_ADD_PERMIT, "", getFromDepLineDataToJson(dl_data) );

                break;

            // change the record
            case CHANGE_PERMIT_CODE:

                dl_data = getModifiedData(dataParameters.getDepLineData(), departServerHashMap);
                getBackWithServer(SERVER_CHANGE_PERMIT, dataParameters.getDepLineData().getId(), getFromDepLineDataToJson(dl_data));

                break;

            // no change was made
            case DATA_WAS_NOT_CHANGED:
            case EDIT_MASTER_PERMIT_CODE:
            case SHOW_PERMIT_CODE:
            default:
        }

        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( status );
        view_listener.OnFinishedRefreshViewStatus( status );
    }
    */

}




