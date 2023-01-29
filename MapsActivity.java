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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, KM_Constants, Enums, Contract.ViewMap {
    //public static final String NO_LINES_SET ="Нет отмеченных коммуникаций";
    public static final String CHECK_NO_COMMUNICATIONS =" , либо обозначьте отсутствие коммуникаций";
    public static final int ZOOM_NEW = 10;
    public static final int ZOOM_BASE = 17;

    Contract.PresenterMaps presenterMaps;

    //private LatLng firstPos;
    private GoogleMap mMap;
    private UiSettings uiSettings;
    private LatLngBounds.Builder builder;
    private SharedPreferences sharedPrefs;
    //private ArrayList<LatLng> line =  new ArrayList<>();
    //private String marker_code;
    //private String department_user;
    //private float  mid_zoom = 15;
    private Button bt_map_contour_line, bt_map_end_line, bt_map_clear, bt_map_exit, bt_check_no_lines;
    private TextView tv_maps_state;
    HashMap<String, Integer> hashmap_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MapsActivity: onCreate ");
        // instantiating object of Presenter Interface
        presenterMaps = new PresenterMaps(this, getIntent());

        initMapViewLayout();
        /* String dep_line_data_json = getIntent().getStringExtra(DEP_LINE_DATA);
        String permit_code = getIntent().getStringExtra(DATA_TYPE);
        DepLinesData dep_line_data = new Gson().fromJson(dep_line_data_json, DepLinesData.class); */
        initMapsButtons ();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Init Map activity layout
    protected void initMapViewLayout() {

        sharedPrefs = getSharedPreferences(PREF_ACTIVITY, MODE_PRIVATE);
        setContentView(R.layout.activity_maps);
        bt_map_contour_line = findViewById(R.id.bt_map_contour_line);
        bt_map_end_line = findViewById(R.id.bt_map_end_line);
        bt_map_clear = findViewById(R.id.bt_map_clear);
        bt_map_exit = findViewById(R.id.bt_map_exit);
        bt_check_no_lines = findViewById(R.id.bt_check_no_lines);
        tv_maps_state = findViewById(R.id.tv_maps_state);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        presenterMaps.onMapReady();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void centerMap( LatLng center_point) {
        if (Build.VERSION.SDK_INT > 20) {
            int m_type = Integer.parseInt(sharedPrefs.getString(MAP_TYPE, "2"));
            mMap.setMapType(m_type);
        } else mMap.setMapType(4);

        //firstPos = null; // Initilization the first point
        mMap.setMyLocationEnabled(true);
        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(true);
        builder = new LatLngBounds.Builder();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center_point, ZOOM_BASE));
    }

    //Set up buttons and titles
    @Override
    public void setUpButtonsAppearance(String line_code) {

        switch (line_code) {

            //buttons' setup for a new line
            case NEW_PERMIT_CODE:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.INVISIBLE);
                bt_check_no_lines.setVisibility(View.INVISIBLE);
                refreshMapStatus( START_LINE_CODE );
                break;

            //buttons' setup for a permit to be edited
            case ADD_PERMIT_CODE:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.INVISIBLE);
                refreshMapStatus( START_LINE_CODE + CHECK_NO_COMMUNICATIONS );
                break;

            //buttons' setup for a permit to be edited
            case EDIT_PERMIT_CODE:
            case SHOW_PERMIT_CODE:
            case DATA_WAS_SAVED:
            case DATA_WAS_NOT_CHANGED:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.INVISIBLE);
                bt_check_no_lines.setVisibility(View.INVISIBLE);
                refreshMapStatus( SHOW_PERMIT_CODE );
                break;

            //buttons' setup for the line to be finished
            case CLEAR_LINE_CODE:
            case START_LINE_CODE:
                bt_map_contour_line.setVisibility(View.INVISIBLE);
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.VISIBLE);
                refreshMapStatus( line_code );
                break;

            case END_LINE_CODE:
                bt_map_end_line.setVisibility(View.INVISIBLE);
                bt_map_clear.setVisibility(View.VISIBLE);
                refreshMapStatus( line_code );
                break;

            case ADD_LINE_OR_FINISH_CODE:
                bt_map_contour_line.setVisibility(View.VISIBLE);
            case ADD_LINE_CODE:
                bt_map_end_line.setVisibility(View.VISIBLE);
                bt_map_clear.setVisibility(View.VISIBLE);
                refreshMapStatus( line_code );
                break;

            default:

        }
    }

    public void initMapsButtons () {

        //Click to start drawing lines
        bt_map_contour_line.setOnClickListener(v -> {

            presenterMaps.onButtonContourLineClick();
        });

        //Click to stop drawing lines
        bt_map_end_line.setOnClickListener(v -> {
            //Add line group to ArrayList of group of lines
            presenterMaps.onButtonEndLineClick();
        });

        //Click to clear lines
        bt_map_clear.setOnClickListener(v -> {

            presenterMaps.onButtonMapClearClick();
        });

        //Click to stop drawing lines
        bt_map_exit.setOnClickListener(v -> {
            //Save all the lines to dep_line_data
            presenterMaps.onButtonMapExitClick();
        });

        //Click to check there is no communications of chosen department user
        bt_check_no_lines.setOnClickListener(v -> {

            presenterMaps.onButtonCheckNoLinesClick();
        });
    }

    @Override
    public void setOnMapClickListenerns() {
        //setOnMapClickListener for drawing lines
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                presenterMaps.OnMapClickListener(latLng);
            }
        });
    }

    @Override
    public void clearMap(){
        mMap.clear();
    }

    @Override
    public void addMarker(LatLng latLng){
        //
        Marker myMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng));
        //.title(Objects.requireNonNull(getContext()).getString(R.string.title_on_marker_to_new_order))
        myMarker.setTag(null);
    }

    @Override
    public void refreshMapStatus(String status) {
        tv_maps_state.setText(status);
    }

    //draw a line defined by 2 points
    @Override
    public void setMarkerLine ( LatLng latLng1, LatLng latLng2, String depart, String date_reg, int color_line ) {
        //int color_line = hashmap_color.get( depart );
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

    @Override
    public void finishMapsActivity() {
        Log.d(LOG_TAG, "MapsActivity: finishMapsActivity ");
        finish();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "MapsActivity: onDestroy ");
        super.onDestroy();
        presenterMaps.onDestroy();
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



    /* public void setResultToPermitActivity(DepLinesData dep_line_data, String code) {

        Intent back_intent = getIntent();
        back_intent.putExtra(DATA_TYPE, code);
        String dep_line_data_json = new Gson().toJson(dep_line_data);
        back_intent.putExtra(DEP_LINE_DATA, dep_line_data_json);
        setResult(MAPS_ACTIVITY_REQUEST_CODE, back_intent);
        finish();
    } */