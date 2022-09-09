package com.horizam.pro.elean

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.horizam.pro.elean.data.model.BottomNotification
import com.horizam.pro.elean.ui.main.view.activities.HomeActivity
import com.horizam.pro.elean.ui.main.view.activities.OrderDetailsActivity
import com.horizam.pro.elean.ui.main.view.fragments.OrderDetailsFragment
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.NotificationUtils
import com.horizam.pro.elean.utils.PrefManager
import org.greenrobot.eventbus.EventBus

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            scheduleJob(remoteMessage)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }
//
    private fun scheduleJob(remoteMessage: RemoteMessage) {
    /*val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()*/
    val type = remoteMessage.data[Constants.TYPE]
    if (type == Constants.MESSAGE) {
        EventBus.getDefault().post(BottomNotification(Constants.MESSAGE))
        val bundle = Bundle()
        bundle.putString(
            Constants.TYPE,
            remoteMessage.data[Constants.TYPE]
        )
        bundle.putString(
            Constants.MESSAGE,
            remoteMessage.data[Constants.MESSAGE]
        )
        bundle.putString(
            Constants.SENDER_ID,
            remoteMessage.data[Constants.SENDER_ID]
        )
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtras(bundle)
        val pendingIntent = setPendingIntent(
            intent
        )
        NotificationUtils.showNotification(
            remoteMessage.data[Constants.SENDER_ID].toString(),
            applicationContext,
            remoteMessage.data[Constants.SENDER_NAME].toString(),
            remoteMessage.data[Constants.MESSAGE].toString(),
            pendingIntent
        )
    }
 else if (type == Constants.TYPE_ORDER) {
            EventBus.getDefault().post(BottomNotification(Constants.ORDER))
            //get data from data notification
            val title = remoteMessage.data["subject"]
            val message = remoteMessage.data["body"]
            val contentID = remoteMessage.data[Constants.CONTENT_ID]
            //choose activity where you want to move when click
            val bundle = Bundle()
            bundle.putString(
                Constants.CONTENT_ID,
                contentID
            )
            bundle.putString(
                Constants.TYPE,
                remoteMessage.data[Constants.TYPE]
            )

            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtras(bundle)
            val pendingIntent = setPendingIntent(
                intent
            )
//            show notification in status bar
            NotificationUtils.showNotification(
                contentID.toString(),
                applicationContext,
                title!!,
                "$message",
                pendingIntent
            )

        } else if (type == Constants.TYPE_OFFER) {
            val title = remoteMessage.data["subject"]
            val message = remoteMessage.data["body"]
            val contentID = remoteMessage.data[Constants.CONTENT_ID]
            val bundle = Bundle()
            bundle.putString(
                Constants.CONTENT_ID, contentID
            )
            bundle.putString(
                Constants.TYPE,
                remoteMessage.data[Constants.TYPE]
            )

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtras(bundle)
            val pendingIntent = setPendingIntent(
                intent
            )
            //show notification in status bar
            NotificationUtils.showNotification(
                contentID.toString(),
                applicationContext,
                title!!,
                "$message",
                pendingIntent
            )
        }
//    }
}
    private fun setPendingIntent(
        intent: Intent,
    ): PendingIntent? {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        Constants.GENERAL_NOTIFICATION_ID++;
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                Constants.GENERAL_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        return pendingIntent
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
        val tokenBuilder = OneTimeWorkRequestBuilder<MyWorker>()
        val data = workDataOf(Constants.KEY_TOKEN to token)
        tokenBuilder.setInputData(data)
        //WorkManager.getInstance(application).enqueue(tokenBuilder.build())
        val prefManager = PrefManager(this)
        if (token != null) {
            prefManager.fcmToken = token
        }
    }

    /*Create and show a simple notification containing the received FCM message*/
    /*private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build())
    }*/

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}