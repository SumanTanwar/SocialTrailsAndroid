package com.example.socialtrailsapp.ModelData;

public class PostImages {

    private String imageId;
    private String postId;
    private String imagePath;
    private int order;
    public PostImages() {}

    public PostImages(String postId, String imagePath, int order) {
        this.postId = postId;
        this.imagePath = imagePath;
        this.order = order;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
