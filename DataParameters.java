package ru.volganap.nikolay.excavate_coordinator;

import com.google.gson.Gson;

public class DataParameters implements Contract.DataParameters {

    private String department_user;
    private DepLinesData dep_line_data;
    private String state_code;
    private boolean map_is_done;
    private boolean dispatcher_on;
    private String max_records_number;
    private int position = 0;

    private String[] department_array;

    private static DataParameters INSTANCE;

    private DataParameters() {
    }

    public static DataParameters getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataParameters();
        }
        return INSTANCE;
    }

    //set Position
    public void setPosition(int position) {
        this.position = position;
    }

    //get Position
    public int getPosition() {
        return this.position;
    }

    //set Dispatcher Mode
    public void setDispatcherMode ( boolean dispatcher_on ) {
        this.dispatcher_on = dispatcher_on;
    }

    //get Dispatcher Mode
    public boolean getDispatcherMode () {
        return this.dispatcher_on;
    }

    //get Department Array
    public void setDepartmentArray(String[] department_array) {
        this.department_array = department_array;
    }

    //get Department Array
    public String[] getDepartmentArray() {
        return this.department_array;
    }

    // get Model Departmen User
    public void setModelDepartmentUser(String department_user) {
        this.department_user = department_user;
    }

    // get Model Departmen User
    public String getModelDepartmentUser() {
        return this.department_user;
    }

    //set DepLineData
    public void setDepLineData(DepLinesData dep_line_data) {
        this.dep_line_data = dep_line_data;
    }

    //get DepLineData
    public DepLinesData getDepLineData() {
        return dep_line_data;
    }

    //set State Code
    public void setStateCode(String state_code) {
        this.state_code = state_code;
    }

    //get State Code
    public String getStateCode() {
        return this.state_code;
    }

    //set MapIsDone Code
    public void setMapIsDone (boolean map_is_done) {
        this.map_is_done = map_is_done;
    }

    //get MapIsDone Code
    public boolean getMapIsDone () {
        return this.map_is_done;
    }

    //set Max Records Number
    public void setMaxRecordsNumber(String max_records_number) {
        this.max_records_number = max_records_number;
    }

    //get Max Records Number
    public String getMaxRecordsNumber() {
        return this.max_records_number;
    }

}
