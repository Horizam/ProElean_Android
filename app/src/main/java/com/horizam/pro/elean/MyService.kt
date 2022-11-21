package com.horizam.pro.elean



import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.horizam.pro.elean.Constants.Companion.channelId
import com.horizam.pro.elean.Constants.Companion.channelName
import com.horizam.pro.elean.data.model.BottomNotification
import com.horizam.pro.elean.ui.main.view.activities.HomeActivity
import com.horizam.pro.elean.ui.main.view.activities.OrderDetailsActivity
import com.horizam.pro.elean.utils.NotificationUtils
import org.greenrobot.eventbus.EventBus

//
//const val channelId = "notificationChannel"
//const val channelName = "com.taaply.nfcShare"

class MyService : FirebaseMessagingService() {

    private lateinit var broadcastManager : LocalBroadcastManager

    override fun onCreate() {
        broadcastManager = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data != null) {
            Log.e("notify_type", message.data.toString())
            try {
                getMessage(message,message.notification!!.title!!, message.notification!!.body!!)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("NewToken", token.toString())
    }

    private fun getMessage(remoteMessage: RemoteMessage,title: String, message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        var broadCastIntent = Intent()
        var intent = Intent(applicationContext, HomeActivity::class.java)
        Log.e("Notification  : ", remoteMessage.data.toString())


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
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(intent)

                NotificationUtils.showNotification(
                    remoteMessage.data[Constants.SENDER_ID].toString(),
                    applicationContext,
                    remoteMessage.data[Constants.SENDER_NAME].toString(),
                    remoteMessage.data[Constants.MESSAGE].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.CREATE_DISPUTE -> {
                EventBus.getDefault().post(BottomNotification(Constants.CREATE_DISPUTE))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.CREATE_DISPUTE,
                    remoteMessage.data[Constants.CREATE_DISPUTE]
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
                    remoteMessage.data[Constants.CREATE_DISPUTE].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
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
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
//                NotificationUtils.showNotification(
//                    contentID.toString(),
//                    applicationContext,
//                    title!!,
//                    "$message",
//                    pendingIntent
//                )
                NotificationUtils.showNotification(
                    remoteMessage.data[Constants.SENDER_ID].toString(),
                    applicationContext,
                    remoteMessage.data[Constants.SENDER_NAME].toString(),
                    remoteMessage.data[Constants.ORDER].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )

            }
            Constants.REJECT_DISPUTE -> {
                EventBus.getDefault().post(BottomNotification(Constants.REJECT_DISPUTE))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.REJECT_DISPUTE,
                    remoteMessage.data[Constants.REJECT_DISPUTE]
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
                    remoteMessage.data[Constants.REJECT_DISPUTE].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.ACCEPT_DISPUTE -> {
                EventBus.getDefault().post(BottomNotification(Constants.ACCEPT_DISPUTE))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.ACCEPT_DISPUTE,
                    remoteMessage.data[Constants.ACCEPT_DISPUTE]
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
                    remoteMessage.data[Constants.ACCEPT_DISPUTE].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.EXTEND_REQUEST -> {
                EventBus.getDefault().post(BottomNotification(Constants.EXTEND_REQUEST))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.EXTEND_REQUEST,
                    remoteMessage.data[Constants.EXTEND_REQUEST]
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
                    remoteMessage.data[Constants.EXTEND_REQUEST].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.REJECTED_TIME -> {
                EventBus.getDefault().post(BottomNotification(Constants.REJECTED_TIME))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.REJECTED_TIME,
                    remoteMessage.data[Constants.REJECTED_TIME]
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
                    remoteMessage.data[Constants.REJECTED_TIME].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.ACCEPTED_TIME -> {
                EventBus.getDefault().post(BottomNotification(Constants.ACCEPTED_TIME))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.ACCEPTED_TIME,
                    remoteMessage.data[Constants.ACCEPTED_TIME]
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
                    remoteMessage.data[Constants.ACCEPTED_TIME].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.DELIVER -> {
                EventBus.getDefault().post(BottomNotification(Constants.DELIVER))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.DELIVER,
                    remoteMessage.data[Constants.DELIVER]
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
                    remoteMessage.data[Constants.DELIVER].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.COMPLETE -> {
                EventBus.getDefault().post(BottomNotification(Constants.COMPLETE))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.COMPLETE,
                    remoteMessage.data[Constants.COMPLETE]
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
                    remoteMessage.data[Constants.COMPLETE].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.REVISION -> {
                EventBus.getDefault().post(BottomNotification(Constants.REVISION))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.REVISION,
                    remoteMessage.data[Constants.REVISION]
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
                    remoteMessage.data[Constants.REVISION].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
            Constants.REVIEWED -> {
                EventBus.getDefault().post(BottomNotification(Constants.REVIEWED))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.REVIEWED,
                    remoteMessage.data[Constants.REVIEWED]
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
                    remoteMessage.data[Constants.REVIEWED].toString(),
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
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
                    pendingIntent = PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
                )
            }
        }


//
//    if (remoteMessage.data["notifiable_type"].equals("event")) {
//            broadCastIntent = Intent("Notification").putExtra("notificationType", "other")
//            intent = Intent(
//                applicationContext,
//                HomeActivity::class.java
//            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra("eventId", remoteMessage.data["notifiable_id"])
//        } else if (remoteMessage.data["notifiable_type"].equals("reaction")) {
//            broadCastIntent = Intent("Notification").putExtra("notificationType", "other")
//            intent = Intent(
//                applicationContext,
//                HomeActivity::class.java
//            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra("postId", remoteMessage.data["notifiable_id"])
//        } else if (remoteMessage.data["notifiable_type"].equals("user")) {
//            broadCastIntent = Intent("Notification").putExtra("notificationType", "other")
//            intent = Intent(
//                applicationContext,
//                HomeActivity::class.java
//            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra("profileId", remoteMessage.data["notifiable_id"])
//        } else if (remoteMessage.data["notifiable_type"].equals("message")) {
//            broadCastIntent = Intent("Notification").putExtra("notificationType", "message")
//            intent = Intent(
//                applicationContext,
//                HomeActivity::class.java
//            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra("messageId", remoteMessage.data["notifiable_id"])
//        }


        broadcastManager.sendBroadcast(broadCastIntent)

        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_back)
            .setOnlyAlertOnce(true).setContentTitle(title).setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        notificationManager.notify(0, builder.build())

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
}