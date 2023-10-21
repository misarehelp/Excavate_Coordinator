package ru.volganap.nikolay.haircut_schedule;

import android.content.Intent;
import android.graphics.Bitmap;

import java.util.ArrayList;

public interface Contract {

    interface ActivityReciever {
        void initBroadcastReceiver();
    }

    interface ViewMain {
        // fill in Records List
        void fillInRecordsList( ArrayList<ArrayList<MainScreenData>> data, ArrayList<String> days_interval, ArrayList<String> day_of_week, int [] sum_of_rec );
        // refresh Main Status
        void refreshMainStatus( String status );
        // Set Archive status for Upper line
        void setArchiveStatus (boolean value);
        // set ShowHide Button Visibility
        void setShowHideButtonVisibility (boolean value);
        // get Max Records Number()
        String getMaxRecordsNumber();
        // Show Toast for View
        void showToast (String value);
    }

    interface ViewMainLayout {
        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedRefreshViewStatus( String state );
        // function to be called   once the Handler of Model class completes its execution
        void onFinishedBrUserMadeList(ListViewData listViewData);
        //void onFinishedGetDate(ArrayList<String> days_interval);
        void onFinishedGetServerRecordsData();
        void onFinishedGetServerClientData();
        void onFinishedGetPastFuture(boolean future_recs);
    }

// ***************************************************************************************************
// methods defined in Main Presenter
    interface PresenterMain {

        //void onChangeSharedPrefs( String department_user, boolean dispatcher_on );
        void onChangeSharedPrefs( String key );
        void onChangeDaysBefore( int days );

        //void on Change Server Preferences;
        void onChangeServerPreferences( String max_recs,  int days_before );

        // method to be called when Main Permissions are Granted
        void onPermissionsGranted ();

        void onBroadcastReceive(Intent intent);

        // method to be called when the Previous Next is clicked
        void onButtonPreviousWeekClick();

        // method to be called when the Button Next is clicked
        void onButtonNextWeekClick();

        // method to be called when the Button 'Add a record' is clicked
        void onChangeRecordClick(String date, String time, String index, String type, int theme, String command );

        // method to be called when the Button 'Show clients' is clicked
         void onButtonShowHideFreeRecordsClick();

        // method to be called when the Button 'Show clients' is clicked
        void onTextViewDateClick( int year, int monthOfYear, int dayOfMonth );

        void onMainActivityResume();

        // method to destroy lifecycle of MainActivity
        void onDestroy();
    }

    interface ModelMain {

        void getDateFromModelMain ( Contract.ViewMainLayout view_listener, int value, int year, int monthOfYear, int dayOfMonth );

        void getFromModelBroadcastReceiver(  Contract.ViewMainLayout view_listener, Intent intent );

        void sendModelDataToServer (  Contract.ViewMainLayout view_listener, String command, String dateID, String value);

        void changeFreeRecordsState ();
        void changeDaysBefore (int days);
    }

    // ***************************************************************************************************
    interface RecordActivityToRecordFragment {
        void  onLoadPictureResult( boolean done );
        void onPhotoFragmentViewCreatedToRecord();
    }
    // ***************************************************************************************************

// ***************************************************************************************************
    interface RecordActivityToSomeFragment {
        void  onGetClientDataToFragment( String name, String phone );
    }
    // ***************************************************************************************************
    interface RecordActivityToPhotoFragment {
        void  getSavedFileByName( String filename, int host );
        void  saveBitmapToRepository( Bitmap bitmap, String filename, int host );
        void  deletePictureFile( String filename );
    }
    // ***************************************************************************************************

    interface RecordActivityToFragmentBroadcast {
        void  onBroadcastReceive(Intent intent);
    }

    // ***************************************************************************************************
    interface RecordFragmentToRecordActivity{
        void finishRecordActivity();
        void doPictureAction(Enums.PhotoType picture_type, String filename );
        void deletePictureFile( String filename );
        void  passClientDataToActivity( String name, String phone );
    }

    // ***************************************************************************************************
    interface SomeFragmentToRecordActivity{
        void backToRecordFragment(int host);
    }

    // ***************************************************************************************************
    interface ClientFragmentToRecordActivity {
        void onGetClientDataToActivity(String name, String phone, boolean show_hystory);
    }

    // ***************************************************************************************************
    interface PhotoFragmentToRecordActivity {
        void onSavePictureResult( boolean value);
        void onLoadPictureResult( boolean value);
        void onPhotoFragmentViewCreated();
    }

    // ***************************************************************************************************
// methods defined in Record block
    interface ViewRecord {
        // fill in Records List
        void fillRecordInfoFields ( RecordData rec_data );
        void setRecordButtonsVisibility (String permit_code);
        // Show Toast for View
        void showToast (String value);
        //set last phone number into TextView field
        void setLastCallText (String value);
        // finish Record Activity
        void finishRecordActivity ();
        // method to destroy lifecycle of RecordActivity
    }

    interface PresenterRecord {

        void onBroadcastReceive(Intent intent);
        // method to be called when the Button 'Save' is clicked
        void onButtonSave( RecordData rec_data );
        // method to be called when the Button 'Delete Record' is clicked
        void onButtonChangeRecord(RecordData rec_data );
        // method to be called when the Button 'Delete Record' is clicked
        void onButtonDeleteRecord();
        // method to be called when the Button 'Add phone from a phone book' is clicked
        void onButtonAddLastCall();
        // method to be called when the Button 'Make Call' is clicked
        void onButtonMakeCall( String number );
        // method to be called when the Button 'Send SMS' is clicked
        void onButtonSendSms( RecordData rec_data, String message );
        // method to be called when the Button 'Exit' is clicked
        void onButtonExit();
        // method to destroy lifecycle of RecordActivity
        void onDestroy();
    }

    interface ModelRecord {
        // nested interface for PresenterRecord
        interface OnPresenterRecordCallback {
            // function to be called   once the Handler of Model class completes its execution
            void onCloseRecordAction( );
            void onShowToast( String value );
            void onFinishedGetLastCall( String value );
        }

        void addRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, RecordData rec_data );
        void changeRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, RecordData rec_data  );
        void deleteRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener );
        void getLastIncomingCall ( Contract.ModelRecord.OnPresenterRecordCallback listener );
        void getFromModelBroadcastReceiver( Contract.ModelRecord.OnPresenterRecordCallback act_listener, Intent intent );
        RecordData getSelectedRecordData ();
        void sendSMS ( Contract.ModelRecord.OnPresenterRecordCallback listener, RecordData rec_data, String message );
    }

// ***************************************************************************************************
interface ViewClientList {
    // set View ClientList fields with values
    void showClients (ArrayList<ClientData> value);
    void setRecordButtonsVisibility (String permit_code);
    // Show Toast for View
    void showToast (String value);
}
interface PresenterListClient {
    void onBroadcastReceive(Intent intent);
    // method to be called when the Button 'Delete Record' is clicked
    void onItemChangeClientClick( int position, String name, String phone, String comment );
    // method to be called when the Button 'Delete Record' is clicked
    void onItemDeleteClientClick( int position );
    // method to be called when the Button 'Save' is clicked
    void onButtonAddNewClient( String name, String phone, String comment );
    // method to be called when the Button 'Show records for chosen Client' is clicked
    void onItemShowClientJob( String phone );
    // method to destroy lifecycle of RecordActivity
    void onDestroy();
}

    interface ModelClientList {
        interface OnPresenterClientListCallback {
            // function to be called   once the Handler of Model class completes its execution
            void onShowToast( String value );
            void onUpdateRecycleData( ArrayList<ClientData> client_data_array );
        }

        void addClient ( ClientData client );
        void deleteClient ( int position );
        void changeClient ( int position, ClientData client );
        void getFromModelBroadcastReceiver( Contract.ModelClientList.OnPresenterClientListCallback act_listener, Intent intent );
        void getArchiveClientByPhone ( String phone );
    }

    interface Recycle {
        interface MainInterface {
            void onItemClick(String index, String time, String type);
        }

        interface ClientInterface {
            void onItemClick(int position, String command, ClientData clientData );
        }

        interface HistoryInterface {
            void onItemClick( String filename );
        }
    }

// methods defined in Data Parameters Util
    interface DataParameters {
// ***************************************************************************************************
        //Get Record Data
        ArrayList<RecordData> getRecordDataArray();
        //Get Client Data
        ArrayList<ClientData> getClientDataArray();
        //Get Record Data
        void setRecordDataArray( ArrayList<RecordData> value);
        //Set Client Data
        void setClientDataArray( ArrayList<ClientData> value);
        // Get State Code
        String getStateCode();
        // Set State Code
        void setStateCode(String code);
    }
}

