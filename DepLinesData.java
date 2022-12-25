package ru.volganap.nikolay.excavate_coordinator;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class DepLinesData {
    private String id;
    private String place;
    private String depart_master;
    private String date_start, date_reg;
    private boolean permit_open;
    private String comment;
    private HashMap<String, Boolean> hashmap_required;
    private HashMap<String, Approvement> hashmap_comm_exist;
    private HashMap<String, String> date_approve_hashmap;
    private HashMap<String, ArrayList<ArrayList<LatLng>> > lines_hashmap;

    public DepLinesData(String id, String place, String depart_master, String date_start, String date_reg, boolean permit_open, String comment,
                        HashMap<String, Boolean> hashmap_required, HashMap<String, Approvement> hashmap_comm_exist,
                        HashMap<String, String> date_approve_hashmap, HashMap<String, ArrayList<ArrayList<LatLng>>> lines_hashmap) {
        this.id = id;
        this.place = place;
        this.depart_master = depart_master;
        this.date_start = date_start;
        this.date_reg = date_reg;
        this.permit_open = permit_open;
        this.comment = comment;
        this.hashmap_required = hashmap_required;
        this.hashmap_comm_exist = hashmap_comm_exist;
        this.date_approve_hashmap = date_approve_hashmap;
        this.lines_hashmap = lines_hashmap;
    }

    //getters
    String getId() {return id;}
    String getPlace() {return place;}
    String getDepartMaster() {return depart_master;}

    String getStringDateStart() {
        return date_start;
    }

    String getStringDateReg() {
        return date_reg;
    }

    Boolean getPermitOpen() {return permit_open;}
    String getComment() {return comment;}
    HashMap<String, Boolean> getHashmapRequired() {return hashmap_required;}
    HashMap<String, Approvement> getHashmapCommExist() {return hashmap_comm_exist;}
    HashMap<String, String> getDateApproveHashmap() {return date_approve_hashmap;}
    HashMap<String, ArrayList<ArrayList<LatLng>>> getLinesHashmap() {return lines_hashmap;}

    String getPermitApproved() {
        String permit_approved = "yes";
        for(String key : hashmap_required.keySet()) {
            if (hashmap_required.get(key) && date_approve_hashmap.get(key).equals(Approvement.UNKNOWN.getValue())){
                permit_approved = "no";
            }
        }
        return permit_approved;
    }

    //setters
    void setId(String id) {this.id = id;}

    void setPlace(String place) {this.place = place;}

    void setDepartMaster(String depart_master) {this.depart_master = depart_master;}

    void setStringDateStart(String date_start) {this.date_start = date_start;}

    void setStringDateReg(String date_reg) {this.date_reg = date_reg;}

    void setPermitOpen(boolean permit_open) {this.permit_open = permit_open;}

    void setComment(String comment) {this.comment = comment;}

    void setHashmapRequired(HashMap<String, Boolean> hashmap_required) {this.hashmap_required = hashmap_required;}

    void setHashmapCommExist(HashMap<String, Approvement> hashmap_comm_exist) {this.hashmap_comm_exist = hashmap_comm_exist;}

    void setDateApproveHashmap(HashMap<String, String> date_approve_hashmap) {this.date_approve_hashmap = date_approve_hashmap;}

    void setLinesHashmap(HashMap<String, ArrayList<ArrayList<LatLng>>> lines_hashmap) {this.lines_hashmap = lines_hashmap;}
}
