
package cn.tobeing.adbonline;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.tobeing.adbonline.command.CdCommand;
import cn.tobeing.adbonline.command.DownloadCommand;
import cn.tobeing.adbonline.command.HookCommand;
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

    private OnCmdNewInfoListener cmdNewInfoListener;

    public CMDExecute(){
        processBuilder=new ProcessBuilder();
        commandParser=new CommandParser();
        commandParser.addCommand(new CdCommand().setCMDExecute(this));
        commandParser.addCommand(new MessageCommand());
        commandParser.addCommand(new UploadCommand().setCMDExecute(this));
        commandParser.addCommand(new DownloadCommand().setCMDExecute(this));
        commandParser.addCommand(new HookCommand().setCMDExecute(this));
    }

    /**
     * 
     * @param cmd   命令存放参数
     * @return
     * @throws IOException
     */
    public synchronized String run(String cmd){
        if(commandParser.isParsable(cmd)){
            return commandParser.parser(cmd);
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

    public OnCmdNewInfoListener getCmdNewInfoListener() {
        return cmdNewInfoListener;
    }

    public void setCmdNewInfoListener(OnCmdNewInfoListener cmdNewInfoListener) {
        this.cmdNewInfoListener = cmdNewInfoListener;
    }

    public static interface OnCmdNewInfoListener{
        void onComanNewInfo(String info);
    }
    public void notifyNewCmdInfo(String string){
        if(cmdNewInfoListener!=null){
            cmdNewInfoListener.onComanNewInfo(string);
        }
    }
}
