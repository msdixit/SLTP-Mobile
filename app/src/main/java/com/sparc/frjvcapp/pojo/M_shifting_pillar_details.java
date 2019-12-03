package com.sparc.frjvcapp.pojo;

public class M_shifting_pillar_details{
    public M_shifting_pillar_details( String p_lat, String p_long, String p_rmk, String p_pic,String img_status,String fb_name,String uid,String fb_id,String p_no,String delete_status,String sync_status) {
        this.fb_id = fb_id;
        this.p_lat = p_lat;
        this.p_long = p_long;
        this.p_rmk = p_rmk;
        this.p_pic = p_pic;
        this.p_no = p_no;
        this.fb_name=fb_name;
        this.uid=uid;
        this.img_status=img_status;
        this.delete_status=delete_status;
        this.sync_status=sync_status;
    }
    private String p_no;

    public String getP_no() {
        return p_no;
    }

    public void setP_no(String p_no) {
        this.p_no = p_no;
    }

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

    public String getP_rmk() {
        return p_rmk;
    }

    public void setP_rmk(String p_rmk) {
        this.p_rmk = p_rmk;
    }

    public String getP_pic() {
        return p_pic;
    }

    public void setP_pic(String p_pic) {
        this.p_pic = p_pic;
    }

    public String getImg_status() {
        return img_status;
    }

    public void setImg_status(String img_status) {
        this.img_status = img_status;
    }

    public String getFb_name() {
        return fb_name;
    }

    public void setFb_name(String fb_name) {
        this.fb_name = fb_name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    private String p_lat;
    private String p_long;
    private String p_rmk;
    private String p_pic;
    private String img_status;
    private String fb_name;
    private String uid;
    private String fb_id;

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }

    private String delete_status;

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }

    private String sync_status;




}
