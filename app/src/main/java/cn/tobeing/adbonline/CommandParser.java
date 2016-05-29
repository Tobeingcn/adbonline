package cn.tobeing.adbonline;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.tobeing.adbonline.command.ICommand;

/**
 * Created by sunzheng on 16/5/26.
 */
public class CommandParser implements ICommand{

    public CommandParser(){

    }
    private List<ICommand> commands=new ArrayList<>();

    @Override
    public ICommand setCMDExecute(CMDExecute cmdExecute) {
        return null;
    }

    public boolean isParsable(String cmd){

        for (ICommand iCommand:commands){
            if(iCommand.isParsable(cmd)){
                return true;
            }
        }
        return false;
    }
    private ICommand findCommand(String cmd){
        for (ICommand iCommand:commands){
            if(iCommand.isParsable(cmd)){
                return iCommand;
            }
        }
        return null;
    }
    @Override
    public String parser(String command) {
        ICommand command1=findCommand(command);
        if(command1!=null){
            return command1.parser(command);
        }
        return null;
    }
    public void addCommand(ICommand iCommand){
        commands.add(iCommand);
    }
}
