package com.example.noti1

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.provider.Settings
import java.lang.ref.WeakReference

object MyStore {
    lateinit var      player : MediaPlayer
    lateinit var      sharedPreferences : SharedPreferences
fun startStore(context: Context){
      sharedPreferences =context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

}
    fun setPlayer(context: Context){
        player = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI)
        player.setLooping(true)
    }
    fun startPlayer(){
        player.start()
    }
    fun pausePlayer(){
        player.pause()
    }


    fun setKey( x:String, v:String){

// Lưu trữ dữ liệu
        with(sharedPreferences.edit()) {
            putString(x,v )
            apply()  // Hoặc commit() để lưu đồng bộ
        }
    }
    fun getKey(x: String): String {

        return sharedPreferences.getString(x,"")?:""
    }
}