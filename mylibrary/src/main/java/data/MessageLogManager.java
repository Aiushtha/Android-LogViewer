package data;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageLogManager {

  private static final String activityName = "org.lxz.utils.logviewer.MainActivity";
  private static final String packName = "org.lxz.utils.logviewer";
  private static final String serviceName = "org.lxz.utils.logviewer.MainService";
  private static MessageLogBean bean=new MessageLogBean("","","","","","","","","","");


  private static String currenyPackName;
  private static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static Boolean isAvilible=null;

  public synchronized static void send(LogAction action) {
    if(isAvilible==null)
    {
      isAvilible=isAvilible(packName);
    }
    if(!isAvilible){return;}

    if(currenyPackName==null){currenyPackName=getAppProcessName(AppcationUtils.getInstance());}
    bean.applicationId=currenyPackName;
    bean.createTime=dateFormat.format(new Date());
    try {
      Intent intent = new Intent().setComponent(new ComponentName(packName, serviceName));
      bean.createTime = dateFormat.format(new Date());
      intent.putExtra("DATA", action.map(bean));
      AppcationUtils.getInstance().startService(intent);
    } catch (Exception e) {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      ComponentName cn = new ComponentName(packName, activityName);
      intent.setComponent(cn);
      intent.putExtra("DATA", action.map(bean));
      AppcationUtils.getInstance().startActivity(intent);
    }
  }
  public interface LogAction{
    MessageLogBean map(MessageLogBean bean);
  }

  public static String getAppProcessName(Context context) {
    //当前应用pid
    int pid = android.os.Process.myPid();
    //任务管理类
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    //遍历所有应用
    List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
    for (ActivityManager.RunningAppProcessInfo info : infos) {
      if (info.pid == pid)//得到当前应用
        return info.processName;//返回包名
    }
    return "";
  }

  /**
   * 检查手机上是否安装了指定的软件
   * @param packageName
   * @return
   */
  public static boolean isAvilible(String packageName) {
    final PackageManager packageManager = AppcationUtils.getInstance().getPackageManager();
    List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
    List<String> packageNames = new ArrayList<String>();

    if (packageInfos != null) {
      for (int i = 0; i < packageInfos.size(); i++) {
        String packName = packageInfos.get(i).packageName;
        packageNames.add(packName);
      }
    }
    // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
    return packageNames.contains(packageName);
  }


}
