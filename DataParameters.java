package ru.volganap.nikolay.haircut_schedule;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Date;

public class DataParameters implements Contract.DataParameters {

    private ArrayList<RecordData> rec_data_array;
    private String code;
    private int position;
    //private RecordData rec_data;

    //private  int id;

    //private int position = 0;
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

    //public RecordData getRecordData() {return rec_data;}

    // Get State Code
    public String getStateCode() {
        return code;
    }

    // Get position
    public int getPosition() {
        return position;
    }

    /* public int getId() {
        return id;
    }; */

    //setters
    @Override
    public void setRecordDataArray( ArrayList<RecordData> value) {this.rec_data_array = value;}

    /* public void setRecordData( RecordData value) {this.rec_data = value;} */
    // Set State Code
    public void setStateCode(String code) {
        this.code = code;
    }

    // Get position
    public void setPosition(int position) {
        this.position = position;
    }

    /* public void setId(int id) {
        this.id = id;
    }; */

}
