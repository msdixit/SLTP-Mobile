package com.sparc.frjvcapp.Adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sparc.frjvcapp.OnTapListener;
import com.sparc.frjvcapp.R;
import com.sparc.frjvcapp.pojo.DGPSPillarDataViewModel;
import com.sparc.frjvcapp.pojo.DataViewDetails;
import com.sparc.frjvcapp.setDGPSViewHolder;
import com.sparc.frjvcapp.setViewHolder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class DGPSPillarViewAdapter extends RecyclerView.Adapter<setDGPSViewHolder> {
    List<DGPSPillarDataViewModel> dataitems;
    private Context context;
    private OnTapListener onTapListener;

    public DGPSPillarViewAdapter(Context ctx, List<DGPSPillarDataViewModel> dataitems) {
        this.context = ctx;
        this.dataitems = dataitems;
    }


    @NonNull
    @Override
    public setDGPSViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_dgpsdata_recycle, viewGroup, false);
        return new setDGPSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull setDGPSViewHolder holder, int i) {
        String s[]=dataitems.get(holder.getAdapterPosition()).getFilename().split("/");
        holder.pill_no.setText(dataitems.get(holder.getAdapterPosition()).getPill_no());
        holder.pill_name.setText(s[s.length-1]);
        if (dataitems.get(holder.getAdapterPosition()).getSync_status().equals("1")) {
            holder.pill_sts.setBackgroundResource(R.drawable.ic_sync_black_24dp);

        }else {
            holder.pill_sts.setBackgroundResource(R.drawable.ic_sync_problem_black_24dp);

        }
    }

    @Override
    public int getItemCount() {
        return dataitems.size();
    }
}