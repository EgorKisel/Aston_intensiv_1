package com.example.aston_intensiv_1

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "MusicPlayerNotificationChannel"
    private lateinit var mediaPlayer: MediaPlayer
    private var currentTrackIndex: Int = 0
    private val musicList = listOf("kassetcopy.mp3", "gorillaz.mp3", "zatochka.mp3")
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            nextTrack()
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val playIntent = Intent(this, MusicService::class.java).apply {
            action = "PLAY"
        }

        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = "PAUSE"
        }
        val pausePendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                1,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        val nextIntent = Intent(this, MusicService::class.java).apply {
            action = "NEXT"
        }
        val nextPendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                2,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        val prevIntent = Intent(this, MusicService::class.java).apply {
            action = "PREVIOUS"
        }
        val prevPendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                3,
                prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing music")
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_button_prev,
                    "Previous",
                    prevPendingIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_button_pause,
                    "Pause",
                    pausePendingIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_button_next,
                    "Next",
                    nextPendingIntent
                ).build()
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                "PLAY" -> playMusic()
                "PAUSE" -> pauseMusic()
                "NEXT" -> nextTrack()
                "PREVIOUS" -> previousTrack()
            }
        }
        return START_NOT_STICKY
    }

    private fun updateNotification(title: String, text: String) {
        notificationBuilder.setContentTitle(title)
        notificationBuilder.setContentText(text)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            updateNotification("Music Player", "Playing music")
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            updateNotification("Music Player", "Paused music")
        }
    }

    private fun nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % musicList.size
        val resourceId =
            resources.getIdentifier(musicList[currentTrackIndex].split(".")[0], "raw", packageName)
        val uri = Uri.parse("android.resource://$packageName/$resourceId")
        mediaPlayer.apply {
            reset()
            setDataSource(applicationContext, uri)
            prepareAsync()
            setOnPreparedListener {
                start()
            }
        }
        updateNotification("Music Player", "Playing music")
    }

    private fun previousTrack() {
        currentTrackIndex = if (currentTrackIndex > 0) {
            currentTrackIndex - 1
        } else {
            musicList.size - 1
        }
        val resourceId =
            resources.getIdentifier(musicList[currentTrackIndex].split(".")[0], "raw", packageName)
        val uri = Uri.parse("android.resource://$packageName/$resourceId")
        mediaPlayer.apply {
            reset()
            setDataSource(applicationContext, uri)
            prepareAsync()
            setOnPreparedListener {
                start()
            }
        }
        updateNotification("Music Player", "Playing music")
    }
}