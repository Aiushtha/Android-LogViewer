有时候不想打开android studio ddms 或者 不想在后台(bugtags) 看数据记录</br>
就可以在手机上简单快速的查看数据日志(以悬浮窗的形式)</br>
 以表格的形式展示你 (表格的✎ 列可以拖拽和控制长宽)</br>
原理是主应用程序通过service给另外一个应用LogViewer发数据</br>



![原理图](http://aiushtha-github.stor.sinaapp.com/%E5%9B%BE%E7%89%87/32B0501A-2A54-473C-825A-BB465D55250D.png)


</br>使用场景:</br>
1:比如你下班已经关电脑了 </br>
2:有时你和其他后台人员交流时简单快速方便展示或让他人快速了解业务</br>


>使用方法 安装 LogViewer.apk 或者git clone下载后编译LogViewer工程

>注意: ✎  是可以拖拽
</br>LogViewer.apk </br>
下载地址 </br>
[主下载地址](https://github.com/Aiushtha/Android-LogViewer/blob/master/LogViewer-release.apk) </br>
[备用下载地址](http://aiushtha-github.stor.sinaapp.com/LogViewer-release.apk) 


>发送消息的测试apk TestLogViewer.apk </br>
 下载地址 </br>
 [主下载地址](https://github.com/Aiushtha/Android-LogViewer/blob/master/test-LogViewer-release.apk) </br>
 [备用下载地址](http://aiushtha-github.stor.sinaapp.com/test-LogViewer-release.apk)


>在你的项目中用引用

``` javascript
 compile 'com.aiushtha:logViewer:1.1.2'
 
 allprojects {
    repositories {
        maven { url "https://raw.githubusercontent.com/Aiushtha/Android-LogViewer//master" }
    }
 }
```
>如何给另外LogViewer发消息的代码

``` javascript
    MessageLogManager.send({ 
      it.tag=this.javaClass.simpleName
      it.url="http//baidu.com"
      it.level="1"
      it.content=User("lxz","30",1,"1989").asJsonFromat()
      it.subject="user info"
      it
    })

```

如果你很喜欢使用这个应用,可以发送建议到我的邮箱,或者在项目中留言 275518834@qq.com



以下为软件界面的截图
</br>
![主程序](http://aiushtha-github.stor.sinaapp.com/%E5%9B%BE%E7%89%87/1.png)
</br>
![logview](http://aiushtha-github.stor.sinaapp.com/%E5%9B%BE%E7%89%87/2.png)
</br>
![详情](http://aiushtha-github.stor.sinaapp.com/%E5%9B%BE%E7%89%87/3.png)