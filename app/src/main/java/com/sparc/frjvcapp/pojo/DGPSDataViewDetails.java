package com.sparc.frjvcapp.pojo;

import android.content.Context;

import java.util.ArrayList;

public class DGPSDataViewDetails {
    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String job_id;
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
