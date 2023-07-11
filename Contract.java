package ru.volganap.nikolay.haircut_schedule;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Map;

public interface Contract {

    interface ActivityReciever {
        void initBroadcastReceiver();
    }

    interface ViewMain {
        // fill in Records List
        void fillInRecordsList( ArrayList<ArrayList<Map<String, String>>> data, ArrayList<String> days_interval, ArrayList<String> day_of_week );
        // refresh Main Status
        void refreshMainStatus( String status );
        // Set Permit Fields Visibility for View
        void setViewButtonsFieldsVisibility (String permit_code);
        // Show Toast for View
        void showToast (String value);
        // set View value of current Date
        //void setViewDate( ArrayList<String> days_interval );
        // get Max Records Number()
        String getMaxRecordsNumber();

        // Set Adapter And Item Click Listener for View
        //void setPermitAdapterAndItemClickListener ( ArrayList<Map<String, String>> data );
    }

    interface ViewMainLayout {

        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedButtonSaveSetViewButtonsVisibility( String state );
        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedRefreshViewStatus( String state );
    }

// ***************************************************************************************************
// methods defined in Main Presenter
    interface PresenterMain {

        //void onChangeSharedPrefs( String department_user, boolean dispatcher_on );
        void onChangeSharedPrefs( String key );

        //void on Change Server Preferences;
        void onChangeServerPreferences( String value );

        // method to be called when Main Permissions are Granted
        void onPermissionsGranted ();

        void onBroadcastReceive(Intent intent);

        // method to be called when the Previous Next is clicked
        void onButtonPreviousWeekClick();

        // method to be called when the Button Next is clicked
        void onButtonNextWeekClick();

        // method to be called when the Button 'Add a record' is clicked
        void onButtonAddRecordClick(String date);

        // method to be called when the Button 'Add a client' is clicked
        void onButtonAddClientClick();

        // method to be called when the Button 'Exit' is clicked
        void onButtonExitClick();

        // method to be called when the Button 'Show clients' is clicked
        void onButtonShowClientsClick();

        // method to be called when the Button 'Show clients' is clicked
        void onTextViewDateClick( int year, int monthOfYear, int dayOfMonth );

        // method to be called when Main ListView item is clicked
        void onMainListViewItemClick( int position);

        // on Menu item "Show Archive" click
        void onShowArchiveClick();

        void onMainActivityResume();

        // method to destroy lifecycle of MainActivity
        void onDestroy();
    }

    interface ModelMain {

        // nested interface for BroadcastReciever
        interface OnPresenterMainCallBack {
            //interface OnFinishedSetMainViewLayout {
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedBrUserMadeList(ListViewData listViewData);
            //void onFinishedGetDate(ArrayList<String> days_interval);
            void onFinishedGetServerData();
        }

        void getDateFromModelMain ( Contract.ModelMain.OnPresenterMainCallBack main_listener, int value, int year, int monthOfYear, int dayOfMonth );

        //void getDatePickerModelMain ( Contract.ModelMain.OnPresenterMainCallBack main_listener, int year, int monthOfYear, int dayOfMonth );

        void getFromModelBroadcastReceiver( Contract.ModelMain.OnPresenterMainCallBack main_listener, Contract.ViewMainLayout view_listener, Intent intent );

        void sendModelDataToServer (  Contract.ViewMainLayout view_listener, String command, String dateID, String value);

        //void setModelMainButtonNewClick( Contract.ModelPermit.OnFinishedSetMainViewLayout main_listener, Contract.ViewMainLayout view_listener );
    }

// ***************************************************************************************************
// methods defined in Record block
    interface ViewRecord {
        // fill in Records List
        //void fillInJobsList( ArrayList<Map<String, String>> data );
        // set RecordView fields with values
        void fillRecordInfoFields ( ArrayList<String> rec_data );
        void setRecordButtonsVisibility (String permit_code);
        // Show Toast for View
        void showToast (String value);
        // finish Record Activity
        void finishRecordActivity ();
        // method to destroy lifecycle of RecordActivity
        void onDestroy();
    }

    interface PresenterRecord {

        void onBroadcastReceive(Intent intent);
        // method to be called when the Button 'Save' is clicked
        void onButtonSave( ArrayList<String> rec_data );

        // method to be called when the Button 'Delete Record' is clicked
        void onButtonChangeRecord(ArrayList<String> rec_data );

        // method to be called when the Button 'Delete Record' is clicked
        void onButtonDeleteRecord();

        // method to be called when the Button 'Exit' is clicked
        void onButtonExit();

        // method to be called when the Button 'Show records for chosen Client' is clicked
        void onButtonShowClientJob();

        // method to be called when the Button 'Add phone from a phone book' is clicked
        void onButtonAddFromBook();

        // method to be called when Record ListView item is clicked
        void onRecordListViewItemClick( int position, int id, int viewItew);

        // method to destroy lifecycle of RecordActivity
        void onDestroy();
    }

    interface ModelRecord {

        // nested interface for PresenterRecord
        interface OnPresenterRecordCallback {
            // function to be called   once the Handler of Model class completes its execution
            void onCloseRecordAction( );
            void onShowToast( String value );

        }
        void addRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, ArrayList<String> rec_data );

        void changeRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, ArrayList<String> rec_data  );

        void deleteRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener );

        void getFromModelBroadcastReceiver( Contract.ModelRecord.OnPresenterRecordCallback act_listener, Contract.ViewMainLayout view_listener, Intent intent );

        ArrayList<String> getSelectedRecordData ();

        //void updateDataArrayAfterDelete ( Contract.ViewMainLayout view_listener );

        //void updateDataArrayAfterSave ( Contract.ViewMainLayout view_listener );
    }

// ***************************************************************************************************
// methods defined in Record block
    interface ViewClient {
        // fill in Records List
        void showClientData( );
        //void setViewButtonsFieldsVisibility (String permit_code);
        // Show Toast for View

    }


// methods defined in Data Parameters Util
    interface DataParameters {
// ***************************************************************************************************
        //Get Record Data
        ArrayList<RecordData> getRecordDataArray();

        //Get Record Data
        void setRecordDataArray( ArrayList<RecordData> value);

        // Get State Code
        String getStateCode();

        // Set State Code
        void setStateCode(String code);

    }
}

