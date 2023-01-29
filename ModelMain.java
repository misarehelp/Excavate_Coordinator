package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelMain implements Contract.ModelMain, KM_Constants, Enums {

    private ArrayList<DepLinesData> dep_lines_data_array  = new ArrayList<>();
    private ArrayList<Integer> permit_array_user_made, permit_array_awaiting;
    private Context context;

    private DataParameters dataParameters;

    // initiating the objects of Model
    public ModelMain(Context context, DataParameters dataParameters) {
        this.context = context;
        this.dataParameters = dataParameters;
    }

    public void setModelDepLinesDataArray(ArrayList<DepLinesData> dep_lines_data_array) {
        this.dep_lines_data_array = dep_lines_data_array;
    }

    // get dep_lines_data_array
    public ArrayList<DepLinesData> getModelDepLinesDataArray() {
        return dep_lines_data_array;
    }

    // set permit_array for User Made List
    public void setPermitArrayUserMade(ArrayList<Integer> permit_array_user_made) {
        this.permit_array_user_made = permit_array_user_made;
    }

    // get permit_array for User Made List
    public ArrayList<Integer> getPermitArrayUserMade() {
        return permit_array_user_made;
    }

    // set permit_array for User Made List
    public void setPermitArrayAwaiting(ArrayList<Integer> permit_array_awaiting) {
        this.permit_array_awaiting = permit_array_awaiting;
    }

    public String getNewPermitNumber() {

        int number;
        if (dep_lines_data_array.size() == 0) {
            number = 1;
        } else {
            String help_number = dep_lines_data_array.get(dep_lines_data_array.size()-1).getId();
            number = Integer.parseInt(help_number) + 1;
        }

        return Integer.toString(number);
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

                if ( status.equals(DATA_IS_NOT_READY) ) {
                    status = message;

                } else  {
                    if ((message.equals( DATA_WAS_SAVED )) ) {
                        status = message;

                    } else   {
                        ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
                        for (String item: array_level_json) {
                            dep_lines_data_array.add(new Gson().fromJson(item, DepLinesData.class));
                        }
                    }

                    main_listener.onFinishedBrUserMadeList(getPermitsUserMadeData());
                    main_listener.onFinishedBrAwaitingList(getPermitsAwaitingData());
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

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dep_lines_data_array.get(i).getDepartMaster().equals(dataParameters.getModelDepartmentUser())) {

                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(FROM[0], dep_lines_data_array.get(i).getId());
                hashmap.put(FROM[1], dep_lines_data_array.get(i).getPlace());
                hashmap.put(FROM[2], dep_lines_data_array.get(i).getStringDateStart());
                hashmap.put(FROM[3], dep_lines_data_array.get(i).getStringDateReg());
                hashmap.put(FROM[4], dep_lines_data_array.get(i).getPermitApproved());
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

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dep_lines_data_array.get(i).getHashmapRequired().get(dataParameters.getModelDepartmentUser())) {

                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(FROM[0], dep_lines_data_array.get(i).getId());
                hashmap.put(FROM[1], dep_lines_data_array.get(i).getDepartMaster());
                hashmap.put(FROM[2], dep_lines_data_array.get(i).getPlace());
                hashmap.put(FROM[3], dep_lines_data_array.get(i).getStringDateStart());
                hashmap.put(FROM[4], dep_lines_data_array.get(i).getComment());
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

        dep_lines_data_array = new ArrayList<>();

        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility(DATA_WAS_NOT_CHANGED);
        view_listener.OnFinishedRefreshViewStatus(DATA_REQUEST_PROCESSING);
        view_listener.OnFinishedSetPermitBlockState( PermitBlock.INVISIBLE );

        getBackWithServer( command, depID, value );
    }

    // Send Data to Server
    private void getBackWithServer ( String command, String depID, String value ) {
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

                DepLinesData dep_line_data = new DepLinesData( getNewPermitNumber(), null, dataParameters.getModelDepartmentUser(),
                        null, null,true, null,
                        null, null, null,null);

                dataParameters.setStateCode(NEW_PERMIT_CODE);
                dataParameters.setDepLineData(dep_line_data);

                //Define List of Required Departments in View Adapter
                main_listener.OnFinishedButtonNewListDefineListReqDeps();
                //Show Permit block and Hide Main Block, Show ID number in View
                main_listener.OnFinishedSetPermitIDtextView(dep_line_data.getId());
                // Set View Buttons visibility
                view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( NEW_PERMIT_CODE );

                view_listener.OnFinishedSetPermitBlockState( PermitBlock.VISIBLE );

                main_listener.OnFinishedSetPlaceDateComment("", "", "");

            }
        }, 0);
    }

    @Override
    // method to Update DepLineData Array after Delete
    public void updateDataArrayAfterDelete (Contract.ViewMainLayout view_listener, int position ) {
        // Check if Button Save clicked
                String code = dataParameters.getStateCode();

                switch ( code ) {

                    case EDIT_PERMIT_CODE:

                        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( DATA_WAS_DELETED );
                        view_listener.OnFinishedRefreshViewStatus( DATA_WAS_DELETED );

                        dep_lines_data_array.remove( position );

                        setModelDepLinesDataArray (dep_lines_data_array);
                        getBackWithServer ( SERVER_PUT_ALL, "", getDeplineDataArrayJson());
                        break;

                    case NEW_PERMIT_CODE:

                        if (dataParameters.getDepLineData().getLinesHashmap() != null) {
                            dataParameters.setDepLineData(null);
                        }

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
    public void updateDataArrayAfterSave (Contract.ViewMainLayout view_listener, int position ) {

        String status = DATA_WAS_NOT_CHANGED;
        view_listener.OnFinishedSetPermitBlockState( PermitBlock.INVISIBLE );

        switch ( dataParameters.getStateCode() ) {
            // add a new record
            case NEW_PERMIT_CODE:

                dep_lines_data_array.add( dataParameters.getDepLineData() );
                status = DATA_WAS_SAVED;

                break;

            // change the record
            case ADD_PERMIT_CODE:
                // check if the department user has already approved the chosen permit
                String data_reg_user = dataParameters.getDepLineData().getDateApproveHashmap().get(dataParameters.getModelDepartmentUser());

                if (!data_reg_user.equals(Approvement.UNKNOWN.getValue())) {

                    dep_lines_data_array.set( position, dataParameters.getDepLineData() );
                    status = DATA_WAS_SAVED;
                }
                break;

            // no change was made
            case DATA_WAS_NOT_CHANGED:
            case EDIT_PERMIT_CODE:
            case SHOW_PERMIT_CODE:
            default:
        }

        view_listener.OnFinishedButtonSaveSetViewButtonsVisibility( status );
        view_listener.OnFinishedRefreshViewStatus( status );

        // Send Data to server if changes were
        if (status.equals(DATA_WAS_SAVED)) {
            setModelDepLinesDataArray(dep_lines_data_array);
            getBackWithServer(SERVER_PUT_ALL, "", getDeplineDataArrayJson());
        }
    }

    // update dep_lines_data_array
    private String getDeplineDataArrayJson() {

        ArrayList<String> dld_array_list = new ArrayList<>();
        for (DepLinesData item: dep_lines_data_array) {
            String item_json = new Gson().toJson(item);
            dld_array_list.add(item_json);
        }

        return new Gson().toJson(dld_array_list);
    }

}




