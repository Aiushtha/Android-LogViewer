package org.lxz.utils.logviewer

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.warkiz.widget.IndicatorSeekBar
import data.Event
import data.Item
import data.MessageLogBean
import data.Share
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_item_log_head.view.*
import kotlinx.android.synthetic.main.view_window.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import sql.SqlMessageLog
import sql.SqlMessageLogEntity
import view.SpacesItemDecoration


open class MainActivity : AppCompatActivity(), OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        EventBus.getDefault().register(this);
        initValue()

        addData()
        initBottomSheet()
        initHead()
        initOnClick()
        behaviorListen()


    }

    private fun initOnClick() {
        tv_clear.setOnClickListener(this)
        tv_hide.setOnClickListener(this)
        tv_setting.setOnClickListener(this)
        tv_permissions_setting.setOnClickListener(this)
        btn_menu.setOnClickListener(this)
        checkbox_show.setOnClickListener(this)
        maxSizeSeekBar.setOnSeekChangeListener(object : IndicatorSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: IndicatorSeekBar?, progress: Int, progressFloat: Float, fromUserTouch: Boolean) {
                if (fromUserTouch) {
                    Share.Companion.maxCount = progress
                    if (logAdapter.data.size < Share.maxCount) {
                        logAdapter.data
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?, thumbPosOnTick: Int) {
            }

            override fun onSectionChanged(seekBar: IndicatorSeekBar?, thumbPosOnTick: Int, tickBelowText: String?, fromUserTouch: Boolean) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            }
        })
    }


    private fun behaviorListen() {
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Share.behavior_state = behavior.state
            }
        })
        behavior.state = Share.behavior_state
    }

    private var isShow: Boolean = false
    private lateinit var logAdapter: LogAdapter
    private lateinit var headView: LinearLayout
    private lateinit var behavior: BottomSheetBehavior<View>
    private lateinit var settingAdapter: SettingAdapter
    private lateinit var headApter: HeadApter;

    private var itemHeight = 150;


    override fun onClick(v: View?) {
        when (v) {
            tv_permissions_setting -> getAppDetailSettingIntent(this)
            btn_menu -> {
                if (!drawer_layout.isDrawerOpen(nav_view)) drawer_layout.openDrawer(nav_view) else drawer_layout.closeDrawer(nav_view)
            }
            checkbox_show -> {
                checkbox_show.isChecked = !checkbox_show.isChecked
                App.instance.window.visibility = if (checkbox_show.isChecked) View.VISIBLE else View.GONE
                Share.showWindow = checkbox_show.isChecked
            }
            tv_clear -> {

                App.db.delete(SqlMessageLogEntity::class)

                        .get()

                        .consume {


                            logAdapter.data.clear()
                            logAdapter.notifyDataSetChanged()
                            updateWidonwText()

                        }


            }
            tv_hide -> behavior.state = BottomSheetBehavior.STATE_HIDDEN
            tv_setting -> if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) run { behavior.state = BottomSheetBehavior.STATE_COLLAPSED } else run({ behavior.state = BottomSheetBehavior.STATE_EXPANDED })
            else -> if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) run { behavior.state = BottomSheetBehavior.STATE_COLLAPSED } else run({ behavior.state = BottomSheetBehavior.STATE_EXPANDED })
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Event(log: SqlMessageLogEntity): Unit {
        synchronized(this)
        {
            logAdapter.data.add(0, log);
            if (logAdapter.data.size > Share.maxCount) {
                logAdapter.data.removeAt(logAdapter.data.size - 1)
            }
            logAdapter.notifyDataSetChanged()
            updateWidonwText()
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Event(log: Event): Unit {
        if (isShow) {
            onClick(tv_setting)
        }
    }


    fun calAllItemWidth(): Int {
        return Share.rows.data.sumBy { item -> item.width }

    }


    private fun initValue() {
        headView = layoutInflater.inflate(R.layout.adapter_item_log_head, null) as LinearLayout
        behavior = BottomSheetBehavior.from<View>(bottom_sheet)
        logAdapter = LogAdapter(recyclerView)
        settingAdapter = SettingAdapter(bottom_recycler)
        headApter = HeadApter(headView.head_recycler)
        logAdapter.addHeaderView(headView)

        checkbox_show.isChecked = Share.showWindow
        App.instance.window.visibility = if (checkbox_show.isChecked) View.VISIBLE else View.GONE
        maxSizeSeekBar.setProgress(1.0f * Share.maxCount / maxSizeSeekBar.max)

    }


    private fun initHead() {
        var mItemDragAndSwipeCallback = ItemDragAndSwipeCallback(headApter)
        var mItemTouchHelper = ItemTouchHelper(mItemDragAndSwipeCallback)
        mItemTouchHelper.attachToRecyclerView(headView.head_recycler)
        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START or ItemTouchHelper.END)
        headApter.enableSwipeItem()
        headApter.enableDragItem(mItemTouchHelper)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = 20f
        paint.color = Color.BLACK

        val listener = object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {}
            override fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int) {}
            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                settingAdapter.notifyDataSetChanged()
                logAdapter.notifyDataSetChanged()
            }
        }
        val onItemSwipeListener = object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {}
            override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {}
            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                Share.rows.data.add(Share.rows.data.get(pos))
                settingAdapter.notifyDataSetChanged()
                logAdapter.notifyDataSetChanged()
            }

            override fun onItemSwipeMoving(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, isCurrentlyActive: Boolean) {}
        }
        headApter.setOnItemSwipeListener(onItemSwipeListener)
        headApter.setOnItemDragListener(listener)
    }

    private fun initBottomSheet() {
        var mItemDragAndSwipeCallback = ItemDragAndSwipeCallback(settingAdapter)
        var mItemTouchHelper = ItemTouchHelper(mItemDragAndSwipeCallback)
        mItemTouchHelper.attachToRecyclerView(bottom_recycler)

        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START or ItemTouchHelper.END)
        settingAdapter.enableSwipeItem()
        settingAdapter.enableDragItem(mItemTouchHelper)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = 20f
        paint.color = Color.BLACK

        val listener = object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {}
            override fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int) {}
            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                Log.d("test", "drag end")
                headApter.notifyDataSetChanged()
                logAdapter.notifyDataSetChanged()
            }
        }
        val onItemSwipeListener = object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {}
            override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {}
            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                Share.rows.data.add(Share.rows.data.get(pos))
                headApter.notifyDataSetChanged()
                logAdapter.notifyDataSetChanged()
            }

            override fun onItemSwipeMoving(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, isCurrentlyActive: Boolean) {}
        }
        settingAdapter.setOnItemSwipeListener(onItemSwipeListener)
        settingAdapter.setOnItemDragListener(listener)
    }

    private fun addData() {
        var data: Parcelable? = intent.getParcelableExtra(WebLogActivity.DATA)

        if (data != null) {
            moveTaskToBack(true)
            val bean = data as MessageLogBean
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
            App.db.insert(sqlMessage).toObservable()
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                logAdapter.data.add(0, it)
                                logAdapter.notifyDataSetChanged()

                            }
                    )
        }
    }

    fun updateWidonwText() {
        App.instance.window.post {
            var size = logAdapter.data.size
            if (size == 0) {
                App.instance.window.rl_count.visibility = View.GONE
            } else {
                App.instance.window.rl_count.visibility = View.VISIBLE
                App.instance.window.tv_count.setText(logAdapter.data.size.toString())
            }
        }


    }


    internal inner class LogAdapter(recyclerView: RecyclerView) :
            BaseQuickAdapter<SqlMessageLog, BaseViewHolder>(0) {

        init {
            logAdapter = this
            var linearLayoutManager = LinearLayoutManager(recyclerView.context)
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
            recyclerView.setLayoutManager(linearLayoutManager)
            recyclerView.isEnabled = false
            recyclerView.adapter = this
            App.db.select(SqlMessageLog::class)
                    .orderBy(SqlMessageLogEntity.ID.desc())
                    .limit(Share.maxCount)
                    .get()
                    .toObservable()
                    .toList()
                    .toObservable()
                    .subscribeBy(
                            onNext = {
                                this@LogAdapter.data.clear()
                                this@LogAdapter.addData(it)
                                updateWidonwText()
                            }
                    )
        }


        override fun getItemView(layoutResId: Int, parent: ViewGroup?): View {
//            var view = layoutInflater.inflate(R.layout.adapter_item_log, null)
//            return view
            return RecyclerView(this@MainActivity)
        }

        override fun convert(helper: BaseViewHolder, item: SqlMessageLog) {
            ItemApter((helper.itemView as RecyclerView), item)


        }

    }


    internal inner class SettingAdapter(recyclerView: RecyclerView) :
            BaseItemDraggableAdapter<Item, BaseViewHolder>(Share.rows.data) {

        init {
            var linearLayoutManager = LinearLayoutManager(recyclerView.context)
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
            recyclerView.setLayoutManager(linearLayoutManager)
            recyclerView.adapter = this


        }

        override fun getItemView(layoutResId: Int, parent: ViewGroup?): View {
            var view = layoutInflater.inflate(R.layout.adapter_item_setting, null)
            return view
        }

        override fun convert(helper: BaseViewHolder, item: Item) {
            helper.setText(R.id.tv_id, "✎${item.name}")
            helper.getView<IndicatorSeekBar>(R.id.seekBar).setProgress(item.width.toFloat())

            helper.getView<IndicatorSeekBar>(R.id.seekBar).setOnSeekChangeListener(
                    object : IndicatorSeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: IndicatorSeekBar?, progress: Int, progressFloat: Float, fromUserTouch: Boolean) {
                            if (fromUserTouch) {
                                Share.rows.data.get(helper.adapterPosition).width = progress
                                headApter.notifyDataSetChanged()
                                logAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?, thumbPosOnTick: Int) {}

                        override fun onSectionChanged(seekBar: IndicatorSeekBar?, thumbPosOnTick: Int, tickBelowText: String?, fromUserTouch: Boolean) {}

                        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}

                    }
            )
        }

    }


    internal inner class HeadApter(recyclerView: RecyclerView) :
            BaseItemDraggableAdapter<Item, BaseViewHolder>(Share.rows.data) {

        init {
            var linearLayoutManager = LinearLayoutManager(recyclerView.context)
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
            if (recyclerView.getItemDecorationAt(0) == null) {
                recyclerView.addItemDecoration(SpacesItemDecoration(2))
            }
            this.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
                onClick(tv_setting)
            }
            recyclerView.setLayoutManager(linearLayoutManager)
            recyclerView.adapter = this
            recyclerView.setNestedScrollingEnabled(false)
            recyclerView.isEnabled = false


        }

        override fun getItemView(layoutResId: Int, parent: ViewGroup?): View = createTextView()


        override fun convert(helper: BaseViewHolder, item: Item) {
            helper.setText(R.id.tv_id, "✎${item.name}")
            helper.itemView.layoutParams.width = item.width

        }

    }

    internal inner class ItemApter(recyclerView: RecyclerView, sqlLogItem: SqlMessageLog) :
            BaseItemDraggableAdapter<Item, BaseViewHolder>(Share.rows.data) {
        var logItem = sqlLogItem

        init {
            var linearLayoutManager = LinearLayoutManager(recyclerView.context)
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
            recyclerView.isEnabled = false
            recyclerView.setLayoutManager(linearLayoutManager)
            if (recyclerView.getItemDecorationAt(0) == null) {
                recyclerView.addItemDecoration(SpacesItemDecoration(2))
            }
            this.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(WebLogActivity.DATA, logItem)
                intent.setClassName(packageName, WebLogActivity::class.java.name)
                startActivity(intent)
            }

            recyclerView.layoutParams = ViewGroup.LayoutParams(calAllItemWidth(), itemHeight)
            recyclerView.adapter = this


        }

        override fun getItemView(layoutResId: Int, parent: ViewGroup?): View = createTextView()
        override fun convert(helper: BaseViewHolder, item: Item) {

            var str: String? = when (item.name) {
                "id" -> logItem.id.toString()
                "createTime" -> logItem.createTime
                "level" -> logItem.level
                "type" -> logItem.type
                "src" -> logItem.src
                "subject" -> logItem.subject
                "content" -> logItem.content
                "url" -> logItem.url
                "tag" -> logItem.tag
                else -> ""
            }
            helper.setText(R.id.tv_id, str)
            helper.itemView.layoutParams.width = item.width

        }

    }


    override fun onPause() {
        super.onPause()
        isShow = false
    }

    override fun onResume() {
        super.onResume()
        isShow = true
    }

    @Override
    override fun onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    fun createTextView(): TextView {
        var tv = TextView(this@MainActivity)
        tv.setTextColor(resources.getColor(android.R.color.white))
        tv.gravity = Gravity.CENTER
        tv.setTextSize(20f)
        tv.layoutParams = ViewGroup.LayoutParams(0, itemHeight)
        tv.id = R.id.tv_id
        tv.setBackgroundResource(R.drawable.tv_bg_selector)
        tv.setSingleLine(true)
        tv.maxLines = 1
        return tv
    }

    //Android屏蔽返回键 修改为home键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_layout.isDrawerOpen(nav_view)) {
                drawer_layout.closeDrawer(nav_view)
                return true
            }

            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
            return true//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event)//继续执行父类其他点击事件
    }

    fun getAppDetailSettingIntent(context: Context) {
        var localIntent = Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }


}


