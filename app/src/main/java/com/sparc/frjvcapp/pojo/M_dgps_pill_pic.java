package com.sparc.frjvcapp.pojo;

public class M_dgps_pill_pic {


    public M_dgps_pill_pic(int pic_pill_no, String u_id, String pic_status, String pic_name,String pic_view) {
        this.pic_pill_no = pic_pill_no;
        this.u_id = u_id;
        this.pic_status = pic_status;
        this.pic_name = pic_name;
        this.pic_view=pic_view;
    }

    public String getPic_view() {
        return pic_view;
    }

    public void setPic_view(String pic_view) {
        this.pic_view = pic_view;
    }

    private String pic_view;
    private int pic_pill_no;
    private String u_id;

    public int getPic_pill_no() {
        return pic_pill_no;
    }

    public void setPic_pill_no(int pic_pill_no) {
        this.pic_pill_no = pic_pill_no;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getPic_status() {
        return pic_status;
    }

    public void setPic_status(String pic_status) {
        this.pic_status = pic_status;
    }

    public String getPic_name() {
        return pic_name;
    }

    public void setPic_name(String pic_name) {
        this.pic_name = pic_name;
    }

    private String pic_status;
    private String pic_name;






}
