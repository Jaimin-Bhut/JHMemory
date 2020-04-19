package com.jb.dev.jhmemory.googleDrive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jb.dev.jhmemory.R;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    private Context context;

    int[] imgList = {R.drawable.image_1, R.drawable.image_2,
            R.drawable.ran_ki_vav,R.drawable.image_3, R.drawable.image_4,
            R.drawable.image_5,R.drawable.rann_of_kutch, R.drawable.image_6, R.drawable.image_7, R.drawable.image_8,
            R.drawable.image_9,
            R.drawable.pavagadh,R.drawable.image_10};

    public GridAdapter(Context context) {
        this.context = context;
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
        holder.imageView.setImageResource(imgList[position]);

    }

    @Override
    public int getItemCount() {
        return imgList.length;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_grid_image);
        }
    }
}