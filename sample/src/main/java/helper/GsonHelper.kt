package helper

import com.google.gson.Gson



inline fun <reified T : Any> changeJsonObj(obj:Any,t:Class<T>):T{
    return Gson().fromJson(Gson().toJson(obj), t);
}