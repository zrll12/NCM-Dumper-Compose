package com.rcmiku.ncm.dumper.utils

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextUtil {
    lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}