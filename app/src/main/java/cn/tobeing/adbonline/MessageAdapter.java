package cn.tobeing.adbonline;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunzheng on 16/5/26.
 */
public class MessageAdapter extends BaseAdapter{

    private List<CmdMsg> msgList=new ArrayList<>();

    private Context mContext;

    public MessageAdapter(Context context){

        this.mContext=context;
    }

    public List<CmdMsg> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<CmdMsg> msgList) {
        if(this.msgList==msgList){
            return;
        }
        msgList.clear();
        if(msgList!=null) {
            msgList.addAll(msgList);
        }
    }
    public void appendMessage(List<CmdMsg> msgs){
        if(msgs!=null){
            msgList.addAll(msgs);
        }
    }
    public void clear(){
        msgList.clear();
    }
    @Override
    public int getCount() {
        return msgList==null?0:msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView= (TextView) convertView;
        if(textView==null){
            textView=new TextView(mContext);
        }
        CmdMsg cmd= (CmdMsg) getItem(position);
        textView.setText(cmd.toString());
        return textView;
    }
}
