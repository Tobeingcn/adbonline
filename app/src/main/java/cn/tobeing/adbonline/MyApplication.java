package cn.tobeing.adbonline;

import android.app.Application;

/**
 * Created by sunzheng on 16/5/28.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        GlobalEnv.setContext(this);
    }
}
