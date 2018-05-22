package cmn.v.com.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import data.MessageLogBean
import data.MessageLogManager
import kotlinx.android.synthetic.main.activity_main.*
import cmn.v.com.test.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        MessageLogManager.isAvilible(MessageLogManager.)
        btn.setOnClickListener {
            Toast.makeText(this, "add", Toast.LENGTH_LONG).show()
            MessageLogManager.send({
                it.setContent("{\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":true,\"address\":{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\"},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}")
                        .setLevel("1")

            })


        }
    }


}



