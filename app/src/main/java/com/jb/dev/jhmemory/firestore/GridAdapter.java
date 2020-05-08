package com.jb.dev.jhmemory.firestore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.jb.dev.jhmemory.R;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    private Context context;
    private ArrayList<ImageListModel> imageListModelArrayList;

    GridAdapter(Context context, ArrayList<ImageListModel> list) {
        this.context = context;
        this.imageListModelArrayList = list;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        GridViewHolder viewHolder = new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GridAdapter.GridViewHolder holder, final int position) {
        ImageListModel imageListModel = imageListModelArrayList.get(position);
        Glide.with(context).
                load(imageListModel.getName()).
                centerCrop().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageListModelArrayList.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_grid_image);
        }
    }
}