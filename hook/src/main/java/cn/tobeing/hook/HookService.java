package cn.tobeing.hook;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sunzheng on 16/5/29.
 */
public class HookService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        HookServerImpl impl=HookServerImpl.getInstance();
        impl.init(this.getApplicationContext());
        return impl;
    }

}
