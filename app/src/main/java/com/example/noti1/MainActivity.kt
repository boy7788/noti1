package com.example.noti1
import android.content.Intent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.app.ActivityCompat
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.json.JSONObject
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var textViewMessage: TextView
    private lateinit var editText: EditText
    private lateinit var buttonSend: Button
    private lateinit var buttonSend2: Button


    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewMessage = findViewById(R.id.textViewMessage)
    //  val webSocketManager = WebSocketManager(this)
//webSocketManager.startWebSocket()
       // player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
       // player.setLooping(true)
       // MyStore.startStore(this)
        buttonSend=findViewById(R.id.buttonSend)
        buttonSend2=findViewById(R.id.buttonSend2)
        editText = findViewById(R.id.editText)
        Log.d("mainActive", "start notify")
        MyStore.setKey("connect","0")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (this .checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu cấp quyền từ người dùng
                ActivityCompat.requestPermissions(
                    this  as Activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1
                )
            }
        }
//        val intent = Intent(this, WebSocketService::class.java)
//        startService(intent)
        val intent = Intent(this, WebSocketService::class.java)
        ContextCompat.startForegroundService(this, intent)

     //   val intent = Intent(this, WebSocketService::class.java)
       // bindService(intent, connection, Context.BIND_AUTO_CREATE)



        if (MyStore.getKey( "id")!=""){
            buttonSend.text=getString(R.string.unreg_alert)
         }else{
            buttonSend.text=getString(R.string.reg_alert)
         }
       // GlobalScope.launch(Dispatchers.Main) {
         //  delay(2000) // Chờ 2 giây
          //  WebSocketManager.startPlayer()
      //  }
        editText.setText(MyStore.getKey( "tempId"))
        buttonSend.setOnClickListener {

            // val enteredText = editText.text.toString()
            val cid = MyStore.getKey(  "id")
            if (cid != "") {
                buttonSend.text = getString(R.string.reg_alert)

               val intent3 = Intent("ACTION_UNREG")
//
               sendBroadcast(intent3)

            } else {
                buttonSend.text = getString(R.string.unreg_alert)

                val intent4 = Intent("ACTION_REG")
                intent4.putExtra("ID_CLIENT", editText.text.toString().trim())
                sendBroadcast(intent4)
            }
        }


        buttonSend2.setOnClickListener {

          //  val intent7 = Intent("ACTION_RECEIVED")
          //  intent7.putExtra("COIN_ID",  textViewMessage.text.toString())
          //  sendBroadcast(intent7)
            val intent4 = Intent("ACTION_BREAK")
            sendBroadcast(intent4)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        //webSocketManager.closeWebSocket()
    }

}
