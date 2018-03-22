package com.zhp.sdk.activity;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhp.sdk.toolbar.ToolBarHelper;
import com.zhp.sdk.R;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhp on 2016/11/29.
 */

public class BaseActivity extends AppCompatActivity implements IActivityScreen {

    private ToolBarHelper mToolBarHelper;
    private Toolbar toolbar;
    private TextView centerTitle;
    private TextView titleRightTxt;
    private ImageView titleImage;

    @Override
    public boolean skipInvokeBaseActivity() {
        return false;
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (skipInvokeBaseActivity()) {
            return;
        }
//        super.setContentView(layoutResID);
        mToolBarHelper = new ToolBarHelper(this,layoutResID);
        toolbar = mToolBarHelper.getToolBar();
        setContentView(mToolBarHelper.getContentView());
        /**
         * 将toolbar添加到activity上
         */
        setSupportActionBar(toolbar);
        /*自定义的一些操作*/
        onCreateCustomToolBar(toolbar);
        /*隐藏toolbar左侧的返回按钮*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * 处理自定义的toolbar布局及其控件
     * @param toolbar
     */
    private void onCreateCustomToolBar(Toolbar toolbar){
        toolbar.showOverflowMenu();
        getLayoutInflater().inflate(R.layout.zp_layout_toobar_title,toolbar);
        centerTitle = (TextView) toolbar.findViewById(R.id.titleCenter);
        titleRightTxt = (TextView) toolbar.findViewById(R.id.titleRightTV);
        titleImage = (ImageView) toolbar.findViewById(R.id.titleImage);
        toolbar.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void setTitle(CharSequence title) {
        if(centerTitle!=null){
            centerTitle.setText(title);
        }
        super.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        if(centerTitle!=null){
            centerTitle.setText(titleId);
        }
        super.setTitle(titleId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取intent间传递的参数
     * @param key
     * @return
     */
    public Serializable getExtraParam(String key){
        return getIntent().getSerializableExtra(key);
    }
    public void gotoIntent(Intent intent){
        if(intent!=null){
            startActivity(intent);
        }
    }
    /**
     * 启动新的activity
     * @param activityClass
     */
    public void gotoIntent(Class activityClass){
        Intent i = new Intent(getApplicationContext(),activityClass);
        startActivity(i);
    }
    /**
     * 启动新的activity
     * @param activityClass
     * @param paramMap
     */
    public void gotoIntent(Class activityClass, HashMap<String ,Serializable> paramMap){
        Intent i = new Intent(getApplicationContext(),activityClass);
        if(paramMap!=null){
            Iterator<Map.Entry<String,Serializable>> iterator = paramMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,Serializable> entry = iterator.next();
                i.putExtra(entry.getKey(),entry.getValue());
            }
        }
        startActivity(i);
    }
    public TextView getTitleRightTxt() {
        return titleRightTxt;
    }

    public ImageView getTitleImage() {
        return titleImage;
    }

}
