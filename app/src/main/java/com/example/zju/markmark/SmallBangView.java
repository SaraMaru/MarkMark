package com.example.zju.markmark;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: document your custom view class.
 */
public class SmallBangView extends GridLayout {

    private static final String TAG = "SmallBangView";

    private Bang mCurrentBang = null;
    private Paint mBangPaint;
    /*private Paint mBackgroundPaint;*/
    private Set<Integer> chosen = new TreeSet<Integer>();

    private boolean isWaiting = false;
    public boolean isWaiting() {
        return isWaiting;
    }
    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public int getChosenStart() {
        return (int)Collections.min(chosen);
    } // the interface for bang activity to get start position of chosen text
    public int getChosenEnd() {
        return (int)Collections.max(chosen);
    } // the interface for bang activity to get end position of chosen text
    public String getChosen(int start, int end) {
        String retstr = "";
        for (int i = start; i <= end; i++) {
            TextView child = (TextView) getChildAt(i);
            String tmpstr = child.getText().toString();
            retstr = retstr.concat(tmpstr);
        }
        return retstr;
    } // the interface for bang activity to get chosen text
    public void Markit(Mark mark) {
        for (MarkEntity mE : mark.getEntityMentions()) {
            for (int i = mE.getStart(); i <= mE.getEnd(); i++) {
                TextView child = (TextView)getChildAt(i);
                if (mE.getLabel() == "Location->Country") {
                    child.setBackgroundColor(Color.GREEN);
                }
                else if (mE.getLabel() == "Location->City") {
                    child.setBackgroundColor(Color.BLUE);
                }
                else {
                    child.setBackgroundColor(Color.YELLOW);
                }
            }
        }
    } // the interface for bang activity to refresh the color of this view (not include the `relation`)
    public void tmpMarkit(int start, int end) {
        for (int i = start; i <= end; i++) {
            TextView child = (TextView)getChildAt(i);
            child.setBackgroundColor(Color.GRAY);
        }
    } // the interface for bang activity to refresh color to show is marking `relation` now
    public void nowMarkit(MarkRelation markrelation) {
        for (int i = markrelation.getStart1(); i <= markrelation.getEnd1(); i++) {
            TextView child = (TextView)getChildAt(i);
            if (markrelation.getLabel() == "City∈Country") {
                child.setBackgroundResource(R.drawable.border_one);
            }
            else {
                child.setBackgroundResource(R.drawable.border_two);
            }
        }
        for (int i = markrelation.getStart2(); i <= markrelation.getEnd2(); i++) {
            TextView child = (TextView)getChildAt(i);
            if (markrelation.getLabel() == "City∈Country") {
                child.setBackgroundResource(R.drawable.border_one);
            }
            else {
                child.setBackgroundResource(R.drawable.border_two);
            }
        }
    } // the interface for bang activity to refresh color to show the marked relation
    public void clearChosen() {
        this.chosen = null;
        this.chosen = new TreeSet<Integer>();
    } // the interface for bang activity to clear chosen text

    public SmallBangView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBangPaint = new Paint();
        mBangPaint.setColor(0x22ff0000);
        /*mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);*/
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCurrentBang != null) {
            /*canvas.drawPaint(mBackgroundPaint);*/
            float left = Math.min(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
            float right = Math.max(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
            float top = Math.min(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
            float bottom = Math.max(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, mBangPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";
        int childCount = getChildCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // Reset drawing state
                mCurrentBang = new Bang(current);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentBang != null) {
                    mCurrentBang.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                if (mCurrentBang != null) {
                    float left = Math.min(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
                    float right = Math.max(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
                    float top = Math.min(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
                    float bottom = Math.max(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
                    for (int i = 0; i < childCount; i++) {
                        TextView child = (TextView) getChildAt(i);
                        final int theLeft = child.getLeft();
                        final int theTop = child.getTop();
                        final int theRight = child.getRight();
                        final int theBottom = child.getBottom();
                        if (theLeft >= right || theRight <= left || theTop >= bottom || theBottom <= top) {
                        } // is not enclosed
                        else {
                            if (chosen.contains(i)) {
                                chosen.remove(i);
                            } // is not chosen
                            else {
                                chosen.add(i);
                            } // is chosen
                        } // is enclosed
                        changeColor(child, i);
                    }
                    mCurrentBang = null;
                }
                invalidate();
                return false;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";

                invalidate();
                break;
        }
        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);
        if (mCurrentBang != null) {
            for (int i = 0; i < childCount; i++) {
                TextView child = (TextView) getChildAt(i);
                changeColor(child, i);
            }
        }
        showChosens();
        return true;
    }

    // Change color of text being chosen to red
    private void changeColor(TextView child, int i) {
        if (chosen.contains(i)) {
            child.setTextColor(Color.RED);
        } // is chosen
        else {
            child.setTextColor(Color.BLACK);
        } // is not chosen
    }
    // Show the set `chosen` in Log
    private void showChosens() {
        for (Integer i : chosen) {
            Log.i(TAG, String.valueOf(i));
        }
    }

}
