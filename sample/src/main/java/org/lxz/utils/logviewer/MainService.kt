package org.lxz.utils.logviewer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import android.util.Log


import data.MessageLogBean
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.EventBus
import sql.SqlMessageLogEntity


/**
 * @author Aidan Follestad (afollestad)
 */
class MainService : Service() {

    private fun log(message: String) {
        Log.v("test", message)
    }

    override fun onCreate() {
        super.onCreate()
        //        register();
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override  fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        synchronized(this) {
            try {
                if (intent != null) {
                    val bean = intent.getParcelableExtra<Parcelable>(WebLogActivity.DATA) as MessageLogBean
                    if (bean != null) {
                        Log.d("test", bean.toString())
                        var sqlMessage: SqlMessageLogEntity = SqlMessageLogEntity()
                        sqlMessage.createTime = bean.createTime
                        sqlMessage.content = bean.content
                        sqlMessage.applicationId = bean.applicationId
                        sqlMessage.level = bean.level
                        sqlMessage.src = bean.src
                        sqlMessage.subject = bean.subject
                        sqlMessage.url = bean.url
                        sqlMessage.tag = bean.tag
                        sqlMessage.type = bean.type



                        Log.d("test", "sqlMessage:" + sqlMessage)
                        App.db.insert(sqlMessage).toObservable()
                                .subscribeBy(
                                        onNext = {
                                            EventBus.getDefault().post(it);
                                        }
                                )


                        //                  MessageCache.addMessage(bean);
                        //                  EventBus.getDefault().post(new PostEvent(bean));
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        log("Received binding.")
        return null
    }

}
