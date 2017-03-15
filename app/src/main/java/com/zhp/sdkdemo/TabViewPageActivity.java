package com.zhp.sdkdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhp.sdk.activity.BaseActivity;

import java.util.ArrayList;


/**
 * Created by zhp on 2016/11/30.
 */

public class TabViewPageActivity extends BaseActivity {
    private View tab_1, tab_2, tab_3, tab_4, tab_5;
    private TabLayout tabLayout;
    private ViewPager viewPage;
    private TabPageAdapter tabPageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_viewpage);
        tabLayout = (TabLayout) findViewById(R.id.tablelayout);
        viewPage = (ViewPager) findViewById(R.id.viewpage);

        if (viewPage != null) {

            tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
            viewPage.setOffscreenPageLimit(5);

        }
        initViews();

    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        tab_1 = inflater.inflate(R.layout.layout_view_page, null);
        tab_2 = inflater.inflate(R.layout.layout_view_page, null);
        tab_3 = inflater.inflate(R.layout.layout_view_page, null);
        tab_4 = inflater.inflate(R.layout.layout_view_page, null);
        tab_5 = inflater.inflate(R.layout.layout_view_page, null);

        ((TextView) tab_1.findViewById(R.id.content_txt)).setText("tab1");
        ((TextView) tab_2.findViewById(R.id.content_txt)).setText("tab2");
        ((TextView) tab_3.findViewById(R.id.content_txt)).setText("tab3");
        ((TextView) tab_4.findViewById(R.id.content_txt)).setText("tab4");
        ((TextView) tab_5.findViewById(R.id.content_txt)).setText("tab5");
        tabPageAdapter = new TabPageAdapter();


        tabPageAdapter.addPage(tab_1, "测试1");
        tabPageAdapter.addPage(tab_2, "测试2");
        tabPageAdapter.addPage(tab_3, "测试3");
        tabPageAdapter.addPage(tab_4, "测试4");
        tabPageAdapter.addPage(tab_5, "测试5");

        viewPage.setAdapter(tabPageAdapter);
        tabLayout.setupWithViewPager(viewPage);
        viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                /*
                    position :当前页面，及你点击滑动的页面
                    positionOffset:当前页面偏移的百分比
                    positionOffsetPixels:当前页面偏移的像素位置
                */


            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    class TabPageAdapter extends PagerAdapter {

        private ArrayList<View> pages = new ArrayList<>();
        private ArrayList<String> titles = new ArrayList<>();

        public void addPage(View page, String title) {
            pages.add(page);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            container.addView(pages.get(position), position);
            return pages.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pages.get(position));
        }
    }
}
