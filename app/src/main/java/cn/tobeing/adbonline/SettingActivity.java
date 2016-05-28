package cn.tobeing.adbonline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sunzheng on 16/5/28.
 */
public class SettingActivity extends AppCompatActivity{
    EditText etFrom,etTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        etFrom= (EditText) findViewById(R.id.etFrom);
        etTo= (EditText) findViewById(R.id.etTo);
        OnlineManager.getInstance().init(this);
        etFrom.setText(OnlineManager.getInstance().getFrom());
        etTo.setText(OnlineManager.getInstance().getTo());
        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlineManager.getInstance().setFrom(etFrom.getText().toString());
                OnlineManager.getInstance().setTo(etTo.getText().toString());
                finish();
            }
        });
        findViewById(R.id.btnDisConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlineManager.getInstance().setFrom("");
                OnlineManager.getInstance().setTo("");
                etFrom.setText("");
                etTo.setText("");
                finish();
            }
        });
    }
}
