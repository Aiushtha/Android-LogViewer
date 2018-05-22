package cmn.v.com.test

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import data.MessageLogBean
import data.MessageLogManager
import kotlinx.android.synthetic.main.activity_main.*
import cmn.v.com.test.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when(MessageLogManager.isAvilible(MessageLogManager.packName)){
          true-> {tv_install.text="true";tv_install.setTextColor(Color.GREEN)}
          else-> {tv_install.text="false";tv_install.setTextColor(Color.RED)}
        }

        tv_log.setText(createMessage().asJsonFromat())
        btn.setOnClickListener {
                Toast.makeText(this, "add", Toast.LENGTH_LONG).show()
                MessageLogManager.send({ it })
        }
    }
    data class User(var name:String,var id:String,var sex:Int,var birthday:String)


    private fun createMessage():MessageLogBean {
        MessageLogManager.bean.tag=this.javaClass.simpleName
        MessageLogManager.bean.url="http//baidu.com"
        MessageLogManager.bean.level="1"
        MessageLogManager.bean.content=User("lxz","30",1,"1989").asJsonFromat()
        MessageLogManager.bean.subject="user info"
        return MessageLogManager.bean
    }


}

fun Any?.asJson():String{
    return Gson().toJson(this)
}
fun Any?.asJsonFromat():String= if(this is String) asStringJsonFromat() else asJson().asJsonFromat()

fun String.asStringJsonFromat(): String {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val jsonPar = JsonParser()
    val jsonEl = jsonPar.parse(this)
    return gson.toJson(jsonEl)

}

