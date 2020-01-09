package com.sparc.frjvcapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class M_Dgps_Sync_Data {
    public String getInt_statusId() {
        return int_statusId;
    }

    public void setInt_statusId(String int_statusId) {
        this.int_statusId = int_statusId;
    }

    @SerializedName("int_statusId")
    @Expose
    private String int_statusId;

    public M_Dgps_Sync_Data(String int_statusId,String file_name) {
        this.int_statusId = int_statusId;
        this.file_name = file_name;
    }

    @SerializedName("chrv_statusName")
    @Expose
    private String file_name;



    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("User{").append("First name: ")
                .append(int_statusId).append(", Last name: ")
                .append(file_name).append("}").toString();
    }


}
