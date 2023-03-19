package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelMain implements Contract.ModelMain, KM_Constants, Enums {

    private String  SERVER_ADD_PERMIT = "server_add_permit";
    private String  SERVER_CHANGE_PERMIT = "server_change_permit";
    private String  SERVER_DELETE_PERMIT = "server_delete_permit";

    private ArrayList<DepLinesData> dep_lines_data_array  = new ArrayList<>();
    private ArrayList<Integer> permit_array_user_made, permit_array_awaiting;
    private Context context;

    private DataParameters dataParameters;
    private final HashMap<String, String> departServerHashMap  = new HashMap<>();
    private final HashMap<String, String> departViewHashMap  = new HashMap<>();

    // initiating the objects of Model
    public ModelMain(Context context, DataParameters dataParameters, String [] entries, String [] values) {
        this.context = context;
        this.dataParameters = dataParameters;
        for (int i = 0; i < entries.length; i++) {
            departServerHashMap.put( entries[i], values[i] );
            departViewHashMap.put( values[i], entries[i] );
        }
    }

    // get dep_lines_data_array
    public ArrayList<DepLinesData> getModelDepLinesDataArray() {
        return dep_lines_data_array;
    }

    // Convert DepLinesData into Json
    private String getFromDepLineDataToJson(DepLinesData dep_line_data) {
        return new Gson().toJson(dep_line_data);
    }

    // Convert Json  into DepLinesData
    private DepLinesData getFromJsonToDepLineData(String json_data) {
        return new Gson().fromJson(json_data, DepLinesData.class);
    }

    // set permit_array for User Made List
    private void setPermitArrayUserMade(ArrayList<Integer> permit_array_user_made) {
        this.permit_array_user_made = permit_array_user_made;
    }

    // get permit_array for User Made List
    public ArrayList<Integer> getPermitArrayUserMade() {
        return permit_array_user_made;
    }

    // set permit_array for User Made List
    private void setPermitArrayAwaiting(ArrayList<Integer> permit_array_awaiting) {
        this.permit_array_awaiting = permit_array_awaiting;
    }

    // get permit_array for User Made List
    public ArrayList<Integer> getPermitArrayAwaiting() {
        return permit_array_awaiting;
    }

    // this method will invoke when BroadcastReceiver trigger
    @Override
    public void setModelBroadcastReceiver(Contract.ModelMain.OnFinishedSetMainViewLayout main_listener,
                                          Contract.ViewMainLayout view_listener, Intent intent) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String message = intent.getStringExtra(MESSAGE);
                String status = intent.getStringExtra(SENDER);

                switch ( status ) {
                    // Get Number of Records from Server
                    case SERVER_GET_NEXT_ID:
                        // Setup New Depline Data Permit with ID and Department User        *****************************************
                        DepLinesData dep_line_data = new DepLinesData();
                        dep_line_data.setId( message );
                        dep_line_data.setDepartMaster( dataParameters.getModelDepartmentUser() );

                        dataParameters.setStateCode(NEW_PERMIT_CODE);
                        dataParameters.setDepLineData(dep_line_data);

                        view_listener.OnFinishedGetNumberOfServerRecords();

                        return;
                    // Get Number of Records from Server
                    case SERVER_BASE_HAS_BEEN_RELEASED_BY:
                        // Setup New Depline Data Permit with ID and Department User        *****************************************
                        view_listener.OnFinishedRefreshViewStatus( status );
                        view_listener.OnFinishedClearBusyServerStatus();
                        return;

                    // No Depline Data has got
                    case DATA_IS_NOT_READY:

                        dep_lines_data_array  = new ArrayList<>();
                        status = message;

                        main_listener.onFinishedBrUserMadeList(getPermitsUserMadeData());
                        main_listener.onFinishedBrAwaitingList(getPermitsAwaitingData());

                        break;
                    // Got some Depline Data
                    case DATA_IS_READY:
                            // Getting Depline Data after Command GET_BY_DEP or GET_ALL
                            if (!message.equals(DATA_WAS_SAVED)) {
                                dep_lines_data_array  = new ArrayList<>();
                                ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
                                for (String item: array_level_json) {
                                    DepLinesData dld_item = getFromJsonToDepLineData(item); // get original (LATIN) Depline Data
                                    if (!( null == dld_item)) {

                                        DepLinesData mod_dld_item = getModifiedData(dld_item, departViewHashMap); // convert Depline Data into RUSSIAN one
                                        dep_lines_data_array.add( mod_dld_item );
                                        //dep_lines_data_array.add(new Gson().fromJson(item, DepLinesData.class));
                                    }
                                }

                            } else {
                                // Making changes in Dep_Lines_Data_Array after getting command DATA_WAS_SAVED
                                switch ( dataParameters.getStateCode() ) {
                                    // Delete Chosen Record from the Array after saving Data in Server Base
                                    case EDIT_MASTER_PERMIT_CODE:

                                        dep_lines_data_array.remove( dataParameters.getPosition() );
                                        status = DATA_WAS_DELETED;

                                        break;
                                    // Add New Record to the Array after saving Data in Server Base
                                    case NEW_PERMIT_CODE:

                                        dep_lines_data_array.add( dataParameters.getDepLineData() );
                                        status = DATA_WAS_SAVED;

                                        break;
                                    // Change Chosen Record in the Array after saving Data in Server Base
                                    case CHANGE_PERMIT_CODE:

                                        String data_reg_user = dataParameters.getDepLineData().getDateApproveHashmap().get(dataParameters.getModelDepartmentUser());

                                        if (!data_reg_user.equals(Approvement.UN.getValue())) {
                                            dep_lines_data_array.set(dataParameters.getPosition(), dataParameters.getDepLineData());
                                            status = DATA_WAS_SAVED;
                                        }
                                        break;

                                    default:
                                        status = message;
                                        break;
                                }
                            }

                            main_listener.onFinishedBrUserMadeList(getPermitsUserMadeData());
                            main_listener.onFinishedBrAwaitingList(getPermitsAwaitingData());

                        break;
                    //Config Data has got or confirmation of saving Depline Data to Server
                    default:
                            status = message;
                        break;
                }

                view_listener.OnFinishedRefreshViewStatus( status );
                view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( status );
            }
        }, 0);

    }

    // Fill In Permits User Made List
    private ArrayList<Map<String, String>> getPermitsUserMadeData() {
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ArrayList<Integer> permit_array_user_made = new ArrayList<>();
        boolean rule;

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dataParameters.getDispatcherMode()) {
                rule = false;
            } else {
                rule = dep_lines_data_array.get(i).getDepartMaster().equals(dataParameters.getModelDepartmentUser());
            }

            if (rule) {

                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(FROM[0], dep_lines_data_array.get(i).getId());
                hashmap.put(FROM[1], dep_lines_data_array.get(i).getPlace());

                hashmap.put(FROM[2], dep_lines_data_array.get(i).getStringDateReg());
                hashmap.put(FROM[3], dep_lines_data_array.get(i).getPermitApproved().getValue());
                data.add(hashmap);
                permit_array_user_made.add(i);
            }
        }

        setPermitArrayUserMade(permit_array_user_made);
        return data;
    }

    // Fill In Permits User Made List
    private ArrayList<Map<String, String>> getPermitsAwaitingData() {
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ArrayList<Integer> permit_array_awaiting = new ArrayList<>();
        boolean rule;

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dataParameters.getDispatcherMode()) {
                rule = true;
            } else {
                rule = false;
                for (String department: dep_lines_data_array.get(i).getDateApproveHashmap().keySet()) {
                    if (department.equals(dataParameters.getModelDepartmentUser())) {
                        rule = true;
                    }
                }
            }

            if ( rule ) {
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(FROM[0], dep_lines_data_array.get(i).getId());
                hashmap.put(FROM[1], dep_lines_data_array.get(i).getDepartMaster());
                hashmap.put(FROM[2], dep_lines_data_array.get(i).getPlace());
                hashmap.put(FROM[3], dep_lines_data_array.get(i).getStringDateReg());

                data.add(hashmap);
                permit_array_awaiting.add(i);
            }
        }

        setPermitArrayAwaiting(permit_array_awaiting);
        return data;
    }

    // send Command and/or Data to Server
    @Override
    public void sendModelDataToServer ( Contract.ViewMainLayout view_listener, String command, String depID, String value) {

        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility(DATA_WAS_NOT_CHANGED);
        view_listener.OnFinishedRefreshViewStatus(DATA_REQUEST_PROCESSING);
        view_listener.OnFinishedSetPermitBlockState( PermitBlock.INVISIBLE );

        String mod_depID = depID;
        if ( command.equals(SERVER_GET_BY_DEP) ) {

            if (dataParameters.getDispatcherMode()) {
                command = SERVER_GET_ALL;
                mod_depID = "";
            } else {
                mod_depID = departServerHashMap.get(depID);
            }
        }

        if ( command.equals(SERVER_CLEAR_BUSY) ) {

            mod_depID = departServerHashMap.get(depID);
            if ( null == mod_depID ) {
                mod_depID = "";
            }
        }

        getBackWithServer( command, mod_depID, value );
    }

    // Send Data to Server
    @Override
    public void getBackWithServer ( String command, String depID, String value ) {
        // run task in background
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new OkHttpRequest().serverGetback( context, command, depID, value );
            }
        }, 0);
    }


    // Set  Model Permit Button New Click
    @Override
    public void setModelMainButtonNewClick( Contract.ModelPermit.OnFinishedSetMainViewLayout main_listener,
                                            Contract.ViewMainLayout view_listener ) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Define List of Required Departments in View Adapter
                main_listener.OnFinishedButtonNewListDefineListReqDeps();
                //Show Permit block and Hide Main Block, Show ID number in View
                main_listener.OnFinishedSetPermitIDtextView(dataParameters.getDepLineData().getId());

                main_listener.OnFinishedSetPlaceDateComment("", "", "", "");
                // Set View Buttons visibility
                view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( NEW_PERMIT_CODE );

                view_listener.OnFinishedSetPermitBlockState( PermitBlock.VISIBLE );

            }
        }, 0);
    }

    @Override
    // method to Update DepLineData Array after Delete
    public void updateDataArrayAfterDelete (Contract.ViewMainLayout view_listener ) {
        // Check if Button Save clicked
                String code = dataParameters.getStateCode();

                switch ( code ) {

                    case EDIT_MASTER_PERMIT_CODE:
                        getBackWithServer ( SERVER_DELETE_PERMIT, dataParameters.getDepLineData().getId(), "");
                        break;

                    case NEW_PERMIT_CODE:
                        dataParameters.setDepLineData(null);

                    // no change was made
                    default:
                        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( DATA_WAS_NOT_CHANGED );
                        view_listener.OnFinishedRefreshViewStatus( DATA_WAS_NOT_CHANGED );
                        break;
                }

            view_listener.OnFinishedSetPermitBlockState( PermitBlock.INVISIBLE );
    }

    @Override
    // method to Update DepLineData Array after Save
    public void updateDataArrayAfterSave (Contract.ViewMainLayout view_listener ) {

        String status = DATA_WAS_NOT_CHANGED;
        view_listener.OnFinishedSetPermitBlockState( PermitBlock.INVISIBLE );
        DepLinesData dl_data;

        switch ( dataParameters.getStateCode() ) {
            // add a new record
            case NEW_PERMIT_CODE:
            // Send to Server New Depline Data Permit with ID and Modified Department User        *****************************************
                dl_data = getModifiedData( dataParameters.getDepLineData(), departServerHashMap );
                getBackWithServer(SERVER_ADD_PERMIT, "", getFromDepLineDataToJson(dl_data) );

                break;

            // change the record
            case CHANGE_PERMIT_CODE:

                dl_data = getModifiedData(dataParameters.getDepLineData(), departServerHashMap);
                getBackWithServer(SERVER_CHANGE_PERMIT, dataParameters.getDepLineData().getId(), getFromDepLineDataToJson(dl_data));

                break;

            // no change was made
            case DATA_WAS_NOT_CHANGED:
            case EDIT_MASTER_PERMIT_CODE:
            case SHOW_PERMIT_CODE:
            default:
        }

        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( status );
        view_listener.OnFinishedRefreshViewStatus( status );
    }

    private DepLinesData getModifiedData ( DepLinesData dld, HashMap<String, String> departHashMap) {

        String mod_dep_user = departHashMap.get(dld.getDepartMaster());

        HashMap<String, Approvement> mod_exist = new HashMap<>();
        HashMap<String, String> mod_dt_approve = new HashMap<>();
        HashMap<String, ArrayList<ArrayList<LatLng>> > mod_lines = new HashMap<>();

        for (String key : departHashMap.keySet()) {
            String mod_key = departHashMap.get(key);
            String value_dt_approve = dld.getDateApproveHashmap().get(key);

            if (null != value_dt_approve) {
                mod_exist.put(mod_key, dld.getHashmapCommExist().get(key));
                mod_dt_approve.put(mod_key, value_dt_approve);
            }

            ArrayList<ArrayList<LatLng>> value = dld.getLinesHashmap().get(key);
            if (null != value) {
                mod_lines.put(mod_key, value);
            }
        }

        DepLinesData dl_data = new DepLinesData();

        dl_data.setId(dld.getId());
        dl_data.setStringDateStart(dld.getStringDateStart());
        dl_data.setStringDateEnd(dld.getStringDateEnd());
        dl_data.setPlace(dld.getPlace());
        dl_data.setComment(dld.getComment());
        dl_data.setStringDateReg(dld.getStringDateReg());
        dl_data.setPermitState(dld.getPermitState());

        dl_data.setDepartMaster(mod_dep_user);
        dl_data.setHashmapCommExist(mod_exist);
        dl_data.setDateApproveHashmap(mod_dt_approve);
        dl_data.setLinesHashmap(mod_lines);

        return dl_data;
    }
}




