package cn.tobeing.hook;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sunzheng on 16/5/29.
 */
public class HookServerImpl extends IHookService.Stub{

    private Context mContext;

    public void init(Context context){
        this.mContext=context;
    }
    private List<Object> cacheList= Collections.synchronizedList(new ArrayList<>());

    public static HookServerImpl instance=new HookServerImpl();

    public static HookServerImpl getInstance(){
        return instance;
    }
    @Override
    public String onCommand(String command) throws RemoteException {
        String[] args=command.split("\\s+");
        if(args.length<2){
            return "argument miss";
        }
        String subCmd=args[1];
        try {
            if (subCmd.equals("staticMethod")) {
                return staticMedthod(args);
            } else if (subCmd.equals("staticField")) {
                return staticField(args);
            } else if (subCmd.equals("method")) {
                return method(args);
            }else if(subCmd.equals("field")){
                return field(args);
            }else if(subCmd.equals("current")){
                return current(args);
            }else if(subCmd.equals("cache")){
                return cache(args);
            }
            else {
                return "command not support";
            }
        }catch (Exception e){
            e.printStackTrace();
            return e.toString();
        }
    }
    private String staticMedthod(String[] args){
        if(args.length<4){
            return "argument miss";
        }
        boolean cache= Arrays.asList(args).contains("-cache");
        try {
            Class<?> cls = Class.forName(args[2]);

            Method method=cls.getDeclaredMethod(args[3]);
            method.setAccessible(true);
            Object object=method.invoke(null);
            if(object!=null){
                if(cache){
                    cacheList.add(object);
                }
                return "staticMedthod:sucess:"+object.getClass().getName()+":"+object;
            }
            return "staticMedthod:sucess:"+method.getName()+":return null";
        }catch (Exception e){
            e.printStackTrace();
            return "error:"+e.toString();
        }
    }
    private String staticField(String[] args){
        if(args.length<4){
            return "argument miss";
        }
        boolean cache= Arrays.asList(args).contains("-cache");
        try{
            Class<?> cls = Class.forName(args[2]);

            Field field=cls.getDeclaredField(args[3]);
            field.setAccessible(true);
            Object object=field.get(null);
            if(cache){
                cacheList.add(object);
            }
            return "staticField:sucess:"+object.getClass().getName()+object;
        }catch (Exception e){
            e.printStackTrace();
            return "error:"+e.toString();
        }
    }
    private String method(String args[]){
        if(args.length<3){
            return "argument miss";
        }
        if(cacheList.size()<1){
            return "no object cached,please cache one object by static way first";
        }
        boolean cache= Arrays.asList(args).contains("-cache");

        try {
            Object object = cacheList.get(cacheList.size() - 1);
            Method method = object.getClass().getDeclaredMethod(args[2]);
            method.setAccessible(true);
            Object result=method.invoke(object);
            if(result!=null){
                if(cache){
                    cacheList.add(result);
                }
                return "method:sucess:"+result.getClass().getName()+":"+result;
            }
            return "method:sucess:"+method.getName()+":return null";
        }catch (Exception e){
            return e.toString();
        }
    }
    private String field(String args[]){
        if(args.length<3){
            return "argument miss";
        }
        if(cacheList.size()<1){
            return "no object cached,please cache one object by static way first";
        }
        boolean cache= Arrays.asList(args).contains("-cache");
        try {
            Object object = cacheList.get(cacheList.size() - 1);
            Field field=object.getClass().getDeclaredField(args[2]);
            field.setAccessible(true);
            Object result=field.get(object);
            if(cache){
                cacheList.add(result);
            }
            return "field:sucess:"+result;
        }catch (Exception e) {
            return e.toString();
        }
    }
    private String current(String[] args) {
        if (cacheList.size() < 1) {
            return "no object cached,please cache one object by static way first";
        }
        try {
            StringBuilder sb = new StringBuilder();
            Object object = cacheList.get(cacheList.size() - 1);
            Class<?> cls = object.getClass();
            Field[] fields = object.getClass().getDeclaredFields();
            sb.append("currentObject:[");
            sb.append(cls.getName());
            sb.append("]\n{\n");
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);
                sb.append(field.getName());
                sb.append("[");
                sb.append(value.getClass().getName());
                sb.append("]=");
                sb.append(value);
                sb.append(",\n");
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }
    private String cache(String[] args){
        if(args.length<3){
            return "argument miss";
        }
        if(cacheList.size()<1){
            return "no object cached,please cache one object by static way first";
        }

        String thirCmd=args[2];

        if(thirCmd.equals("pop")){
            Object object=cacheList.get(cacheList.size()-1);
            return "hook pop object:"+object.getClass().getName()+":"+object;
        }else if(thirCmd.equals("list")){
            StringBuilder sb=new StringBuilder();
            sb.append("hook list:{\n");
            for (Object object:cacheList){
                sb.append(object);
                sb.append("[");
                sb.append(object.getClass().getName());
                sb.append("],\n");
            }
            sb.append("}");
            return sb.toString();
        }else if(thirCmd.equals("clear")){
            StringBuilder sb=new StringBuilder();
            sb.append("hook list:{\n");
            for (Object object:cacheList){
                sb.append(object.getClass().getName());
                sb.append(object);
                sb.append(",\n");
            }
            sb.append("}");
            cacheList.clear();
            return sb.toString();
        }else{
            return "method not support";
        }
    }
}
