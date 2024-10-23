package com.example.socialtrailsapp.CustomAdapter;

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
import com.example.socialtrailsapp.R;

import java.util.List;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Uri> imageUris;

    public PostImageAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_images, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        try {
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                    .into(holder.imageView);
        } catch (Exception e) {
            Log.e("GlideError", "Error loading image: " + e.getMessage());
        }



    }

    @Override
    public int getItemCount() {
        if (imageUris != null) {
            return imageUris.size();
        } else{
            return 0;
        }

    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.postdetailimgView);
        }
    }
}
