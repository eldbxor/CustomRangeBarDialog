package com.example.taek.seekbardialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;

import java.util.HashMap;

/**
 * Created by Taek on 2018-01-31.
 */

public class CustomRangeBar extends View{

    // Member Variables ////////////////////////////////////////////////////////

    private static final String TAG = "RangeBar";

    // Default values for variables
    private static final float DEFAULT_TICK_START = 0;

    private static final float DEFAULT_TICK_END = 5;

    private static final float DEFAULT_TICK_INTERVAL = 1;

    private static final float DEFAULT_TICK_HEIGHT_DP = 1;

    private static final float DEFAULT_PIN_PADDING_DP = 16;

    public static final float DEFAULT_MIN_PIN_FONT_SP = 8;

    public static final float DEFAULT_MAX_PIN_FONT_SP = 24;

    private static final float DEFAULT_BAR_WEIGHT_PX = 2;

    private static final int DEFAULT_BAR_COLOR = Color.LTGRAY;

    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private static final int DEFAULT_TICK_COLOR = Color.BLACK;

    // Corresponds to material indigo 500.
    private static final int DEFAULT_PIN_COLOR = 0xff3f51b5;

    private static final float DEFAULT_CONNECTING_LINE_WEIGHT_PX = 4;

    // Corresponds to material indigo 500.
    private static final int DEFAULT_CONNECTING_LINE_COLOR = 0xff3f51b5;

    private static final float DEFAULT_EXPANDED_PIN_RADIUS_DP = 12;

    private static final float DEFAULT_CIRCLE_SIZE_DP = 5;

    private static final float DEFAULT_BAR_PADDING_BOTTOM_DP = 24;

    // Instance variables for all of the customizable attributes

    private float mTickHeightDP = DEFAULT_TICK_HEIGHT_DP;

    private float mTickStart = DEFAULT_TICK_START;

    private float mTickEnd = DEFAULT_TICK_END;

    private float mTickInterval = DEFAULT_TICK_INTERVAL;

    private float mBarWeight = DEFAULT_BAR_WEIGHT_PX;

    private int mBarColor = DEFAULT_BAR_COLOR;

    private int mPinColor = DEFAULT_PIN_COLOR;

    private int mTextColor = DEFAULT_TEXT_COLOR;

    private float mConnectingLineWeight = DEFAULT_CONNECTING_LINE_WEIGHT_PX;

    private int mConnectingLineColor = DEFAULT_CONNECTING_LINE_COLOR;

    private float mThumbRadiusDP = DEFAULT_EXPANDED_PIN_RADIUS_DP;

    private int mTickColor = DEFAULT_TICK_COLOR;

    private float mExpandedPinRadius = DEFAULT_EXPANDED_PIN_RADIUS_DP;

    private int mCircleColor = DEFAULT_CONNECTING_LINE_COLOR;

    private float mCircleSize = DEFAULT_CIRCLE_SIZE_DP;

    private float mMinPinFont = DEFAULT_MIN_PIN_FONT_SP;

    private float mMaxPinFont = DEFAULT_MAX_PIN_FONT_SP;

    // setTickCount only resets indices before a thumb has been pressed or a
    // setThumbIndices() is called, to correspond with intended usage
    private boolean mFirstSetTickCount = true;

    private int mDefaultWidth = 500;

    private int mDefaultHeight = 150;

    private int mTickCount = (int) ((mTickEnd - mTickStart) / mTickInterval) + 1;

/*
    private PinView mLeftThumb;

    private PinView mRightThumb;
*/
    private CustomPinView mLeftThumb;

    private CustomPinView mRightThumb;

    private TextView mSelectorText;

    private CustomBar mBar;

    private CustomConnectionLine mConnectingLine;

    private OnRangeBarChangeListener mListener;

    private OnRangeBarTextListener mPinTextListener;

    private HashMap<Float, String> mTickMap;

    private int mLeftIndex;

    private int mRightIndex;

    private boolean mIsRangeBar = true;

    private float mPinPadding = DEFAULT_PIN_PADDING_DP;

    private float mBarPaddingBottom = DEFAULT_BAR_PADDING_BOTTOM_DP;

    private int mActiveConnectingLineColor;

    private int mActiveBarColor;

    private int mActiveTickColor;

    private int mActiveCircleColor;

    //Used for ignoring vertical moves
    private int mDiffX;

    private int mDiffY;

    private float mLastX;

    private float mLastY;

    private IRangeBarFormatter mFormatter;

    private boolean drawTicks = true;

    private boolean mArePinsTemporary = true;

    private PinTextFormatter mPinTextFormatter = new PinTextFormatter() {
        @Override
        public String getText(String value) {
            if (value.length() > 4) {
                return value.substring(0, 4);
            } else {
                return value;
            }
        }
    };

    /**************************************************************
    *  Constructors
    * *************************************************************/
    public CustomRangeBar(Context context) {
        super(context);
    }

    public CustomRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
         rangeBarInit(context, attrs);
    }

    public CustomRangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
         rangeBarInit(context, attrs);
    }

    /*************************************************************/

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void initSelectorView(TextView textView) {
        this.mSelectorText = textView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final Context ctx = getContext();

        // This is the initial point at which we know the size of the View.

        // Create the two thumb objects and position line in view
        float density = getResources().getDisplayMetrics().density;
        float expandedPinRadius = mExpandedPinRadius / density;

        final float yPos = h - mBarPaddingBottom;
        if (mIsRangeBar) {
            mLeftThumb = new CustomPinView(ctx);
            mLeftThumb.setFormatter(mFormatter);
            mLeftThumb.init(ctx, yPos, expandedPinRadius, mPinColor, mTextColor, mCircleSize,
                    mCircleColor, mMinPinFont, mMaxPinFont, mArePinsTemporary);
        }
        mRightThumb = new CustomPinView(ctx);
        mRightThumb.setFormatter(mFormatter);
        mRightThumb.init(ctx, yPos, expandedPinRadius, mPinColor, mTextColor, mCircleSize,
                mCircleColor, mMinPinFont, mMaxPinFont, mArePinsTemporary);

        // Create the underlying bar.
        final float marginLeft = Math.max(mExpandedPinRadius, mCircleSize);

        final float barLength = w - (2 * marginLeft);
        mBar = new CustomBar(ctx, marginLeft, yPos, barLength, mTickCount, mTickHeightDP, mTickColor,
                mBarWeight, mBarColor);

        // Initialize thumbs to the desired indices
        if (mIsRangeBar) {
            mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
            mLeftThumb.setXValue(getPinValue(mLeftIndex));
        }
        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);
        mRightThumb.setXValue(getPinValue(mRightIndex));

        // Set the thumb indices.
        final int newLeftIndex = mIsRangeBar ? mBar.getNearestTickIndex(mLeftThumb) : 0;
        final int newRightIndex = mBar.getNearestTickIndex(mRightThumb);

        // Call the listener.
        if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {
            if (mListener != null) {
                mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex),
                        getPinValue(mRightIndex));
            }
        }

        // Create the line connecting the two thumbs.
        mConnectingLine = new CustomConnectionLine(ctx, yPos, mConnectingLineWeight,
                mConnectingLineColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBar.draw(canvas);
        if (mIsRangeBar) {
            mConnectingLine.draw(canvas, mLeftThumb, mRightThumb);
            if (drawTicks) {
                mBar.drawTicks(canvas);
            }
            mLeftThumb.draw(canvas);
        } else {
            mConnectingLine.draw(canvas, getMarginLeft(), mRightThumb);
            if (drawTicks) {
                mBar.drawTicks(canvas);
            }
        }
        mRightThumb.draw(canvas);
    }

    private float getMarginLeft() {
        return Math.max(mExpandedPinRadius, mCircleSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If this View is not enabled, don't allow for touch interactions.
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mDiffX = 0;
                mDiffY = 0;

                mLastX = event.getX();
                mLastY = event.getY();
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX());
                this.getParent().requestDisallowInterceptTouchEvent(true);
                final float curX = event.getX();
                final float curY = event.getY();
                mDiffX += Math.abs(curX - mLastX);
                mDiffY += Math.abs(curY - mLastY);
                mLastX = curX;
                mLastY = curY;

                if (mDiffX < mDiffY) {
                    //vertical touch
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                } else {
                    //horizontal touch (do nothing as it is needed for RangeBar)
                }
                return true;

            default:
                return false;
        }
    }

    /**
     * Handles a {@link android.view.MotionEvent#ACTION_DOWN} event.
     *
     * @param x the x-coordinate of the down action
     * @param y the y-coordinate of the down action
     */
    private void onActionDown(float x, float y) {
        if (mIsRangeBar) {
            if (!mRightThumb.isPressed() && mLeftThumb.isInTargetZone(x, y)) {

                pressPin(mLeftThumb);

            } else if (!mLeftThumb.isPressed() && mRightThumb.isInTargetZone(x, y)) {

                pressPin(mRightThumb);
            }
        } else {
            if (mRightThumb.isInTargetZone(x, y)) {
                pressPin(mRightThumb);
            }
        }
    }

    /**
     * Handles a {@link android.view.MotionEvent#ACTION_UP} or
     * {@link android.view.MotionEvent#ACTION_CANCEL} event.
     *
     * @param x the x-coordinate of the up action
     * @param y the y-coordinate of the up action
     */
    private void onActionUp(float x, float y) {
        if (mIsRangeBar && mLeftThumb.isPressed()) {

            releasePin(mLeftThumb);

        } else if (mRightThumb.isPressed()) {

            releasePin(mRightThumb);

        } else {

            float leftThumbXDistance = mIsRangeBar ? Math.abs(mLeftThumb.getX() - x) : 0;
            float rightThumbXDistance = Math.abs(mRightThumb.getX() - x);

            if (leftThumbXDistance < rightThumbXDistance) {
                if (mIsRangeBar) {
                    mLeftThumb.setX(x);
                    releasePin(mLeftThumb);
                }
            } else {
                mRightThumb.setX(x);
                releasePin(mRightThumb);
            }

            // Get the updated nearest tick marks for each thumb.
            final int newLeftIndex = mIsRangeBar ? mBar.getNearestTickIndex(mLeftThumb) : 0;
            final int newRightIndex = mBar.getNearestTickIndex(mRightThumb);
            // If either of the indices have changed, update and call the listener.
            if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {

                mLeftIndex = newLeftIndex;
                mRightIndex = newRightIndex;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex),
                            getPinValue(mRightIndex));
                }
            }
        }
    }

    /**
     * Handles a {@link android.view.MotionEvent#ACTION_MOVE} event.
     *
     * @param x the x-coordinate of the move event
     */
    private void onActionMove(float x) {

        // Move the pressed thumb to the new x-position.
        if (mIsRangeBar && mLeftThumb.isPressed()) {
            movePin(mLeftThumb, x);
        } else if (mRightThumb.isPressed()) {
            movePin(mRightThumb, x);
        }

        // If the thumbs have switched order, fix the references.
        if (mIsRangeBar && mLeftThumb.getX() > mRightThumb.getX()) {
            final CustomPinView temp = mLeftThumb;
            mLeftThumb = mRightThumb;
            mRightThumb = temp;
        }

        // Get the updated nearest tick marks for each thumb.
        int newLeftIndex = mIsRangeBar ? mBar.getNearestTickIndex(mLeftThumb) : 0;
        int newRightIndex = mBar.getNearestTickIndex(mRightThumb);

        final int componentLeft = getLeft() + getPaddingLeft();
        final int componentRight = getRight() - getPaddingRight() - componentLeft;

        if (x <= componentLeft) {
            newLeftIndex = 0;
            movePin(mLeftThumb, mBar.getLeftX());
        } else if (x >= componentRight) {
            newRightIndex = getTickCount() - 1;
            movePin(mRightThumb, mBar.getRightX());
        }
        /// end added code
        // If either of the indices have changed, update and call the listener.
        if (newLeftIndex != mLeftIndex || newRightIndex != mRightIndex) {

            mLeftIndex = newLeftIndex;
            mRightIndex = newRightIndex;
            if (mIsRangeBar) {
                mLeftThumb.setXValue(getPinValue(mLeftIndex));
            }
            mRightThumb.setXValue(getPinValue(mRightIndex));

            if (mListener != null) {
                mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex),
                        getPinValue(mRightIndex));
            }
        }
    }

    /**
     * Set the thumb to be in the pressed state and calls invalidate() to redraw
     * the canvas to reflect the updated state.
     *
     * @param thumb the thumb to press
     */
    private void pressPin(final CustomPinView thumb) {
        if (mFirstSetTickCount) {
            mFirstSetTickCount = false;
        }
        if (mArePinsTemporary) {
            ValueAnimator animator = ValueAnimator.ofFloat(0, mExpandedPinRadius);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mThumbRadiusDP = (Float) (animation.getAnimatedValue());
                    thumb.setSize(mThumbRadiusDP, mPinPadding * animation.getAnimatedFraction());
                    invalidate();
                }
            });
            animator.start();
        }

        thumb.press();
    }

    /**
     * Set the thumb to be in the normal/un-pressed state and calls invalidate()
     * to redraw the canvas to reflect the updated state.
     *
     * @param thumb the thumb to release
     */
    private void releasePin(final CustomPinView thumb) {

        final float nearestTickX = mBar.getNearestTickCoordinate(thumb);
        thumb.setX(nearestTickX);
        int tickIndex = mBar.getNearestTickIndex(thumb);
        thumb.setXValue(getPinValue(tickIndex));

        if (mArePinsTemporary) {
            ValueAnimator animator = ValueAnimator.ofFloat(mExpandedPinRadius, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mThumbRadiusDP = (Float) (animation.getAnimatedValue());
                    thumb.setSize(mThumbRadiusDP,
                            mPinPadding - (mPinPadding * animation.getAnimatedFraction()));
                    invalidate();
                }
            });
            animator.start();
        } else {
            invalidate();
        }

        thumb.release();
    }

    /**
     * Set the value on the thumb pin, either from map or calculated from the tick intervals
     * Integer check to format decimals as whole numbers
     *
     * @param tickIndex the index to set the value for
     */
    private String getPinValue(int tickIndex) {
        if (mPinTextListener != null) {
            return mPinTextListener.getPinValue(this, tickIndex);
        }
        float tickValue = (tickIndex == (mTickCount - 1))
                ? mTickEnd
                : (tickIndex * mTickInterval) + mTickStart;
        String xValue = mTickMap.get(tickValue);
        if (xValue == null) {
            if (tickValue == Math.ceil(tickValue)) {
                xValue = String.valueOf((int) tickValue);
            } else {
                xValue = String.valueOf(tickValue);
            }
        }
        return mPinTextFormatter.getText(xValue);
    }

    /**
     * Moves the thumb to the given x-coordinate.
     *
     * @param thumb the thumb to move
     * @param x     the x-coordinate to move the thumb to
     */
    private void movePin(CustomPinView thumb, float x) {

        // If the user has moved their finger outside the range of the bar,
        // do not move the thumbs past the edge.
        if (x < mBar.getLeftX() || x > mBar.getRightX()) {
            // Do nothing.
        } else if (thumb != null) {
            thumb.setX(x);
            invalidate();
        }
    }


    public void setOnRangeBarChangeListener(OnRangeBarChangeListener listener) {
        mListener = listener;
    }

    public void setPinTextListener(OnRangeBarTextListener mPinTextListener) {
        this.mPinTextListener = mPinTextListener;
    }

    public void setFormatter(IRangeBarFormatter formatter) {
        if (mLeftThumb != null) {
            mLeftThumb.setFormatter(formatter);
        }

        if (mRightThumb != null) {
            mRightThumb.setFormatter(formatter);
        }

        mFormatter = formatter;
    }

    public void setDrawTicks(boolean drawTicks) {
        this.drawTicks = drawTicks;
    }

    public void setTickStart(float tickStart) {
        int tickCount = (int) ((mTickEnd - tickStart) / mTickInterval) + 1;
        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;
            mTickStart = tickStart;

            // Prevents resetting the indices when creating new activity, but
            // allows it on the first setting.
            if (mFirstSetTickCount) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex),
                            getPinValue(mRightIndex));
                }
            }
            if (indexOutOfRange(mLeftIndex, mRightIndex)) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex),
                            getPinValue(mRightIndex));
                }
            }

            createBar();
            createPins();
        } else {
            Log.e(TAG, "tickCount less than 2; invalid tickCount.");
            throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
        }
    }

    public void setTickInterval(float tickInterval) {
        int tickCount = (int) ((mTickEnd - mTickStart) / tickInterval) + 1;
        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;
            mTickInterval = tickInterval;

            // Prevents resetting the indices when creating new activity, but
            // allows it on the first setting.
            if (mFirstSetTickCount) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex), getPinValue(mRightIndex));
                }
            }
            if (indexOutOfRange(mLeftIndex, mRightIndex)) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex), getPinValue(mRightIndex));
                }
            }

            createBar();
            createPins();
        } else {
            Log.e(TAG, "tickCount less than 2; invalid tickCount.");
            throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
        }
    }

    public void setTickEnd(float tickEnd) {
        int tickCount = (int) ((tickEnd - mTickStart) / mTickInterval) + 1;
        if (isValidTickCount(tickCount)) {
            mTickCount = tickCount;
            mTickEnd = tickEnd;

            // Prevents resetting the indices when creating new activity, but
            // allows it on the first setting.
            if (mFirstSetTickCount) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex), getPinValue(mRightIndex));
                }
            }
            if (indexOutOfRange(mLeftIndex, mRightIndex)) {
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex), getPinValue(mRightIndex));
                }
            }

            createBar();
            createPins();
        } else {
            Log.e(TAG, "tickCount less than 2; invalid tickCount.");
            throw new IllegalArgumentException("tickCount less than 2; invalid tickCount.");
        }
    }

    public void setTickHeight(float tickHeight) {
        mTickHeightDP = tickHeight;
        createBar();
    }

    public void setBarWeight(float barWeight) {
        mBarWeight = barWeight;
        createBar();
    }

    public void setBarColor(int barColor) {
        mBarColor = barColor;
        createBar();
    }

    public void setPinColor(int pinColor) {
        mPinColor = pinColor;
        createPins();
    }

    public void setPinTextColor(int textColor) {
        mTextColor = textColor;
        createPins();
    }

    public void setRangeBarEnabled(boolean isRangeBar) {
        mIsRangeBar = isRangeBar;
        invalidate();
    }

    public void setTemporaryPins(boolean arePinsTemporary) {
        mArePinsTemporary = arePinsTemporary;
        invalidate();
    }

    public void setTickColor(int tickColor) {
        mTickColor = tickColor;
        createBar();
    }

    public void setSelectorColor(int selectorColor) {
        mCircleColor = selectorColor;
        createPins();
    }

    public void setConnectingLineWeight(float connectingLineWeight) {
        mConnectingLineWeight = connectingLineWeight;
        createConnectingLine();
    }

    public void setConnectingLineColor(int connectingLineColor) {
        mConnectingLineColor = connectingLineColor;
        createConnectingLine();
    }

    public void setPinRadius(float pinRadius) {
        mExpandedPinRadius = pinRadius;
        createPins();
    }

    public float getTickStart() {
        return mTickStart;
    }

    public float getTickEnd() {
        return mTickEnd;
    }

    public int getTickCount() {
        return mTickCount;
    }

    public void setRangePinsByIndices(int leftPinIndex, int rightPinIndex) {
        if (indexOutOfRange(leftPinIndex, rightPinIndex)) {
            Log.e(TAG,
                    "Pin index left " + leftPinIndex + ", or right " + rightPinIndex
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");
            throw new IllegalArgumentException(
                    "Pin index left " + leftPinIndex + ", or right " + rightPinIndex
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");
        } else {

            if (mFirstSetTickCount) {
                mFirstSetTickCount = false;
            }
            mLeftIndex = leftPinIndex;
            mRightIndex = rightPinIndex;
            createPins();

            if (mListener != null) {
                mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex), getPinValue(mRightIndex));
            }
        }

        invalidate();
        requestLayout();
    }

    public void setSeekPinByIndex(int pinIndex) {
        if (pinIndex < 0 || pinIndex > mTickCount) {
            Log.e(TAG,
                    "Pin index " + pinIndex
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + 0 + ") and less than the maximum value ("
                            + mTickCount + ")");
            throw new IllegalArgumentException(
                    "Pin index " + pinIndex
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + 0 + ") and less than the maximum value ("
                            + mTickCount + ")");

        } else {

            if (mFirstSetTickCount) {
                mFirstSetTickCount = false;
            }
            mRightIndex = pinIndex;
            createPins();

            if (mListener != null) {
                mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex), getPinValue(mRightIndex));
            }
        }
        invalidate();
        requestLayout();
    }

    public void setRangePinsByValue(float leftPinValue, float rightPinValue) {
        if (valueOutOfRange(leftPinValue, rightPinValue)) {
            Log.e(TAG,
                    "Pin value left " + leftPinValue + ", or right " + rightPinValue
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");
            throw new IllegalArgumentException(
                    "Pin value left " + leftPinValue + ", or right " + rightPinValue
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");
        } else {
            if (mFirstSetTickCount) {
                mFirstSetTickCount = false;
            }

            mLeftIndex = (int) ((leftPinValue - mTickStart) / mTickInterval);
            mRightIndex = (int) ((rightPinValue - mTickStart) / mTickInterval);
            createPins();

            if (mListener != null) {
                mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex), getPinValue(mRightIndex));
            }
        }
        invalidate();
        requestLayout();
    }

    public void setSeekPinByValue(float pinValue) {
        if (pinValue > mTickEnd || pinValue < mTickStart) {
            Log.e(TAG,
                    "Pin value " + pinValue
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");
            throw new IllegalArgumentException(
                    "Pin value " + pinValue
                            + " is out of bounds. Check that it is greater than the minimum ("
                            + mTickStart + ") and less than the maximum value ("
                            + mTickEnd + ")");

        } else {
            if (mFirstSetTickCount) {
                mFirstSetTickCount = false;
            }
            mRightIndex = (int) ((pinValue - mTickStart) / mTickInterval);
            createPins();

            if (mListener != null) {
                mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                        getPinValue(mLeftIndex), getPinValue(mRightIndex));
            }
        }
        invalidate();
        requestLayout();
    }

    public boolean isRangeBar() {
        return mIsRangeBar;
    }

    public String getLeftPinValue() {
        return getPinValue(mLeftIndex);
    }

    public String getRightPinValue() {
        return getPinValue(mRightIndex);
    }

    public int getLeftIndex() {
        return mLeftIndex;
    }

    public int getRightIndex() {
        return mRightIndex;
    }

    public double getTickInterval() {
        return mTickInterval;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            mBarColor = DEFAULT_BAR_COLOR;
            mConnectingLineColor = DEFAULT_BAR_COLOR;
            mCircleColor = DEFAULT_BAR_COLOR;
            mTickColor = DEFAULT_BAR_COLOR;
        } else {
            mBarColor = mActiveBarColor;
            mConnectingLineColor = mActiveConnectingLineColor;
            mCircleColor = mActiveCircleColor;
            mTickColor = mActiveTickColor;
        }

        createBar();
        createPins();
        createConnectingLine();
        super.setEnabled(enabled);
    }

    public void setPinTextFormatter(PinTextFormatter pinTextFormatter) {
        this.mPinTextFormatter = pinTextFormatter;
    }

    // Private Methods /////////////////////////////////////////////////////////
    /**
     * Does all the functions of the constructor for RangeBar. Called by both
     * RangeBar constructors in lieu of copying the code for each constructor.
     *
     * @param context Context from the constructor.
     * @param attrs   AttributeSet from the constructor.
     */
    private void rangeBarInit(Context context, AttributeSet attrs) {
        //TODO tick value map
        if (mTickMap == null) {
            mTickMap = new HashMap<Float, String>();
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, com.appyvet.rangebar.R.styleable.RangeBar, 0, 0);

        try {

            // Sets the values of the user-defined attributes based on the XML
            // attributes.
            final float tickStart = ta
                    .getFloat(com.appyvet.rangebar.R.styleable.RangeBar_tickStart, DEFAULT_TICK_START);
            final float tickEnd = ta
                    .getFloat(com.appyvet.rangebar.R.styleable.RangeBar_tickEnd, DEFAULT_TICK_END);
            final float tickInterval = ta
                    .getFloat(com.appyvet.rangebar.R.styleable.RangeBar_tickInterval, DEFAULT_TICK_INTERVAL);
            int tickCount = (int) ((tickEnd - tickStart) / tickInterval) + 1;
            if (isValidTickCount(tickCount)) {

                // Similar functions performed above in setTickCount; make sure
                // you know how they interact
                mTickCount = tickCount;
                mTickStart = tickStart;
                mTickEnd = tickEnd;
                mTickInterval = tickInterval;
                mLeftIndex = 0;
                mRightIndex = mTickCount - 1;

                if (mListener != null) {
                    mListener.onRangeChangeListener(this, mLeftIndex, mRightIndex,
                            getPinValue(mLeftIndex),
                            getPinValue(mRightIndex));
                }

            } else {

                Log.e(TAG, "tickCount less than 2; invalid tickCount. XML input ignored.");
            }

            mTickHeightDP = ta
                    .getDimension(com.appyvet.rangebar.R.styleable.RangeBar_tickHeight, DEFAULT_TICK_HEIGHT_DP);
            mBarWeight = ta.getDimension(com.appyvet.rangebar.R.styleable.RangeBar_barWeight, DEFAULT_BAR_WEIGHT_PX);
            mBarColor = ta.getColor(com.appyvet.rangebar.R.styleable.RangeBar_rangeBarColor, DEFAULT_BAR_COLOR);
            mTextColor = ta.getColor(com.appyvet.rangebar.R.styleable.RangeBar_textColor, DEFAULT_TEXT_COLOR);
            mPinColor = ta.getColor(com.appyvet.rangebar.R.styleable.RangeBar_pinColor, DEFAULT_PIN_COLOR);
            mActiveBarColor = mBarColor;
            mCircleSize = ta.getDimension(com.appyvet.rangebar.R.styleable.RangeBar_selectorSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_SIZE_DP,
                            getResources().getDisplayMetrics())
            );
            mCircleColor = ta.getColor(com.appyvet.rangebar.R.styleable.RangeBar_selectorColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mActiveCircleColor = mCircleColor;
            mTickColor = ta.getColor(com.appyvet.rangebar.R.styleable.RangeBar_tickColor, DEFAULT_TICK_COLOR);
            mActiveTickColor = mTickColor;
            mConnectingLineWeight = ta.getDimension(com.appyvet.rangebar.R.styleable.RangeBar_connectingLineWeight,
                    DEFAULT_CONNECTING_LINE_WEIGHT_PX);
            mConnectingLineColor = ta.getColor(com.appyvet.rangebar.R.styleable.RangeBar_connectingLineColor,
                    DEFAULT_CONNECTING_LINE_COLOR);
            mActiveConnectingLineColor = mConnectingLineColor;
            mExpandedPinRadius = ta
                    .getDimension(com.appyvet.rangebar.R.styleable.RangeBar_pinRadius, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            DEFAULT_EXPANDED_PIN_RADIUS_DP, getResources().getDisplayMetrics()));
            mPinPadding = ta.getDimension(com.appyvet.rangebar.R.styleable.RangeBar_pinPadding, TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PIN_PADDING_DP,
                            getResources().getDisplayMetrics()));
            mBarPaddingBottom = ta.getDimension(com.appyvet.rangebar.R.styleable.RangeBar_rangeBarPaddingBottom,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            DEFAULT_BAR_PADDING_BOTTOM_DP, getResources().getDisplayMetrics()));
            mIsRangeBar = ta.getBoolean(com.appyvet.rangebar.R.styleable.RangeBar_rangeBar, true);
            mArePinsTemporary = ta.getBoolean(com.appyvet.rangebar.R.styleable.RangeBar_temporaryPins, true);

            float density = getResources().getDisplayMetrics().density;
            mMinPinFont = ta.getDimension(com.appyvet.rangebar.R.styleable.RangeBar_pinMinFont,
                    DEFAULT_MIN_PIN_FONT_SP * density);
            mMaxPinFont = ta.getDimension(com.appyvet.rangebar.R.styleable.RangeBar_pinMaxFont,
                    DEFAULT_MAX_PIN_FONT_SP * density);

            mIsRangeBar = ta.getBoolean(com.appyvet.rangebar.R.styleable.RangeBar_rangeBar, true);
        } finally {
            ta.recycle();
        }
    }

    /**
     * Returns if either index is outside the range of the tickCount.
     *
     * @param leftThumbIndex  Integer specifying the left thumb index.
     * @param rightThumbIndex Integer specifying the right thumb index.
     * @return boolean If the index is out of range.
     */
    private boolean indexOutOfRange(int leftThumbIndex, int rightThumbIndex) {
        return (leftThumbIndex < 0 || leftThumbIndex >= mTickCount
                || rightThumbIndex < 0
                || rightThumbIndex >= mTickCount);
    }

    /**
     * Returns if either value is outside the range of the tickCount.
     *
     * @param leftThumbValue  Float specifying the left thumb value.
     * @param rightThumbValue Float specifying the right thumb value.
     * @return boolean If the index is out of range.
     */
    private boolean valueOutOfRange(float leftThumbValue, float rightThumbValue) {
        return (leftThumbValue < mTickStart || leftThumbValue > mTickEnd
                || rightThumbValue < mTickStart || rightThumbValue > mTickEnd);
    }

    /**
     * If is invalid tickCount, rejects. TickCount must be greater than 1
     *
     * @param tickCount Integer
     * @return boolean: whether tickCount > 1
     */
    private boolean isValidTickCount(int tickCount) {
        return (tickCount > 1);
    }

    /**
     * Creates a new mBar
     */
    private void createBar() {
        mBar = new CustomBar(getContext(),
                getMarginLeft(),
                getYPos(),
                getBarLength(),
                mTickCount,
                mTickHeightDP,
                mTickColor,
                mBarWeight,
                mBarColor);
        invalidate();
    }

    /**
     * Creates a new ConnectingLine.
     */
    private void createConnectingLine() {

        mConnectingLine = new CustomConnectionLine(getContext(),
                getYPos(),
                mConnectingLineWeight,
                mConnectingLineColor);
        invalidate();
    }

    /**
     * Creates two new Pins.
     */
    private void createPins() {
        Context ctx = getContext();
        float yPos = getYPos();

        if (mIsRangeBar) {
            mLeftThumb = new CustomPinView(ctx);
            mLeftThumb.init(ctx, yPos, 0, mPinColor, mTextColor, mCircleSize, mCircleColor,
                    mMinPinFont, mMaxPinFont, false);
        }
        mRightThumb = new CustomPinView(ctx);
        mRightThumb
                .init(ctx, yPos, 0, mPinColor, mTextColor, mCircleSize, mCircleColor, mMinPinFont,
                        mMaxPinFont, false);

        float marginLeft = getMarginLeft();
        float barLength = getBarLength();

        // Initialize thumbs to the desired indices
        if (mIsRangeBar) {
            mLeftThumb.setX(marginLeft + (mLeftIndex / (float) (mTickCount - 1)) * barLength);
            mLeftThumb.setXValue(getPinValue(mLeftIndex));
        }
        mRightThumb.setX(marginLeft + (mRightIndex / (float) (mTickCount - 1)) * barLength);
        mRightThumb.setXValue(getPinValue(mRightIndex));

        invalidate();
    }

    /**
     * Get yPos in each of the public attribute methods.
     *
     * @return float yPos
     */
    private float getYPos() {
        return (getHeight() - mBarPaddingBottom);
    }

    /**
     * Get barLength in each of the public attribute methods.
     *
     * @return float barLength
     */
    private float getBarLength() {
        return (getWidth() - 2 * getMarginLeft());
    }

    // Inner Classes ///////////////////////////////////////////////////////////

    /**
     * A callback that notifies clients when the RangeBar has changed. The
     * listener will only be called when either thumb's index has changed - not
     * for every movement of the thumb.
     */
    public interface OnRangeBarChangeListener {

        public void onRangeChangeListener(CustomRangeBar rangeBar, int leftPinIndex,
                                          int rightPinIndex, String leftPinValue, String rightPinValue);
    }

    public interface PinTextFormatter {

        public String getText(String value);
    }

    /**
     * @author robmunro
     *         A callback that allows getting pin text exernally
     */
    public static interface OnRangeBarTextListener {

        public String getPinValue(CustomRangeBar rangeBar, int tickIndex);
    }
}
