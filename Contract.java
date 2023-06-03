package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

public interface Contract {

    interface ViewMain {
        //  set View Permit Block Params
        void setViewPermitBlockParams( Enums.PermitBlock state );

        //  set View User Made Block Visibility
        void setViewUserMadeBlockVisibility( boolean state );

        // get View value of department user from resources
        String getViewDepartmentUser();

        // get View Department User()
        String getViewModeUser();

        // get View Dispatcher Default()
        String getDispatcherDefault();

        // get Max Records Number()
        String getMaxRecordsNumber();

        // set View value of department user
        void setViewDepartmentUser( String user );

        // get Resourses values of department array
        String [] getDepartmentValuesArray();

        // get Resourses entries of department array
        String [] getDepartmentEntriesArray();

        // fill in User Made List
        void fillInPermitsUserMadeList( ArrayList<Map<String, String>> data );

        // fill in User Awaiting List
        void fillInPermitsAwaitingList( ArrayList<Map<String, String>> data );

        // refresh Main Status
        void refreshMainStatus( String status );

        // Set Permit Id
        void setPermitIdTextView( String id );

        // Set Place DateStart and Comment fields for View
        void setPlaceDateComment (String place, String date_start, String date_end, String comment );

        // Set Permit Fields Visibility for View
        void setViewButtonsFieldsVisibility (String permit_code);

        // Define List Of Required Deps for View
        void defineListOfRequiredDeps ();

        // Set Adapter And Item Click Listener for View
        void setPermitAdapterAndItemClickListener ( ArrayList<Map<String, String>> data );

        // Set set Department Block Visible for View
        void setDepartmentBlockVisible ();

        // Set set Department Block Invisible for View
        void setDepartmentBlockInvisible ();

        // Show Toast for View
        void showToast (String value);

        // set Required Deps Visibility
        void setRequiredDepsVisibility (int state);

        // Run Maps Activity
        //void runMapsActivity( Intent intent );
    }

    interface ViewMap {
        //set the center of the map
        void centerMap (LatLng center_point);

        // refresh Maps Status
        void refreshMapStatus(String status);

        // set User TextView
        void setUserTextView(String status);

        // set Up Maps Buttons Appearance
        void setUpButtonsAppearance(String status);

        // set Up Maps Buttons Appearance
        void setMarkerLine ( LatLng latLng1, LatLng latLng2, String depart, String date_reg, int color, boolean master );

        // set Up Maps Buttons Appearance
        void setMasterRectangle ( LatLng latLng1, LatLng latLng2, String depart, String date_reg, int color_line, int color_back );

        // set On Map Click Listenerns
        void setOnMapClickListenerns ();

        // Clear Map
        void clearMap ();

        // Add Marker on Map
        void addMarker (LatLng latLng);

        // Add Color Legend of Departments
        void addColorRowToLegend( String[] depart, int [] color_text, int [] color_bg );

        // finish Maps Activity
        void finishMapsActivity();
    }

    interface ViewMainLayout {

        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedButtonSaveSetViewButtonsVisibility( String state );
        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedSetPermitBlockState( Enums.PermitBlock state );
        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedRefreshViewStatus( String state );
        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedGetNumberOfServerRecords();
        // function to be called   once the Handler of Model class completes its execution
        void OnFinishedClearBusyServerStatus();
    }

    interface ModelMain {

        // nested interface for BroadcastReciever
        interface OnFinishedSetMainViewLayout {
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedBrUserMadeList(ArrayList<Map<String, String>> data);
            // function to be called   once the Handler of Model class completes its execution
            void onFinishedBrAwaitingList(ArrayList<Map<String, String>> data);
        }

        void setModelBroadcastReceiver( Contract.ModelMain.OnFinishedSetMainViewLayout main_listener,
                                        Contract.ViewMainLayout view_listener, Intent intent );

        void getBackWithServer ( String command, String depID, String value );

        void sendModelDataToServer ( Contract.ViewMainLayout view_listener, String command, String depID, String value);

        void updateDataArrayAfterDelete ( Contract.ViewMainLayout view_listener );

        void updateDataArrayAfterSave ( Contract.ViewMainLayout view_listener );

        void setModelMainButtonNewClick( Contract.ModelPermit.OnFinishedSetMainViewLayout main_listener,
                                           Contract.ViewMainLayout view_listener );

        // ***************************************************************************************************

        ArrayList<DepLinesData> getModelDepLinesDataArray();

        ArrayList<Integer> getPermitArrayUserMade();

        ArrayList<Integer> getPermitArrayAwaiting();

        //String getNewPermitNumber();
    }

    interface ModelPermit {

        // nested interface for Button New Permit Click
        interface OnFinishedSetMainViewLayout {
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedSetPermitIDtextView( String value );
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedSetPlaceDateComment( String place, String date_st, String date_end, String comment );
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedButtonNewListDefineListReqDeps();
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedSetPermitSimpleAdapter( ArrayList<Map<String, String>> data );
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedButtonDepsChooseVisible();
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedButtonDepsChooseInvisible();
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedShowToast( String  value );
        }

        void setModelPermitListItemClick( Contract.ModelPermit.OnFinishedSetMainViewLayout permit_listener,
                                          Contract.ViewMainLayout view_listener, Contract.ModelMain modelMain, int position, int id, int viewItew);

        //void updateDataAfterMapsActivity( Contract.ModelPermit.OnFinishedSetMainViewLayout permit_listener, Contract.ViewMainLayout view_listener );

        void setModelPermitButtonDepsChooseClick( Contract.ModelPermit.OnFinishedSetMainViewLayout permit_listener,
                                                  Contract.ViewMainLayout view_listener, String value, String sample );

        // ***************************************************************************************************

        //set Required Deps Array for Permit List View
        void setRequiredDepsArray (AdapterView<?> parent);

        //set  Model Permit Place Date Comment
        void setModelPermitPlaceDateComment(String place, String date_start, String date_end, String comment);
    }

    interface ModelMap {

        // nested interface for Init Map
        interface CallbackSetMapsLayout {
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedSetUpButtonsAppearance(String value);

            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedRefreshMapStatus(String value);

            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedSetUser(String value);

            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedDrawLegend( String[] depart, int [] color_text, int [] color_bg );
        }

        void initMap( Contract.ModelMap.CallbackSetMapsLayout listener, Intent intent );

        // nested interface for Drawing Lines on the Map
        interface CallbackOnMapReady {
            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedSetMarkerLine(MarkerLineData value);

            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedCenterMap(LatLng value);

            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedSetOnMapClickListenerns();

            // function to be called   once the Handler of Model class completes its execution
            void OnFinishedMapClearListenerns();

            // function to be called   once the Handler of Model class completes its execution
            void OnAddMarkerListenerns(LatLng latLng);

            // function to be called   once the Handler of Model class completes its execution
            void OnCloseMapsListenerns();
        }

        void getSavedLines( Contract.ModelMap.CallbackOnMapReady listener );

        void getCenterMap( Contract.ModelMap.CallbackOnMapReady listener );

        void checkOnMapClickListenerns( Contract.ModelMap.CallbackOnMapReady listener );

        void doButtonContourLineLogic( Contract.ModelMap.CallbackOnMapReady map_listener,  Contract.ModelMap.CallbackSetMapsLayout layout_listener );

        void doButtonEndLineLogic( Contract.ModelMap.CallbackSetMapsLayout listener );

        void doButtonMapClearLogic( Contract.ModelMap.CallbackOnMapReady map_listener,  Contract.ModelMap.CallbackSetMapsLayout layout_listener );

        void doButtonMapExitLogic( Contract.ModelMap.CallbackOnMapReady map_listener );

        void doButtonCheckNoLinesLogic( Contract.ModelMap.CallbackOnMapReady map_listener );

        void doMapClickLogic( Contract.ModelMap.CallbackOnMapReady map_listener,  Contract.ModelMap.CallbackSetMapsLayout layout_listener, LatLng latLng );

    }
        // ***************************************************************************************************
// methods defined in Main Presenter
    interface PresenterMain {

        //void onChangeSharedPrefs( String department_user, boolean dispatcher_on );
        void onChangeSharedPrefs( String key );

        //void on Change Server Preferences;
        void onChangeServerPreferences( String value );

        // method to be called when Main Permissions are Granted
        void onPermissionsGranted ( String[] department_array );

        // method to be called when Main Activity initiliazes
        void onBroadcastReceive(Intent intent);

        // on Button Delete Click
        void onButtonDeleteClick();

        // on Menu item "Show Archive" click
        void onShowArchiveClick();

        // on Menu item "Clear ID counter" click
        void onClearIdCounterClick();

        // on Menu item "Delete all permits" click
        void onDeleteAllPermitsClick();

        // on Button Save Click
        void onButtonExitClick();

        // method to be called when the Button Check is clicked
        void onButtonCheckClick();

        // method to be called when the Button Check is clicked
        void onButtonDepsChooseClick( String value, String sample, String place, String date_start, String date_end, String comment );

        // method to be called when the Button New is clicked
        void onButtonNewClick();

        // method to be called when Main ListView item is clicked
        void onMainListViewItemClick( int position, int id, int viewItew);

        // method to be called when Main ListView item is clicked
        void onButtonShowMapClick( Context context );
        //void onButtonShowMapClick( Context context, ActivityResultLauncher<Intent> activityResultLaunch );

        // method to be called when onActivityResult is triggered
        void onMainActivityResume();

        // method to be called when Main ListView item is clicked
        void onPermitListViewItemClick( AdapterView<?> parent);

        // method to destroy lifecycle of MainActivity
        void onDestroy();
    }

// methods defined in Maps Presenter
    interface PresenterMaps {
    // ***************************************************************************************************
        //method to do logic after Maps is ready
        void onMapReady();

        //method to do logic after Button Contour Line is Clicked
        void onButtonContourLineClick();

        //method to do logic after Button End Line is Clicked
        void onButtonEndLineClick();

        //method to do logic after Button Map Clear is Clicked
        void onButtonMapClearClick();

        //method to do logic after Button Map Exit is Clicked
        void onButtonMapExitClick();

        //method to do logic after Button Check No Lines is Clicked
        void onButtonCheckNoLinesClick();

        //method to do logic for drawing lines OnMapClickListener
        void OnMapClickListener( LatLng latLng);

        // method to destroy lifecycle of MainActivity
        void onDestroy();
    }

// methods defined in Data Parameters Util
    interface DataParameters {
        // ***************************************************************************************************
        //Set Department Array
        void setDepartmentArray(String[] department_array);

        //Get Department Array
        String[] getDepartmentArray();

        // Get Model Department User
        String getModelDepartmentUser();

        // Set Model Department User
        void setModelDepartmentUser(String department_user);

        // Set DepLinesData
        void setDepLineData(DepLinesData dep_line_data);

        // Get DepLinesData
        DepLinesData getDepLineData();

        // Get State Code
        String getStateCode();

        // Set State Code
        void setStateCode(String code);

    }
}

