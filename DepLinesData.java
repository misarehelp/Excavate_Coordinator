package ru.volganap.nikolay.haircut_schedule;

import java.util.ArrayList;
import java.util.HashMap;

public class DepLinesData implements Enums {
    private String id;
    private String place;
    private String master;
    private String date_st, date_reg, date_end;
    private Enums.PermitState state = PermitState.OP;
    private String comment;

    private HashMap<String, Approvement> exist;
    private HashMap<String, String> dt_approve;

    public DepLinesData() {
    }

    //getters
    String getId() {return id;}
    String getPlace() {return place;}
    String getDepartMaster() {return master;}

    String getStringDateStart() {
        return date_st;
    }

    String getStringDateReg() {
        return date_reg;
    }

    String getStringDateEnd() {
        return date_end;
    }

    Enums.PermitState getPermitState() {return state;}
    String getComment() {return comment;}

    HashMap<String, Approvement> getHashmapCommExist() {return exist;}
    HashMap<String, String> getDateApproveHashmap() {return dt_approve;}

    Approvement getPermitApproved() {
        Approvement permit_approved = Approvement.YES;
        for(String key : dt_approve.values()) {
            if ( key.equals(Approvement.ND.getValue())) {
                permit_approved = Approvement.NO;
            }
        }
        return permit_approved;
    }

    //setters
    void setId(String id) {this.id = id;}

    void setPlace(String place) {this.place = place;}

    void setDepartMaster(String master) {this.master = master;}

    void setStringDateStart(String date_st) {this.date_st = date_st;}

    void setStringDateReg(String date_reg) {this.date_reg = date_reg;}

    void setStringDateEnd(String date_end) {this.date_end = date_end;}

    void setPermitState(Enums.PermitState state) {this.state = state;}

    void setComment(String comment) {this.comment = comment;}

    void setHashmapCommExist(HashMap<String, Approvement> exist) {this.exist = exist;}

    void setDateApproveHashmap(HashMap<String, String> dt_approve) {this.dt_approve = dt_approve;}


}
