package ru.volganap.nikolay.excavate_coordinator;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, KM_Constants {
    public static final String NO_LINES_SET ="Нет отмеченных коммуникаций";
    public static final String START_LINE_CODE ="Тапом на карте отметьте  стартовую точку начала новой линии";
    public static final String CHECK_NO_COMMUNICATIONS =" , либо обозначьте отсутствие коммуникаций";
    public static final String ADD_LINE_CODE ="Укажите тапом на карте следующую точку";
    public static final String ADD_LINE_OR_FINISH_CODE ="Укажите тапом  на карте следующую точку или нажмите *Закончить*";
    //public static final String EDIT_LINE_CODE ="Режим редактирования (добавления/удаления) коммуникаций";
    public static final String END_LINE_CODE ="Тапом на карте начните новую группу линий или завершите построение линий";
    public static final String CONTOUR_LINE_CODE ="Для обозначения контура необходимо установить не менее 3 точек";
    public static final String CLEAR_LINE_CODE ="Последняя введеная линия (группа линий) удалена";
    public static final String POSITION_IS_NOT_DEFINED ="Координаты не определены";
    public static final Double LAT_BASE = 55.989174;
    public static final Double LONG_BASE = 48.604360;
    public static final int ZOOM_NEW = 10;
    public static final int ZOOM_BASE = 17;

    private int[] color_depart = {Color.BLUE, Color.MAGENTA, Color.GREEN, Color.RED, Color.YELLOW, Color.GRAY, Color.LTGRAY, Color.DKGRAY, Color.CYAN};
    private LatLng firstPos;
    private GoogleMap mMap;
    private UiSettings uiSettings;
    private LatLngBounds.Builder builder;
    private SharedPreferences sharedPrefs;
    private ArrayList<LatLng> line =  new ArrayList<>();
    private String marker_code;
    private String department_user;
    //private float  mid_zoom = 15;
    private Button bt_map_contour_line, bt_map_end_line, bt_map_clear, bt_map_exit, bt_check_no_lines;
    private TextView tv_maps_state;
    HashMap<String, Integer> hashmap_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MapsActivity: onCreate ");
        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
        setContentView(R.layout.activity_maps);
        bt_map_contour_line = findViewById(R.id.bt_map_contour_line);
        bt_map_end_line = findViewById(R.id.bt_map_end_line);
        bt_map_clear = findViewById(R.id.bt_map_clear);
        bt_map_exit = findViewById(R.id.bt_map_exit);
        bt_check_no_lines = findViewById(R.id.bt_check_no_lines);
        tv_maps_state = findViewById(R.id.tv_maps_state);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        String dep_line_data_json = getIntent().getStringExtra(DEP_LINE_DATA);
        String permit_code = getIntent().getStringExtra(DATA_TYPE);
        DepLinesData dep_line_data = new Gson().fromJson(dep_line_data_json, DepLinesData.class);

        setupColorDepartment();

        mMap = googleMap;

        LatLng center_point = null;
        try {
          center_point = dep_line_data.getLinesHashmap().get(dep_line_data.getDepartMaster()).get(0).get(0);
        } catch (Exception error) {
            //
        }
        initMapSetup(mMap,  center_point);

        //defineGroupZoom();

        drawSavedLines(dep_line_data);

        marker_code = START_LINE_CODE;

        setUpButtonsAppearance(permit_code);

        initMapsButtons (dep_line_data, permit_code);

        if ( permit_code.equals(NEW_PERMIT_CODE) || permit_code.equals(ADD_PERMIT_CODE) ) {
            setOnMapClickListenerns();
        }
    }

    public void setupColorDepartment() {

        String[] department_array = getResources().getStringArray(R.array.department_values);
        department_user = sharedPrefs.getString(DEPARTMENT_USER, department_array[0]);
        hashmap_color = new HashMap<>();
        for (int i=0; i < department_array.length; i++) {
            hashmap_color.put(department_array[i], color_depart[i]);
        }
    }

    public void drawSavedLines(DepLinesData dep_line_data) {

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
                    setMarkerLine(latLng1, latLng2, key_department, date_approve);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void initMapSetup(GoogleMap mMap, LatLng center_point) {
        if (Build.VERSION.SDK_INT > 20) {
            int m_type = Integer.parseInt(sharedPrefs.getString(MAP_TYPE, "2"));
            mMap.setMapType(m_type);
        } else mMap.setMapType(4);

        firstPos = null; // Initilization the first point
        mMap.setMyLocationEnabled(true);
        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(true);
        builder = new LatLngBounds.Builder();

        if (center_point == null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_BASE, LONG_BASE), ZOOM_NEW));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center_point, ZOOM_BASE));
        }
    }

    //Set up buttons and titles
    public void setUpButtonsAppearance(String line_code) {

        switch (line_code) {

            //buttons' setup for a new line
            case NEW_PERMIT_CODE:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.INVISIBLE);
                bt_check_no_lines.setVisibility(View.INVISIBLE);
                refreshMapStatus(START_LINE_CODE);
                break;

            //buttons' setup for a permit to be edited
            case ADD_PERMIT_CODE:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.INVISIBLE);
                refreshMapStatus(START_LINE_CODE + CHECK_NO_COMMUNICATIONS);
                break;

            //buttons' setup for a permit to be edited
            case SHOW_PERMIT_CODE:
            case EDIT_PERMIT_CODE:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.INVISIBLE);
                bt_check_no_lines.setVisibility(View.INVISIBLE);
                refreshMapStatus(line_code);
                break;

            //buttons' setup for the line to be finished
            case CLEAR_LINE_CODE:
            case START_LINE_CODE:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.VISIBLE);
                refreshMapStatus(line_code);
                break;

            case END_LINE_CODE:
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.VISIBLE);
                refreshMapStatus(line_code);
                break;

            case ADD_LINE_OR_FINISH_CODE:
                bt_map_contour_line.setVisibility(View.VISIBLE);
            case ADD_LINE_CODE:
                bt_map_end_line.setVisibility(View.VISIBLE);
                bt_map_clear.setVisibility(View.VISIBLE);
                refreshMapStatus(line_code);
                break;

            default:

        }
    }

    public void initMapsButtons (DepLinesData dep_line_data_orig, String orig_code) {

        ArrayList<ArrayList<LatLng>> lines_group = new ArrayList<>();
        //Click to start drawing lines
        bt_map_contour_line.setOnClickListener(v -> {

            if (!(line == null) && line.size() > 2) {
                LatLng endPos = line.get(0);
                line.add(endPos);
                setMarkerLine (firstPos, endPos, department_user, "");
                lines_group.add(line);
                marker_code = START_LINE_CODE;
                setUpButtonsAppearance(START_LINE_CODE);
            } else {
                refreshMapStatus(CONTOUR_LINE_CODE);
            }
        });

        //Click to stop drawing lines
        bt_map_end_line.setOnClickListener(v -> {
            //Add line group to ArrayList of group of lines
            if (line != null && marker_code.equals(ADD_LINE_OR_FINISH_CODE)){
                lines_group.add(line);
                marker_code = START_LINE_CODE;
                setUpButtonsAppearance(END_LINE_CODE);
                //line.clear();
            } else {
                refreshMapStatus(NO_LINES_SET);
            }
        });

        //Click to clear lines
        bt_map_clear.setOnClickListener(v -> {
            marker_code = CLEAR_LINE_CODE;
            mMap.clear();
            drawSavedLines(dep_line_data_orig);

            setUpButtonsAppearance(marker_code);

            if (lines_group != null){
                lines_group.clear();
            }
        });

        //Click to stop drawing lines
        bt_map_exit.setOnClickListener(v -> {
            //Save all the lines to dep_line_data
            DepLinesData dep_line_data = dep_line_data_orig;
            String code = orig_code;

            if (!orig_code.equals(SHOW_PERMIT_CODE)) {
                HashMap<String, ArrayList<ArrayList<LatLng>>> lines_hashmap = new HashMap<>();

                if ( lines_group !=null ) {
                    if (!(dep_line_data.getLinesHashmap() == null)) {
                        lines_hashmap = dep_line_data.getLinesHashmap();
                    }
                    lines_hashmap.put(department_user, lines_group);
                    dep_line_data.setLinesHashmap(lines_hashmap);
                    dep_line_data = getCommAndDateAproveSet( dep_line_data, code, Approvement.YES );

                } else {
                    code = DATA_WAS_NOT_CHANGED;
                }
            }

            setResultToPermitActivity (dep_line_data, code);

        });

        //Click to check there is no communications of chosen department user
        bt_check_no_lines.setOnClickListener(v -> {
            DepLinesData dep_line_data = getCommAndDateAproveSet( dep_line_data_orig, orig_code, Approvement.NO );
            setResultToPermitActivity (dep_line_data, orig_code);
        });
    }

    public DepLinesData getCommAndDateAproveSet(DepLinesData dep_line_data, String code, Approvement appr) {

        String str_date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());

        // set the communications YES state
        HashMap<String, Approvement> hashmap_comm_exist = dep_line_data.getHashmapCommExist();
        hashmap_comm_exist.put(department_user, appr);
        dep_line_data.setHashmapCommExist(hashmap_comm_exist);

        // set the Date Approve YES state
        HashMap<String, String> date_approve_hashmap = dep_line_data.getDateApproveHashmap();
        date_approve_hashmap.put(department_user, str_date);
        dep_line_data.setDateApproveHashmap(date_approve_hashmap);

        if ( code.equals(NEW_PERMIT_CODE) ) {
            dep_line_data.setStringDateReg(str_date);
        }

        return dep_line_data;
    }

    public void setResultToPermitActivity(DepLinesData dep_line_data, String code) {

        Intent back_intent = getIntent();
        back_intent.putExtra(DATA_TYPE, code);
        String dep_line_data_json = new Gson().toJson(dep_line_data);
        back_intent.putExtra(DEP_LINE_DATA, dep_line_data_json);
        setResult(MAPS_ACTIVITY_REQUEST_CODE, back_intent);
        finish();
    }

    public void setOnMapClickListenerns() {
        //setOnMapClickListener for drawing lines
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LatLng secondPos;
                if (null == latLng) {
                    refreshMapStatus(POSITION_IS_NOT_DEFINED);
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
                        Marker myMarker = mMap.addMarker(new MarkerOptions()
                                .position(latLng));
                        //.title(Objects.requireNonNull(getContext()).getString(R.string.title_on_marker_to_new_order))
                        myMarker.setTag(null);

                        //add point to the line ArrayList
                        line.add(latLng);
                        setUpButtonsAppearance(marker_code);
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
                        setUpButtonsAppearance(marker_code);
                        setMarkerLine (firstPos, secondPos, department_user, "");
                        break;
                    //default
                    default:
                        break;
                }
                //secondPos = (null == firstPos) ? latLng : firstPos;
            }
        });
    }

    public void refreshMapStatus(String status) {
        tv_maps_state.setText(status);
    }

    //draw a line defined by 2 points
    public void setMarkerLine ( LatLng latLng1, LatLng latLng2, String depart, String date_reg ) {
        int color_line = hashmap_color.get( depart );
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(latLng2)
                .add(latLng1)
                .color(color_line)
                .clickable(true)
                .width(4);
        mMap.addPolyline(polylineOptions);

        Double midLatitude = (latLng2.latitude + latLng1.latitude)/2;
        Double midLongitude = (latLng2.longitude + latLng1.longitude)/2;
        LatLng midLatLng = new LatLng(midLatitude, midLongitude);

        /*BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker();
         if (i==0) {
            bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } */
        String snippet_data = "Служба: " + depart + "\n" + "Дата и время: " + date_reg;

        /* Marker marker = mMap.addMarker(new MarkerOptions()
                .position(midLatLng)
                .title(depart)
                .alpha(0.0f)
                //.snippet(snippet_data)
                .zIndex(3)
                //.icon(bitmapDescriptor)
                .anchor(0.0f, 0.0f)
        );

        setMarkerInfoWindowAdapter();
        marker.showInfoWindow(); */
    }

    public void setMarkerInfoWindowAdapter() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context context = getApplicationContext();
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });
    }

    public void defineGroupZoom() {
        LatLngBounds bounds = builder.build();
        Log.d(LOG_TAG, "MapsActivity - setOnCameraIdleListener works, the last marker is set up");
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

}


    /* public void showDistance(LatLng latLng1, LatLng latLng2) {
        //float [] distance = new float[1];
        //Location.distanceBetween(latLng2.latitude, latLng2.longitude, latLng1.latitude, latLng1.longitude, distance);
        //String s = "Дистанция по прямой - " + Math.round(distance[0]) + "  метров";
    } */

       /*  int zoomCurrent = Integer.parseInt(sharedPrefs.getString(MARKER_SCALE, "15"));
        int cameraDelay = Integer.parseInt(sharedPrefs.getString(MARKER_DELAY, "600"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(midLatLng)
                .zoom(zoomCurrent)
                //.zoom(mid_zoom)
                .bearing(0)
                .tilt(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), cameraDelay, null); */
              /*
        //Start line drawing
        if (markers.size()==0  && dep_line_data.master) {
            bt_map_start_line.setVisibility(View.VISIBLE);
        }

        //Finish line drawing
        if (markers.size()>0 && !(dep_line_data.master) && (!loc_depart.equals(dep_line_data.depart))) {
            bt_map_end_line.setVisibility(View.VISIBLE);
        }



        //Show cluster of Markers
        i = markers.size();
        mMap.setOnCameraIdleListener(() -> {
                Log.d(LOG_TAG, "MapsActivity: onCameraIdle(), i=" + i);
                for (i=0; i < markers.size()-1; i++) {
                    setMarkerLine(dep_line_data.master, i, markers.get(i), markers.get(i+1),
                            dep_line_data.depart, dep_line_data.date_reg.toString());
                    //setMarker(i-1, markers_end.get(i-1), dep_line_data.depart, dep_line_data.date_reg.toString());
                }

                mMap.setOnCameraIdleListener(null);
                kidMarkersAreReady = true;
                defineGroupZoom();
        });
        */

        /* mMap.setOnMyLocationButtonClickListener(() -> {
                if (currentPos != null && lastPos != null) {
                    showDistance(currentPos, lastPos);
                }
                return true;
            }
        ); */

        /*mMap.setOnMyLocationChangeListener(location -> {
                currentPos = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(LOG_TAG, "MapsActivity - setOnMyLocationChangeListener fired");
                //builder.include(currentPos);

                for (i=0; i < markers_start.size(); i++) {
                    LatLng line_start = markers_start.get(i);
                    //LatLng line_end = markers_end.get(i);
                    //Double longitude = Double.parseDouble(data.get(i).get(ATTRIBUTE_NAME_LONG));
                    builder.include(line_start);
                    //builder.include(line_end);
                }

                mMap.setOnMyLocationChangeListener(null);
                parentMarkerIsReady = true;
                defineGroupZoom();
                //mid_zoom = mMap.getCameraPosition().zoom;
        }); */


//mMap.getUiSettings().setMapToolbarEnabled(true);
//mMap.getUiSettings().setAllGesturesEnabled(true);
//mMap.getUiSettings().setMyLocationButtonEnabled(true);
//CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//mMap.moveCamera(CameraUpdateFactory.newLatLng(marker_pos));
//mMap.animateCamera(cameraUpdate);
//mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude , longitude) , 17));