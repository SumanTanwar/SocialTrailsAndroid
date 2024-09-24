package com.example.socialtrailsapp.Interface;

public interface DataOperationCallback<T> {
    void onSuccess(T data);
    void onFailure(String error);
}
