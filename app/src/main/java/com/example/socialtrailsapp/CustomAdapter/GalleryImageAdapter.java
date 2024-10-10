package com.example.socialtrailsapp.CustomAdapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.UserPostDetailActivity;

import java.util.List;

public class GalleryImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> imageUrls;
    private List<String> postIds;
    public GalleryImageAdapter(Context context, List<String> imageUrls,List<String> postIds) {
        mContext = context;
        this.imageUrls = imageUrls;
        this.postIds = postIds;
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
            imageView.setLayoutParams(new GridView.LayoutParams(350, 380));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundResource(R.drawable.border_image);
        } else {
            imageView = (ImageView) convertView;
        }


        Glide.with(mContext)
                .load(imageUrls.get(position))
                .into(imageView);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UserPostDetailActivity.class);
            intent.putExtra("postdetailId", postIds.get(position));
            mContext.startActivity(intent);
        });

        return imageView;
    }
}
