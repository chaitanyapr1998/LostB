package com.example.chaitanya.lostb;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseNotificationMessaging extends FirebaseMessagingService {

    FirebaseUser mUser;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        String myId = remoteMessage.getData().get("sent"); //my id
        String user = remoteMessage.getData().get("user"); //i received msg from user id
        String toEmail = remoteMessage.getData().get("toEmail"); //i received msg from email

        //String cUser = "nouser";

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null && myId.equals(mUser.getUid())){
            if (!mUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user"); //i received msg from user id
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("msg");
        String toEmail = remoteMessage.getData().get("toEmail"); //i received msg from email

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        bundle.putString("toEmail", toEmail);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                notificationSound);


        oreoNotification.getManager().notify(3, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String toEmail = remoteMessage.getData().get("toEmail");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        bundle.putString("toEmail", toEmail);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        noti.notify(3, builder.build());
    }
}
