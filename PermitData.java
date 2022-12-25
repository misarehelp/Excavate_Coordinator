package ru.volganap.nikolay.excavate_coordinator;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermitData implements KM_Constants {

    Context context;
    boolean [] required_array;
    private String[] department_array;
    private String permit_code = DATA_WAS_NOT_CHANGED;
    private DepLinesData dep_line_data;

    //getters
    public ArrayList<Map<String, String>> getUserMadeListData(int[] to, ArrayList<DepLinesData> dep_lines_data_array, String department_user) {
        String[] from = {"0", "1", "2", "3", "4"};
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ArrayList<Integer> permit_array = new ArrayList();

        for (int i = 0; i < dep_lines_data_array.size(); i++) {
            //check if user has created permits
            if (dep_lines_data_array.get(i).getDepartMaster().equals(department_user)) {
                Map<String, String> hashmap = new HashMap<>();
                hashmap.put(from[0], dep_lines_data_array.get(i).getId());
                hashmap.put(from[1], dep_lines_data_array.get(i).getPlace());
                hashmap.put(from[2], dep_lines_data_array.get(i).getStringDateStart());
                hashmap.put(from[3], dep_lines_data_array.get(i).getStringDateReg());
                hashmap.put(from[4], dep_lines_data_array.get(i).getPermitApproved());
                data.add(hashmap);
                permit_array.add(i);
            }
        }
        return data;
    }
}
