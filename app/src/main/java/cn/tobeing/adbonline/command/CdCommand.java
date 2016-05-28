package cn.tobeing.adbonline.command;

import android.text.TextUtils;

import java.io.File;

import cn.tobeing.adbonline.CMDExecute;

/**
 * Created by sunzheng on 16/5/26.
 */
public class CdCommand extends AbsCommand{
    @Override
    public String getCommand() {
        return "cd";
    }
    @Override
    public String onCommand(String[] args, CMDExecute cmdExecute) {
        if(args.length<2){
            return "argument path is neeed";
        }
        PathInfo info=findNewPath(cmdExecute.getCurrentPath(),args[1],true);
        if(info.isSucess){
            cmdExecute.setCurrentPath(info.path);
        }
        return info.message;
    }

    public PathInfo findNewPath(String currentPath,String path,boolean isFirst){
        if(path.startsWith("./")){
            return findNewPath(currentPath,path.substring("./".length()),false);
        }else if(path.startsWith("../")){
            return findNewPath(new File(currentPath).getParent(),path.substring("../".length()),false);
        }else if(path.startsWith("/")&&!isFirst){
            return findNewPath(currentPath,path.substring("/".length()),false);
        }
        else {
            String errorMessage;
            File file;

            if(currentPath==null||path.startsWith("/")) {
                file=new File(path);
            }else{
                file = new File(currentPath, path);
            }
            if(!file.exists()){
                errorMessage=" path not exist";
            }else if(file.isDirectory()){
                return new PathInfo(file.getPath(),true,"");
            }else{
                errorMessage=" path is not dir";
            }
            return new PathInfo(currentPath,false,errorMessage);
        }
    }
    class PathInfo{

        public PathInfo(String path,boolean isSucess,String message){
            this.path=path;
            this.isSucess=isSucess;
            this.message=message;
        }
        public boolean isSucess;

        public String path;

        public String message;
    }
}
