package com.zhp.sdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zhp.sdk.activity.BaseActivity;
import com.zhp.sdk.widget.flowlayout.FlowLayout;
import com.zhp.sdk.widget.flowlayout.TagAdapter;
import com.zhp.sdk.widget.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by zhp.dts on 2017/8/8.
 */

public class TagFlowActivity extends BaseActivity {


    private TagFlowLayout tagFlowLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagflow);
        tagFlowLayout = (TagFlowLayout) findViewById(R.id.tag_flow_layout);
        List<String> listTag = new ArrayList();
        listTag.add("很不错");
        listTag.add("很不错了");
        listTag.add("JAVA");
        listTag.add("PHP");
        listTag.add("C/C++");
        listTag.add(".net");
        listTag.add("javascript");
        listTag.add("css3.0");

        TagAdapter<String> tagAdapter = new TagAdapter<String>(listTag) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView txt = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tag_txt, tagFlowLayout, false);
                txt.setText(s);
                return txt;
            }
        };
        tagFlowLayout.setAdapter(tagAdapter);
    }
}
