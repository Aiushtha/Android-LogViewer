package data;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppcationUtils {

    private static Application application = null;

    public static Application getInstance(){ return application!=null?application:(application=get());}

    private static Application get(){

        Application application = null;
        Class<?> activityThreadClass;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            final Method method2 = activityThreadClass.getMethod(
                    "currentActivityThread", new Class[0]);
            // 得到当前的ActivityThread对象
            Object localObject = method2.invoke(null, (Object[]) null);

            final Method method = activityThreadClass
                    .getMethod("getApplication");
            application = (Application) method.invoke(localObject, (Object[]) null);
            return application;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return application;
    }
}
