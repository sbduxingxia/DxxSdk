package com.zhp.sdk.widget;

import android.content.Context;

import com.zhp.sdk.spinnerwheel.adapters.AbstractWheelTextAdapter;
import com.zhp.sdk.R;

import java.util.ArrayList;


/**
 * Created by 01432709 on 2016/12/27.
 */

public class SpnnerWheelAdapter extends AbstractWheelTextAdapter {
    // items
    protected ArrayList<ISpannerText> items;
    protected SpnnerWheelAdapter(Context context,ArrayList<ISpannerText> items) {
        super(context, R.layout.zp_item_scroll_center,R.id.item_scroll_txt);
        this.items=items;
    }
    protected SpnnerWheelAdapter(Context context, int itemResource, int itemTextResource,ArrayList<ISpannerText> items) {
        super(context, itemResource, itemTextResource);
        this.items = items;

    }

    @Override
    protected CharSequence getItemText(int index) {
        if (items!=null&&index >= 0 && index < items.size()) {
            ISpannerText item = items.get(index);
            if (item.getText()!=null) {
                return (CharSequence) item.getText();
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        if(items==null)return 0;
        return items.size();
    }
}
