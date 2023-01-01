package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

public class Presenter implements Contract.Presenter, KM_Constants, Contract.ModelMain.OnFinishedListenerPermitBlock,
                                                    Contract.ModelMain.OnFinishedListenerDepartmentUser,
                                                    Contract.ModelMain.OnFinishedListenerBroadcastReceiver,
                                                    Contract.ModelPermit.OnFinishedListenerButtonNewPermitClick,
                                                    Contract.ModelPermit.OnFinishedListenerListItemClick {

    // creating object of View Interface
    private Contract.View mainView;

    // creating object of Model Interface
    private Contract.ModelMain modelMain;
    private Contract.ModelPermit modelPermit;

    // initiating the objects of View and Model Interface
    public Presenter(Contract.View mainView, Contract.ModelMain modelMain, Contract.ModelPermit modelPermit) {
        this.mainView = mainView;
        this.modelMain = modelMain;
        this.modelPermit = modelPermit;
    }

    // ************* start methods passed from View to ModelMain *******************
    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangePermitBlockViewParams( ViewGroup.LayoutParams params ) {
        modelMain.setModelParams(this, params);
    }

    @Override
    // operations to be performed - Init Main MainViewLayout
    public void onChangeSharedPrefs( String department_user ) {
        modelMain.setModelUser(this, department_user);
    }

    @Override
    // operations to be performed - BroadcastReceiver trigger
    public void onBroadcastReceive(String message) {
        modelMain.setModelBroadcastReceiver(this, message);
    }

    @Override
    // operations to be performed - BroadcastReceiver trigger
    public void updateViewServerData( String command, String depID, String value) {
        modelMain.sendModelDataToServer ( command, depID, value);
        mainView.refreshMainStatus(DATA_REQUEST_PROCESSING);
    }

    @Override
    // operations to be performed - View Data changerd
    public void onChangeViewData(String type) {
        modelMain.updateModelDepLineDataArray( modelPermit, type );
    }

    // ************* start methods passed from View to ModelPermit *******************

    @Override
    public void onButtonNewClick() {
        modelPermit.setModelPermitButtonNewClick( this, modelMain );
    }

    @Override
    public void onListViewItemClick( int position, int id, int viewItew) {
        modelPermit.setModelPermitListItemClick( this, modelMain, position, id, viewItew );
    }

    // ************* end of methods passed from View *******************

    // ************* start  of callbacks passed from ModelMain *******************
    @Override
    // method to return Layout Params of Permit Block
    public void onFinishedPermitBlock (ViewGroup.LayoutParams params) {
        if (mainView != null) {
            mainView.setViewPermitBlockParams(params);
        }
    }

    @Override
    // method to return Dapartment name
    public void onFinishedDepartmentUser (String department_user) {
        if (mainView != null) {
            mainView.setViewDepartmentUser(department_user);
        }
    }

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
    // ************* end of callbacks passed from Model *******************

    // ************* start  of callbacks passed from ModelPermit *******************

    @Override
    // method to return code to  MainActivity
    public void OnFinishedButtonNewListItemClick( String code ) {
        if (mainView != null) {
            mainView.runPermitBlock(code);
        }
    }

    // ************* end of callbacks passed from ModelPermit *******************

    @Override
    public void onDestroy() {
        mainView = null;
    }

}

