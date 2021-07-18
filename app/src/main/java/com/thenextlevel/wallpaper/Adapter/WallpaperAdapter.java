package com.thenextlevel.wallpaper.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thenextlevel.wallpaper.FullScreenWallpaper;
import com.thenextlevel.wallpaper.Model.WallpaperModel;
import com.thenextlevel.wallpaper.R;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperViewHolder> {

    private Context context;
    private List<WallpaperModel> wallpaperModelsList;

    public WallpaperAdapter(Context context, List<WallpaperModel> wallpaperModelsList) {
        this.context = context;
        this.wallpaperModelsList = wallpaperModelsList;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, final int position) {

        Glide.with(context)
                .load(wallpaperModelsList.get(position).getMediumUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FullScreenWallpaper.class)
                        .putExtra("originalUrl", wallpaperModelsList.get(position).getOriginalUrl()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperModelsList.size();
    }
}

class WallpaperViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;

    public WallpaperViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imageViewItem);
    }
}
