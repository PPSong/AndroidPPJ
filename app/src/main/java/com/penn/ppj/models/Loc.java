package com.penn.ppj.models;

/**
 * Created by penn on 20/02/2017.
 */

public class Loc {
    double lat;
    double lon;

    public Loc(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
