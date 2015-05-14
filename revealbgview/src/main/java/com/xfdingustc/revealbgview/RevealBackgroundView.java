package com.xfdingustc.revealbgview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Xiaofei on 2015/5/14.
 */
public class RevealBackgroundView extends View {
  private static final String TAG = "RevealBackgroundView";

  private Paint mFillPaint;
  public static final int STATE_NOT_STATED = 0;
  public static final int STATE_FILL_STATED = 1;
  public static final int STATE_FINISHED = 2;

  private static final int FILL_TIME = 400;

  private static final Interpolator INTERPOLATOR = new AccelerateInterpolator();

  private int mStartLocationX;
  private int mStartLocationY;

  private int mCurrentState = STATE_NOT_STATED;

  private ObjectAnimator mRevealAnimator;
  private float mCurrentRadius;
  private OnStateChangeListener mOnStateChangeListener;


  public RevealBackgroundView(Context context) {
    super(context);
    init();
  }

  public RevealBackgroundView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    Log.i(TAG, "Init");
    mFillPaint = new Paint();
    mFillPaint.setStyle(Paint.Style.FILL);
    mFillPaint.setColor(Color.WHITE);
    mOnStateChangeListener = null;
  }

  public void startFromLocation(int[] tapLocationOnScreen) {
    changeState(STATE_FILL_STATED);
    mStartLocationX = tapLocationOnScreen[0];
    mStartLocationY = tapLocationOnScreen[1];
    mRevealAnimator = ObjectAnimator.ofInt(this, "radius", 0,
        getWidth() + getHeight()).setDuration(FILL_TIME);
    mRevealAnimator.setInterpolator(INTERPOLATOR);
    mRevealAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        changeState(STATE_FINISHED);
      }
    });
    mRevealAnimator.start();
  }

  public void setToFinishedFrame() {
    changeState(STATE_FINISHED);
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (mCurrentState == STATE_FINISHED) {
      Log.i(TAG, "state = FINISHED");
      canvas.drawRect(0, 0, getWidth(), getHeight(), mFillPaint);
    } else {
      Log.i(TAG, "Not finished! color = " + mFillPaint.getColor() + " radius = " + mCurrentRadius);
      canvas.drawCircle(mStartLocationX, mStartLocationY, mCurrentRadius, mFillPaint);
    }
  }

  private void changeState(int state) {
    if (mCurrentState == state) {
      return;
    }

    mCurrentState = state;
    if (mOnStateChangeListener != null) {
      mOnStateChangeListener.onStateChange(state);
    }

  }

  public void setRadius(int radius) {
    mCurrentRadius = radius;
    invalidate();
  }

  public void setFillPaintColor(int color) {
    mFillPaint.setColor(color);
  }

  public static interface OnStateChangeListener {
    public void onStateChange(int state);
  }

  public void setOnStateChangeListener(OnStateChangeListener listener) {
    mOnStateChangeListener = listener;
  }

}
