package com.lock.quizlocker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.MultiSelectListPreference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.util.Log

class MySettingFragment: PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref)
//        setPreferencesFromResource(R.xml.pref, rootKey)

        val categoryPref = findPreference("category") as MultiSelectListPreference
        categoryPref.summary = categoryPref.values.joinToString(", ")

        categoryPref.setOnPreferenceChangeListener { preference, newValue ->
            val newValueSet = newValue as? HashSet<*>?:return@setOnPreferenceChangeListener true

            categoryPref.summary = newValue.joinToString(", ")
            true
        }


        // 퀴즈 잠금화면 사용 스위치 객체 가져옴
        val useLockScreenPref = findPreference("useLockScreen") as SwitchPreference
        val intent = Intent(activity, LockScreenService::class.java)
        useLockScreenPref.setOnPreferenceClickListener {
            when {
                useLockScreenPref.isChecked -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        Log.d("startForegroundService","이리옴니다.")
                        activity.startForegroundService(intent)
                    }else{
                        Log.d("startService","이리옴니다.")
                        activity.startService(intent)
                    }

                }
                else -> activity.stopService(intent)
            }
            true
        }

        if (useLockScreenPref.isChecked){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Log.d("startForegroundtrue","이리옴니다.")
                activity.startForegroundService(intent)
            }else{
                activity.startService(intent)
                Log.d("startServicetrue","이리옴니다.")
            }
        }



    }
}