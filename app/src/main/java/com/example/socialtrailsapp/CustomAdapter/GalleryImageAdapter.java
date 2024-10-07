package com.example.socialtrailsapp.CustomAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.socialtrailsapp.R;

import java.util.List;

public class GalleryImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> imageUrls; // List of image URLs

    public GalleryImageAdapter(Context context, List<String> imageUrls) {
        mContext = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 380)); // Set image size
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundResource(R.drawable.border_image);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load image using Glide
        Glide.with(mContext)
                .load(imageUrls.get(position))
                .into(imageView);

        return imageView;
    }
}
