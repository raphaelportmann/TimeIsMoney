package ch.hslu.mobpro.timeismoney.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import ch.hslu.mobpro.timeismoney.MainActivity.Companion.EXTRA_TASK

class TimeService : Service() {
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var startTime: Long = 0
    private var task: String = ""
    private val NOTIFICATION_ID = 1365
    private val ACTION_STOP_SERVICE = "stop"
    private val channelId = "timer_channel"

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopService()
        } else {
            handler = Handler(Looper.getMainLooper())
            startTime = System.currentTimeMillis()
            runnable = Runnable { updateElapsedTime() }
            handler.postDelayed(runnable, 1000) // Update every second
            task = intent?.getStringExtra(EXTRA_TASK) ?: ""
            // Create and display the foreground notification
            startForeground(NOTIFICATION_ID, createNotification())
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Stop")
        handler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun updateElapsedTime() {
        val elapsedTime = System.currentTimeMillis() - startTime
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24

        val elapsedTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        // Update the notification content with the elapsed time
        val notification = createNotification(elapsedTimeString)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        handler.postDelayed(runnable, 1000) // Schedule the next update after 1 second
    }

    private fun createNotification(contentText: String = "Timer is running"): Notification {
        val stopIntent = Intent(this, TimeService::class.java)
        stopIntent.action = ACTION_STOP_SERVICE
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channel = NotificationChannel(
            channelId,
            "Timer Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        channel.enableLights(false)
        channel.enableVibration(false)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Task \"$task\" ist aktiv")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setColor(Color.BLUE)
            .addAction(
                android.R.drawable.ic_media_rew,
                "Aufzeichnung stoppen",
                stopPendingIntent
            )
            .build()
    }

    private fun stopService() {
        handler.removeCallbacks(runnable)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        val intent = Intent(ACTION_SERVICE_STOPPED)
        sendBroadcast(intent)
    }

    companion object {
        const val ACTION_SERVICE_STOPPED = "ch.hslu.mobpro.timeismoney.SERVICE_STOPPED"
    }
}
