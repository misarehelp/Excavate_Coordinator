package ru.volganap.nikolay.excavate_coordinator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ModelMap implements Contract.ModelMap,  KM_Constants, Enums  {

    private static final String POSITION_IS_NOT_DEFINED ="Координаты не определены";
    private static final String NO_LINES_SET ="Нет отмеченных коммуникаций";
    private static final Double LAT_BASE = 55.989174;
    private static final Double LONG_BASE = 48.604360;
    private int[] color_depart = {Color.BLUE, Color.MAGENTA, Color.GREEN, Color.RED, Color.YELLOW, Color.GRAY, Color.parseColor("#FFA16F24"), //brown
                                    Color.DKGRAY, Color.CYAN};

    private DepLinesData dep_line_data;
    private DataParameters dataParameters;
    private String department_user, department_master;
    private HashMap<String, Integer> hashmap_color;
    private String state_code;
    private String marker_code;
    private ArrayList<LatLng> line =  new ArrayList<>();
    private ArrayList<ArrayList<LatLng>> lines_group = new ArrayList<>();
    private LatLng firstPos;

    // initiating the objects of Model
    public ModelMap() {
        //this.state_code = DATA_WAS_NOT_CHANGED;
    }

    @Override
    public void initMap( Contract.ModelMap.CallbackSetMapsLayout listener, Intent intent ) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                dataParameters = DataParameters.getInstance();

                dep_line_data = dataParameters.getDepLineData();
                state_code = dataParameters.getStateCode();
                department_master = dep_line_data.getDepartMaster();
                department_user = dataParameters.getModelDepartmentUser();
                String [] department_array = dataParameters.getDepartmentArray();

                marker_code = START_LINE_CODE;

                listener.OnFinishedSetUpButtonsAppearance(state_code);

                listener.OnFinishedSetUser(department_user);

                listener.OnFinishedDrawLegend( department_array, color_depart, getColorBackground(department_array));

            }
        }, 0);

    }

    private int [] getColorBackground( String[] department_array ) {

        //department_user = dataParameters.getModelDepartmentUser();
        int [] color_bg = new int [department_array.length];
        hashmap_color = new HashMap<>();
        for (int i=0; i < department_array.length; i++) {
            hashmap_color.put(department_array[i], color_depart[i]);
            color_bg[i] = getComplementaryColor(color_depart[i]);
        }
        return color_bg;
    }

    private int getComplementaryColor( int color) {

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color),
                hsv);
        if (hsv[2] < 0.5) {
            //hsv[2] = 0.7f;
            hsv[2] = 0.9f;
        } else {
            //hsv[2] = 0.3f;
            hsv[2] = 0.1f;
        }
        hsv[1] = hsv[1] * 0.2f;
        //hsv[1] = hsv[1] * 0.2f;
        return Color.HSVToColor(hsv);
        /*double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE; */
    }

    @Override
    public void getSavedLines( Contract.ModelMap.CallbackOnMapReady listener ) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (dep_line_data.getLinesHashmap() == null) {
                    return;
                }
                HashMap<String, ArrayList<ArrayList<LatLng>>> linesHashmap = dep_line_data.getLinesHashmap();

                for (String key_department : linesHashmap.keySet()) {
                    // level of department
                    ArrayList<ArrayList<LatLng>> line_group = linesHashmap.get(key_department);
                    // level of group of lines
                    for (ArrayList<LatLng> lines: line_group) {
                        //level of a line
                        for (int i=0; i < lines.size()-1; i++) {
                            LatLng latLng1 = lines.get(i);
                            LatLng latLng2 = lines.get(i+1);
                            String date_approve = dep_line_data.getDateApproveHashmap().get(key_department);
                            boolean master = department_master.equals(key_department);

                            listener.OnFinishedSetMarkerLine(new MarkerLineData(latLng1, latLng2, key_department, date_approve, hashmap_color.get(key_department), master));
                        }
                    }
                }
            }
        }, 0);

    }

    @Override
    public void getCenterMap( Contract.ModelMap.CallbackOnMapReady listener ) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                LatLng center_point;
                try {
                    center_point = dep_line_data.getLinesHashmap().get(dep_line_data.getDepartMaster()).get(0).get(0);
                } catch (Exception error) {
                    center_point = new LatLng(LAT_BASE, LONG_BASE);
                }
                // Initilization the first point
                listener.OnFinishedCenterMap(center_point);

            }
        }, 0);
    }

    @Override
    public void checkOnMapClickListenerns( Contract.ModelMap.CallbackOnMapReady listener ) {

        if (( state_code.equals(NEW_PERMIT_CODE)) || ( state_code.equals(ADD_PERMIT_CODE ))) {
            listener.OnFinishedSetOnMapClickListenerns();;
        }
    }

    @Override
    public void doButtonContourLineLogic( Contract.ModelMap.CallbackOnMapReady listener_map,  Contract.ModelMap.CallbackSetMapsLayout listener_layout ) {

        if (!(line == null) && line.size() > 2) {
            LatLng endPos = line.get(0);
            line.add(endPos);
            lines_group.add(line);
            boolean master = department_master.equals(department_user);

            listener_map.OnFinishedSetMarkerLine ( new MarkerLineData(firstPos, endPos, department_user, "", hashmap_color.get(department_user), master) );

            marker_code = START_LINE_CODE;
            listener_layout.OnFinishedSetUpButtonsAppearance( START_LINE_CODE );

        } else {
            listener_layout.OnFinishedRefreshMapStatus( CONTOUR_LINE_CODE );
        }
    }

    @Override
    public void doButtonEndLineLogic( Contract.ModelMap.CallbackSetMapsLayout listener ) {
        //Add line group to ArrayList of group of lines
        if (line != null && marker_code.equals( ADD_LINE_OR_FINISH_CODE) ){
            lines_group.add(line);
            marker_code = START_LINE_CODE;
            listener.OnFinishedSetUpButtonsAppearance( END_LINE_CODE );
            //line.clear();
        } else {
            listener.OnFinishedRefreshMapStatus( NO_LINES_SET );
        }
    }

    @Override
    public void doButtonMapClearLogic( Contract.ModelMap.CallbackOnMapReady map_listener,  Contract.ModelMap.CallbackSetMapsLayout layout_listener ) {

        marker_code =  START_LINE_CODE;
        map_listener.OnFinishedMapClearListenerns();

        layout_listener.OnFinishedSetUpButtonsAppearance( state_code );

        if (lines_group != null){
            lines_group.clear();
        }
    }

    @Override
    public void doButtonMapExitLogic( Contract.ModelMap.CallbackOnMapReady map_listener ) {
        //Save all the lines to dep_line_data
        DepLinesData dep_line_data = dataParameters.getDepLineData();

        if ( (state_code.equals(ADD_PERMIT_CODE)) || (state_code.equals(NEW_PERMIT_CODE)) ) {
            HashMap<String, ArrayList<ArrayList<LatLng>>> lines_hashmap = new HashMap<>();

            if ( (lines_group != null) && (lines_group.size() != 0) ) {
                if (!(dep_line_data.getLinesHashmap() == null)) {
                    lines_hashmap = dep_line_data.getLinesHashmap();
                }
                lines_hashmap.put(department_user, lines_group);
                dep_line_data.setLinesHashmap(lines_hashmap);
                dep_line_data = getCommAndDateAproveSet( dep_line_data, dataParameters.getStateCode(), Approvement.YES );

                dataParameters.setDepLineData(dep_line_data);
                //code = DATA_WAS_SAVED ;
            } else {
                dataParameters.setStateCode( DATA_WAS_NOT_CHANGED );
            }
        } else {
            dataParameters.setStateCode( DATA_WAS_NOT_CHANGED );
        }

        map_listener.OnCloseMapsListenerns();
    }

    @Override
    public void doButtonCheckNoLinesLogic( Contract.ModelMap.CallbackOnMapReady map_listener ) {

        DepLinesData dep_line_data = getCommAndDateAproveSet( dataParameters.getDepLineData(), dataParameters.getStateCode(), Approvement.NO );

        dataParameters.setDepLineData(dep_line_data);
        //dataParameters.setStateCode( DATA_WAS_SAVED );

        map_listener.OnCloseMapsListenerns();
    }

    //get Communication and Date Aprovement Set
    private DepLinesData getCommAndDateAproveSet(DepLinesData dep_line_data, String code, Approvement appr) {

        String str_date = getDateNowToString ();

        // set the communications YES state
        HashMap<String, Approvement> hashmap_comm_exist = dep_line_data.getHashmapCommExist();
        hashmap_comm_exist.put(department_user, appr);
        dep_line_data.setHashmapCommExist(hashmap_comm_exist);

        // set the Date Approve YES state
        HashMap<String, String> date_approve_hashmap = dep_line_data.getDateApproveHashmap();
        date_approve_hashmap.put(department_user, str_date);
        dep_line_data.setDateApproveHashmap(date_approve_hashmap);

        if ( code.equals( NEW_PERMIT_CODE )) {
            dep_line_data.setStringDateReg( str_date );
        }

        return dep_line_data;
    }

    //get Date Now To String
    private String getDateNowToString () {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
    }

    // Do logic after clicking on the map
    @Override
    public void doMapClickLogic( Contract.ModelMap.CallbackOnMapReady map_listener,  Contract.ModelMap.CallbackSetMapsLayout layout_listener, LatLng latLng ){

        LatLng secondPos;
        if (null == latLng) {
            layout_listener.OnFinishedRefreshMapStatus(POSITION_IS_NOT_DEFINED);
            return;
        }
        //Add point to ArrayList of line
        //line.add(latLng);
        switch (marker_code) {
            //draw a start point for a new line
            case START_LINE_CODE:
            case END_LINE_CODE:
                line = new ArrayList<>();
                firstPos = latLng;
                marker_code = ADD_LINE_CODE;

                map_listener.OnAddMarkerListenerns(latLng);

            //add point to the line ArrayList
                line.add(latLng);

                layout_listener.OnFinishedSetUpButtonsAppearance(marker_code);
                break;
            //buttons' setup for a permit to be edited
            case ADD_LINE_CODE:
            case ADD_LINE_OR_FINISH_CODE:
                marker_code = ADD_LINE_OR_FINISH_CODE;
                if (null == firstPos) {
                    return;
                }
                secondPos = firstPos;
                firstPos = latLng;
                line.add(latLng);

                layout_listener.OnFinishedSetUpButtonsAppearance(marker_code);
                boolean master = department_master.equals(department_user);

                map_listener.OnFinishedSetMarkerLine ( new MarkerLineData(firstPos, secondPos, department_user, "", hashmap_color.get(department_user), master) );

                break;
            //default
            default:
                break;
        }
        //secondPos = (null == firstPos) ? latLng : firstPos;
    }
}

    /* private void setResultToPermitActivity(DepLinesData dep_line_data, String code) {

        Intent back_intent = getIntent();
        back_intent.putExtra(DATA_TYPE, code);
        String dep_line_data_json = new Gson().toJson(dep_line_data);
        back_intent.putExtra(DEP_LINE_DATA, dep_line_data_json);
        setResult(MAPS_ACTIVITY_REQUEST_CODE, back_intent);
        finish();
    } */