package cn.tobeing.adbonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunzheng on 16/5/27.
 */
public class OnlineManager {
    private String server="http://adbonline.tobeing.cn";

    private static final int BUFFER_SIZE=1024;
    private Context mContext;
    private OnMessageListener onMessageListener;
    private List<String> cacheMessage=new ArrayList<>();
    private static final String TAG="OnlineManager";
    private static OnlineManager instance=new OnlineManager();
    SharedPreferences sp;

    private Handler mWorkHandler;

    public static OnlineManager getInstance(){
        return instance;
    }
    private OnlineManager(){
        HandlerThread thread=new HandlerThread(TAG);
        thread.start();
        mWorkHandler=new WorkHandle(thread.getLooper());
        mWorkHandler.sendEmptyMessage(MSG_PULL_MESSAGE);
    }
    public void init(Context context){
        mContext=context;
        sp=context.getSharedPreferences(TAG,Context.MODE_PRIVATE);
        from=sp.getString("from","");
        to=sp.getString("to","");
    }
    private String from;

    private String to;

    public String getUser() {
        return from;
    }

    public void setUser(String from) {
        this.from = from;
        sp.edit().putString("from",from).commit();
    }

    public String getRemoteUser() {
        return to;
    }

    public void setRemoteUser(String to) {
        this.to = to;
        sp.edit().putString("to",to).commit();
    }

    public OnMessageListener getOnMessageListener() {
        return onMessageListener;
    }

    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
    }

    private static final int MSG_PULL_MESSAGE=0x0001;

    private static final int MSG_PUSH_MESSAGE=0x0002;

    private class WorkHandle extends Handler{
        public WorkHandle(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PULL_MESSAGE:{

                    removeMessages(msg.what);
                    pullMessage();
                    sendEmptyMessageDelayed(MSG_PULL_MESSAGE,3000);
                }
                break;
                case MSG_PUSH_MESSAGE:{
                   boolean sucess =pushMessage((String) msg.obj);
                    if(!sucess){
                        cacheMessage.add((String) msg.obj);
                    }
                    List<String> sucessList=new ArrayList<>();
                    for (String mesg:cacheMessage){
                        if(pushMessage(mesg)){
                            sucessList.add(mesg);
                        }
                    }
                    cacheMessage.removeAll(sucessList);
                    if(cacheMessage.size()>10){
                        cacheMessage.remove(10);
                    }
                }
                break;
            }
        }
    }
    public void sendMessage(String message){
        Log.d(TAG,"sendMessage"+message);
        mWorkHandler.obtainMessage(MSG_PUSH_MESSAGE,message).sendToTarget();
    }
    private void pullMessage() {
        if (TextUtils.isEmpty(getRemoteUser()) || TextUtils.isEmpty(getUser())) {
            return;
        }
        String urlPath = server+"/message.php?action=get&from=" + toURLEncoded(getRemoteUser()) + "&to=" + toURLEncoded(getUser());
//        String urlPath = "http://tobeing.cn/apps/adbonline/message.php?action=get&from=" + getRemoteUser() + "&to=" + getUser();

//        Log.d("suntest","请求数据："+urlPath);
        try {
            // 新建一个URL对象
            URL url = new URL(urlPath);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            // 开始连接
            urlConn.connect();

            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                String text=InputStreamTOString(urlConn.getInputStream());
                JSONObject object=new JSONObject(text);
                if(object.getInt("error")==0){
                    String message=object.getString("data");
                    message=toURLDecoded(message);
                    if(onMessageListener!=null){
                        onMessageListener.onMessage(message);
                        Log.d("suntest","接收到消息如下："+message);
                    }else{
                        Log.d("suntest","接收到错误格式消息如下："+message);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean pushMessage(String message){
        if (TextUtils.isEmpty(getRemoteUser()) || TextUtils.isEmpty(getUser())) {
            return false;
        }
        String content="action=send&from=" + toURLEncoded(getUser()) + "&to=" + toURLEncoded(getRemoteUser())+"&message="+toURLEncoded(message);
        String urlPath = server+"/message.php";

        Log.d("suntest","提交信息数据："+urlPath);
        try {
            // 新建一个URL对象
            URL url = new URL(urlPath);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            urlConn.setRequestMethod("POST");
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            // 开始连接
            urlConn.connect();
            //DataOutputStream流
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
            //要上传的参数
            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                String text=InputStreamTOString(urlConn.getInputStream());
                Log.d("suntest","接收到格式消息如下："+text);
                JSONObject object=new JSONObject(text);
                if(object.getInt("error")==0){
                    return true;
                }
                Log.d("suntest","接收到错误格式消息如下："+text);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 将InputStream转换成String
     * @param in InputStream
     * @return String
     * @throws Exception
     *
     */
    public static String InputStreamTOString(InputStream in) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        return new String(outStream.toByteArray());
    }
    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d("suntest","toURLEncoded error:"+paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.d("suntest","toURLEncoded error:"+paramString, localException);
        }

        return paramString;
    }
    public static String toURLDecoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d("suntest","toURLDecoded error:"+paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLDecoder.decode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.d("suntest","toURLDecoded error:"+paramString, localException);
        }

        return paramString;
    }
    public interface OnMessageListener{
        void onMessage(String message);
    }
}
