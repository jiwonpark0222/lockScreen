package com.lock.quizlocker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.lock.quizlocker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val fragment = MySettingFragment()
    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        val initButton = mainBinding.initButton

        initButton.setOnClickListener { initAnswerCount() }


        setContentView(view)
        checkPermission()
        fragmentManager.beginTransaction().replace(R.id.preferenceContent, fragment).commit()

    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(!Settings.canDrawOverlays(this)){
                val uri = Uri.fromParts("package", packageName, null)
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                startActivityForResult(intent, 0)
            }else{
                val intent = Intent(applicationContext, LockScreenService::class.java)
                startForegroundService(intent)
            }
        }
    }

    fun initAnswerCount(){
        val correctAnswerPref = getSharedPreferences("correctAnswer", Context.MODE_PRIVATE)
        val wrongAnswerPref = getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE)

        correctAnswerPref.edit().clear().apply()
        wrongAnswerPref.edit().clear().apply()
    }
}