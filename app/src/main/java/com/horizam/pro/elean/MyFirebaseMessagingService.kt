package com.horizam.pro.elean

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
import com.horizam.pro.elean.ui.main.view.activities.SplashActivity
import com.horizam.pro.elean.utils.NotificationUtils
import com.horizam.pro.elean.utils.PrefManager
import org.greenrobot.eventbus.EventBus




const val channelId = "notificationChannel"
const val channelName = "com.taaply.nfcShare"

class MyFirebaseMessagingService : FirebaseMessagingService() {
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
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)

    }

        private fun getMessage(remoteMessage: RemoteMessage,title: String, message: String) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel =
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notificationChannel)
            }
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
                        Constants.CONTENT_ID,
                        remoteMessage.data[Constants.CONTENT_ID]
                    )
                    val intent = Intent(this, SplashActivity::class.java)
                    intent.putExtras(bundle)
                    val pendingIntent = setPendingIntent(
                        intent
                    )
                    remoteMessage.data[Constants.CONTENT_ID]?.let {
                        NotificationUtils.showNotification(
                            it,
                            applicationContext,
                            title!!,
                            "$message",
                            pendingIntent
                        )
                    }
                }
                Constants.ORDER->   {
                    EventBus.getDefault().post(BottomNotification(Constants.ORDER))
                    //get data from data notification
                    val title = remoteMessage.data["subject"]
                    val message = remoteMessage.data["body"]
                    //choose activity where you want to move when click
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.CONTENT_ID,
                        remoteMessage.data[Constants.CONTENT_ID]
                    )
                    bundle.putString(
                        Constants.TYPE,
                        remoteMessage.data[Constants.TYPE]
                    )
                    val intent = Intent(this, SplashActivity::class.java)
                    intent.putExtras(bundle)
                    val pendingIntent = setPendingIntent(
                        intent
                    )
//            show notification in status bar
                    remoteMessage.data[Constants.CONTENT_ID]?.let {
                        NotificationUtils.showNotification(
                            it,
                            applicationContext,
                            title!!,
                            "$message",
                            pendingIntent
                        )
                    }

//            Constants.ORDER -> {
//                EventBus.getDefault().post(BottomNotification(Constants.ORDER))
//                val title = remoteMessage.data["subject"]
//                val message = remoteMessage.data["body"]
//                val contentID = remoteMessage.data[Constants.CONTENT_ID]
//                val bundle = Bundle()
//                bundle.putString(
//                    Constants.TYPE,
//                    remoteMessage.data[Constants.TYPE]
//
//                )
//                bundle.putString(
//                    Constants.CONTENT_ID,
//                    remoteMessage.data[Constants.CONTENT_ID]
//                )
//                val intent = Intent(this,HomeActivity::class.java)
//                intent.putExtras(bundle)
//                val pendingIntent = setPendingIntent(
//                    intent
//                )
//                NotificationUtils.showNotification(
//                    contentID.toString(),
//                    applicationContext,
//                    title!!,
//                    "$message",
//                    pendingIntent= PendingIntent.getActivity(
//                        this, 0, intent,
//                        PendingIntent.FLAG_MUTABLE)
//                )
//                NotificationUtils.showNotification(
//                    remoteMessage.data[Constants.CONTENT_ID].toString(),
//                    applicationContext,
//                    remoteMessage.data[Constants.SENDER_NAME].toString(),
//                    remoteMessage.data[Constants.ORDER].toString(),
//                    pendingIntent= PendingIntent.getActivity(
//                        this, 0, intent,
//                        PendingIntent.FLAG_MUTABLE)
//                )

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
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this, SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
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
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this, SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
            }
            Constants.EXTEND_REQUEST -> {
                EventBus.getDefault().post(BottomNotification(Constants.EXTEND_REQUEST))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.EXTEND_REQUEST ,
                    remoteMessage.data[Constants.EXTEND_REQUEST ]
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this, SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
            }
            Constants.REJECTED_TIME -> {
                EventBus.getDefault().post(BottomNotification(Constants.REJECTED_TIME))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.REJECTED_TIME ,
                    remoteMessage.data[Constants.REJECTED_TIME ]
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this,SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
            }
            Constants.ACCEPTED_TIME -> {
                EventBus.getDefault().post(BottomNotification(Constants.ACCEPTED_TIME))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.ACCEPTED_TIME ,
                    remoteMessage.data[Constants.ACCEPTED_TIME ]
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this, SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
            }
            Constants.DELIVER -> {
                EventBus.getDefault().post(BottomNotification(Constants.DELIVER ))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.DELIVER  ,
                    remoteMessage.data[Constants.DELIVER]
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this, SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
            }
            Constants.COMPLETE -> {
                EventBus.getDefault().post(BottomNotification(Constants.COMPLETE))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.COMPLETE ,
                    remoteMessage.data[Constants.COMPLETE ]
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this, SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
            }
            Constants.REVISION -> {
                EventBus.getDefault().post(BottomNotification(Constants.REVISION))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.REVISION ,
                    remoteMessage.data[Constants.REVISION]
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this, SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
            }
            Constants.REVIEWED -> {
                EventBus.getDefault().post(BottomNotification(Constants.REVIEWED ))
                val bundle = Bundle()
                bundle.putString(
                    Constants.TYPE,
                    remoteMessage.data[Constants.TYPE]

                )
                bundle.putString(
                    Constants.REVIEWED ,
                    remoteMessage.data[Constants.REVIEWED]
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    remoteMessage.data[Constants.CONTENT_ID]
                )
                val intent = Intent(this,SplashActivity::class.java)
                intent.putExtras(bundle)
                val pendingIntent = setPendingIntent(
                    intent
                )
                remoteMessage.data[Constants.CONTENT_ID]?.let {
                    NotificationUtils.showNotification(
                        it,
                        applicationContext,
                        title!!,
                        "$message",
                        pendingIntent
                    )
                }
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
                    pendingIntent= PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_MUTABLE)
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