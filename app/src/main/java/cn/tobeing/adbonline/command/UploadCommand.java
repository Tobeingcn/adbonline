package cn.tobeing.adbonline.command;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.tobeing.adbonline.CMDExecute;
import cn.tobeing.adbonline.GlobalEnv;
import cn.tobeing.adbonline.ZipCompressor;

/**
 * Created by sunzheng on 16/5/28.
 */
public class UploadCommand extends AbsCommand{
    private String server="http://resource.tobeing.cn/doUpload.php";
    @Override
    public String getCommand() {
        return "upload";
    }
    @Override
    public String onCommand(String[] args, CMDExecute cmdExecute) {
        if(args.length==1&&args[0].toLowerCase().equals(getCommand())){
            return "argument path is need";
        }
        if(args.length>2){
            return "argument to much ";
        }
        File file=new File(cmdExecute.getCurrentPath(),args[1]);
        File zipFile=null;
        if(!file.exists()){
            return "file not exist:"+file.getAbsolutePath();
        }
        if(isFileZipNeed(file)){
            zipFile=zipFile(file);
            if(file==zipFile){
                zipFile=null;
            }else{
                file=zipFile;
            }
        }
        String uploadUrl=server;
        String srcPath=file.getAbsolutePath();
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\"" + end);
            dos.writeBytes(end);
            //将SD 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
            FileInputStream fis = new FileInputStream(srcPath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);

            }
            fis.close();
            System.out.println("file send to server............");
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            //读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            String response=convertStreamToString(is);
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.getInt("error")==0){
                JSONObject content=jsonObject.getJSONObject("content");
                return "上传成功："+(zipFile==null?"":"已压缩")+content.getString("url");
            }else{
                return "上传失败："+jsonObject.optString("message");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(zipFile!=null) {
                zipFile.delete();
            }
        }
        return "上传失败，请重试";
    }
    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private String getExtName(String name){
        if(name.indexOf(".")>0){
            return name.substring(name.lastIndexOf('.')+1,name.length());
        }
        return "";
    }
    private static List<String> exts=new ArrayList<>();
    static {
        exts.add("apk");
        exts.add("bat");
        exts.add("exe");
        exts.add("sh");
    }
    private boolean isFileZipNeed(File file){
        if(file.isDirectory()||exts.contains(getExtName(file.getName()))){
            return true;
        }else{
            return false;
        }
    }
    private File zipFile(File file){
        File file1=new File(GlobalEnv.getContext().getCacheDir().getPath(),file.getName()+".zip");
        ZipCompressor compressor=new ZipCompressor(file1.getAbsolutePath());
        compressor.compressExe(file.getAbsolutePath());
        if(file1.exists()){
            Log.d("压缩后的文件",file1.getAbsolutePath());
            return file1;
        }else{
            return file;
        }
    }
}
