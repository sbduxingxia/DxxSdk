package com.zhp.sdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhp.sdk.Tip;
import com.zhp.sdk.activity.BaseActivity;
import com.zhp.sdk.activity.WebActivity;
import com.zhp.sdk.loading.ILoadingInfo;
import com.zhp.sdk.loading.ILoadingListener;
import com.zhp.sdk.loading.LoadingManager;
import com.zhp.sdk.update.IUpdateInfo;
import com.zhp.sdk.update.IUpdateListener;
import com.zhp.sdk.update.UpdateManager;
import com.zhp.sdk.widget.popup.ISpannerText;
import com.zhp.sdk.widget.popup.ListViewPopup;
import com.zhp.sdk.widget.popup.SpnnerWheelPopup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    protected LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout ll_box = (LinearLayout) findViewById(R.id.ll_box);
        for (int i = 0; i < ll_box.getChildCount(); i++) {
            ll_box.getChildAt(i).setOnClickListener(this);
        }
        initUpdateDialog();
        rootView = (LinearLayout) findViewById(R.id.activity_main);

    }

    private void initUpdateDialog() {
        UpdateManager.instance().bind(new IUpdateListener() {
            @Override
            public void updateSuccess() {

            }

            @Override
            public void updateError(int code, String msg) {

            }

            @Override
            public void updateDestroy() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.diy_flow) {
            gotoIntent(TagFlowActivity.class);
        } else if (v.getId() == R.id.open_web) {
            gotoIntent(WebActivity.createIntent(this, "file:///android_asset/jsplus.html", "js插件测试"));
        } else if (v.getId() == R.id.audio_steal) {
            gotoIntent(CallingRecordActivity.class);
        } else if (v.getId() == R.id.tab_viewpage) {
            gotoIntent(TabViewPageActivity.class);
        } else if (v.getId() == R.id.pull_to_refresh) {
            HashMap<String, Serializable> params = new HashMap<>();
            params.put(PullToFreshListActivity.EXTRA_TEST, "test");
            gotoIntent(PullToFreshListActivity.class, params);
        } else if (v.getId() == R.id.tip) {
            Tip.show("tip nomarl");
        } else if (v.getId() == R.id.tip_center) {
            Tip.showCenter("tip center");
        } else if (v.getId() == R.id.loading_open) {

            LoadingManager.instance().bind(new ILoadingListener() {
                @Override
                public void loadingStart() {
                    Log.d(TAG, "iLoadingListener -> loadingStart");
                }

                @Override
                public void loadingEnd() {
                    Log.d(TAG, "iLoadingListener -> loadingEnd");
                }
            });
            LoadingManager.instance().show(new ILoadingInfo() {
                @Override
                public String getLoadingTips() {
                    return "努力中";
                }

                @Override
                public Activity getActivity() {
                    return MainActivity.this;
                }

                @Override
                public boolean isBackPressClose() {
                    return true;
                }


            });

        } else if (v.getId() == R.id.update_open) {//打开升级窗口
//            Tip.showCenter("有新版本");
            UpdateManager.instance().show(new IUpdateInfo() {


                @Override
                public Activity getActivity() {
                    return MainActivity.this;
                }

                @Override
                public boolean isBackPressClose() {
                    return false;
                }

                @Override
                public String getId() {
                    return "abc";
                }

                @Override
                public String getDownUrls() {
                    return "http://gdown.baidu.com/data/wisegame/2e55c46cd6d3ab4a/jinritoutiao_589.apk";
                }

                @Override
                public String getAppVersion() {
                    return null;
                }

                @Override
                public Integer getAppCode() {
                    return null;
                }

                @Override
                public ArrayList<String> getHosts() {
                    return null;
                }

                @Override
                public Long getLastPublishTime() {
                    return null;
                }

                @Override
                public boolean isForceUpdate() {
                    return false;
                }

                @Override
                public Integer getMinCode() {
                    return null;
                }

                @Override
                public String getTitle() {
                    return "升级提醒";
                }

                @Override
                public String getContentTips() {
                    return "有新版本需要更新！";
                }

                @Override
                public String getAppName() {
                    return "ihaier4.6.4.apk";
                }
            });
        } else if (v.getId() == R.id.spnner_wheel) {
            final SpnnerWheelPopup popup = new SpnnerWheelPopup(this, rootView);


            final String cities[][] = new String[][]{
                    new String[]{"A", "B", "C", "D", "E", "F", "G", "H"},
                    new String[]{"New York", "Washington", "Chicago", "Atlanta", "Orlando", "Los Angeles", "Houston", "New Orleans"},
                    new String[]{"Ottawa", "Vancouver", "Toronto", "Windsor", "Montreal", "Calgary", "Winnipeg", "Edmonton"},
                    new String[]{"Kyiv", "Simferopol", "Lviv", "Kharkiv", "Odessa", "Mariupol", "Lugansk", "Sevastopol"}
            };

            SpnnerWheelPopup.OnOptionChangeListener optionChangeListener = new SpnnerWheelPopup.OnOptionChangeListener() {
                @Override
                public void optionSelected(int position, int selectedIndex) {
                    if (position + 1 < cities.length) {
                        popup.setDataList(position + 1, initWheelList(cities[position + 1]));//默认选中 index:0
                    }
                }

                @Override
                public void optionResultSave(int[] optionValues) {
                    String str = "";
                    for (int i = 0; i < optionValues.length; i++) {
                        str += String.valueOf(optionValues[i]);
                    }
                    Log.d(TAG, "wheel result : " + str);
                }
            };
            popup.setOptionChangeListener(optionChangeListener);
            popup.setDataList(0, initWheelList(cities[0]), 4);//默认选中 index:4
            popup.show();

        } else if (v.getId() == R.id.list_popup) {
            ListViewPopup listViewPopup = new ListViewPopup(getApplicationContext(), rootView);
            final String[] listName = {"ABC", "CDE"};//{"ABC","CDE","FGH","IGK","NTN","BMS","ABC","CDE","FGH","IGK","NTN","BMS","ABC","CDE","FGH","IGK","NTN","BMS"};
            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return listName.length;
                }

                @Override
                public Object getItem(int position) {
                    return listName[position];
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView txt = (TextView) LayoutInflater.from(getApplicationContext()).inflate(android.R.layout.simple_expandable_list_item_1, null);
                    txt.setText(listName[position]);
                    return txt;
                }
            };
            listViewPopup.setAdapter(baseAdapter, 40);
            listViewPopup.setTitle("字符串显示");
            listViewPopup.show();
        } else if (v.getId() == R.id.diy_view) {
            gotoIntent(DiyViewActivity.class);
        }
    }

    public ArrayList<ISpannerText> initWheelList(String[] datas) {
        ArrayList<ISpannerText> result = new ArrayList<ISpannerText>();
        for (int i = 0; i < datas.length; i++) {
            result.add(new WheelItemData(datas[i]));
        }
        return result;
    }

    public class WheelItemData implements ISpannerText {

        private String name;

        public WheelItemData(String name) {
            this.name = name;
        }

        @Override
        public CharSequence getText() {
            return name;
        }
    }
}
