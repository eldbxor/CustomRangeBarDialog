package com.example.taek.seekbardialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;

/**
 * Created by HyeonSeok on 2017-08-15.
 */

public class SmartKeyWeTimeChoiceDialog extends Dialog{
    private Activity mActivity;

    private View.OnClickListener mConfirmClick;
    // private Button mReturnBtn;
    private String mTitle;
//    private TextView mValueText;
    private CustomRangeBar mRangeBar;
    private float widthOfValueTextView;

    public SmartKeyWeTimeChoiceDialog(Activity activity, View.OnClickListener confirmClick, Button button, String title) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);

        mActivity = activity;
        mConfirmClick= confirmClick;
        // mReturnBtn = button;
        mTitle = title;
    }

    public void close() {
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_smart_keywe_time_choice);

        TextView titleView = findViewById(R.id.title);
        titleView.setText(mTitle);

        ImageButton closeBtn = findViewById(R.id.popup_close_icon);
        closeBtn.setOnClickListener(mCloseClick);

//        mValueText = findViewById(R.id.value_text);
        // mValueText의 실제 Width 구하기
/*
        Rect realSize = new Rect();
        mValueText.getPaint().getTextBounds(mValueText.getText().toString(), 0, mValueText.getText().length(), realSize);
        widthOfValueTextView = realSize.width() / 2;
        Toast.makeText(mActivity, "TextView's width - " + String.valueOf(widthOfValueTextView), Toast.LENGTH_SHORT).show();
*/

        mRangeBar = findViewById(R.id.range_slider_view);
//        mRangeBar.initSelectorView(mValueText);
        mRangeBar.setOnRangeBarChangeListener(mRangeBarListener);
        mRangeBar.setOnTouchListener(mTouchListener);

        // mRangeBar.setRangePinsByIndices(0, (int)mReturnBtn.getTag());
/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mValueText.setX(getValueX(mRangeBar.getRightPinValue()));
            }
        },100);
*/

        Button updateBtn = findViewById(R.id.popup_confirm_button);
        if (mConfirmClick != null)
            updateBtn.setOnClickListener(mConfirmClick);
        else
            updateBtn.setOnClickListener(mDialogConfirmClick);

    }

    private CustomRangeBar.OnRangeBarChangeListener mRangeBarListener = new CustomRangeBar.OnRangeBarChangeListener() {
        @Override
        public void onRangeChangeListener(CustomRangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
            // mReturnBtn.setTag(rightPinIndex);
//            mValueText.setText(rangeBar.getRightPinValue());
        }
    };
    private RangeBar.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 2) {
//                mValueText.setVisibility(View.INVISIBLE);
            }
            else {
//                mValueText.setVisibility(View.VISIBLE);
                // mValueText.setX(getValueX(mValueText.getText().toString()));
            }

            return false;
        }

    };
/*
    private float getValueX(String valueStr) {
        final float defWidth = 924;
        int value = Integer.valueOf(valueStr);
        float tickCount = mRangeBar.getTickCount();
        int idx  = (value - 5) / 5;
        float rangeBarWidth = mRangeBar.getWidth();
        float fValue = idx/tickCount;
        float start = (70 * rangeBarWidth) / defWidth;
        float gap = 66;
        switch (idx) {
            case 0 : gap = (66 * rangeBarWidth) / defWidth; break;
            case 1 : gap = (56 * rangeBarWidth) / defWidth; break;
            case 2 : gap = (58 * rangeBarWidth) / defWidth; break;
            case 3 : gap = (59 * rangeBarWidth) / defWidth; break;
            case 4 : gap = (60 * rangeBarWidth) / defWidth; break;
            case 5 : gap = (62 * rangeBarWidth) / defWidth; break;
        }
        float posX = gap + (start * fValue) + (rangeBarWidth * fValue);

        return posX;
    }
*/

    private float getValueX(String valueStr) {
        float mExpandedPinRadius = 0;
        float mCircleSize = 15;
        final float marginLeft = Math.max(mExpandedPinRadius, mCircleSize) + 16; // circle's size + rangeBar's marginLeft + dialog's marginLeft

        float mRightIndex = mRangeBar.getRightIndex();

        float mTickCount = mRangeBar.getTickCount();
        // float mTickCount = (float) ((mRangeBar.getTickEnd() - mRangeBar.getTickStart()) / mRangeBar.getTickInterval()) + 1;

        float posX;
        final float barLength = mRangeBar.getWidth() - ((2 * marginLeft) + 16);
        if (mRightIndex == 0) {
            posX = marginLeft;
        } else {
            posX = marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength - widthOfValueTextView;
        }
//        posX = mRangeBar.getWidth() - 32 - 30;
        return posX;
    }

    private View.OnClickListener mCloseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
    private View.OnClickListener mDialogConfirmClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private void setSystemUiVisibilityOff(AppCompatDialog dialog) {
        View decorView = dialog.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
