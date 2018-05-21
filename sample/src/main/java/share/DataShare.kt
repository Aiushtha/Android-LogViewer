package com.lxz.kotlin.tools.share

import android.content.SharedPreferences

import com.google.gson.Gson

import java.util.HashMap

import io.reactivex.Observable

class DataShare {
    val androidShare: AndroidShare

    init {
        androidShare = AndroidShare(filename)
    }


    fun begin(): Build {
        return Build()
    }

    inner class Build {
        internal var localEditor: SharedPreferences.Editor = androidShare.settings.edit()
        fun put(key: String, value: String) {
            val localEditor = androidShare.settings.edit()
            localEditor.putString(key, value)
        }

        fun saveJsonObject(key: String, value: String) {
            val localEditor = androidShare.settings.edit()
            localEditor.putString(key, value)
        }

        fun commit() {
            localEditor.commit()
        }
    }


    fun clear(filename: String) {
        instance.androidShare.clear(filename)
    }

    companion object {
        private val filename = "DataObject"
        val dataObjectShare: DataShare by lazy { DataShare() }

        private val map = HashMap<String, Any>()

        //    public static void init(Context ctx) {
        //        context = ctx;
        //        getInstance();
        //    }

        val instance: DataShare
            get() = dataObjectShare

        fun clear() {
            map.clear()
            instance.androidShare.clear(filename)
        }

        fun saveJsonObject(obj: Any): Boolean {

            try {

                map[obj.javaClass.name] = obj
                instance.androidShare.put(obj.javaClass.name,
                        Gson().toJson(obj))

                return true
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return false
        }


        fun saveJsonObject(key: String, obj: Any): Any {
            map[key] = obj
            instance.androidShare.put(key,
                    Gson().toJson(obj))
            return obj
        }

        fun cleanJsonObject(key: String){
            map.remove(key)
            instance.androidShare.clearKey(key)
        }


        fun <T> getJsonObject(key: String, cls: Class<T>): T? {
            try {
                val obj = map[key]
                if (obj != null) return obj as T?
                val content = instance.androidShare.getString(key)
                return Gson().fromJson(content, cls)
            } catch (e: Exception) {

            }

            return null
        }

        fun <T> getJsonObject(key: String, cls: Class<T> ,defaultvalue:T?): T? {
            try {
                val obj = map[key]
                if (obj != null) return obj as T
                val content = instance.androidShare.getString(key)
                return Gson().fromJson(content, cls)
            } catch (e: Exception) {

            }
            return defaultvalue
        }

        fun <T> getJsonObject(cls: Class<T>): T? {

            try {

                val obj = map.get(cls.name)
                if (obj != null) return obj as T?
                val content = instance.androidShare.getString(cls.name)
                return Gson().fromJson(content, cls)
            } catch (e: Exception) {

            }

            return null
        }


        fun <T> getJsonObjectObservable(cls: Class<T>): Observable<T> {
            return Observable.defer {
                Observable.create<T>{ e ->
                    var obj: Any? = map.get(cls.name)
                    if (obj != null) {
                        e.onNext(obj as T)
                    } else {
                        val content = instance.androidShare.getString(cls.name)
                        if (content != null) {
                            obj = Gson().fromJson(content, cls)
                            if (obj != null) {
                                e.onNext(Gson().fromJson(content, cls))
                            } else {
                                e.onError(NullPointerException(cls.name))
                            }
                        } else {
                            e.onError(NullPointerException(cls.name))
                        }
                    }

                    e.onComplete()
                }
            }


        }


        fun clean(cls: Class<*>): Boolean {
            map.remove(cls.name)
            instance.androidShare.put(cls.name, "")
            return true

        }

        fun put(key: String, value: String) {
            instance.androidShare.put(key, value)
        }

        fun put(key: String, value: Int?) {
            instance.androidShare.put(key, value)
        }

        fun put(key: String, value: Float?) {
            instance.androidShare.put(key, value)
        }

        fun put(key: String, value: Boolean?) {
            instance.androidShare.put(key, value!!)
        }

        fun put(key: String, value: Long?) {
            instance.androidShare.put(key, value)
        }


        fun getString(key: String): String? {
            return instance.androidShare.getString(key)
        }

        fun getString(key: String, defaultvalue: String): String? {
            return instance.androidShare.getString(key, defaultvalue)
        }

        fun getInt(key: String): Int? {
            return instance.androidShare.getInt(key)
        }

        fun getInt(key: String, defaultvalue: Int?): Int? {
            return instance.androidShare.getInt(key, defaultvalue)
        }

        fun getBoolean(key: String): Boolean {
            return instance.androidShare.getBoolean(key, false)!!
        }

        fun getBoolean(key: String, defaultvalue: Boolean?): Boolean {
            return instance.androidShare.getBoolean(key, defaultvalue)!!
        }


        fun getLong(key: String): Long {
            return instance.androidShare.getLong(key)!!
        }

        fun getLong(key: String, defaultvalue: Long?): Long {
            return instance.androidShare.getLong(key, defaultvalue)!!
        }

        fun getFloat(key: String): Float {
            return instance.androidShare.getFloat(key, 0f)!!
        }

        fun getFloat(key: String, defaultvalue: Float): Float {
            return instance.androidShare.getFloat(key, defaultvalue)!!
        }


    }
}
