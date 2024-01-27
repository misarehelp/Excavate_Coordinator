package ru.volganap.nikolay.haircut_schedule;

import android.content.Intent;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public interface Contract {

    interface ActivityReciever {
        void initBroadcastReceiver();
    }

    interface ViewMain {
        // fill in Records List
        void fillInRecordsList( ArrayList<ArrayList<MainScreenData>> data, ArrayList<String> days_interval );
        // refresh Main Status
        void refreshMainStatus( String status );
        // Set Archive status for Upper line
        void setArchiveStatus (Enums.RecordVisibility value);
        // get Max Records Number()
        String getMaxRecordsNumber();
        // Show Toast for View
        void showToast (String value);
        // pass Data To Calendar
        void passDataToCalendar ( HashMap<String, Integer> cal_hashmap, HashMap <String, Boolean> holiday_hashmap, HashMap <String, Integer> note_hashmap );
    }

    interface ViewMainLayout {
        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedRefreshViewStatus( String state );
        // function to be called   once the Handler of Model class completes its execution
        void onFinishedBrUserMadeList(ListViewData listViewData);
        //void onFinishedGetDate(ArrayList<String> days_interval);
        void onFinishedGetServerRecordsData();
        void onFinishedGetServerClientData();
        void onFinishedGetPastFuture(Enums.RecordVisibility value);
    }

// ***************************************************************************************************
// methods defined in Main Presenter
    interface PresenterMain {

        void onChangeSharedPrefs( String key );
        void onChangeDaysBefore( int days );

        //void on Change Server Preferences;
        void onChangeServerPreferences( String max_recs,  int days_before );

        //void on Delete Archive Records;
        void onDeleteArchiveRecords();

        // method to be called when Main Permissions are Granted
        void onPermissionsGranted ();

        void onBroadcastReceive(Intent intent);

        // method to be called when the Button 'Add a record' is clicked
        void onChangeRecordClick(String date, String time, String index, String type, int theme, String command );

        // method to be called when the Button 'Show clients' is clicked
         void onButtonShowHideFreeRecordsClick();

        // method to be called when the Button 'Show clients' is clicked
        void onTextViewDateClick( Calendar calendar, int dayOfWeek );

        void onMainActivityResume();

        // method to destroy lifecycle of MainActivity
        void onDestroy();
    }

    interface ModelMain {

        void getDateFromModelMain ( Contract.ViewMainLayout view_listener, Calendar calendar, int dayOfWeek );

        void getFromModelBroadcastReceiver(  Contract.ViewMainLayout view_listener, Intent intent );

        void sendModelDataToServer (  Contract.ViewMainLayout view_listener, String command, String dateID, String value);

        void changeFreeRecordsState ();
        void changeDaysBefore (int days);
    }

    // ***************************************************************************************************
    interface CalendarFragmentToMainActivity {
        void  onDateSet(Calendar calendar, int dayOfWeek, String date_str );
    }

    // ***************************************************************************************************
    interface MainActivityToCalendarFragment {
        void setCalendarHashMap (HashMap<String, Integer> cal_hashmap, HashMap <String, Boolean> holiday_hashmap,
                                 HashMap <String, Integer> note_hashmap, HashMap<String, Integer> calendar_colors);
        void syncCalendarDayToPage (int day);
    }

    // ***************************************************************************************************
    interface RecordActivityToRecordFragment {
        void  onLoadPictureResult( boolean done );
        void onPhotoFragmentViewCreatedToRecord();
    }
    // ***************************************************************************************************

// ***************************************************************************************************
    interface RecordActivityToSomeFragment {
        void  onGetClientDataToFragment( ClientData value );
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
        void onGetClientDataToActivity(ClientData value, boolean show_hystory);
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
        void setRecordButtonsVisibility (String code);
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
        // method to be called when the Button 'Add, Change or Delete Record' is clicked
        void onButtonChangeRecord(RecordData rec_data, String command_code );
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

        void changeRecordData ( Contract.ModelRecord.OnPresenterRecordCallback listener, RecordData rec_data, String command_code  );
        void getLastIncomingCall ( Contract.ModelRecord.OnPresenterRecordCallback listener );
        void getFromModelBroadcastReceiver( Contract.ModelRecord.OnPresenterRecordCallback act_listener, Intent intent );
        RecordData getSelectedRecordData ();
        void sendSMS ( Contract.ModelRecord.OnPresenterRecordCallback listener, RecordData rec_data, String message );
    }

// ***************************************************************************************************
interface ViewClientList {
    // set View ClientList fields with values
    void showClients (ArrayList<ClientData> value);
    void setRecordButtonsVisibility (String code);
    // Show Toast for View
    void showToast (String value);
}
interface PresenterListClient {
    void onBroadcastReceive(Intent intent);
    // method to be called when the Button 'Delete Record' is clicked
    void onItemChangeClientClick( int position, String name, String phone );
    // method to be called when the Button 'Delete Record' is clicked
    void onItemDeleteClientClick( int position );
    // method to be called when the Button 'Save' is clicked
    void onButtonAddNewClient( String name, String phone );
    // method to be called when the Button 'Show records for chosen Client' is clicked
    void onItemShowClientJob( String id );
    // method to destroy lifecycle of RecordActivity
    void onDestroy();
}

    interface ModelClientList {
        interface OnPresenterClientListCallback {
            // function to be called   once the Handler of Model class completes its execution
            void onShowToast( String value );
            void onUpdateRecycleData( ArrayList<ClientData> client_data_array );
        }

        void getClientID ( ClientData client );
        void deleteClient ( int position );
        void changeClient ( int position, ClientData client );
        void getFromModelBroadcastReceiver( Contract.ModelClientList.OnPresenterClientListCallback act_listener, Intent intent );
        void getArchiveClientById ( String id );
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

