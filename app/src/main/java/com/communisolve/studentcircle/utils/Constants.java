package com.communisolve.studentcircle.utils;

import com.communisolve.studentcircle.Model.UserModel;

import java.util.ArrayList;

public class Constants {
    public static final String USER_REF = "users";
    public static final String POSTS_REF = "posts";
    public static final String TOKENS_REF = "tokens";
    public static final String FOLLOWER_REF = "followers";
    public static final String FOLLOWING_REF = "following";
    public static final String LIKES_REF = "likes";
    public static final String COMMENTS_REF = "comments";


    public static UserModel currentUser;
    public static String currentSelectedUserUID;

//
//    fun showNotification(
//            context: Context, id: Int, title: String?, body: String?,
//            intent: Intent?
//    ) {
//        val notificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel(notificationManager)
//        }
//        val pendingIntent = PendingIntent.getActivity(
//                context,
//                0,
//                intent,
//                PendingIntent.FLAG_ONE_SHOT
//        )
//
//        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setSmallIcon(R.drawable.ic_point_logo)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .build()
//
//        notificationManager.notify(id, notification)
//    }
//
//    @SuppressLint("NewApi")
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channelName = "point_marketplace_app"
//        val channel = NotificationChannel(
//                CHANNEL_ID,
//                channelName,
//                NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "Point Market Place App"
//            enableLights(true)
//            lightColor = Color.GREEN
//        }
//        notificationManager.createNotificationChannel(channel)
//
//    }

}
