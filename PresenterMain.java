package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class PresenterMain implements Contract.PresenterMain, Constants, Enums, Contract.ViewMainLayout {
    private Contract.ViewMain mainView;
    private Contract.ModelMain modelMain;
    private Context context;
    private DataParameters dataParameters;
    private Calendar calendar_backup = Calendar.getInstance();
    private int dayOfWeek_backup = 0;

    // initiating the objects of View and Model Interface
    public PresenterMain(Contract.ViewMain mainView, Context context, int theme_type, int days_before ) {

        this.mainView = mainView;
        this.context = context;
        dataParameters = DataParameters.getInstance();
        dataParameters.setStateCode(DATA_WAS_NOT_CHANGED);
        modelMain = new ModelMain( context, dataParameters, theme_type, days_before );
    }

    // ************* start methods passed from View to ModelMain *******************
    @Override
    public void onPermissionsGranted() {
        modelMain.sendModelDataToServer( this, SERVER_GET_CLIENTS, "", "");
    }
    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangeSharedPrefs( String key) {
        onPermissionsGranted();
    }

    @Override
    public void onChangeDaysBefore(int days) {
        modelMain.changeDaysBefore(days);
    }

    @Override
    // operations to be performed - Change Server Preferences
    public void onChangeServerPreferences( String max_recs,  int days_before) {
        modelMain.sendModelDataToServer( this, SERVER_CHANGE_CONFIG, Integer.toString(days_before), max_recs);
    }

    @Override
    public void onDeleteArchiveRecords() {
        modelMain.sendModelDataToServer( this, SERVER_DELETE_ARCHIVE, "", "");
    }

    @Override
    // operations to be performed - BroadcastReceiver trigger
    public void onBroadcastReceive(Intent intent) {
        modelMain.getFromModelBroadcastReceiver(this,  intent);
    }

    @Override
    public void onChangeRecordClick(String date, String time, String index, String type, int theme, String command ) {

        dataParameters.setStateCode(command);

        if (!command.equals(SERVER_ADD_RECORD)) {
            dataParameters.setRecordPosition(Integer.parseInt(index));
        }

        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra(DATE_CODE, date);
        intent.putExtra(TIME_CODE, time);
        intent.putExtra(INDEX_CODE, index);
        intent.putExtra(TYPE_CODE, type);
        intent.putExtra(THEME, theme);
        context.startActivity(intent);
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonShowHideFreeRecordsClick() {
        modelMain.changeFreeRecordsState();
        modelMain.getDateFromModelMain( this, calendar_backup, dayOfWeek_backup);
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onTextViewDateClick( Calendar calendar, int dayOfWeek ) {
        modelMain.getDateFromModelMain( this, calendar, dayOfWeek );
        calendar_backup.setTime(calendar.getTime());
        dayOfWeek_backup = dayOfWeek;
    }

    @Override
    public void onFinishedGetPastFuture(RecordVisibility value) {
        mainView.setArchiveStatus(value);
    }

    // ************* start  of callbacks passed from ModelMain *******************
    //First pass - to get records
    @Override
    public void onFinishedGetServerClientData() {
        modelMain.sendModelDataToServer( this, SERVER_GET_ALL, "", "");
    }

    @Override
    // Second pass - to define period (present or past)
    public void onFinishedGetServerRecordsData () {
        if (mainView != null) {
            mainView.passDataToCalendar( dataParameters.getCalendarHashmap(), dataParameters.getHolidayHashMap() );
        }
    }

    @Override
    // method to return Data for UserMadeList
    public void onFinishedBrUserMadeList (ListViewData listViewData) {
        if (mainView != null) {
            mainView.fillInRecordsList( listViewData.getOutputArray(), listViewData.getDaysInterval() );
        }
    }

    // ************* start  of callbacks passed from ModelMain *******************
    @Override
    // method to return code to  MainActivity
    public void OnFinishedRefreshViewStatus( String state ) {
        if (mainView != null) {
            if (state.contains(SERVER_ANSWER_CONFIG_CHANGED))  mainView.showToast(state);
            mainView.refreshMainStatus(state);
        }
    }

    @Override
    public void onMainActivityResume(){

        String code = dataParameters.getStateCode();
        if (!code.equals(DATA_WAS_NOT_CHANGED)) {
            //there was made some chages in a record
            mainView.passDataToCalendar( dataParameters.getCalendarHashmap(), dataParameters.getHolidayHashMap()  );
            modelMain.getDateFromModelMain( this, calendar_backup, dayOfWeek_backup);
        }

        OnFinishedRefreshViewStatus(code);
    }

    @Override
    public void onDestroy() {
        mainView = null;
    }
}