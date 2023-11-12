package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ModelMain implements Contract.ModelMain, Constants, Enums {

    //private long DAY_OFFSET = 1000 * 60 * 60 * 24;
    private String START_WORK_TIME = "08:00";
    private String END_WORK_TIME = "20:00";
    private int TIME_MINUTE_STEP = 30;
    private String FREE_PERIOD = "* свободно *";

    final int COLOR_FREE_RECORD_DARK = R.color.colorFreeRecordDark;
    final int COLOR_NOTE_RECORD_DARK = R.color.colorNoteRecordDark;
    final int COLOR_HAIRCUT_RECORD_DARK = R.color.colorHairCutRecordDark;
    final int COLOR_FREE_RECORD_LGRAY = R.color.colorFreeRecordLight;
    final int COLOR_NOTE_RECORD_LGRAY  = R.color.colorNoteRecordLight;
    final int COLOR_HAIRCUT_RECORD_LGRAY  = R.color.colorHairCutRecordLight;
    final int COLOR_HAIRCUT_RECORD_DOUBT  = R.color.colorHairCutRecordDoubt;

    final int IMG_FREE_RECORD = R.drawable.phone_call_green;
    final int IMG_NOTE_RECORD = R.drawable.note_color_2;
    final int IMG_HAIRCUT_RECORD = R.drawable.scissors_2;
    final int IMG_HAIRCUT_DOUBT = R.drawable.vopros_22;
    private boolean show_free_rec = true;
    private boolean color_text_dark;
    private boolean future_recs = true;
    private int days_before_now;
    private Context context;
    DataParameters dataParameters;
    final Calendar calendar = Calendar.getInstance();

    // initiating the objects of Model
    public ModelMain( Context context, DataParameters dataParameters, int theme_position, int days_before ) {

        this.context = context;
        calendar.setTime( new Date() );
        this.dataParameters = dataParameters;
        this.days_before_now = days_before;

        switch (theme_position) {
            default:
            case THEME_LIGHT_SMALL:
            case THEME_LIGHT_MEDIUM:
            case THEME_LIGHT_BIG:
            //case THEME_NEUTRAL_SMALL:
            //case THEME_NEUTRAL_MEDIUM:
            //case THEME_NEUTRAL_BIG:
                color_text_dark = false;
                break;
            case THEME_DARK_SMALL:
            case THEME_DARK_MEDIUM:
            case THEME_DARK_BIG:
                color_text_dark = true;
                break;
        }
    }

    //change state of records (show free ones or not)
    @Override
    public void changeFreeRecordsState() {
        show_free_rec = !show_free_rec;
    }

    @Override
    public void changeDaysBefore(int days) {
        days_before_now = days;
    }

    // Do  Model Logic to get Date
    @Override
    public void getDateFromModelMain( Contract.ViewMainLayout view_listener, int period, int year, int monthOfYear, int dayOfMonth ) {

        if (year == 0) {
            calendar.add(Calendar.DAY_OF_MONTH, period);
        } else {
            calendar.set(year, monthOfYear, dayOfMonth);
        }
        //calendar.getTime();

        final Calendar cal_zero = Calendar.getInstance();
        cal_zero.add( Calendar.DAY_OF_YEAR, days_before_now * (-1));

        if ( ((calendar.before(cal_zero) && future_recs) || (calendar.after(cal_zero) && !future_recs)) ) {
            // check if period was changed from the present to the past or from the past to the present
            future_recs = !future_recs;
            show_free_rec = future_recs;
            view_listener.onFinishedGetPastFuture(future_recs);
        }

        ListViewData listViewData = getArrangedByDaysArray();
        view_listener.onFinishedBrUserMadeList(listViewData);
    }

    // Convert Json  into Record Data
    private RecordData getFromJsonToRecordData(String data) {
        return new Gson().fromJson(data, RecordData.class);
    }

    // Convert Json  into Client Data
    private ClientData getFromJsonToClientData(String data) {
        return new Gson().fromJson(data, ClientData.class);
    }

    // this method will invoke when BroadcastReceiver trigger
    @Override
    public void getFromModelBroadcastReceiver( Contract.ViewMainLayout view_listener, Intent intent) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ArrayList<RecordData> rec_data_array = new ArrayList<>();
                ArrayList<ClientData> client_data_array = new ArrayList<>();

                String message = intent.getStringExtra(MESSAGE);
                String status = intent.getStringExtra(SENDER);
                String command = intent.getStringExtra(COMMAND);

                // Getting Client Data after Command SERVER_GET_CLIENTS
                if (command.equals(SERVER_GET_CLIENTS)) {

                    switch ( status ) {

                        case DATA_IS_READY:
                            // Getting Record Data after Command GET_BY_DATE or GET_ALL
                            ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
                            for (String item : array_level_json) {
                                ClientData cd = getFromJsonToClientData(item); // get original (LATIN) Record Data
                                if (!(null == cd)) {
                                    client_data_array.add(cd);
                                }
                            }
                            break;

                        // Config answer was got from the server
                        case SERVER_ANSWER_CONFIG_CHANGED:
                        case DATA_IS_NOT_READY:
                        default:
                            status = message;
                            break;
                    }

                    dataParameters.setClientDataArray(client_data_array);
                    view_listener.onFinishedGetServerClientData();

                }  else {// Getting Record Data after Command GET_BY_DATE or GET_ALL

                    switch ( status ) {

                        case DATA_IS_READY:

                            final Calendar cal_zero = Calendar.getInstance();
                            cal_zero.add( Calendar.DAY_OF_YEAR, -1);
                            Date date_yesterday = cal_zero.getTime();
                            Date date_last_record = new Date();
                            Date date_past_first_record = new Date();
                            Date date_actual_first_record = new Date();
                            Date now_record = new Date();
                            String str_last_rec = "";
                            String str_past_first_rec = "";
                            String str_actual_first_rec = "";
                            int old_recs = 0;

                            ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);

                            for (String item : array_level_json) {
                                RecordData rd = getFromJsonToRecordData(item); // get original (LATIN) Record Data

                                if (!(null == rd)) {
                                    rec_data_array.add(rd);

                                    Date current_record = convertStringtoTimeStamp ( rd.getDate(), rd.getTime());
                                    // define the latest record
                                    if (current_record.after(date_last_record)) {
                                        date_last_record = current_record;
                                        str_last_rec = rd.getDate();
                                    }
                                    // define the first actual record
                                    if (current_record.before(date_actual_first_record) || current_record.after(date_yesterday) ) {
                                        date_actual_first_record = current_record;
                                        str_actual_first_rec = rd.getDate();
                                    }
                                    // define the earliest record
                                    if (current_record.before(date_past_first_record)) {
                                        date_past_first_record = current_record;
                                        str_past_first_rec = rd.getDate();
                                    }
                                    // define the nummber of past and actual records
                                    if (current_record.before(date_yesterday)) {
                                        old_recs++;
                                    }
                                }
                            }

                            int len = rec_data_array.size();
                            if (len > 0) {
                                status = status + "Всего " + old_recs + " прошлых (c " + str_past_first_rec + " по настоящее) и " +
                                        (len - old_recs) + " актуальных записей (с " + str_actual_first_rec + " по " + str_last_rec + ")";
                            }
                            break;

                        // Config answer was got from the server
                        case SERVER_ANSWER_CONFIG_CHANGED:
                            sendModelDataToServer( view_listener, SERVER_GET_CLIENTS, "", "");
                            //view_listener.OnFinishedRefreshViewStatus( message );
                            return;
                        case DATA_IS_NOT_READY:
                        default:
                            status = message;
                            break;
                    }

                    dataParameters.setRecordDataArray(rec_data_array);
                    view_listener.onFinishedGetServerRecordsData();
                }

                view_listener.OnFinishedRefreshViewStatus( status );
            }
        }, 0);
    }

    // send Command and/or Data to Server
    @Override
    public void sendModelDataToServer ( Contract.ViewMainLayout view_listener, String command, String dateID, String value) {

        new OkHttpRequest().serverGetback( context, command, dateID, value );
        view_listener.OnFinishedRefreshViewStatus(DATA_REQUEST_PROCESSING);
    }

    // Fill In Records of Haicut schedule List
     private ArrayList<MainScreenData> getListRecordData( ArrayList<RecordData> rda ) {

         ArrayList<MainScreenData> data = new ArrayList<>();
         int rda_size = rda.size();
         MainScreenData mainScreenData;

         // Hide free records
         if (!show_free_rec) {
             for (int i = 0; i < rda_size; i++) {
                 mainScreenData = new MainScreenData();
                 mainScreenData.setType(INDEX_FREE_RECORD);
                 mainScreenData.setTime(rda.get(i).getTime());
                 data.add(getMainSreenDataFromRecordData(mainScreenData, rda.get(i)));
             }

         } else { //Show all records
             Calendar calend_start = Calendar.getInstance();
             Calendar calend_end = Calendar.getInstance();
             Calendar current1 = Calendar.getInstance();
             Calendar current2 = Calendar.getInstance();

             SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

             try {
                 Date start_time = time_formatter.parse(START_WORK_TIME);
                 Date end_time = time_formatter.parse(END_WORK_TIME);
                 calend_start.setTime(start_time);
                 calend_end.setTime(end_time);

                 for (Date time = calend_start.getTime(); calend_start.before(calend_end); calend_start.add(Calendar.MINUTE, TIME_MINUTE_STEP), time = calend_start.getTime()) {
                     // put Free Records to all time range from 8:00 to 20:00
                     String string_time = convertTimeToString(time);

                     mainScreenData = new MainScreenData();
                     mainScreenData.setTime(string_time);
                     mainScreenData.setJob("");
                     mainScreenData.setName(FREE_PERIOD);
                     mainScreenData.setType(INDEX_FREE_RECORD);
                     mainScreenData.setIndex(INDEX_FREE_RECORD);
                     if (color_text_dark) {
                         mainScreenData.setColor(context.getResources().getColor(COLOR_FREE_RECORD_DARK));
                     } else {
                         mainScreenData.setColor(context.getResources().getColor(COLOR_FREE_RECORD_LGRAY));
                     }
                     mainScreenData.setResource(IMG_FREE_RECORD);

                     data.add(mainScreenData);
                     int d_ind = data.indexOf(mainScreenData);

                     for (int i = 0; i < rda_size; i++) {

                         Date current_time = time_formatter.parse(rda.get(i).getTime());
                         current1.setTime(current_time);
                         current2.setTime(current_time);
                         int diff = Integer.parseInt(rda.get(i).getDuration());
                         current2.add(Calendar.MINUTE, diff - 1);

                         //check if Time Range is fit for current job
                         if ((current1.equals(calend_start) && calend_start.before(current2)) || (current1.before(calend_start) && calend_start.equals(current2))
                                 || (current1.before(calend_start) && calend_start.before(current2))) {
                             //check if Start Time is equal to rda item time to set the time to busy
                             if (current1.equals(calend_start)) {

                                 data.set(d_ind, getMainSreenDataFromRecordData(mainScreenData, rda.get(i)));

                             } else {
                                 data.remove(d_ind);
                                 d_ind--;
                             }
                         }
                     }
                 }

             } catch (ParseException e) {
                 Log.d(LOG_TAG, "ModelRecord - Exception is: " + e);
             }
         }

        return data;
    }

    private MainScreenData getMainSreenDataFromRecordData ( MainScreenData mainScreenData, RecordData rd) {

        mainScreenData.setJob(rd.getJob());
        mainScreenData.setName(rd.getName());
        mainScreenData.setIndex(rd.getIndex());

        if (rd.getJob().equals(INDEX_NOTE)) {
            mainScreenData.setType(INDEX_NOTE);
            if (color_text_dark) {
                mainScreenData.setColor(context.getResources().getColor(COLOR_NOTE_RECORD_DARK));
            } else {
                mainScreenData.setColor(context.getResources().getColor(COLOR_NOTE_RECORD_LGRAY));
            }
            mainScreenData.setResource(IMG_NOTE_RECORD);

        } else {

            if ( null == rd.getId() || rd.getId().equals("") ) {
                if (color_text_dark) {
                    mainScreenData.setColor(context.getResources().getColor(COLOR_HAIRCUT_RECORD_DARK));
                } else {
                    mainScreenData.setColor(context.getResources().getColor(COLOR_HAIRCUT_RECORD_LGRAY));
                }

                mainScreenData.setResource(IMG_HAIRCUT_RECORD);

            } else {
                mainScreenData.setColor(context.getResources().getColor(COLOR_HAIRCUT_RECORD_DOUBT));
                mainScreenData.setResource(IMG_HAIRCUT_DOUBT);
            }
        }
        return mainScreenData;
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
        ArrayList<ArrayList<MainScreenData>> period_data = new ArrayList<>();
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
        String date_time_value = date + " " + time;
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault());
        try {
            date_time = dt.parse(date_time_value);

        } catch (ParseException e) {
            Log.d(LOG_TAG, "ModelRecord - Exception is: " + e);
        }

        return date_time;
    }

    public String convertDateToString (Date date ) {
        return  new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(date);
    }

    public String convertTimeToString (Date time ) {
        return  new SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(time);
    }
}
