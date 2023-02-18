package ru.volganap.nikolay.excavate_coordinator;

import com.google.android.gms.maps.model.LatLng;

public class MarkerLineData {
    private LatLng point1;
    private LatLng point2;
    private String department;
    private String date;
    private int color;
    private boolean master;

    public MarkerLineData( LatLng point1, LatLng point2, String department, String date, int color, boolean master  ) {
        this.point1 = point1;
        this.point2 = point2;
        this.department = department;
        this.date = date;
        this.color = color;
        this.master = master;
    }

    public LatLng getPoint1 () {
        return point1;
    }

    public LatLng getPoint2 () {
        return point2;
    }

    public String getDepartment () {
        return department;
    }

    public String getDate () {
        return date;
    }

    public int getColor () {
        return color;
    }

    public  boolean getMaster() { return master;}
}
