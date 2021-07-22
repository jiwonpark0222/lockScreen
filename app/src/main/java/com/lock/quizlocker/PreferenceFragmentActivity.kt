package com.lock.quizlocker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceFragment

class PreferenceFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pref_fragment)
//        supportFragmentManager.beginTransaction().replace(android.R.id.content, MySettingFragment()).commit()
    }
}