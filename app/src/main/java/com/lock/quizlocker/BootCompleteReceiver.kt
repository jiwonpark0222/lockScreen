package com.lock.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

class BootCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when{
            intent?.action == Intent.ACTION_BOOT_COMPLETED ->{
                Log.d("quizLocker","부팅이 완료됨")
                val intent = Intent(context, LockScreenService::class.java)
                context?.let {
                    // 퀴즈잠금화면 설정값이 ON 인지 확인
                    val pref = PreferenceManager.getDefaultSharedPreferences(context)
                    val useLockScreen = pref.getBoolean("useLockScreen", false)
                    if (useLockScreen){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            it.startForegroundService(intent)
                        }else{
                            it.startService(intent)
                        }
                    }

                }


            }
        }


    }
}