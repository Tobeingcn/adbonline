package cn.tobeing.adbonline.command;

import cn.tobeing.adbonline.CMDExecute;

/**
 * Created by sunzheng on 16/5/26.
 */
public interface ICommand {
    boolean isParsable(String command);
    String parser(String command, CMDExecute cmdExecute);
}
