package com.example.ventasrovianda.Utils;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationServiceFirebase extends FirebaseMessagingService {

    public NotificationServiceFirebase() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(@NonNull  String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(@NonNull  String s, @NonNull  Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(@NonNull  String s) {
        super.onNewToken(s);
    }

    @NonNull
    @Override
    protected Intent getStartCommandIntent(@NonNull  Intent intent) {
        return super.getStartCommandIntent(intent);
    }

    @Override
    public void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
    }
}
