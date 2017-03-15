package com.zhp.sdk.widget.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.view.View;
import android.widget.TextView;

import com.zhp.sdk.spinnerwheel.AbstractWheel;
import com.zhp.sdk.spinnerwheel.OnWheelChangedListener;
import com.zhp.sdk.spinnerwheel.OnWheelScrollListener;
import com.zhp.sdk.spinnerwheel.WheelVerticalView;
import com.zhp.sdk.R;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * 四个wheel的选择弹窗
 * 0：20dp，标示作用
 * 1-3：均分，分类项
 * Created by zhp on 2016/12/27.
 */

public class SpnnerWheelPopup extends PopupWindow implements View.OnClickListener,OnWheelScrollListener,OnWheelChangedListener {
    private final String TAG = getClass().getSimpleName();
    protected View rootView;
    protected Button saveBtn;
    protected TextView titleTxt;
    protected ArrayList<WheelVerticalView> wheelList=new ArrayList<WheelVerticalView>();
    protected HashMap<Integer,ArrayList<ISpannerText>> dataMap = new HashMap<>();
//    protected HashMap<Integer,Integer> isScrolling=new HashMap<>();
    protected HashMap<Integer,Integer> optionSeelectValue=new HashMap<>();
    protected int[] wheelIds ={R.id.spnner_wheel_index_whv,R.id.spnner_wheel_option_whv1,R.id.spnner_wheel_option_whv2,R.id.spnner_wheel_option_whv3};
    protected OnOptionChangeListener optionChangeListener;
    protected Context mContext;
    protected View mParent;
    public SpnnerWheelPopup(Context context,View parent){
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
        rootView = inflater.inflate(R.layout.zp_widget_spnnerwheel,null);
//        rootView.setOnClickListener(this);
        saveBtn =(Button)rootView.findViewById(R.id.spnner_wheel_btn_save);
        saveBtn.setOnClickListener(this);
        titleTxt = (TextView) rootView.findViewById(R.id.spnner_wheel_txt_title);
        this.setOutsideTouchable(true);
        for(int i =0;i<wheelIds.length;i++){
            WheelVerticalView wheelView = (WheelVerticalView)rootView.findViewById(wheelIds[i]);
            wheelView.setTag(i);
            wheelView.addChangingListener(this);
            wheelView.addScrollingListener(this);
            optionSeelectValue.put(i,-1);
            dataMap.put(i,new ArrayList<ISpannerText>());
            wheelList.add(wheelView);

        }
        //设置界面
        this.setContentView(rootView);

    }
    public void setTitle(String title){
        if(titleTxt!=null){
            titleTxt.setText(title);
        }
    }
    public void setTitle(int titleId){
        if(titleTxt!=null){
            titleTxt.setText(titleId);
        }
    }

    /**
     * 显示弹窗
     */
    public void show() {
        this.showAtLocation(mParent, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    /**
     * 设置选择框当前位置
     * @param position
     * @param index
     */
    public void setCurrentItem(int position,int index){

    }
    /**
     * 添加监听
     * @param optionChangeListener
     */
    public void setOptionChangeListener(OnOptionChangeListener optionChangeListener){
        this.optionChangeListener=optionChangeListener;
    }

    /**
     * 设置数据源
     * @param position
     * @param dataList
     */
    public void setDataList(Integer position,ArrayList<ISpannerText> dataList){
        if(position<wheelIds.length&&position>=0){
            dataMap.put(position,dataList);
            freshWheelView(position);
        }else{
            throw new IndexOutOfBoundsException();
        }

    }

    /**
     * 设置数据源并指定默认选择项
     * @param position wheel控件的位置
     * @param dataList 数据源list
     * @param index 选中list中的数据项位置
     */
    public void setDataList(Integer position,ArrayList<ISpannerText> dataList,Integer index){
        if(index<0&&index>=dataList.size()){
            setDataList(position,dataList);
            return;
        }
        if(position<wheelIds.length&&position>=0&&index<dataList.size()){
            dataMap.put(position,dataList);
            freshWheelView(position,index);
        }else{
            throw new IndexOutOfBoundsException();
        }

    }

    /**
     * 刷新ui
     * @param position
     */
    synchronized protected void freshWheelView(int position){
        if(position<wheelList.size()&&position>=0){
            wheelList.get(position).setViewAdapter(new SpnnerWheelAdapter(mContext,dataMap.get(position)));
            wheelList.get(position).setCurrentItem(0);
            optionSeelectValue.put(position,0);
            if(optionChangeListener!=null){
                optionChangeListener.optionSelected(position,0);
            }
            wheelList.get(position).setVisibility(View.VISIBLE);

        }
    }
    /**
     * 刷新ui
     * @param position
     */
    synchronized protected void freshWheelView(int position,int index){
        if(position<wheelList.size()&&position>=0){
            wheelList.get(position).setViewAdapter(new SpnnerWheelAdapter(mContext,dataMap.get(position)));
            wheelList.get(position).setCurrentItem(index);
            optionSeelectValue.put(position,index);
            if(optionChangeListener!=null){
                optionChangeListener.optionSelected(position,index);
            }
            wheelList.get(position).setVisibility(View.VISIBLE);

        }
    }
    @Override
    public void onClick(View v) {
        if(v==saveBtn){
            if(optionChangeListener!=null){
                int[] optionValues = new int[wheelIds.length];
                for(int i=0;i<optionValues.length;i++){
                    optionValues[i]=optionSeelectValue.get(i);
                }
                optionChangeListener.optionResultSave(optionValues);
            }
            dismiss();
        }else{
            dismiss();
        }
    }

    @Override
    public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
        int index = (int) wheel.getTag();
//        Log.e(TAG,"scrolling onChanged:"+index);
        optionSeelectValue.put(index,newValue);

    }

    @Override
    public void onScrollingStarted(AbstractWheel wheel) {
        int index = (int) wheel.getTag();
//        Log.e(TAG,"scrolling started:"+index);
        if(optionChangeListener!=null) {
            for (int i = index + 1; i < wheelList.size(); i++) {
                wheelList.get(i).setVisibility(View.INVISIBLE);
//                wheelList.get(i).setViewAdapter(null);
                optionSeelectValue.put(i, -1);
            }
        }

    }

    @Override
    public void onScrollingFinished(AbstractWheel wheel) {
        final int index = (int) wheel.getTag();
        final int selectIndex = optionSeelectValue.get(index);
        if(selectIndex>=0){
            optionSeelectValue.put(index,selectIndex);
            //提示发生变化
            if(optionChangeListener!=null){
                optionChangeListener.optionSelected(index,selectIndex);
            }
        }
//        Log.e(TAG,"scrolling finished:"+index);

    }



    public interface OnOptionChangeListener{
        /**
         * 选择过程中的数据变化输出
         * @param position
         * @param selectedIndex
         */
        void optionSelected(int position,int selectedIndex);
        /**
         * 最终的选中结果
         * @param optionValues
         */
        void optionResultSave(int[] optionValues);
    }


}
