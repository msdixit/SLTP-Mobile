package com.sparc.frjvcapp.pojo;

public class M_fb {
    private String fb_name;
    private String fb_id;
    private String div_id;
    private String fb_range_id;
    private String fb_type;
    private String fb_cmv_path;
    private String fb_mmv_path;
    private String m_fb_updated_pillar_kml;


    public M_fb(String fb_name, String fb_id, String fb_range_id, String div_id, String fb_type, String fb_cmv_path, String fb_mmv_path, String m_fb_updated_pillar_kml) {
        this.fb_name = fb_name;
        this.fb_id = fb_id;
        this.fb_range_id = fb_range_id;
        this.div_id = div_id;
        this.fb_type = fb_type;
        this.fb_cmv_path = fb_cmv_path;
        this.fb_mmv_path = fb_mmv_path;
        this.m_fb_updated_pillar_kml = m_fb_updated_pillar_kml;
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

    public String getDiv_id() {
        return div_id;
    }

    public void setDiv_id(String div_id) {
        this.div_id = div_id;
    }

    public String getFb_range_id() {
        return fb_range_id;
    }

    public void setFb_range_id(String fb_range_id) {
        this.fb_range_id = fb_range_id;
    }

    public String getFb_type() {
        return fb_type;
    }

    public void setFb_type(String fb_type) {
        this.fb_type = fb_type;
    }

    public String getFb_cmv_path() {
        return fb_cmv_path;
    }

    public void setFb_cmv_path(String fb_cmv_path) {
        this.fb_cmv_path = fb_cmv_path;
    }

    public String getFb_mmv_path() {
        return fb_mmv_path;
    }

    public void setFb_mmv_path(String fb_mmv_path) {
        this.fb_mmv_path = fb_mmv_path;
    }

    public String getM_fb_updated_pillar_kml() {
        return m_fb_updated_pillar_kml;
    }

    public void setM_fb_updated_pillar_kml(String m_fb_updated_pillar_kml) {
        this.m_fb_updated_pillar_kml = m_fb_updated_pillar_kml;
    }
}
