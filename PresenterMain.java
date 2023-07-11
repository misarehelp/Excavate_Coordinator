package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Map;

public class PresenterMain implements Contract.PresenterMain, KM_Constants, Enums, Contract.ViewMainLayout, Contract.ModelMain.OnPresenterMainCallBack {

    // creating object of View Interface
    private Contract.ViewMain mainView;
    // creating object of Client Interface
    private Contract.ViewClient clientView;

    // creating object of Model Interface
    private Contract.ModelMain modelMain;
    private Context context;
    private DataParameters dataParameters;

    // initiating the objects of View and Model Interface
    public PresenterMain(Contract.ViewMain mainView, Context context ) {

        this.mainView = mainView;
        this.context = context;
        //modelMain = new ModelMain(context, dataParameters, entriesArray, mainView.getDepartmentValuesArray());
        dataParameters = DataParameters.getInstance();
        dataParameters.setStateCode(DATA_WAS_NOT_CHANGED);

        modelMain = new ModelMain( context, dataParameters );
        //modelPermit =  new ModelPermit(dataParameters, entriesArray.length);
    }

    // ************* start methods passed from View to ModelMain *******************


    @Override
    public void onPermissionsGranted() {

        modelMain.sendModelDataToServer( this, SERVER_GET_ALL, "", "");
        //modelMain.getDateFromModelMain(this, 0);
    }
    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangeSharedPrefs( String key) {

        //onPermissionsGranted();
        /*
        if (key.equals(DEPARTMENT_USER)) {
            sendUserToModelAndSetView( department_user);
        }
        if (key.equals(MODE_USER)) {
            // trigger when Mode user is changed
            sendUserToModelAndSetView( department_user);
        } */
    }

    @Override
    // operations to be performed - Change Server Preferences
    public void onChangeServerPreferences( String max_records_number) {

        //modelMain.sendModelDataToServer ( this, SERVER_CHANGE_CONFIG, "", max_records_number );
        OnFinishedButtonSaveSetViewButtonsVisibility(DATA_WAS_NOT_CHANGED);
        OnFinishedRefreshViewStatus(DATA_REQUEST_PROCESSING);
    }

    @Override
    // operations to be performed - BroadcastReceiver trigger
    public void onBroadcastReceive(Intent intent) {
        modelMain.getFromModelBroadcastReceiver(this, this, intent);
    }

    @Override
    public void onButtonPreviousWeekClick() {

        modelMain.getDateFromModelMain( this,PERIOD * (-1), 0,0,0);
    }

    @Override
    public void onButtonNextWeekClick() {

        modelMain.getDateFromModelMain( this, PERIOD, 0,0,0);
    }

    @Override
    public void onButtonAddRecordClick(String date) {

        dataParameters.setStateCode(ADD_CODE);
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra(ADD_CODE, date);
        context.startActivity(intent);
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonAddClientClick() {
        //modelMain.updateDataArrayAfterDelete( this );
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonShowClientsClick() {
        //modelMain.updateDataArrayAfterDelete( this );
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonExitClick() {
        //modelMain.updateDataArrayAfterSave( this );
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onTextViewDateClick( int year, int monthOfYear, int dayOfMonth ) {
        modelMain.getDateFromModelMain( this, 0, year, monthOfYear, dayOfMonth );
    }

    @Override
    // operations to be performed - on Menu item "Show Archive" click
    public void onShowArchiveClick() {
        modelMain.sendModelDataToServer (  this, SERVER_GET_ARCHIVE, "", "" );

       OnFinishedButtonSaveSetViewButtonsVisibility(DATA_WAS_NOT_CHANGED);
       OnFinishedRefreshViewStatus(DATA_REQUEST_PROCESSING);
    }

    @Override
    public void onMainListViewItemClick( int position ) {

        dataParameters.setStateCode(CHANGE_CODE);
        dataParameters.setPosition(position);
        context.startActivity(new Intent(context, RecordActivity.class));
        //modelPermit.setModelPermitListItemClick( this, this, modelMain, position, id, viewItew );
    }

    // ************* start  of callbacks passed from ModelMain *******************
    @Override
    // method to return Data for UserMadeList
    public void onFinishedGetServerData () {
        if (mainView != null) {
            modelMain.getDateFromModelMain( this,0, 0,0,0);
        }
    }

    @Override
    // method to return Data for UserMadeList
    public void onFinishedBrUserMadeList (ListViewData listViewData) {
        if (mainView != null) {
            mainView.fillInRecordsList(listViewData.getOutputArray(), listViewData.getDaysInterval(), listViewData.getDaysOfWeek());
        }
    }
    // ************* start  of callbacks passed from ModelPermit *******************

    @Override
    // method to return code to  MainActivity
    public void OnFinishedButtonSaveSetViewButtonsVisibility( String state ) {
        if (mainView != null) {
            mainView.setViewButtonsFieldsVisibility(state);
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedRefreshViewStatus( String state ) {
        if (mainView != null) {
            mainView.refreshMainStatus( state );
        }
    }

    /*@Override
    // method to return code to  MainActivity
    public void OnFinishedShowToast( String  value ) {
        if (mainView != null) {
            mainView.showToast(value);
        }
    } */

    @Override
    public void onMainActivityResume(){
        String code = dataParameters.getStateCode();
        if (!code.equals(DATA_WAS_NOT_CHANGED)) {
            modelMain.getDateFromModelMain(this,0, 0,0,0);
        }
        OnFinishedRefreshViewStatus(dataParameters.getStateCode());
    }

    @Override
    public void onDestroy() {

        mainView = null;
    }
}