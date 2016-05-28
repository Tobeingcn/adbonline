package cn.tobeing.adbonline;

import android.content.Context;

/**
 * Created by sunzheng on 16/5/28.
 */
public class GlobalEnv {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        GlobalEnv.mContext = mContext;
    }
}
