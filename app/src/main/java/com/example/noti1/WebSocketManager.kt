package com.example.noti1
import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.TextView
import androidx.core.app.NotificationCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import android.media.MediaPlayer
import kotlin.coroutines.coroutineContext
import android.app.Service

import android.os.IBinder
import android.provider.Settings
import org.json.JSONObject
import org.json.JSONException
import android.Manifest
import android.app.Activity

import android.content.pm.PackageManager

import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.lang.ref.WeakReference
import android.os.Handler
import android.os.Looper
import okhttp3.Response

object WebSocketManager {




    fun getKey():String{
        return "*:Q345\$^89abc84fP(K345@BC9a+9Kpf"
    }


      fun handleMessage(message: String) {
        // Đẩy thông báo khi nhận được tin nhắn từ server
        NotificationHelper.showNotification(message)
    }
    fun adjustKey(key: String, length: Int): String {
        return if (key.length >= length) {
            key.substring(0, length)
        } else {
            (key.repeat(length / key.length + 1)).substring(0, length)
        }
    }

    fun encrypt(text: String, key: String): String {
        val adjustedKey = adjustKey(key, text.length)
        val encryptedText = StringBuilder()

        for (i in text.indices) {
            val shift = adjustedKey[i].code
            val encryptedChar = ((text[i].code + shift) % 65536).toChar()  // 65536 là số ký tự trong bảng mã Unicode
            encryptedText.append(encryptedChar)
        }

        return encryptedText.toString()
    }

    fun decrypt(encryptedText: String, key: String): String {
        val adjustedKey = adjustKey(key, encryptedText.length)
        val decryptedText = StringBuilder()

        for (i in encryptedText.indices) {
            val shift = adjustedKey[i].code
            val decryptedChar = ((encryptedText[i].code - shift + 65536) % 65536).toChar()  // 65536 là số ký tự trong bảng mã Unicode
            decryptedText.append(decryptedChar)
        }

        return decryptedText.toString()
    }

}