package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Map;

public class PresenterMain implements Contract.PresenterMain, KM_Constants, Enums, Contract.ViewMainLayout, Contract.ModelMain.OnFinishedSetMainViewLayout,
        Contract.ModelPermit.OnFinishedSetMainViewLayout {

    // creating object of View Interface
    private Contract.ViewMain mainView;

    // creating object of Model Interface
    private Contract.ModelMain modelMain;
    private Contract.ModelPermit modelPermit;
    private DataParameters dataParameters;

    // initiating the objects of View and Model Interface
    public PresenterMain(Contract.ViewMain mainView, Context context ) {

        dataParameters = DataParameters.getInstance();
        dataParameters.setStateCode(DATA_WAS_NOT_CHANGED);
        dataParameters.setMapIsDone(false);
        String [] entriesArray = mainView.getDepartmentEntriesArray();
        if ( null == dataParameters.getModelDepartmentUser()) {
            dataParameters.setModelDepartmentUser(entriesArray[0]);
        }

        this.mainView = mainView;

        modelMain = new ModelMain(context, dataParameters, entriesArray, mainView.getDepartmentValuesArray());
        modelPermit =  new ModelPermit(dataParameters, entriesArray.length);
    }

    // ************* start methods passed from View to ModelMain *******************

    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangeSharedPrefs( String key) {

        String department_user = mainView.getViewDepartmentUser();
        boolean dispatcher_on = isDispatcherOn();

        if (key.equals(DEPARTMENT_USER)) {
            sendUserToModelAndSetView( department_user);
        }
        if (key.equals(MODE_USER)) {
            // trigger when Mode user is changed
            if (dispatcher_on) {
                department_user = mainView.getViewModeUser();
            }

            sendUserToModelAndSetView( department_user);
        }

    }

    @Override
    // operations to be performed - Change Server Preferences
    public void onChangeServerPreferences( String max_records_number) {

        modelMain.sendModelDataToServer ( this, SERVER_CHANGE_CONFIG, "", max_records_number );
    }

    private boolean isDispatcherOn () {
        boolean state = mainView.getViewModeUser().equals(mainView.getDispatcherDefault());
        mainView.setViewUserMadeBlockVisibility(state);
        dataParameters.setDispatcherMode(state);
        return state;
    }

    private void sendUserToModelAndSetView( String department_user) {

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
        mainView.setViewDepartmentUser(department_user);
    }

    @Override
    public void onPermissionsGranted( String[] department_array ) {

        dataParameters.setDepartmentArray(department_array);
        dataParameters.setMaxRecordsNumber(mainView.getMaxRecordsNumber());

        if (isDispatcherOn()) {
            onChangeSharedPrefs(MODE_USER);
        } else {
            onChangeSharedPrefs(DEPARTMENT_USER);
        }
    }

    @Override
    public void onButtonCheckClick() {

        modelMain.sendModelDataToServer ( this, SERVER_GET_BY_DEP, dataParameters.getModelDepartmentUser(), "" );
    }

    @Override
    // operations to be performed - BroadcastReceiver trigger
    public void onBroadcastReceive(Intent intent) {
        modelMain.setModelBroadcastReceiver(this, this, intent);
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
    // operations to be performed - on Menu item "Clear ID counter" click
    public void onDeleteAllPermitsClick() {
        modelMain.sendModelDataToServer ( this, SERVER_DELETE_ALL, "", "" );
    }

    @Override
    public void onButtonNewClick() {
        modelMain.getBackWithServer(SERVER_GET_NEXT_ID,"","");
        //modelMain.setModelMainButtonNewClick( this, this );
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonDeleteClick() {
        modelMain.updateDataArrayAfterDelete( this );
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonExitClick() {
        modelMain.updateDataArrayAfterSave( this );
    }

    @Override
    // method to be called when the Button Put/Show Communications is clicked
    public void onButtonShowMapClick( Context context ) {
        if (dataParameters.getDispatcherMode () ) {
            dataParameters.setStateCode(SHOW_PERMIT_CODE);
        }
        Intent maps_activity = new Intent(context, MapsActivity.class);
        context.startActivity(maps_activity);
    }

    @Override
    public void onMainActivityResume() {
        //this.dataParameters = DataParameters.getInstance();
        if (dataParameters.getMapIsDone()) {
            dataParameters.setMapIsDone(false);
            onButtonExitClick();
        }
    }
    // ************* start methods passed from View to ModelPermit *******************

    @Override
    public void onButtonDepsChooseClick (String value, String sample, String place, String date_start, String date_end, String comment) {
        modelPermit.setModelPermitButtonDepsChooseClick(this, this, value, sample );
        modelPermit.setModelPermitPlaceDateComment ( place, date_start, date_end, comment );
    }

    @Override
    public void onMainListViewItemClick( int position, int id, int viewItew) {

        modelPermit.setModelPermitListItemClick( this, this, modelMain, position, id, viewItew );
    }

    @Override
    //method to do logic after an item of chosen deps is clicked
    public void onPermitListViewItemClick( AdapterView<?> parent ) {
        modelPermit.setRequiredDepsArray( parent );
    }

    // ************* start  of callbacks passed from ModelMain *******************
    @Override
    // method to return Data for UserMadeList
    public void onFinishedBrUserMadeList (ArrayList<Map<String, String>> data) {
        if (mainView != null) {
            mainView.fillInPermitsUserMadeList(data);
        }
    }

    @Override
    // method to return Data for AwaitingList
    public void onFinishedBrAwaitingList (ArrayList<Map<String, String>> data) {
        if (mainView != null) {
            mainView.fillInPermitsAwaitingList(data);
        }
    }

    @Override
    // method to start Filling a New Permit
    public void OnFinishedGetNumberOfServerRecords () {

        modelMain.setModelMainButtonNewClick( this, this );
    }

    @Override
    // method to start Filling a New Permit
    public void OnFinishedClearBusyServerStatus () {
        onButtonCheckClick();
    }

    // ************* start  of callbacks passed from ModelPermit *******************

    @Override
    // method to return code to  MainActivity
    public void OnFinishedSetPermitIDtextView( String value ) {
        if (mainView != null) {
            mainView.setPermitIdTextView( value );
        }
    }

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

    @Override
    // method to return code to  MainActivity
    public void OnFinishedButtonNewListDefineListReqDeps() {
        if (mainView != null) {
            mainView.defineListOfRequiredDeps();
            mainView.setRequiredDepsVisibility(View.GONE);
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedSetPermitBlockState( Enums.PermitBlock state) {
        if (mainView != null) {
            mainView.setViewPermitBlockParams( state );
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedSetPlaceDateComment( String place, String date_st, String date_end, String comment ) {
        if (mainView != null) {
            mainView.setPlaceDateComment( place, date_st, date_end, comment );
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedSetPermitSimpleAdapter( ArrayList<Map<String, String>> data ) {
        if (mainView != null) {
            mainView.setPermitAdapterAndItemClickListener(data);
            mainView.setRequiredDepsVisibility(View.VISIBLE);
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedButtonDepsChooseVisible() {
        if (mainView != null) {
            mainView.setDepartmentBlockVisible();
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedButtonDepsChooseInvisible() {
        if (mainView != null) {
            mainView.setDepartmentBlockInvisible();
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedShowToast( String  value ) {
        if (mainView != null) {
            mainView.showToast(value);
        }
    }
    // ************* end of callbacks passed from ModelPermit *******************

    @Override
    public void onDestroy() {
        modelMain.sendModelDataToServer(this, SERVER_CLEAR_BUSY, dataParameters.getModelDepartmentUser(), "" );
        mainView = null;
    }
}