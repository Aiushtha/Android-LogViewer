package org.lxz.utils.logviewer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.tbruyelle.rxpermissions2.RxPermissions
import data.MessageLogBean
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_webview.*
import sql.SqlMessageLogEntity
import java.io.File
import java.io.FileOutputStream


/**
 * Created by Lin on 2016/10/12.
 */

class WebLogActivity : Activity() {
    var content: String = ""
    var webContext:String=""
    lateinit var data: SqlMessageLogEntity
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        web.settings.javaScriptEnabled = true
        data = intent.getParcelableExtra(DATA)
        web.setWebViewClient(MyWebViewClient())
        web.loadUrl("file:///android_asset/rtf/code_demo.html")



        rl_share.setOnClickListener {

            var rxPermissions = RxPermissions(this)
            rxPermissions.requestEach(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    .subscribeBy (
                            onNext = {
                                if(it.granted){
                                    shareFile()
                                }
                            }
                    )


        }


    }

    private fun shareFile() {
//        var file=whriteStringFile("${content}")
        var file=whriteStringFile(
                "<html>\n" +
                        "<body>\n" +
                        "\n" +
                        "<h1>LogViewer</h1>\n" +
                        "\n" +
                        "<pre>${content.replace("\n".toRegex(), "<br>")
                                .replace("\t".toRegex(), "    ")}" +
                        "</pre>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>\n"
        )
        var share=Intent(Intent.ACTION_SEND);
        var uri:Uri
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(getApplicationContext(), getAppProcessName(this)+".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(Intent.EXTRA_STREAM, uri);//此处一定要用Uri.fromFile(file),其中file为File类型，否则附件无法发送成功。
        share.setType("text/html");
        startActivity(Intent.createChooser(share,"logviewer"));
    }

    internal inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }


        override fun onPageFinished(view: WebView, url: String) {
            var messageLog: MessageLogBean = MessageLogBean(
                    data.applicationId!!,
                    data.createTime!!,
                    data.id.toString(),
                    data.level!!,
                    data.type!!,
                    data.src!!,
                    data.subject!!,
                    data.content!!,
                    data.url!!,
                    data.tag!!)

            this@WebLogActivity.content = gsonFromat(messageLog)
            this@WebLogActivity.content = changeAddBr(content)
            web.loadUrl("javascript:code('" + this@WebLogActivity.content + "'" + ")")
           super.onPageFinished(view, url)
        }
    }


    /**
     * 生成一个临时的缓存文件
     */
    private fun getFile(): File {
        val dir = getFiledir()
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(Environment.getExternalStorageDirectory()
                .path , "temp.html")
    }


    fun getFiledir(): File {
        return File(Environment.getExternalStorageDirectory().path)
    }


    fun getAppProcessName(context: Context): String {
        //当前应用pid
        val pid = android.os.Process.myPid()
        //任务管理类
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //遍历所有应用
        val infos = manager.runningAppProcesses
        for (info in infos) {
            if (info.pid == pid)
            //得到当前应用
                return info.processName//返回包名
        }
        return ""
    }

    private fun changeAddBr(content: String): String {
        return content.replace("\n".toRegex(), "<br>")
    }

    private fun gsonFromat(data: MessageLogBean): String {
        return GsonBuilder()
                .setPrettyPrinting()
                .create().toJson(data)
    }

    companion object {
        var DATA = "DATA"
    }


    fun whriteStringFile(str: String):File{

        var file=getFile()

        file.createNewFile()
        try {


            if (!file.exists()) {

                val dir = File(file.getParent())

                dir.mkdirs()

                file.createNewFile()

            }

            val outStream = FileOutputStream(file)

            outStream.write(str.toByteArray(Charsets.UTF_8))

            outStream.close()

        } catch (e: Exception) {

            e.printStackTrace()

        }
        return file

    }


}
