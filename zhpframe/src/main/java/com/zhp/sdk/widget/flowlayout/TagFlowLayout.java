package com.zhp.sdk.widget.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zhp.sdk.R;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhp.dts on 2017/8/8.
 */
public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {
    private TagAdapter mTagAdapter;
    private boolean mSupportMulSelected;
    private int mSelectedMax;
    private static final String TAG = "TagFlowLayout";
    private MotionEvent mMotionEvent;
    private Set<Integer> mSelectedView;
    private TagFlowLayout.OnSelectListener mOnSelectListener;
    private TagFlowLayout.OnTagClickListener mOnTagClickListener;
    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSupportMulSelected = true;
        this.mSelectedMax = -1;
        this.mSelectedView = new HashSet();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        this.mSupportMulSelected = ta.getBoolean(R.styleable.TagFlowLayout_multi_suppout, true);
        this.mSelectedMax = ta.getInt(R.styleable.TagFlowLayout_max_select, -1);
        ta.recycle();
        if (this.mSupportMulSelected) {
            this.setClickable(true);
        }

    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int cCount = this.getChildCount();

        for (int i = 0; i < cCount; ++i) {
            TagView tagView = (TagView) this.getChildAt(i);
            if (tagView.getVisibility() != View.GONE && tagView.getTagView().getVisibility() == View.GONE) {
                tagView.setVisibility(View.GONE);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnSelectListener(TagFlowLayout.OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
        if (this.mOnSelectListener != null) {
            this.setClickable(true);
        }

    }

    public void setOnTagClickListener(TagFlowLayout.OnTagClickListener onTagClickListener) {
        this.mOnTagClickListener = onTagClickListener;
        if (onTagClickListener != null) {
            this.setClickable(true);
        }

    }

    public void setAdapter(TagAdapter adapter) {
        this.mTagAdapter = adapter;
        this.mTagAdapter.setOnDataChangedListener(this);
        this.changeAdapter();
    }

    private void changeAdapter() {
        this.removeAllViews();
        TagAdapter adapter = this.mTagAdapter;
        TagView tagViewContainer = null;
        HashSet preCheckedList = this.mTagAdapter.getPreCheckedList();

        for (int i = 0; i < adapter.getCount(); ++i) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));
            tagViewContainer = new TagView(this.getContext());
            tagView.setDuplicateParentStateEnabled(true);
            tagViewContainer.setLayoutParams(tagView.getLayoutParams());
            tagViewContainer.addView(tagView);
            this.addView(tagViewContainer);
            if (preCheckedList.contains(Integer.valueOf(i))) {
                tagViewContainer.setChecked(true);
            }
        }

        this.mSelectedView.addAll(preCheckedList);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1) {
            this.mMotionEvent = MotionEvent.obtain(event);
        }

        return super.onTouchEvent(event);
    }

    public boolean performClick() {
        if (this.mMotionEvent == null) {
            return super.performClick();
        } else {
            int x = (int) this.mMotionEvent.getX();
            int y = (int) this.mMotionEvent.getY();
            this.mMotionEvent = null;
            TagView child = this.findChild(x, y);
            int pos = this.findPosByView(child);
            if (child != null) {
                this.doSelect(child, pos);
                if (this.mOnTagClickListener != null) {
                    return this.mOnTagClickListener.onTagClick(child.getTagView(), pos, this);
                }
            }

            return super.performClick();
        }
    }

    public void setMaxSelectCount(int count) {
        if (this.mSelectedView.size() > count) {
            Log.w("TagFlowLayout", "you has already select more than " + count + " views , so it will be clear .");
            this.mSelectedView.clear();
        }

        this.mSelectedMax = count;
    }

    public Set<Integer> getSelectedList() {
        return new HashSet(this.mSelectedView);
    }

    private void doSelect(TagView child, int position) {
        if (this.mSupportMulSelected) {
            if (!child.isChecked()) {
                if (this.mSelectedMax == 1 && this.mSelectedView.size() == 1) {
                    Iterator iterator = this.mSelectedView.iterator();
                    Integer preIndex = (Integer) iterator.next();
                    TagView pre = (TagView) this.getChildAt(preIndex.intValue());
                    pre.setChecked(false);
                    child.setChecked(true);
                    this.mSelectedView.remove(preIndex);
                    this.mSelectedView.add(Integer.valueOf(position));
                } else {
                    if (this.mSelectedMax > 0 && this.mSelectedView.size() >= this.mSelectedMax) {
                        return;
                    }

                    child.setChecked(true);
                    this.mSelectedView.add(Integer.valueOf(position));
                }
            } else {
                child.setChecked(false);
                this.mSelectedView.remove(Integer.valueOf(position));
            }

            if (this.mOnSelectListener != null) {
                this.mOnSelectListener.onSelected(new HashSet(this.mSelectedView));
            }
        }

    }

    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());
        String selectPos = "";
        if (this.mSelectedView.size() > 0) {
            int key;
            for (Iterator var3 = this.mSelectedView.iterator(); var3.hasNext(); selectPos = selectPos + key + "|") {
                key = ((Integer) var3.next()).intValue();
            }

            selectPos = selectPos.substring(0, selectPos.length() - 1);
        }

        bundle.putString(KEY_CHOOSE_POS, selectPos);
        return bundle;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
        } else {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POS);
            if (!TextUtils.isEmpty(mSelectPos)) {
                String[] split = mSelectPos.split("\\|");
                String[] var5 = split;
                int var6 = split.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    String pos = var5[var7];
                    int index = Integer.parseInt(pos);
                    this.mSelectedView.add(Integer.valueOf(index));
                    TagView tagView = (TagView) this.getChildAt(index);
                    tagView.setChecked(true);
                }
            }

            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
        }
    }

    private int findPosByView(View child) {
        int cCount = this.getChildCount();

        for (int i = 0; i < cCount; ++i) {
            View v = this.getChildAt(i);
            if (v == child) {
                return i;
            }
        }

        return -1;
    }

    private TagView findChild(int x, int y) {
        int cCount = this.getChildCount();

        for (int i = 0; i < cCount; ++i) {
            TagView v = (TagView) this.getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                Rect outRect = new Rect();
                v.getHitRect(outRect);
                if (outRect.contains(x, y)) {
                    return v;
                }
            }
        }

        return null;
    }

    public void onChanged() {
        this.changeAdapter();
    }

    public interface OnTagClickListener {
        boolean onTagClick(View var1, int var2, FlowLayout var3);
    }

    public interface OnSelectListener {
        void onSelected(Set<Integer> var1);
    }
}

