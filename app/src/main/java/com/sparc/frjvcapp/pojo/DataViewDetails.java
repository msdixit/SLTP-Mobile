package com.sparc.frjvcapp.pojo;

public class DataViewDetails {
    public String lat;
    public String lon;
    public String pillarNo;
    public String image;
    public  String syncStatus;
    public String unique;

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getPillarNo() {
        return pillarNo;
    }

    public void setPillarNo(String pillarNo) {
        this.pillarNo = pillarNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
