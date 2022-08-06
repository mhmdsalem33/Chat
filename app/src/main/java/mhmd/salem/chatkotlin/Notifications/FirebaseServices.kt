package mhmd.salem.chatkotlin.Notifications


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mhmd.salem.chatkotlin.Activites.UsersActivity
import mhmd.salem.chatkotlin.R
import kotlin.random.Random


private const val CHANNEL_ID = "my_channel"
class FirebaseServices :FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        val intent = Intent(this , UsersActivity::class.java)
        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationID      =  Random.nextInt()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createNotificationChannel(notificationManager)
        }


        val pendingIntent = PendingIntent.getActivity(this , 0 , intent , FLAG_ONE_SHOT)

        val notification  = NotificationCompat.Builder(this , CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID , notification)



    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager)
    {
        val channelName = "ChannelName"
        val channel     = NotificationChannel(CHANNEL_ID , channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN

        }
        notificationManager.createNotificationChannel(channel)
    }

}