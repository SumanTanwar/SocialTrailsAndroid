package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.Notification;

import java.util.List;

public interface INotification {
    void sendNotificationToUser(Notification notification);
    void fetchNotifications(String userId, final DataOperationCallback<List<Notification>> callback);
}
