package com.lock.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class ScreenOffReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when{
            intent?.action == Intent.ACTION_SCREEN_OFF ->{
                Log.d("ScreenOffReceiver","퀴즈잠금: 화면이 꺼졌습니다.")
                Log.d("ScreenOffReceiver","${context}")
                val quizIntent = Intent(context, QuizLockerActivity::class.java)
//                val intent = Intent(context, QuizLockerActivity::class.java)
                quizIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                quizIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context?.startActivity(quizIntent)


//                Toast.makeText(context,"퀴즈 잠금: 화면이 꺼졌습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}