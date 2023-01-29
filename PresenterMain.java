package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class PresenterMain implements Contract.PresenterMain, KM_Constants, Enums, Contract.ViewMainLayout,
        Contract.ModelMain.OnFinishedSetMainViewLayout,
        Contract.ModelPermit.OnFinishedSetMainViewLayout {

    // creating object of View Interface
    private Contract.ViewMain mainView;

    // creating object of Model Interface
    private Contract.ModelMain modelMain;
    private Contract.ModelPermit modelPermit;
    private DataParameters dataParameters;

    // initiating the objects of View and Model Interface
    public PresenterMain(Contract.ViewMain mainView, Context context ) {

        //this.dataParameters = new DataParameters();
        this.dataParameters = DataParameters.getInstance();
        dataParameters.setStateCode(DATA_WAS_NOT_CHANGED);
        this.mainView = mainView;
        this.modelMain = new ModelMain(context, dataParameters);
        this.modelPermit =  new ModelPermit(dataParameters);
    }

    // ************* start methods passed from View to ModelMain *******************

    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangeSharedPrefs( String department_user ) {
        modelMain.sendModelDataToServer ( this, SERVER_GET_ALL, "", "" );
        dataParameters.setModelDepartmentUser(department_user);
        mainView.setViewDepartmentUser(department_user);
    }

    @Override
    public void onPermissionsGranted( String[] department_array ) {
        modelPermit.initRequiredArray( department_array );
    }

    @Override
    public void onButtonCheckClick() {
        modelMain.sendModelDataToServer ( this, SERVER_GET_ALL, "", "" );
    }

    @Override
    // operations to be performed - BroadcastReceiver trigger
    public void onBroadcastReceive(Intent intent) {
        modelMain.setModelBroadcastReceiver(this, this, intent);
    }

    @Override
    public void onButtonNewClick() {
        //String number = modelMain.getNewPermitNumber();
        //modelPermit.setModelPermitButtonNewClick( this, this, number );
        modelMain.setModelMainButtonNewClick( this, this );
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonDeleteClick() {
        int position = modelPermit.getPosition();
        modelMain.updateDataArrayAfterDelete( this, position );
    }

    @Override
    // method to be called when the Button Delete is clicked
    public void onButtonSaveClick() {
        int position = modelPermit.getPosition();
        modelMain.updateDataArrayAfterSave( this, position );
    }

    @Override
    // method to be called when the Button Put/Show Communications is clicked
    public void onButtonShowMapClick( Context context ) {

        Intent maps_activity = new Intent(context, MapsActivity.class);
        context.startActivity(maps_activity);
    }

    @Override
    public void onMainActivityResume() {

        onButtonSaveClick();
        /*this.dataParameters = DataParameters.getInstance();
        if (!dataParameters.getStateCode().equals(DATA_WAS_NOT_CHANGED)) {
            modelPermit.updateDataAfterMapsActivity( this, this );
        } */
    }
    // ************* start methods passed from View to ModelPermit *******************

    @Override
    public void onButtonDepsChooseClick (String value, String sample, String place, String date_start, String comment) {
        modelPermit.setModelPermitButtonDepsChooseClick(this, this, value, sample );
        modelPermit.setModelPermitPlaceDateComment ( place, date_start, comment );
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
    public void OnFinishedSetPlaceDateComment( String place, String date, String comment ) {
        if (mainView != null) {
            mainView.setPlaceDateComment( place, date, comment );
        }
    }

    @Override
    // method to return code to  MainActivity
    public void OnFinishedSetPermitSimpleAdapter( ArrayList<Map<String, String>> data ) {
        if (mainView != null) {
            mainView.setPermitAdapterAndItemClickListener(data);
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
        mainView = null;
    }
}


//String permit_code = modelPermit.getStateCode();
//String dep_line_data_json = modelPermit.getDepLineDataJson();
//String presenter_main_json = new Gson().toJson(PresenterMain.class);
//String params_json = new Gson().toJson( dataParameters );

//maps_activity.putExtra(DEP_LINE_DATA, dep_line_data_json);
//maps_activity.putExtra(DATA_TYPE, permit_code);
//maps_activity.putExtra(PARAMS_DATA, params_json);

        /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Name","Harneet");
        editor.apply(); */