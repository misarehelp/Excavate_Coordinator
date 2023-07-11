package ru.volganap.nikolay.haircut_schedule;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.DatePicker;

import com.google.gson.Gson;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ModelMain implements Contract.ModelMain, KM_Constants, Enums {

    //private long DAY_OFFSET = 1000 * 60 * 60 * 24;

    private Context context;
    DataParameters dataParameters;
    final Calendar calendar = Calendar.getInstance();

    // initiating the objects of Model
    public ModelMain(Context context, DataParameters dataParameters ) {

        this.context = context;
        calendar.setTime( new Date() );
        this.dataParameters = dataParameters;
    }

    // Do  Model Logic to get Date
    @Override
    public void getDateFromModelMain( Contract.ModelMain.OnPresenterMainCallBack main_listener, int value, int year, int monthOfYear, int dayOfMonth ) {

        if (year == 0) {
            calendar.add(Calendar.DAY_OF_MONTH, value);
        } else {
            calendar.set(year, monthOfYear, dayOfMonth);
        }
        // check if a chosen date before now
        Date now = new Date();
        if (calendar.getTime().before(now)) calendar.setTime(now);

        ListViewData listViewData = getArrangedByDaysArray();
        main_listener.onFinishedBrUserMadeList(listViewData);
    }

    // Do  Model Logic to get Date
    /*@Override
    public void getDatePickerModelMain( Contract.ModelMain.OnPresenterMainCallBack main_listener, int year, int monthOfYear, int dayOfMonth ) {

        calendar.set(year, monthOfYear, dayOfMonth);

        Date now = new Date();
        if (calendar.getTime().before(now)) {
            calendar.setTime(now);
        }

        ListViewData listViewData = getArrangedByDaysArray();
        main_listener.onFinishedBrUserMadeList(listViewData);
        //
    } */

    // Convert Json  into Record Data
    private RecordData getFromJsonToRecordData(String record_data) {
        return new Gson().fromJson(record_data, RecordData.class);
    }

    // this method will invoke when BroadcastReceiver trigger
    @Override
    public void getFromModelBroadcastReceiver( Contract.ModelMain.OnPresenterMainCallBack main_listener, Contract.ViewMainLayout view_listener, Intent intent) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<RecordData> rec_data_array;
                String message = intent.getStringExtra(MESSAGE);
                String status = intent.getStringExtra(SENDER);

                switch ( status ) {

                    case DATA_IS_READY:
                        // Getting Record Data after Command GET_BY_DATE or GET_ALL
                        rec_data_array  = new ArrayList<>();
                        ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
                        for (String item: array_level_json) {
                            RecordData rd = getFromJsonToRecordData(item); // get original (LATIN) Record Data
                            if (!( null == rd)) {
                                rec_data_array.add( rd );
                            }
                        }

                        dataParameters.setRecordDataArray(rec_data_array);
                        main_listener.onFinishedGetServerData();
                        break;

                    // Config answer was got from the server
                    case SERVER_ANSWER_CONFIG:
                    case DATA_IS_NOT_READY:
                    default:
                        status = message;
                        break;
                }

                view_listener.OnFinishedRefreshViewStatus( status );
                view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( status );
            }
        }, 0);

    }

    // send Command and/or Data to Server
    @Override
    public void sendModelDataToServer ( Contract.ViewMainLayout view_listener, String command, String dateID, String value) {

        new OkHttpRequest().serverGetback( context, command, dateID, value );

        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility(DATA_WAS_NOT_CHANGED);
        view_listener.OnFinishedRefreshViewStatus(DATA_REQUEST_PROCESSING);
    }

    // Fill In Records of Haicut schedule List
     private ArrayList<Map<String, String>> getListRecordData( ArrayList<RecordData> rda ) {

        ArrayList<Map<String, String>> data = new ArrayList<>();

        for (int i = 0; i < rda.size(); i++) {

                Map<String, String> hashmap = new HashMap<>();

                hashmap.put(FROM[0], rda.get(i).getTime());;
                hashmap.put(FROM[1], rda.get(i).getJob());
                hashmap.put(FROM[2], rda.get(i).getName());
                hashmap.put(FROM[3], rda.get(i).getComment());
                hashmap.put(FROM[4], rda.get(i).getIndex());

                data.add(hashmap);
        }

        return data;
    }

    public ArrayList<RecordData> getSortedByTimeArray ( ArrayList<RecordData> rda) {

        if (null == rda) {
            return null;
        }

        int n = rda.size();
        if (n > 1) {

            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    Date first = convertStringtoTimeStamp(rda.get(j).getDate(), rda.get(j).getTime());
                    Date second = convertStringtoTimeStamp(rda.get(j + 1).getDate(), rda.get(j + 1).getTime());
                    if (first.getTime() > second.getTime()) {
                        RecordData temp = rda.get(j);
                        rda.set(j, rda.get(j + 1));
                        rda.set(j + 1, temp);
                    }
                }
            }
        }
        return rda;
    }

    public ListViewData getArrangedByDaysArray () {

        ArrayList<RecordData> rda = dataParameters.getRecordDataArray();
        ArrayList<ArrayList<Map<String, String>>> period_data = new ArrayList<>();
        ArrayList<String> days_interval = new ArrayList<>();
        ArrayList<String> days_of_week = new ArrayList<>();

        Date record_day = calendar.getTime();
        Calendar start = Calendar.getInstance();
        start.setTime(record_day);
        Calendar end = Calendar.getInstance();
        end.setTime(record_day);
        end.add(Calendar.DATE, PERIOD );

        ArrayList<ArrayList<RecordData>> temp_period_level = new ArrayList<>();

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {

            String current_date = convertDateToString(date);

            int ordinal = start.get(Calendar.DAY_OF_WEEK);
            days_of_week.add(WEEKDAYS[ordinal-1]);
            days_interval.add(current_date);

            ArrayList<RecordData> day_level = new ArrayList<>();

            for (RecordData rd: rda) {
                if (current_date.equals(rd.getDate())) {
                    String ind = Integer.toString(rda.indexOf(rd));
                    rd.setIndex(ind);
                    day_level.add(rd);
                }
            }
            temp_period_level.add(day_level);
        }

        //get list sorted by time
        for (ArrayList<RecordData> rpl: temp_period_level) {
            ArrayList<RecordData> temp_day_level = getSortedByTimeArray(rpl);
            period_data.add( getListRecordData( temp_day_level ) );
        }

        return new ListViewData(period_data, days_interval, days_of_week);
    }

    public Date convertStringtoTimeStamp (String date, String time) {
        Date date_time = null;
        //Timestamp timestamp = null;
        String date_time_value = date + " " + time;
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy hh:mm", java.util.Locale.getDefault());
        try {
            date_time = dt.parse(date_time_value);
            //timestamp = new java.sql.Timestamp(date_time.getTime());

        } catch (ParseException e) {
            Log.d(LOG_TAG, "ModelRecord - Exception is: " + e);
        }

        return date_time;
    }

    public String convertDateToString (Date date ) {
        return  new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(date);
    }


}




