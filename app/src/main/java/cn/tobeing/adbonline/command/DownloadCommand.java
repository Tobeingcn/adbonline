package cn.tobeing.adbonline.command;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import cn.tobeing.adbonline.CMDExecute;

/**
 * Created by sunzheng on 16/5/28.
 */
public class DownloadCommand extends AbsCommand{

    @Override
    public String getCommand() {
        return "download";
    }

    @Override
    public String onCommand(String[] args) {
        if (args.length > 3 || args.length < 2) {
            return "argument is miss or to much";
        }
        String destUrl = args[1];
        String fileName;
        if (args.length > 2) {
            fileName = args[2];
        } else {
            fileName = getFileName(destUrl);
        }
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        byte[] buf = new byte[1024];
        int size = 0;
        try {
            //建立链接
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            //连接指定的资源
            httpUrl.connect();
            //获取网络输入流
            bis = new BufferedInputStream(httpUrl.getInputStream());
            //建立文件
            File file = new File(cmdExecute.getCurrentPath(),fileName);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try {
                    file.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(!file.exists()){
                    return "下载失败:无法创建文件"+file.getAbsolutePath();
                }
            }
            fos = new FileOutputStream(file.getAbsoluteFile());

            Log.d("download","正在获取链接[" + destUrl + "]的内容...\n将其保存为文件["
                        + file.getAbsoluteFile() + "]");
            //保存文件
            while ((size = bis.read(buf)) != -1)
                fos.write(buf, 0, size);

            fos.close();
            bis.close();
            httpUrl.disconnect();
            return "文件下载成功:"+file.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "下载失败";
    }
    private String getFileName(String url){
        if(url.contains("/")){
            return url.substring(url.lastIndexOf("/"));
        }else{
            return UUID.randomUUID().toString();
        }
    }
}
