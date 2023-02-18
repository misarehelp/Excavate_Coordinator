package ru.volganap.nikolay.excavate_coordinator;

import android.content.Intent;
import com.google.android.gms.maps.model.LatLng;

public class PresenterMaps implements Contract.PresenterMaps, KM_Constants, Enums,
                                Contract.ModelMap.CallbackSetMapsLayout, Contract.ModelMap.CallbackOnMapReady {

    // creating object of View Interface
    private Contract.ViewMap mapView;

    // creating object of Model Interface
    private Contract.ModelMap modelMap;

    // initiating the objects of View and Model Interface
    public PresenterMaps(Contract.ViewMap mapsView, Intent intent) {
        this.mapView = mapsView;
        this.modelMap = new ModelMap();
        modelMap.initMap(this, intent);
    }

    // ************* start methods passed from View to ModelMap *******************
    @Override
    public void onMapReady() {
        modelMap.getSavedLines(this);
        modelMap.getCenterMap(this);
        modelMap.checkOnMapClickListenerns(this);
    }

    @Override
    public void onButtonContourLineClick() {
        modelMap.doButtonContourLineLogic(this, this);
    }

    @Override
    public void onButtonEndLineClick() {
        modelMap.doButtonEndLineLogic(this);
    }

    @Override
    public void onButtonMapClearClick() {
        modelMap.doButtonMapClearLogic(this, this);
    }

    @Override
    public void onButtonMapExitClick() {
        modelMap.doButtonMapExitLogic(this);
    }

    @Override
    public void onButtonCheckNoLinesClick() {
        modelMap.doButtonCheckNoLinesLogic(this);
    }

    @Override
    public void OnMapClickListener( LatLng latLng) {
        modelMap.doMapClickLogic(this, this, latLng);
    }

    // ************* start  of callbacks passed from ModelMap *******************

    @Override
    public void OnFinishedCenterMap( LatLng value ) {
        mapView.centerMap(value);
    }

    @Override
    public void OnFinishedSetUpButtonsAppearance( String value ) {
        mapView.setUpButtonsAppearance(value);
    }

    @Override
    public void OnFinishedSetUser( String value ) {
        mapView.setUserTextView(value);
    }

    @Override
    public void OnFinishedSetMarkerLine( MarkerLineData value ) {
        mapView.setMarkerLine(value.getPoint1(), value.getPoint2(), value.getDepartment(), value.getDate(), value.getColor(), value.getMaster());
    }

    @Override
    public void OnAddMarkerListenerns( LatLng latLng ) {
        mapView.addMarker(latLng);
    }

    @Override
    public void OnFinishedSetOnMapClickListenerns() {
        mapView.setOnMapClickListenerns();
    }

    @Override
    public void OnFinishedMapClearListenerns() {
        modelMap.getSavedLines(this);
        mapView.clearMap();
    }

    @Override
    public void OnFinishedRefreshMapStatus(String value) {
        mapView.refreshMapStatus(value);
    }

    @Override
    public void OnFinishedDrawLegend( String[] depart, int [] color_text, int [] color_bg ) {
        mapView.addColorRowToLegend( depart, color_text, color_bg );
    }

    @Override
    public void OnCloseMapsListenerns() {
        mapView.finishMapsActivity();
    }

    // ************* end  of callbacks passed from ModelMap *******************

    @Override
    public void onDestroy() {
        mapView = null;
    }
}
