package com.sparc.frjvcapp.pojo;

public class M_dgps_revisit_pillar_data_dwnld {
    public M_dgps_revisit_pillar_data_dwnld(String p_lat, String p_long, String pillar_no, String fb_name, String fb_id, String p_syrvey_sts, String m_dgps_surv_sts, String m_dgps_file_sts, String o_Id, String m_survey_status) {
        this.m_survey_status = m_survey_status;
        this.o_Id = o_Id;
        this.p_lat = p_lat;
        this.p_long = p_long;
        this.pillar_no = pillar_no;
        this.fb_name = fb_name;
        this.fb_id = fb_id;
        this.p_syrvey_sts = p_syrvey_sts;
        this.m_dgps_surv_sts = m_dgps_surv_sts;
        this.m_dgps_file_sts = m_dgps_file_sts;
    }

    public String getM_pndjv_pill_no() {
        return m_pndjv_pill_no;
    }

    public void setM_pndjv_pill_no(String m_pndjv_pill_no) {
        this.m_pndjv_pill_no = m_pndjv_pill_no;
    }

    private String m_pndjv_pill_no;
    private String m_survey_status;

    public String getO_Id() {
        return o_Id;
    }

    public void setO_Id(String o_Id) {
        this.o_Id = o_Id;
    }

    private String o_Id;
    private String p_lat;

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

    public String getm_dgps_surv_sts() {
        return m_dgps_surv_sts;
    }

    public void setP_dgps_survey_sts(String m_dgps_surv_sts) {
        this.m_dgps_surv_sts = m_dgps_surv_sts;
    }

    private String p_long;
    private String pillar_no;
    private String fb_name;
    private String fb_id;
    private String p_syrvey_sts;
    private String m_dgps_surv_sts;

    public String getM_dgps_surv_sts() {
        return m_dgps_surv_sts;
    }

    public void setM_dgps_surv_sts(String m_dgps_surv_sts) {
        this.m_dgps_surv_sts = m_dgps_surv_sts;
    }

    public String getM_dgps_file_sts() {
        return m_dgps_file_sts;
    }

    public void setM_dgps_file_sts(String m_dgps_file_sts) {
        this.m_dgps_file_sts = m_dgps_file_sts;
    }

    private String m_dgps_file_sts;

    public String getM_survey_status() {
        return m_survey_status;
    }

    public void setM_survey_status(String m_survey_status) {
        this.m_survey_status = m_survey_status;
    }
}
