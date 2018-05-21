package com.lxz.kotlin.tools.share


import kotlin.reflect.KProperty


class ShareSave<T> {
    val defaultValue: T
    var key: String? = null
    var cla: Class<T>? = null


    constructor ( defaultObj: Any ) {
        defaultValue = defaultObj as T
        cla = defaultObj::class.java as Class<T>?;


    }

    operator fun getValue(thisRef: Any, prop: KProperty<*>): T {
        if (key == null) {
            key = getKey(thisRef, prop)
        }
        return when (true) {
            prop.returnType.classifier!!.equals(String.javaClass) -> DataShare.getString(key!!, defaultValue as String) as T
            prop.returnType.classifier!!.equals(Float.javaClass) -> DataShare.getFloat(key!!, defaultValue as Float) as T
            prop.returnType.classifier!!.equals(Int.javaClass) -> DataShare.getInt(key!!, defaultValue as Int) as T
            prop.returnType.classifier!!.equals(Boolean::class.java.kotlin) -> DataShare.getBoolean(key!!, defaultValue as Boolean) as T
            else -> {
                DataShare.getJsonObject(key!!, cla!!, defaultValue) as T
            }
        } ?: defaultValue
    }

    private fun getKey(thisRef: Any, prop: KProperty<*>): String? = "&{thisRef::class.java.name}/${prop.name}"

    operator fun setValue(thisRef: Any, prop: KProperty<*>, value: T) {

        if (key == null) {
            key = getKey(thisRef, prop)
        }
        when (true) {
            value == null -> DataShare.saveJsonObject(key!!, defaultValue!!)
            prop.returnType.classifier!!.equals(String.javaClass) -> DataShare.put(key!!, value as String)
            prop.returnType.classifier!!.equals(Float.javaClass) -> DataShare.put(key!!, value as Float)
            prop.returnType.classifier!!.equals(Int.javaClass) -> DataShare.put(key!!, value as Int)
            prop.returnType.classifier!!.equals(Boolean::class.java.kotlin) -> DataShare.put(key!!, value as Boolean)
            else -> DataShare.saveJsonObject(key!!, value!!)
        }

    }


}
