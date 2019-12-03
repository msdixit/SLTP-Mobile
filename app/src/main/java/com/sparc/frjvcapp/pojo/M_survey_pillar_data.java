package com.sparc.frjvcapp.pojo;

public class M_survey_pillar_data {
    public M_survey_pillar_data(String p_lat, String p_long, String pillar_no, String fb_name, String fb_id,String p_syrvey_sts) {
        this.p_lat = p_lat;
        this.p_long = p_long;
        this.pillar_no = pillar_no;
        this.fb_name = fb_name;
        this.fb_id = fb_id;
        this.p_syrvey_sts=p_syrvey_sts;
    }

    private String p_lat;
    private String p_long;
    private String pillar_no;
    private String fb_name;
    private String fb_id;
    private String p_syrvey_sts;

    public String getP_lat() {
        return p_lat;
    }

    public void setP_lat(String p_lat) {
        this.p_lat = p_lat;
    }

    public String getP_long() {
        return p_long;
    }

    public void setP_long(String p_long) {
        this.p_long = p_long;
    }

    public String getPillar_no() {
        return pillar_no;
    }

    public void setPillar_no(String pillar_no) {
        this.pillar_no = pillar_no;
    }

    public String getFb_name() {
        return fb_name;
    }

    public void setFb_name(String fb_name) {
        this.fb_name = fb_name;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getP_syrvey_sts() {
        return p_syrvey_sts;
    }

    public void setP_syrvey_sts(String p_syrvey_sts) {
        this.p_syrvey_sts = p_syrvey_sts;
    }
}
