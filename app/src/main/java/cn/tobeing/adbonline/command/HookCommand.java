package cn.tobeing.adbonline.command;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import cn.tobeing.adbonline.GlobalEnv;
import cn.tobeing.hook.IHookService;

/**
 * Created by sunzheng on 16/5/29.
 */
public class HookCommand extends AbsCommand{
    private Object bindLocker=new Object();

    private IHookService iHookService;

    private ServiceBinder serviceBinder;

    @Override
    public String getCommand() {
        return "hook";
    }

    @Override
    public String onCommand(String[] args) {
        return null;//not implements
    }

    @Override
    public String parser(String command) {
        String[] args = command.split("\\s+");
        if(args.length<2){
            return "argument misss:"+args;
        }
        if(args[1].equals("bind")){
            return bindService(args[2],args[3]);
        }
        try {
            if(checkServiceBind()) {
                return iHookService.onCommand(command);
            }else{
                return "service not binder";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "hook cmd error:"+command;
        }
    }
    public String bindService(String packageName,String serviceName) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, serviceName));
        if (serviceBinder == null) {
            serviceBinder = new ServiceBinder();
        }
        if (checkServiceBind()) {
            GlobalEnv.getContext().unbindService(serviceBinder);
        }
        boolean sucess = false;
        try{
            sucess = GlobalEnv.getContext().bindService(intent, serviceBinder, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (sucess) {
            GlobalEnv.getContext().startService(intent);
            return "bing...";
        }
        return "bing fail";
    }
    private boolean checkServiceBind(){
        return iHookService!=null;
    }
    private class ServiceBinder implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (bindLocker){
                try {
                    iHookService=IHookService.Stub.asInterface(service);
                    cmdExecute.notifyNewCmdInfo("service bind sucess");
                }catch (Exception e){
                    e.printStackTrace();
                    cmdExecute.notifyNewCmdInfo("service bind faile");
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            synchronized (bindLocker) {
                cmdExecute.notifyNewCmdInfo("service bind unbind");
            }
        }
    }
}
