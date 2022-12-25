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

        //modelMain.getDataFromModelMain(this, 0);
        modelMain.getDateFromModelMain(this, 0);
    }
    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangeSharedPrefs( String key) {

        onPermissionsGranted();
        //String department_user = mainView.getViewDepartmentUser();
        /* boolean dispatcher_on = isDispatcherOn();

        if (key.equals(DEPARTMENT_USER)) {
            sendUserToModelAndSetView( department_user);
        }
        if (key.equals(MODE_USER)) {
            // trigger when Mode user is changed
            if (dispatcher_on) {
                department_user = mainView.getViewModeUser();
            }

            sendUserToModelAndSetView( department_user);
        } */
    }

    @Override
    // operations to be performed - Change Server Preferences
    public void onChangeServerPreferences( String max_records_number) {

        modelMain.sendModelDataToServer ( this, SERVER_CHANGE_CONFIG, "", max_records_number );
    }

    private void sendUserToModelAndSetView( String department_user) {
        /*
        String previous_user = dataParameters.getModelDepartmentUser();
        if ( null == previous_user ) {
            previous_user = department_user;
        }

        if (dataParameters.getDispatcherMode()) {
            modelMain.sendModelDataToServer(this, SERVER_CLEAR_BUSY, previous_user, "");
        } else {
            modelMain.sendModelDataToServer(this, SERVER_CLEAR_BUSY, previous_user, SERVER_CLEAR_BUSY);
        }

        dataParameters.setModelDepartmentUser(department_user);
        mainView.setViewDepartmentUser(department_user); */
    }

    @Override
    // operations to be performed - BroadcastReceiver trigger
    public void onBroadcastReceive(Intent intent) {
        modelMain.getFromModelBroadcastReceiver(this, this, intent);
    }

    @Override
    public void onButtonPreviousDayClick() {

        modelMain.getDateFromModelMain(this,  -1);
        //modelMain.sendModelDataToServer ( this, SERVER_GET_BY_DEP, dataParameters.getModelDepartmentUser(), "" );
    }

    @Override
    public void onButtonNextDayClick() {

        modelMain.getDateFromModelMain(this,  1);
        //modelMain.sendModelDataToServer ( this, SERVER_GET_BY_DEP, dataParameters.getModelDepartmentUser(), "" );
    }

    @Override
    public void onButtonAddRecordClick() {

        dataParameters.setStateCode(ADD_CODE);
        context.startActivity(new Intent(context, RecordActivity.class));
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
    // operations to be performed - on Menu item "Show Archive" click
    public void onShowArchiveClick() {
        modelMain.sendModelDataToServer ( this, SERVER_GET_ARCHIVE, "", "" );
    }

    @Override
    // operations to be performed - on Menu item "Clear ID counter" click
    public void onClearIdCounterClick() {
        modelMain.sendModelDataToServer ( this, SERVER_CLEAR_START_ID, "", "" );
    }

    @Override
    public void onMainListViewItemClick( int position, int id, int viewItew) {

        dataParameters.setStateCode(CHANGE_CODE);
        dataParameters.setPosition(position);
        context.startActivity(new Intent(context, RecordActivity.class));
        //modelPermit.setModelPermitListItemClick( this, this, modelMain, position, id, viewItew );
    }

    // ************* start  of callbacks passed from ModelMain *******************
    @Override
    // method to return Data for UserMadeList
    public void onFinishedBrUserMadeList (ArrayList<Map<String, String>> data) {
        if (mainView != null) {
            mainView.fillInRecordsList(data);
        }
    }

    @Override
    // method to return Data for UserMadeList
    public void onFinishedGetDate (String date) {
        if (mainView != null) {
            mainView.setViewDate(date);
            modelMain.sendModelDataToServer ( this, SERVER_GET_BY_DATE, date, "" );
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
    public void OnFinishedSetPermitSimpleAdapter( ArrayList<Map<String, String>> data ) {

        if (mainView != null) {
            mainView.setPermitAdapterAndItemClickListener(data);
            mainView.setRequiredDepsVisibility(View.VISIBLE);
        }
    }*/


    /*@Override
    // method to return code to  MainActivity
    public void OnFinishedShowToast( String  value ) {
        if (mainView != null) {
            mainView.showToast(value);
        }
    } */

    @Override
    public void onMainActivityResume(){
        OnFinishedRefreshViewStatus( dataParameters.getStateCode());
        modelMain.getDateFromModelMain(this, 0);
    }

    @Override
    public void onDestroy() {
        //modelMain.sendModelDataToServer(this, SERVER_CLEAR_BUSY, dataParameters.getModelDepartmentUser(), "" );
        mainView = null;
    }
}