package com.zhp.sdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.zhp.sdk.RunOnUI;
import com.zhp.sdk.activity.BaseActivity;
import com.zhp.sdk.pulltofresh.PullToRefreshBase;
import com.zhp.sdk.pulltofresh.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by zhp on 2016/11/30.
 */

public class PullToFreshListActivity extends BaseActivity implements PullToRefreshBase.OnPullEventListener {
    public final static String EXTRA_TEST = "extra_test";
    private ArrayList<String> lists = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String test = (String) getExtraParam(EXTRA_TEST);
        Log.d("PullToFreshListActivity", test);
        setContentView(R.layout.activity_pulltofresh);
        PullToRefreshListView listView = (PullToRefreshListView) findViewById(R.id.pulltofresh_listv);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getTestData()));
//        listView.setOnPullEventListener(this);
        initLitsitems(listView);
    }

    private ArrayList<String> getTestData() {
        int beginIndex = lists.size();
        for (int i = 1; i < 20; i++) {
            lists.add("测试index=" + (i + beginIndex));
        }
        return lists;
    }

    private void initLitsitems(final PullToRefreshListView listView) {
        listView.setMode(PullToRefreshBase.Mode.BOTH);
//        listView.showInvisibleViews();
//        PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY;
//        PullToRefreshBase.Mode.PULL_FROM_END;
//        PullToRefreshBase.Mode.PULL_FROM_START;
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(PullToFreshListActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                Log.d("pulltorefresh", "freshListener - onRefresh");
            }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {


                Log.d("pulltorefresh", "freshListener2 - onPullDownToRefresh(顶部刷新)");
                RunOnUI.runOnUIDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        //重建adapter，重新加载

                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.d("pulltorefresh", "freshListener2 - onPullUpToRefresh（底部加载）");
                RunOnUI.runOnUIDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        //添加数据，notifyChangedData();
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onPullEvent(PullToRefreshBase refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
        Log.d("pulltorefresh", "state = " + state.name() + ",  mode = " + direction.name());
    }
}
