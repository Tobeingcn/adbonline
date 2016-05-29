
package cn.tobeing.adbonline;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.tobeing.adbonline.command.CdCommand;
import cn.tobeing.adbonline.command.DownloadCommand;
import cn.tobeing.adbonline.command.MessageCommand;
import cn.tobeing.adbonline.command.UploadCommand;

/**
 * 执行dos命令
 * @author heyangbin
 */
public class CMDExecute {

    private ProcessBuilder processBuilder;

    private CommandParser commandParser;

    private String currentPath;

    public CMDExecute(){
        processBuilder=new ProcessBuilder();
        commandParser=new CommandParser();
        commandParser.addCommand(new CdCommand());
        commandParser.addCommand(new MessageCommand());
        commandParser.addCommand(new UploadCommand());
        commandParser.addCommand(new DownloadCommand());
    }

    /**
     * 
     * @param cmd   命令存放参数
     * @return
     * @throws IOException
     */
    public synchronized String run(String cmd){
        if(commandParser.isParsable(cmd)){
            return commandParser.parser(cmd,this);
        }
        String result = "";
        try {
            String[] newCmds=cmd.split("\\s+");
            processBuilder.command(newCmds);
            processBuilder.directory(new File(getCurrentPath()));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            InputStream in = process.getInputStream();
            byte[] buffer = new byte[1024];
            int ret = in.read(buffer);
            while (ret != -1) {
                String temp = new String(buffer, 0, ret);
                result = result + temp;
                ret = in.read(buffer);
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "cmd:["+cmd+"],running error,commands may not exist)";
        }
        return result;
    }
    public String getCurrentPath(){
        if(currentPath==null){
            return "/";
        }
        return currentPath;
    }
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
}
