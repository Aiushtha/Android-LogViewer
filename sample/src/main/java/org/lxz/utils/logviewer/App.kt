package org.lxz.utils.logviewer

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import com.yhao.floatwindow.*
import data.Event
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import org.greenrobot.eventbus.EventBus
import sql.Models
import kotlin.properties.Delegates

/**
 * Created by yhao on 2017/12/18.
 * https://github.com/yhaolpz
 */

class App : Application() {

    init {
        instance = this
    }

    companion object {
        var instance: App by Delegates.notNull()
        lateinit var db: KotlinReactiveEntityStore<Persistable>

        var TAG: String = "tag"
    }


    private val mPermissionListener = object : PermissionListener {
        override fun onSuccess() {
            Log.d(TAG, "onSuccess")
        }

        override fun onFail() {
            Log.d(TAG, "onFail")
        }
    }

    private val mViewStateListener = object : ViewStateListener {
        override fun onPositionUpdate(x: Int, y: Int) {
            Log.d(TAG, "onPositionUpdate: x=$x y=$y")
        }

        override fun onShow() {
            Log.d(TAG, "onShow")
        }

        override fun onHide() {
            Log.d(TAG, "onHide")
        }

        override fun onDismiss() {
            Log.d(TAG, "onDismiss")
        }

        override fun onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart")
        }

        override fun onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd")
        }

        override fun onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop")
        }
    }

    lateinit var dbSource: DatabaseSource
    val dataBase: KotlinReactiveEntityStore<Persistable> by lazy {
        dbSource = DatabaseSource(instance, Models.DEFAULT, 1)
        dbSource.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS)
        KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(dbSource.configuration))
    }

    lateinit var window: View

    override fun onCreate() {
        super.onCreate()
        db = dataBase
        window=LayoutInflater.from(applicationContext).inflate(R.layout.view_window,null)

        FloatWindow
                .with(applicationContext)
                .setView(window)
                .setWidth(Screen.width, 0.2f) //设置悬浮控件宽高
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide, 0, 0)
                .setMoveStyle(500, BounceInterpolator())
                .setFilter(true, MainActivity::class.java, WebLogActivity::class.java)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .build()



        window.setOnClickListener { v ->
            EventBus.getDefault().post(Event(0));
            val context = v.context.applicationContext
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
//            override fun onActivityPaused(activity: Activity?) {
//
//                App.instance.windowImage.visibility = View.GONE
//            }
//
//
//            override fun onActivityResumed(activity: Activity?) {
//                App.instance.windowImage.visibility = View.GONE
//            }
//
//            override fun onActivityStarted(activity: Activity?) {
//            }
//
//            override fun onActivityDestroyed(activity: Activity?) {
//                App.instance.windowImage.visibility
//            }
//
//            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
//            }
//
//            override fun onActivityStopped(activity: Activity?) {
//            }
//
//            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
//            }
//
//        })
    }

}
