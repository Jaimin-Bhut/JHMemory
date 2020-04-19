package com.jb.dev.jhmemory.googleDrive;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jb.dev.jhmemory.R;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    private Context context;
    private List<Uri> list;

    GridAdapter(Context context, List<Uri> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        GridViewHolder viewHolder = new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GridAdapter.GridViewHolder holder, int position) {
        Uri imageListModel = list.get(position);
        Log.e("Uri>>>", imageListModel + "");
        Glide.with(context).load(list.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_grid_image);
        }
    }
}