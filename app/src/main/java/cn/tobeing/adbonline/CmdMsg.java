package cn.tobeing.adbonline;

import android.text.TextUtils;

import org.w3c.dom.Text;

/**
 * Created by sunzheng on 16/5/26.
 */
public class CmdMsg {

    private String from;

    public CmdMsg(){

    }

    public CmdMsg(String message,String cmd,String path){

        this.message=message;
        this.path=path;
        this.cmd=cmd;
    }

    private String message;

    private String path;

    private String cmd;

    private boolean isRemote;


    public String getMessage() {
        return message;
    }

    public CmdMsg setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getPath() {
        return path;
    }
    public CmdMsg setPath(String path){
        this.path=path;
        return this;
    }

    public String getCmd() {
        return cmd;
    }

    public CmdMsg setCmd(String cmd) {
        this.cmd = cmd;
        return this;
    }

    @Override
    public String toString() {
        return (TextUtils.isEmpty(from)?"":("("+from+")"))+path+":"+cmd+(TextUtils.isEmpty(message)?"":("\n"+message));
    }
    public String getSendMessage(){
        return path+":"+cmd+(TextUtils.isEmpty(message)?"":("\n"+message));
    }
    public boolean isRemote() {
        return isRemote;
    }

    public CmdMsg setRemote(boolean remote) {
        isRemote = remote;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public CmdMsg setFrom(String from) {
        this.from = from;
        return this;

    }
}
