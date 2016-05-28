package cn.tobeing.adbonline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.tobeing.adbonline.command.MessageCommand;

public class MainActivity extends AppCompatActivity implements OnlineManager.OnMessageListener{

    private CheckBox cbAuto;

    private Button btnConfirm;

    private EditText etCmd;

    private ListView lvMessage;

    private MessageAdapter messageAdapter;

    CMDExecute cmdExecute=new CMDExecute();

    private CommandParser autoCommands;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnlineManager.getInstance().init(this);
        OnlineManager.getInstance().setOnMessageListener(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        autoCommands=new CommandParser();
        autoCommands.addCommand(new MessageCommand());

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        HandlerThread thread=new HandlerThread("t");
        thread.start();
        mWorkHandler=new WorkHandler(thread.getLooper());
        cbAuto=$(R.id.cbAuto);
        btnConfirm=$(R.id.button_confirm);
        etCmd=$(R.id.etCommmand);
        etCmd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnConfirm.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cbAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnConfirm.setEnabled(!isChecked&& TextUtils.isEmpty(etCmd.getText().toString()));
                etCmd.setEnabled(!isChecked);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> cmds=new ArrayList<String>();
                cmds.add(etCmd.getText().toString());
                mWorkHandler.obtainMessage(WorkHandler.MSG_EXECUTE_CMD,cmds).sendToTarget();
                etCmd.setText("");
            }
        });
        cbAuto.setChecked(true);

        lvMessage=$(R.id.lvMessage);
        messageAdapter=new MessageAdapter(this);
        lvMessage.setAdapter(messageAdapter);
        lvMessage.smoothScrollToPosition(messageAdapter.getCount()-1);
        etCmd.setText("已启动，欢迎使用");
        btnConfirm.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
    private void refresh(){
        if(TextUtils.isEmpty(OnlineManager.getInstance().getTo())||TextUtils.isEmpty(OnlineManager.getInstance().getTo())){
            setTitle("未连接，请点击设置连接");
        }else{
            setTitle(OnlineManager.getInstance().getFrom()+"正在与"+OnlineManager.getInstance().getTo()+"通讯");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_clear:{
                messageAdapter.clear();
                messageAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.action_connection:{
                startActivity(new Intent(this,SettingActivity.class));
            }
            break;
            case R.id.action_disconnection:{
                OnlineManager.getInstance().setFrom("");
                OnlineManager.getInstance().setTo("");
                refresh();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    private static final int MSG_APPEND_MESSAGE=0x0001;

    private static final int MSG_NEW_COMMAND=0x002;

    private WorkHandler mWorkHandler;

    private Handler UIHandler=new Handler(){


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case MSG_APPEND_MESSAGE:{
                if(msg.obj!=null){
                    List<CmdMsg> cmdMsgs=(List<CmdMsg>) msg.obj;
                    for (CmdMsg cmdMsg:cmdMsgs){
                        OnlineManager.getInstance().sendMessage(cmdMsg.toString());
                    }
                    messageAdapter.appendMessage((List<CmdMsg>) msg.obj);
                    messageAdapter.notifyDataSetChanged();
                    lvMessage.smoothScrollToPosition(messageAdapter.getCount());
                }
            }
            break;
            case MSG_NEW_COMMAND:{
                String com= (String) msg.obj;
                etCmd.setText(com);
                if(cbAuto.isChecked()||autoCommands.isParsable(com)){
                    btnConfirm.performClick();
                }
            }
            break;
        }
    }
};

    @Override
    public void onMessage(String message) {
        UIHandler.obtainMessage(MSG_NEW_COMMAND,message).sendToTarget();
    }

    private class WorkHandler extends Handler {
        public static final int MSG_EXECUTE_CMD=0x1001;
        public WorkHandler(Looper looper){
        super(looper);
    }
    @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_EXECUTE_CMD: {
                    List<CmdMsg> command=new ArrayList<>();
                    List<String> cmds = (List<String>) msg.obj;
                    for (String cmd:cmds){
                        String path=cmdExecute.getCurrentPath();
                        String message=cmdExecute.run(cmd);
                        command.add(new CmdMsg().setCmd(cmd).setMessage(message).setPath(path));
                    }
                    UIHandler.obtainMessage(MSG_APPEND_MESSAGE,command).sendToTarget();
                }
            }
        }
    }
    public <T> T $(int resId){
        View view =findViewById(resId);
        return (T)view;
    }
}
