package data

import android.support.design.widget.BottomSheetBehavior
import com.lxz.kotlin.tools.share.ShareSave
import data.Item
import data.Rows

class Share {
    companion object {
        var rows: Rows by ShareSave(Rows(arrayListOf(
                Item("id", 300),
                Item("createTime", 300),
                Item("level", 300),
                Item("type", 300),
                Item("src", 300),
                Item("subject", 300),
                Item("content", 300),
                Item("url", 300),
                Item("tag", 300))))

        var behavior_state:Int by ShareSave(BottomSheetBehavior.STATE_COLLAPSED)

        var showWindow:Boolean by ShareSave(true)
        var maxCount:Int by ShareSave(100)
    }

}