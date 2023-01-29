package ru.volganap.nikolay.excavate_coordinator;

import com.google.gson.Gson;

public class DataParameters implements Contract.DataParameters {

    private String department_user;
    private DepLinesData dep_line_data;
    private String state_code;

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

    // Get DepLinesDataJson
    public String getDepLineDataJson() {
        return new Gson().toJson(dep_line_data);
    }

    //set State Code
    public void setStateCode(String state_code) {
        this.state_code = state_code;
    }

    //get State Code
    public String getStateCode() {
        return this.state_code;
    }

}
