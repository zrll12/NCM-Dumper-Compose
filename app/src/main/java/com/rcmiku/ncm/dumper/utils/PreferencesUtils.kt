package com.rcmiku.ncm.dumper.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesUtils {
    private var context = AppContextUtil.context
    private val sharedPreferences: SharedPreferences? =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun perfSet(key: String, value: String) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    fun perfGet(key: String): String? {
        return sharedPreferences?.getString(key, null)
    }

    fun perfSet(key: String, value: Boolean) {
        sharedPreferences?.edit()?.putBoolean(key, value)?.apply()
    }

    fun perfGetBoolean(key: String): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }

    fun perfRemove(key: String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
    }
}