package com.zhp.sdkdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhp.sdk.activity.BaseActivity;
import com.zhp.sdk.utils.MobileUtils;
import com.zhp.sdkdemo.service.RecordCallService;

/**
 * 通话时录音测试
 * Created by zhp.dts on 2017/3/3.
 */

public class CallingRecordActivity extends BaseActivity {

    private Button btnGoCall;
    private EditText edtNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_record);
        btnGoCall = (Button) findViewById(R.id.btn_to_call);
        edtNumber = (EditText) findViewById(R.id.edt_calling_num);
        btnGoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MobileUtils.call(edtNumber.getText().toString())) {
                    RecordCallService.start(edtNumber.getText().toString(), "");
                }
            }
        });
    }
}
