package com.sparc.frjvcapp.pojo;

public class M_range {
    private String r_name;
    private String r_id;
    private String d_id;

    public M_range(String r_name, String r_id,String d_id) {
        this.r_name = r_name;
        this.r_id = r_id;
        this.d_id=d_id;
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
    }


}
