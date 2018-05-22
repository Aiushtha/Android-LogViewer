package data

import android.os.Parcel
import android.os.Parcelable

data class MessageLogBean(
        var applicationId: String,
        var createTime: String,
        var id: String,
        var level: String,
        var type: String,
        var src: String,
        var subject: String,
        var content: String,
        var url: String,
        var tag: String
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(applicationId)
        writeString(createTime)
        writeString(id)
        writeString(level)
        writeString(type)
        writeString(src)
        writeString(subject)
        writeString(content)
        writeString(url)
        writeString(tag)

    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MessageLogBean> = object : Parcelable.Creator<MessageLogBean> {
            override fun createFromParcel(source: Parcel): MessageLogBean = MessageLogBean(source)
            override fun newArray(size: Int): Array<MessageLogBean?> = arrayOfNulls(size)
        }
    }
}