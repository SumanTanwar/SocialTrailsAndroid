package com.example.socialtrailsapp.ModelData;

public class LikeResult {
    private int count;
    private boolean isLike;

    public LikeResult(int count, boolean isLike) {
        this.count = count;
        this.isLike = isLike;
    }

    public int getCount() {
        return count;
    }

    public boolean isLike() {
        return isLike;
    }
}
