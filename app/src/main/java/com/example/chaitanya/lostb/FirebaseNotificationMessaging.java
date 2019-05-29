package com.example.chaitanya.lostb;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.chaitanya.lostb.FirebaseApplication.CHANNEL_4_ID;

//Creating notification for the chat
public class FirebaseNotificationMessaging extends FirebaseMessagingService {

    FirebaseUser mUser;
    private NotificationManagerCompat notificationManagerCompat;
    Context c;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        c = getApplicationContext();
        notificationManagerCompat = NotificationManagerCompat.from(c);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        String myId = remoteMessage.getData().get("sent"); //my id
        String user = remoteMessage.getData().get("user"); //i received msg from user id
        String toEmail = remoteMessage.getData().get("toEmail"); //i received msg from email


        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null && myId.equals(mUser.getUid())){
            if (!mUser.equals(user)) {
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("msg");

                Intent in = new Intent(c, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid", user);
                bundle.putString("toEmail", toEmail);
                in.putExtras(bundle);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(c, 4, in, PendingIntent.FLAG_ONE_SHOT);
                String id = CHANNEL_4_ID;
                Notification n = new NotificationCompat.Builder(c, id)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();
                notificationManagerCompat.notify(4, n);
            }
        }
    }
}
