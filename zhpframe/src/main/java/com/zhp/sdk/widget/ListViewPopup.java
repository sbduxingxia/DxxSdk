package com.zhp.sdk.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhp.sdk.R;
import com.zhp.sdk.utils.Common;

/**
 * Created by zhp.dts on 2017/1/6.
 * IListViewPopupResult 不设置时，保存按钮默认关闭窗口
 */

public class ListViewPopup extends PopupWindow implements View.OnClickListener {

    protected Context mContext;
    protected View mParent,rootView;
    protected Button saveBtn;
    protected TextView titleTxt;
    protected ListView contentlstV;
    protected LinearLayout contentLnly;
    protected final int MaxHeight=210;

    protected IListViewPopupResult iListViewPopupResult;
    public ListViewPopup(Context context, View parent){
        mContext = context;
        mParent = parent;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);//必须设置宽度
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);//必须设置高度
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);// 这样设置才能点击返回建关闭
        this.setFocusable(true);//添加后方可返回建关闭
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.zhp_popwindow_anim_style);
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.zp_widget_list_popup,null);
        saveBtn = (Button) rootView.findViewById(R.id.list_pop_btn_save);
        titleTxt = (TextView) rootView.findViewById(R.id.list_pop_txt_title);
        contentlstV = (ListView) rootView.findViewById(R.id.list_pop_listv);
        contentLnly = (LinearLayout) rootView.findViewById(R.id.list_pop_content_lnly);
        saveBtn.setOnClickListener(this);
        this.setContentView(rootView);
    }
    /**
     * 显示弹窗
     */
    public void show() {
        this.showAtLocation(mParent, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    /**
     * 设置listview控件的adapter及其item的dp值
     * @param mBaseAdapter
     */
    public void setAdapter(BaseAdapter mBaseAdapter,int itemDpHeight){
        if(contentlstV!=null){
            if(itemDpHeight>0){
                int cHeight = mBaseAdapter.getCount()*itemDpHeight;
                if(cHeight<MaxHeight){
                    contentLnly.getLayoutParams().height=Common.toPx(mContext,cHeight);
                }
            }
            mBaseAdapter.getCount();
            contentlstV.setAdapter(mBaseAdapter);
        }
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        if(titleTxt!=null){
            titleTxt.setText(title);
        }
    }

    /**
     * 添加listview的onItemCLiclListner
     * @param itemClickListener
     */
    public void addItemClickListener(AdapterView.OnItemClickListener itemClickListener){
        if(contentlstV!=null){
            contentlstV.setOnItemClickListener(itemClickListener);
        }
    }
    public void setiListViewPopupResult(IListViewPopupResult iListViewPopupResult) {
        this.iListViewPopupResult = iListViewPopupResult;
    }

    @Override
    public void onClick(View v) {
        if(v==saveBtn){
            if(iListViewPopupResult!=null&&!iListViewPopupResult.saveToNext()){
                return;
            }
            dismiss();
        }
    }
    public interface IListViewPopupResult{
        /**
         * 是否可以保存并退出popup window
         * true:退出;
         * false:不能退出
         * @return
         */
        boolean saveToNext();
    }

}
