package com.horizam.pro.elean

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.horizam.pro.elean.data.model.BottomNotification
import com.horizam.pro.elean.ui.main.view.activities.HomeActivity
import com.horizam.pro.elean.ui.main.view.activities.OrderDetailsActivity
import com.horizam.pro.elean.utils.NotificationUtils
import com.horizam.pro.elean.utils.PrefManager
import org.greenrobot.eventbus.EventBus

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            scheduleJob(remoteMessage)

        }
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)

    }
    private fun scheduleJob(remoteMessage: RemoteMessage) {
        when (remoteMessage.data[Constants.TYPE]) {
            Constants.MESSAGE -> {
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
                val intent = Intent(this, OrderDetailsActivity::class.java)
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
            "Cancel_And_Create_Dispute" -> {
                EventBus.getDefault().post(BottomNotification(Constants.TYPE_ORDER))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.TYPE_ORDER,
                    remoteMessage.data[Constants.TYPE_ORDER]
                )
                bundle.putString(
                    Constants.SENDER_ID,
                    remoteMessage.data[Constants.SENDER_ID]
                )
                val intent = Intent(this, OrderDetailsActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                NotificationUtils.showNotification(
                    remoteMessage.data[Constants.SENDER_ID].toString(),
                    applicationContext,
                    remoteMessage.data[Constants.SENDER_NAME].toString(),
                    remoteMessage.data[Constants.TYPE_ORDER].toString(),
                    pendingIntent
                )
            }
            Constants.ORDER -> {
                EventBus.getDefault().post(BottomNotification(Constants.ORDER))
                val title = remoteMessage.data["subject"]
                val message = remoteMessage.data["body"]
                val contentID = remoteMessage.data[Constants.CONTENT_ID]
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.ORDER,
                    remoteMessage.data[Constants.ORDER]
                )
                bundle.putString(
                    Constants.SENDER_ID,
                    remoteMessage.data[Constants.SENDER_ID]
                )
                val intent = Intent(this, OrderDetailsActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                NotificationUtils.showNotification(
                    contentID.toString(),
                    applicationContext,
                    title!!,
                    "$message",
                    pendingIntent
                )
                NotificationUtils.showNotification(
                    remoteMessage.data[Constants.SENDER_ID].toString(),
                    applicationContext,
                    remoteMessage.data[Constants.SENDER_NAME].toString(),
                    remoteMessage.data[Constants.ORDER].toString(),
                    pendingIntent
                )

            }
            "Order_Dispute_Rejected" -> {
                val title = remoteMessage.data["subject"]
                val message = remoteMessage.data["body"]
                val contentID = remoteMessage.data[Constants.CONTENT_ID]
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
                NotificationUtils.showNotification(
                    contentID.toString(),
                    applicationContext,
                    title!!,
                    "$message",
                    pendingIntent
                )
            }
            Constants.OFFER -> {
                val title = remoteMessage.data["subject"]
                val message = remoteMessage.data["body"]
                val contentID = remoteMessage.data[Constants.OFFER]
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
                NotificationUtils.showNotification(
                    contentID.toString(),
                    applicationContext,
                    title!!,
                    "$message",
                    pendingIntent
                )
            }
        }
}
    private fun setPendingIntent(
        intent: Intent,
    ): PendingIntent? {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        Constants.GENERAL_NOTIFICATION_ID++
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(
                    this,
                    Constants.GENERAL_NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        return pendingIntent
    }
    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
        val tokenBuilder = OneTimeWorkRequestBuilder<MyWorker>()
        val data = workDataOf(Constants.KEY_TOKEN to token)
        tokenBuilder.setInputData(data)
        val prefManager = PrefManager(this)
        if (token != null) {
            prefManager.fcmToken = token
            Log.e("FCM",prefManager.fcmToken)
        }

    }
    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}