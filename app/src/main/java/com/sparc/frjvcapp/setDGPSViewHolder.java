package com.sparc.frjvcapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class setDGPSViewHolder extends RecyclerView.ViewHolder{
    public TextView pill_no;
    public TextView pill_name;
    public ImageView pill_sts;

    public setDGPSViewHolder(@NonNull View itemView) {
        super(itemView);
        pill_no= itemView.findViewById(R.id.pill_no);
        pill_name= itemView.findViewById(R.id.pillar_name);
        pill_sts= itemView.findViewById(R.id.dgpssync);


    }
}
