package ru.volganap.nikolay.haircut_schedule;

import java.util.ArrayList;
import java.util.HashMap;

public class DataParameters implements Contract.DataParameters {

    String START_HOLIDAY_TIME = "01:00";
    String INDEX_NOTE = "-2";
    private ArrayList<RecordData> rec_data_array;
    private ArrayList<ClientData> client_data_array;
    private String code;
    private int rec_pos, client_pos;
    private static DataParameters INSTANCE;
    HashMap <String, Boolean> holiday_hashmap;
    HashMap <String, Integer> note_hashmap;

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
    public String  getStateCode() {
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
    // Get Holiday HashMap
    public HashMap <String, Boolean> getHolidayHashMap() {
        return holiday_hashmap;
    }
    // Get Note HashMap
    public HashMap <String, Integer> getNoteHashMap() {
        return note_hashmap;
    }

    //setters
    @Override
    public void setRecordDataArray( ArrayList<RecordData> value) {this.rec_data_array = value;}
    @Override
    public void setClientDataArray(ArrayList<ClientData> value) {
        this.client_data_array = value;
    }
    // Set State Code
    public void setStateCode(String  code) {
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
        HashMap<String, Integer> workday_hashmap = new HashMap<>();
        holiday_hashmap = new HashMap<>();
        note_hashmap = new HashMap<>();
        for (RecordData rd : rec_data_array) {
            // check if a record is not a Holiday
            if (!rd.getTime().equals(START_HOLIDAY_TIME)) {
                // check if a record is Work Day
                if (!rd.getJob().equals(INDEX_NOTE)) {
                    if (workday_hashmap.containsKey(rd.getDate())) {
                        int i = workday_hashmap.get(rd.getDate());
                        workday_hashmap.put(rd.getDate(), ++i);
                    } else {
                        workday_hashmap.put(rd.getDate(), 1);
                    }
                } else {
                    if (note_hashmap.containsKey(rd.getDate())) {
                        int i = note_hashmap.get(rd.getDate());
                        note_hashmap.put(rd.getDate(), ++i);
                    } else {
                        note_hashmap.put(rd.getDate(), 1);
                    }
                }
            } else {
                holiday_hashmap.put(rd.getDate(), true);
            }
        }
        return workday_hashmap;
    }

}
