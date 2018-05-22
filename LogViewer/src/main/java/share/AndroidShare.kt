package com.lxz.kotlin.tools.share

import android.content.Context
import android.content.SharedPreferences
import toolkit.AppcationUtils


class AndroidShare(protected var filename: String) {
    protected var context: Context
    var settings: SharedPreferences
    init {
        this.context = AppcationUtils.getInstance()
        this.settings = context.getSharedPreferences(filename,
                Context.MODE_PRIVATE)
    }

    fun clearKey(key: String) {

        val localEditor = settings.edit()
        localEditor.putString(key, null)
        localEditor.commit()
    }

    fun put(key: String, value: String?) {

        val localEditor = settings.edit()
        localEditor.putString(key, value)

        localEditor.commit()
    }

    fun put(key: String, value: Int?) {

        val localEditor = settings.edit()
        localEditor.putInt(key, value!!)
        localEditor.commit()
    }

    fun put(key: String, value: Boolean) {
        val localEditor = settings.edit()
        localEditor.putBoolean(key, value)
        localEditor.commit()
    }

    fun put(key: String, value: Float?) {
        val localEditor = settings.edit()
        localEditor.putFloat(key, value!!)
        localEditor.commit()
    }


    fun put(key: String, value: Long?) {
        val localEditor = settings.edit()
        localEditor.putLong(key, value!!)
        localEditor.commit()
    }


    fun getString(key: String): String? {
        return settings.getString(key, null)
    }

    fun getString(key: String, value: String): String? {
        return settings.getString(key, value)
    }

    fun getFloat(key: String, value: Float): Float? {
        return settings.getFloat(key, value)
    }

    fun getFloat(key: String): Float? {
        return settings.getFloat(key, 0f)
    }

    fun getInt(key: String, value: Int?): Int? {
        return settings.getInt(key, value!!)
    }

    fun getInt(key: String): Int? {
        return settings.getInt(key, 0)
    }

    fun getLong(key: String, value: Long?): Long? {
        return settings.getLong(key, value!!)
    }

    fun getLong(key: String): Long? {
        return settings.getLong(key, 0L)
    }

    fun getBoolean(key: String, value: Boolean?): Boolean? {
        return settings.getBoolean(key, value!!)
    }

    fun getBoolean(key: String): Boolean? {
        return settings.getBoolean(key, false)
    }

    fun clear(filename: String) {
        val settings = context.getSharedPreferences(filename,
                Context.MODE_PRIVATE)
        val localEditor = settings.edit()
        localEditor.clear()
                .commit()
    }


}
