package ru.volganap.nikolay.haircut_schedule;

import java.util.ArrayList;
import java.util.HashMap;

public class DataParameters implements Contract.DataParameters {

    private ArrayList<RecordData> rec_data_array;
    private ArrayList<ClientData> client_data_array;
    private String code;
    private int rec_pos, client_pos;
    private static DataParameters INSTANCE;

    private DataParameters() {
    }

    public static DataParameters getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataParameters();
        }
        return INSTANCE;
    }

    //getters
    @Override
    public ArrayList<RecordData> getRecordDataArray() {return rec_data_array;}
    @Override
    public ArrayList<ClientData> getClientDataArray() {
        return client_data_array;
    }
    // Get State Code
    public String getStateCode() {
        return code;
    }
    // Get Record position
    public int getRecordPosition() {
        return rec_pos;
    }
    // Get Client position
    public int getClientPosition() {
        return client_pos;
    }

    //setters
    @Override
    public void setRecordDataArray( ArrayList<RecordData> value) {this.rec_data_array = value;}
    @Override
    public void setClientDataArray(ArrayList<ClientData> value) {
        this.client_data_array = value;
    }
    // Set State Code
    public void setStateCode(String code) {
        this.code = code;
    }
    // Get Record position
    public void setRecordPosition(int rec_pos) {
        this.rec_pos = rec_pos;
    }
    // Get Client position
    public void setClientPosition(int client_pos) {
        this.client_pos = client_pos;
    }

    // get Calendar HashMap
    public HashMap<String, Integer> getCalendarHashmap() {
        HashMap<String, Integer> cal_hashmap = new HashMap<>();
        for (RecordData rd : rec_data_array) {
            if (cal_hashmap.containsKey(rd.getDate())) {
                int i = cal_hashmap.get(rd.getDate());
                cal_hashmap.put(rd.getDate(), ++i );
            } else {
                cal_hashmap.put(rd.getDate(), 1 );
            }
        }
        return cal_hashmap;
    }

}
