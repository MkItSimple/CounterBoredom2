package com.mkitsimple.counterboredom2.ui

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mkitsimple.counterboredom.ui.NotificationHelper
import com.mkitsimple.counterboredom2.ui.main.MainActivity

// This will recieve the incoming messages of notifications
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var helper: NotificationHelper? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //Log.d(TAG," onMessageReceived ")
        // if remote message not null
        if(remoteMessage.notification != null){
            //Log.d(TAG," Notification : " + remoteMessage.notification!!.body.toString())
            val title: String = remoteMessage.notification?.title!!
            val body: String = remoteMessage.notification?.body!!
            // helper will build and display the notification recieved by this MyFirebaseMessagingService
            helper?.displayNotification(applicationContext, title, body, MainActivity.currentUser)
        }
    }
}