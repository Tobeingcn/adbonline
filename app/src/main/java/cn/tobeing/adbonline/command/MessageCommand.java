package cn.tobeing.adbonline.command;

import android.os.PatternMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.tobeing.adbonline.CMDExecute;

/**
 * Created by sunzheng on 16/5/28.
 */
public class MessageCommand implements ICommand{
    @Override
    public ICommand setCMDExecute(CMDExecute cmdExecute) {
        return this;
    }

    public boolean isAllNumber(String path) {
        String reg = "^\\d+$";
        Pattern allNumberPattern = Pattern.compile(reg);
        Matcher matcher = allNumberPattern.matcher(path);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
    public boolean isMessage(String path){
        if (path.contains(" ")) {
            path = path.substring(0, path.indexOf(" "));
        }
        String reg2 = "^[a-zA-Z][a-zA-Z0-9_]*$";
        Pattern pattern2 = Pattern.compile(reg2);
        Matcher matcher2 = pattern2.matcher(path);
        if (matcher2.matches()) {
            return false;
        } else {
            return true;
        }
    }
    @Override
    public boolean isParsable(String command) {
        if(isAllNumber(command)||isMessage(command)){
            return true;
        }
        return false;
    }

    @Override
    public String parser(String command) {
        return "";
    }
}
