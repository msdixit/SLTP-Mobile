package com.sparc.frjvcapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sparc.frjvcapp.OnTapListener;
import com.sparc.frjvcapp.R;
import com.sparc.frjvcapp.pojo.DataViewDetails;
import com.sparc.frjvcapp.setViewHolder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class DataViewAdapter extends RecyclerView.Adapter<setViewHolder> {
    List<DataViewDetails> dataitems;
    private Context context;
    private OnTapListener onTapListener;

    public DataViewAdapter(Context ctx, List<DataViewDetails> dataitems) {
        this.context = ctx;
        this.dataitems = dataitems;
    }


    @NonNull
    @Override
    public setViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_dataview, viewGroup, false);
        return new setViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final setViewHolder holder, int i) {
        holder.txtPillarNo.setText("Pillar No: " + dataitems.get(holder.getAdapterPosition()).getPillarNo());
        holder.txtPillarLat.setText("Lat: " + dataitems.get(holder.getAdapterPosition()).getLat());
        holder.txtPillarLon.setText("Lon:" + dataitems.get(holder.getAdapterPosition()).getLon());
        if (dataitems.get(holder.getAdapterPosition()).getSyncStatus().equals("0")) {
            holder.DeleteImg.setVisibility(View.VISIBLE);
            String ab = dataitems.get(holder.getAdapterPosition()).getImage();
            try {
                InputStream is = new FileInputStream(ab);
                Drawable icon = new BitmapDrawable(is);
                holder.PillarImg.setImageDrawable(icon);
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        } else {
            holder.DeleteImg.setVisibility(View.INVISIBLE);
            String ab = dataitems.get(holder.getAdapterPosition()).getImage();
            Glide.with(context).load("http://203.129.207.130:5065/pillarsphoto/" + ab + "").into(holder.PillarImg);
            holder.DeleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTapListener != null) {
                        onTapListener.OnTapView(holder.getAdapterPosition(), "delete");
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataitems.size();
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }


}
