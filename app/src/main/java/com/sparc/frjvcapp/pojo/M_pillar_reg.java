package com.sparc.frjvcapp.pojo;

public class M_pillar_reg {
    private String img_status;
    private String delete_status;
    private String d_id;
    private String r_id;
    private String fb_id;
    private String p_no;
    private String p_lat;
    private String p_long;
    private String p_type;
    private String p_pos;
    private String p_status;
    private String p_cond;
    private String p_rmk;
    private String p_pic;
    private String p_sts;
    private int point_no;
    private String b_type;
    private String patch_no;
    private String ring_no;
    private String p_loc_type;
    private String p_paint_status;
    private String fb_name;
    private String p_sl_no;
    private String uid;

    public M_pillar_reg(String d_id, String r_id, String fb_id,String p_sl_no, String p_lat, String p_long, String p_type, String p_cond, String p_rmk, String p_pic, String p_sts,String patch_no,String ring_no,String p_loc_type,String p_no,String p_paint_status,String fb_name,String uid,int point_no,String img_status,String delete_status) {
        this.d_id = d_id;
        this.r_id = r_id;
        this.fb_id = fb_id;
        this.p_sl_no=p_sl_no;
        this.p_lat = p_lat;
        this.p_long = p_long;
        this.p_type = p_type;
        this.p_cond = p_cond;
        this.p_rmk = p_rmk;
        this.p_pic = p_pic;
        this.p_sts = p_sts;
        this.patch_no=patch_no;
        this.ring_no=ring_no;
        this.p_loc_type=p_loc_type;
        this.p_no = p_no;
        this.p_paint_status=p_paint_status;
        this.fb_name=fb_name;
        this.uid=uid;
        this.point_no=point_no;
        this.img_status=img_status;
        this.delete_status=delete_status;
    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

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

    public String getP_type() {
        return p_type;
    }

    public void setP_type(String p_type) {
        this.p_type = p_type;
    }

    public String getP_pos() {
        return p_pos;
    }

    public void setP_pos(String p_pos) {
        this.p_pos = p_pos;
    }

    public String getP_status() {
        return p_status;
    }

    public void setP_status(String p_status) {
        this.p_status = p_status;
    }

    public String getP_cond() {
        return p_cond;
    }

    public void setP_cond(String p_cond) {
        this.p_cond = p_cond;
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

    public String getP_sts() {
        return p_sts;
    }

    public void setP_sts(String p_sts) {
        this.p_sts = p_sts;
    }

    public String getB_type() {
        return b_type;
    }

    public void setB_type(String b_type) {
        this.b_type = b_type;
    }

    public String getPatch_no() {
        return patch_no;
    }

    public void setPatch_no(String patch_no) {
        this.patch_no = patch_no;
    }

    public String getRing_no() {
        return ring_no;
    }

    public void setRing_no(String ring_no) {
        this.ring_no = ring_no;
    }

    public String getP_loc_type() {
        return p_loc_type;
    }

    public void setP_loc_type(String p_loc_type) {
        this.p_loc_type = p_loc_type;
    }

    public String getP_paint_status() {
        return p_paint_status;
    }

    public void setP_paint_status(String p_paint_status) {
        this.p_paint_status = p_paint_status;
    }

    public String getFb_name() {
        return fb_name;
    }

    public void setFb_name(String fb_name) {
        this.fb_name = fb_name;
    }

    public String getP_sl_no() {
        return p_sl_no;
    }

    public void setP_sl_no(String p_sl_no) {
        this.p_sl_no = p_sl_no;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPoint_no() {
        return point_no;
    }

    public void setPoint_no(int point_no) {
        this.point_no = point_no;
    }

    public String getImg_status() {
        return img_status;
    }

    public void setImg_status(String img_status) {
        this.img_status = img_status;
    }

    public String getDelete_status() {
        return delete_status;
    }

    public void setDelete_status(String delete_status) {
        this.delete_status = delete_status;
    }
}
