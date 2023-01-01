package ru.volganap.nikolay.excavate_coordinator;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelMain implements Contract.ModelMain, KM_Constants {

    private ArrayList<DepLinesData> dep_lines_data_array  = new ArrayList<>();
    private ArrayList<Integer> permit_array;
    private String department_user;
    Context context;

    // initiating the objects of Model
    public ModelMain(Context context) {
        this.context = context;
    }

    public void setModelDepLinesDataArray(ArrayList<DepLinesData> dep_lines_data_array) {
        this.dep_lines_data_array = dep_lines_data_array;
    }

    // get dep_lines_data_array
    public ArrayList<DepLinesData> getModelDepLinesDataArray() {
        return dep_lines_data_array;
    }


    public void setPermitArray(ArrayList<Integer> permit_array) {
        this.permit_array = permit_array;
    }

    // get dep_lines_data_array
    public ArrayList<Integer> getPermitArray() {
        return permit_array;
    }

    // get Model Departmen User
    public String getModelDepartmentUser() {
        return this.department_user;
    }

    // this method will invoke when MainActivity initiate ViewLayout
    @Override
    public void setModelParams(Contract.ModelMain.OnFinishedListenerPermitBlock listener, ViewGroup.LayoutParams params) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                params.height = 0;
                listener.onFinishedPermitBlock(params);
            }
        }, 0);

    }

    // this method will invoke when User changes Department
    @Override
    public void setModelUser(Contract.ModelMain.OnFinishedListenerDepartmentUser listener, String department_user) {

        this.department_user = department_user;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onFinishedDepartmentUser(department_user);
            }
        }, 0);

    }

    // this method will invoke when BroadcastReceiver trigger
    @Override
    public void setModelBroadcastReceiver(Contract.ModelMain.OnFinishedListenerBroadcastReceiver listener, String message) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> array_level_json = new Gson().fromJson(message, ArrayList.class);
                for (String item: array_level_json) {
                    dep_lines_data_array.add(new Gson().fromJson(item, DepLinesData.class));
                }

                listener.onFinishedBrUserMadeList(getPermitsUserMadeData());

                listener.onFinishedBrAwaitingList(getPermitsAwaitingData());
            }
        }, 0);

    }

    // Fill In Permits User Made List
    private ArrayList<Map<String, String>> getPermitsUserMadeData() {
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ArrayList<Integer> permit_array = new ArrayList<>();

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dep_lines_data_array.get(i).getDepartMaster().equals(department_user)) {
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(FROM[0], dep_lines_data_array.get(i).getId());
                hashmap.put(FROM[1], dep_lines_data_array.get(i).getPlace());
                hashmap.put(FROM[2], dep_lines_data_array.get(i).getStringDateStart());
                hashmap.put(FROM[3], dep_lines_data_array.get(i).getStringDateReg());
                hashmap.put(FROM[4], dep_lines_data_array.get(i).getPermitApproved());
                data.add(hashmap);
                permit_array.add(i);
            }
        }

        setPermitArray(permit_array);
        return data;
    }

    // Fill In Permits User Made List
    private ArrayList<Map<String, String>> getPermitsAwaitingData() {
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ArrayList<Integer> permit_array = new ArrayList<>();

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dep_lines_data_array.get(i).getHashmapRequired().get(department_user)) {
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(FROM[0], dep_lines_data_array.get(i).getId());
                hashmap.put(FROM[1], dep_lines_data_array.get(i).getDepartMaster());
                hashmap.put(FROM[2], dep_lines_data_array.get(i).getPlace());
                hashmap.put(FROM[3], dep_lines_data_array.get(i).getStringDateStart());
                hashmap.put(FROM[4], dep_lines_data_array.get(i).getComment());
                data.add(hashmap);
                permit_array.add(i);
            }
        }

        setPermitArray(permit_array);
        return data;
    }

    // send Command and/or Data to Server
    @Override
    public void sendModelDataToServer ( String command, String depID, String value) {
        String value_json = value;

        if (command.equals(SERVER_PUT_ALL)) {
            value_json = getDeplineDataArrayJson();
        }

        new OkHttpRequest().serverGetback(context, command, depID, value_json);
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

    @Override
    // method to Update DepLineData Array
    public void updateModelDepLineDataArray (Contract.ModelPermit modelPermit, String type ) {

        switch (type) {
            // add a new record
            case NEW_PERMIT_CODE:
                dep_lines_data_array.add( modelPermit.getDepLineData() );
                break;
            // change the record
            case EDIT_PERMIT_CODE:
            case ADD_PERMIT_CODE:
                dep_lines_data_array.set( modelPermit.getPosition(), modelPermit.getDepLineData() );
                break;
            // delete the record
            case DATA_WAS_DELETED:
                dep_lines_data_array.remove( modelPermit.getPosition() );
                break;
            // no change was made
            default:
                return;
        }

        sendModelDataToServer ( SERVER_PUT_ALL, "", "") ;
    }

}




