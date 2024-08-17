package com.example.noti1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.noti1.WebSocketManager.decrypt
import com.example.noti1.WebSocketManager.getKey
import com.example.noti1.WebSocketManager.handleMessage
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.Bundle

import kotlinx.coroutines.*
class WebSocketService : Service() {
    private lateinit var player: MediaPlayer

    private var webSocket: WebSocket? = null

    private   var connectBreak: Int=0
    private   var cbreak: Int=0
    private val jobRing = mutableListOf<Job>()
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private fun isWebSocketConnected(): Boolean {
        return webSocket != null && (webSocket as WebSocket).send("ping")
    }
    override fun onCreate() {
        super.onCreate()
         player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)

        player.isLooping = true


        connect(this)
//        serviceScope.launch {
//            while (true) {
//
//                if (webSocket == null || !isWebSocketConnected()) {
//                    connect(this@WebSocketService)
//                }
//                delay(13000)
//            }
//        }
        val filter = IntentFilter()
        filter.addAction("ACTION_BREAK")
        filter.addAction("ACTION_PLAY")
        filter.addAction("ACTION_PAUSE")
        filter.addAction("ACTION_STOP")
        filter.addAction("ACTION_UNREG")
        filter.addAction("ACTION_REG")
        filter.addAction("ACTION_RECEIVED")
      //  registerReceiver(serviceReceiver, filter)
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(serviceReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(serviceReceiver, filter)
        }
        //  WebSocketManager.connect(this)

       // MyStore.setPlayer(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       //chạy mỗi khi tắt app (active), chuyển màn hình về app
       // player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)

        //  player = MediaPlayer.create(this, R.raw.your_music_file)
     //   player.isLooping = true

        return START_STICKY
    }
    fun connect(context: Context) {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("ws://198.13.54.73:8765")
            .build()

        webSocket =client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {

                val idc=MyStore.getKey("id")
                // val idc=""
                if (idc !="") {
                    val jsend = JSONObject().apply {
                        put("clientId",idc)
                        put("alert", 1)
                    }
                    val messageToSend = jsend.toString()
                    val esend = WebSocketManager.encrypt(messageToSend, WebSocketManager.getKey())
                    sendMessage(esend)
                }
                // Hoặc hiển thị một thông báo (toast)
                //    Toast.makeText(context, "WebSocket kết nối thành công", Toast.LENGTH_SHORT).show()
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {


            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Xử lý dữ liệu nhận được

                val st= WebSocketManager.decrypt(text, WebSocketManager.getKey())

                var jdata: JSONObject? = null
                try {
                    jdata = JSONObject(st)
                } catch (e: JSONException) {
                    jdata = JSONObject()
                }

                if (jdata!!.length() > 0) {
                    if (jdata.has("alert")) {
                        if (jdata.getInt("alert") == 1) {
                            if (MyStore.getKey("id")!=""){
                              //  val intent2 = Intent("ACTION_PLAY")
                                 //sendBroadcast(intent2)

                               // pause()

                               // play()

                                received(jdata.getString("coinId"))

                            }


                        }
                    }
                }


            }
        })


    }
    fun handleMessage(message: String) {
        // Đẩy thông báo khi nhận được tin nhắn từ server
        NotificationHelper.showNotification(message)
    }
    private val serviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_PLAY" -> {


                    play( )
                }
                "ACTION_BREAK" -> {
                  cbreak=1
                }
                "ACTION_PAUSE" -> pause()
                "ACTION_STOP" -> stopMusic()
              "ACTION_RECEIVED" -> {
                    val cn = intent.getStringExtra("COIN_ID")?:"NO"
                    received(cn)
                }
            "ACTION_REG" -> {
                    val c = intent.getStringExtra("ID_CLIENT")?:"NO"
                    regAlert(c)
                }

                "ACTION_UNREG" -> unRegAlert()
            }
        }
    }
    fun unRegAlert(){
        cbreak=1
        pause()
        jobRing.forEach { it.cancel() }
        jobRing.clear()
        val idc=MyStore.getKey("id")
        // val idc=""
        if (idc !="") {
            val jsend = JSONObject().apply {
                put("clientId",idc)
                put("alert", 0)
            }
            val messageToSend = jsend.toString()
            val esend = WebSocketManager.encrypt(messageToSend, WebSocketManager.getKey())
            sendMessage(esend)

            MyStore.setKey("id", "")
        }
    }
    fun regAlert(c:String){
        val jsend = JSONObject().apply {
            put("clientId",c)
            put("alert", 1)
        }
        val messageToSend = jsend.toString()
        val esend = WebSocketManager.encrypt(messageToSend, WebSocketManager.getKey())
        sendMessage(esend)

        MyStore.setKey("id", c)
        MyStore.setKey("tempId", c)
    }
     fun received(c:String){
      //   cbreak=1
//         pause()
//         jobRing.forEach { it.cancel() }
//         jobRing.clear()
         val idc=MyStore.getKey("id")

         if (idc !="") {
             val jsend = JSONObject().apply {
                 put("clientId", idc)
                 put("alert", 2)
                 put("coinId", c)
             }
             val messageToSend = jsend.toString()
             val esend = WebSocketManager.encrypt(messageToSend, WebSocketManager.getKey())
             sendMessage(esend)
         }
    }


    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    override fun onDestroy() {
        player.stop()
        player.release()
       // WebSocketManager.closeWebSocket()
        super.onDestroy()
        unregisterReceiver(serviceReceiver)

        // Đóng kết nối WebSocket nếu cần
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
       // return binder
    }
    fun play() {
        if (!player.isPlaying) {
            player.start()
        }
    }

    fun pause() {
        if (player.isPlaying) {
            player.pause()
        }
    }

    fun stopMusic() {
        if (player.isPlaying) {
            player.stop()
            player.prepare() // Prepare player for future playback
        }
    }

}
