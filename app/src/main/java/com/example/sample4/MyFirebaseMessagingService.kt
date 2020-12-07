package com.example.sample4

import android.media.MediaPlayer
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseMesagingService"
    var token = ""

    override fun onNewToken(s: String): Unit {
        super.onNewToken(s)
        Log.d("NEW_TOKEN", s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Notification from phone: ${remoteMessage.from}")
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.toString());
        }
    }
   /* fun broadcastIntent() {
        val intent = Intent()
        intent.setAction("com.myApp.CUSTOM_EVENT")
        // We should use LocalBroadcastManager when we want INTRA app
        // communication
        val YOUR_CONTEXT = "ABCDE"
        LocalBroadcastManager.getInstance(YOUR_CONTEXT).sendBroadcast(intent)
    }*/


}