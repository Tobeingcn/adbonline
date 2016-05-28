package cn.tobeing.adbonline.command;

import android.text.TextUtils;

import cn.tobeing.adbonline.CMDExecute;

/**
 * Created by sunzheng on 16/5/28.
 */
public abstract class AbsCommand implements ICommand{
    public abstract String getCommand();

    @Override
    public boolean isParsable(String command) {
        String com=getCommand();
        if(TextUtils.isEmpty(com)){
            return false;
        }
        if(command.toLowerCase().equals(com)||command.toLowerCase().startsWith(com+" ")){
            return true;
        }
        return false;
    }

    @Override
    public String parser(String command, CMDExecute cmdExecute) {
        String[] args = command.split("\\s+");
        return onCommand(args,cmdExecute);
    }
    public abstract String onCommand(String[] args,CMDExecute cmdExecute);
}
