package sql

import android.os.Parcelable
import io.requery.*

@Entity
interface SqlMessageLog : Parcelable, Persistable {


    @get:Key
    @get:Generated
    var id:Int?
    var applicationId: String?
    var createTime: String?
    var level: String?
    var type: String?
    var src: String?
    var subject: String?
    var content: String?
    var url: String?
    var tag: String?
}