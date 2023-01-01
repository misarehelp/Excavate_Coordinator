package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

public interface Contract {
    interface View {

        // set Permit Block LayoutParams
        void setViewPermitBlockParams(ViewGroup.LayoutParams params);

        // get View value of department user from resources
        String getViewDepartmentUser();

        // set View value of department user
        void setViewDepartmentUser(String user);

        // set Buttons Interface
        void setButtonsInterface(String message);

        // fill in User Made List
        void fillInPermitsUserMadeList(ArrayList<Map<String, String>> data);

        // fill in User Awaiting List
        void fillInPermitsAwaitingList(ArrayList<Map<String, String>> data);

        // refresh Main Status
        void refreshMainStatus(String status);

        // Run Permit Block
        void runPermitBlock(String permit_code);
    }

    interface ModelMain {
        // nested interface for PermitBlock
        interface OnFinishedListenerPermitBlock {
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedPermitBlock(ViewGroup.LayoutParams params);
        }

        void setModelParams(Contract.ModelMain.OnFinishedListenerPermitBlock onFinishedListener, ViewGroup.LayoutParams params);

        // ***************************************************************************************************

        // nested interface for DepartmentUser TextView
        interface OnFinishedListenerDepartmentUser {
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedDepartmentUser(String string);
        }

        void setModelUser(Contract.ModelMain.OnFinishedListenerDepartmentUser onFinishedListener, String department_user);

        // ***************************************************************************************************

        // nested interface for BroadcastReciever
        interface OnFinishedListenerBroadcastReceiver {
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedBrUserMadeList(ArrayList<Map<String, String>> data);

            // function to be called   once the Handler of Model class completes its execution
            void onFinishedBrAwaitingList(ArrayList<Map<String, String>> data);
        }

        void setModelBroadcastReceiver( Contract.ModelMain.OnFinishedListenerBroadcastReceiver onFinishedListener, String message );

        // ***************************************************************************************************

        void sendModelDataToServer ( String command, String depID,String value);

        // ***************************************************************************************************

        void updateModelDepLineDataArray ( Contract.ModelPermit modelPermit, String type );

        // ***************************************************************************************************

        ArrayList<DepLinesData> getModelDepLinesDataArray();

        ArrayList<Integer> getPermitArray();

        String getModelDepartmentUser();
    }

    interface ModelPermit {

        // nested interface for Button New Permit Click
        interface OnFinishedListenerButtonNewPermitClick {
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedButtonNewListItemClick( String code );
        }

        void setModelPermitButtonNewClick( Contract.ModelPermit.OnFinishedListenerButtonNewPermitClick onFinishedListener,
                                           Contract.ModelMain modelMain );

        // ***************************************************************************************************

        // nested interface for ListView Item Click
        interface OnFinishedListenerListItemClick {
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedButtonNewListItemClick( String code );
        }

        void setModelPermitListItemClick( Contract.ModelPermit.OnFinishedListenerListItemClick onFinishedListener,
                                          Contract.ModelMain modelMain, int position, int id, int viewItew);

        // ***************************************************************************************************

        // set DepLinesData
        void setDepLineData(DepLinesData dep_line_data);

        // get DepLinesData
        DepLinesData getDepLineData();

        //set Position for List View
        void setPosition(int position);

        //get Position from List View
        Integer getPosition();
    }


    interface Presenter {
        // method to be called when Main Activity initiliazes
        void onChangePermitBlockViewParams( ViewGroup.LayoutParams params );

        // method to be called when Main Activity initiliazes
        void onChangeSharedPrefs( String department_user );

        // method to be called when Main Activity initiliazes
        void onBroadcastReceive(String message);

        // method to be called when Main Activity initiliazes
        void updateViewServerData (String command, String depID,String value);

        // fill in User Awaiting List
        void onChangeViewData(String status);

        // method to be called when either the Button or ListView item is clicked
        void onButtonNewClick();

        // method to be called when either the Button or ListView item is clicked
        void onListViewItemClick( int position, int id, int viewItew);

        // method to destroy lifecycle of MainActivity
        void onDestroy();
    }
}

