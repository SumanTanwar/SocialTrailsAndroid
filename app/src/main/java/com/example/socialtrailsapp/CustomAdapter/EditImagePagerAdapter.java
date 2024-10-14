package com.example.socialtrailsapp.CustomAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialtrailsapp.R;

import java.util.ArrayList;

public class EditImagePagerAdapter extends RecyclerView.Adapter<EditImagePagerAdapter.ViewHolder> {
    private ArrayList<Uri> imageUris;
    private Context context;
    private OnImageRemovedListener listener;


    public interface OnImageRemovedListener {
        void onImageRemoved(String imageUrl);
    }
    public EditImagePagerAdapter(ArrayList<Uri> imageUris, Context context, OnImageRemovedListener listener) {
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

        Glide.with(context)
                    .load(imageUri)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                    .into(holder.imageView);

        holder.closeButton.setOnClickListener(v -> {
                removeEditImage(position);
        });

        if (imageUris.size() == 1) {
            holder.closeButton.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void removeEditImage(int position) {
        if (position >= 0 && position < imageUris.size()) {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        listener.onImageRemoved(imageUris.get(position).toString());
                        imageUris.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, imageUris.size());
                        notifyDataSetChanged();

                    })
                    .setNegativeButton("No", null)
                    .show();
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