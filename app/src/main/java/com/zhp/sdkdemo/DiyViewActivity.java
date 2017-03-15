package com.zhp.sdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zhp.sdk.activity.BaseActivity;
import com.zhp.sdk.widget.select.OnRatingListener;
import com.zhp.sdk.widget.select.StarRating;

/**
 * Created by zhp.dts on 2017/3/15.
 */

public class DiyViewActivity extends BaseActivity implements OnRatingListener {

    private StarRating starRating1, starRating2;
    private TextView txt1, txt2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_view);
        starRating1 = (StarRating) findViewById(R.id.starRating1);
        starRating1.setOnStarChangeListener(this);
        starRating2 = (StarRating) findViewById(R.id.starRating2);
        starRating2.setOnStarChangeListener(this);
        txt1 = (TextView) findViewById(R.id.starRatingTxt1);
        txt2 = (TextView) findViewById(R.id.starRatingTxt2);

    }

    @Override
    public void onRatingChange(View v, float rating) {
        if (v == starRating1) {
            txt1.setText(rating + "");
        } else if (v == starRating2) {
            txt2.setText(rating + "");
        }
    }
}
