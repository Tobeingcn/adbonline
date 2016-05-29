package cn.tobeing.adbonline;

import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sunzheng on 16/5/29.
 */
public class TestSingleton {

    private static String TAG="TAG_TestSingleton";

    private String testHello="hello";

    private String testHello2="hello2";

    private String testHello3="hello3";
    private String[] testStringArrays=new String[]{"hello","world","ttt"};

    private List<String> testStringList= Arrays.asList(new String[]{"hello","world","ttt"});

    private Map<String,UUID> testMap=new Hashtable<>();
    {
        testMap.put("1",UUID.randomUUID());
        testMap.put("2",UUID.randomUUID());
        testMap.put("3",UUID.randomUUID());
    }

    private static TestSingleton instance=new TestSingleton();

    public synchronized static  TestSingleton getInstance(){
        return instance;
    }

    private String getHelloWorld(){
        return "hello world";
    }

    private UUID testUUID=UUID.randomUUID();

    private UUID getTestUUID(){
        return UUID.randomUUID();
    }

}
