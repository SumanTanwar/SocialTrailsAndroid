package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.R;

import java.util.ArrayList;


public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {
    private ArrayList<Uri> imageUris;
    private Context context;
    private OnImageRemovedListener listener;

    public interface OnImageRemovedListener {
        void onImageRemoved();
    }
    public ImagePagerAdapter(ArrayList<Uri> imageUris, Context context, OnImageRemovedListener listener) {
        this.imageUris = imageUris;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        holder.imageView.setImageURI(imageUri);

        holder.closeButton.setOnClickListener(v -> {
            removeImage(position);
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void removeImage(int position) {
        if (position >= 0 && position < imageUris.size()) {
            imageUris.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, imageUris.size());

            if (imageUris.isEmpty() && listener != null) {
                listener.onImageRemoved();
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton closeButton;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            closeButton = itemView.findViewById(R.id.closeButton);
        }
    }
}
