package com.sparc.frjvcapp.Adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sparc.frjvcapp.OnTapListener;
import com.sparc.frjvcapp.R;
import com.sparc.frjvcapp.Viewholders.DGPSDataViewHolder;
import com.sparc.frjvcapp.pojo.DGPSDataViewDetails;
import com.sparc.frjvcapp.pojo.DataViewDetails;
import com.sparc.frjvcapp.setViewHolder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class DGPSDataViewAdapter  extends RecyclerView.Adapter<DGPSDataViewHolder>{
    List<DGPSDataViewDetails> dataitems;
    private Context context;
    private OnTapListener onTapListener;

    public DGPSDataViewAdapter(Context ctx, List<DGPSDataViewDetails> dataitems) {
        this.context = ctx;
        this.dataitems = dataitems;
    }
    @NonNull
    @Override
    public DGPSDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dgps_dataview_card_view, viewGroup, false);
        return new DGPSDataViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull final DGPSDataViewHolder holder, int i) {
        holder.txtPillarNo.setText("Pillar No: " + dataitems.get(holder.getAdapterPosition()).getPillarNo());
        holder.txtjobid.setText("Job ID: " + dataitems.get(holder.getAdapterPosition()).getJob_id());
        if (dataitems.get(holder.getAdapterPosition()).getSyncStatus().equals("0")) {

            holder.DeleteImg.setVisibility(View.VISIBLE);
            holder.DeleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTapListener != null) {
                        onTapListener.OnTapView(holder.getAdapterPosition(), "delete");
                    }
                }
            });
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
            holder.DeleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTapListener != null) {
                        onTapListener.OnTapView(holder.getAdapterPosition(), "delete");
                    }
                }
            });
            String ab = dataitems.get(holder.getAdapterPosition()).getImage();
            try {
                InputStream is = new FileInputStream(ab);
                Drawable icon = new BitmapDrawable(is);
                holder.PillarImg.setImageDrawable(icon);

            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
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
