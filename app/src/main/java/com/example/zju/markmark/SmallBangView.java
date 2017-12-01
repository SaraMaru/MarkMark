package com.example.zju.markmark;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
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
    private Mark thisMark = new Mark();
    public void setThisMark(Mark thisMark) {
        this.thisMark = thisMark;
    }
    public MarkRelation tempRelation = null;
    public void setTempRelation(MarkRelation tempRelation) {
        this.tempRelation = tempRelation;
    }
    private Paint mBangPaint;
    private Paint raPaint;
    private Paint rbPaint;
    private Paint eaPaint;
    private Paint ebPaint;
    private Paint ecPaint;
    private Paint temPaint;
    private Set<Integer> chosen = new TreeSet<>();
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
    public boolean getChosenEmpty() {
        if (chosen.size() == 0) return true;
        for (int i : chosen) {
            TextView child = (TextView)getChildAt(i);
            if (child.getText() == null) return true;
        }
        return false;
    }
    /*public void Markit(Mark mark) {
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
    } // the interface for bang activity to refresh the color of this view (not include the `relation`)*/
    /*public void tmpMarkit(int start, int end) {
        for (int i = start; i <= end; i++) {
            TextView child = (TextView)getChildAt(i);
            child.setBackgroundColor(Color.GRAY);
        }
    } // the interface for bang activity to refresh color to show is marking `relation` now
    public void nowMarkit(MarkRelation markrelation) {
        for (int i = markrelation.getStart1(); i <= markrelation.getEnd1(); i++) {
            TextView child = (TextView)getChildAt(i);
            *//*if (markrelation.getLabel() == "City∈Country") {
                child.setBackgroundResource(R.drawable.border_one);
            }
            else {
                child.setBackgroundResource(R.drawable.border_two);
            }*//*
            child.setBackgroundColor(child.getDrawingCacheBackgroundColor());
        }
        *//*for (int i = markrelation.getStart2(); i <= markrelation.getEnd2(); i++) {
            TextView child = (TextView)getChildAt(i);
            if (markrelation.getLabel() == "City∈Country") {
                child.setBackgroundResource(R.drawable.border_one);
            }
            else {
                child.setBackgroundResource(R.drawable.border_two);
            }
        }*//*
    } // the interface for bang activity to refresh color to show the marked relation*/
    public void clearChosen() {
        this.chosen = null;
        this.chosen = new TreeSet<Integer>();
    } // the interface for bang activity to clear chosen text


    public SmallBangView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBangPaint = new Paint();
        mBangPaint.setColor(0x22ff0000);

        final int StrokeWidth = 5;
        raPaint = new Paint();
        raPaint.setStyle(Paint.Style.STROKE);
        raPaint.setStrokeWidth(StrokeWidth);
        raPaint.setColor(0xffD05A6E); // IMAYOH
        raPaint.setAntiAlias(true);
        rbPaint = new Paint();
        rbPaint.setStyle(Paint.Style.STROKE);
        rbPaint.setStrokeWidth(StrokeWidth);
        rbPaint.setColor(0xff7DB9DE); // WASURENAGUSA
        rbPaint.setAntiAlias(true);
        eaPaint = new Paint();
        eaPaint.setStyle(Paint.Style.FILL);
        eaPaint.setColor(0x66FFB11B); // YAMABUKI
        eaPaint.setAntiAlias(true);
        ebPaint = new Paint();
        ebPaint.setStyle(Paint.Style.FILL);
        ebPaint.setColor(0x668F77B5); // SHION
        ebPaint.setAntiAlias(true);
        ecPaint = new Paint();
        ecPaint.setStyle(Paint.Style.FILL);
        ecPaint.setColor(0x66CAAD5F); //KARASHI
        ecPaint.setAntiAlias(true);
        temPaint = new Paint();
        temPaint.setStyle(Paint.Style.STROKE);
        temPaint.setStrokeWidth(StrokeWidth);
        temPaint.setColor(0xff1C1C1C); // SUMI
        temPaint.setAntiAlias(true);
        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TextView child = (TextView)getChildAt(0);
        final int childHeight = child.getHeight();
        final int childWidth = child.getWidth();
        for (MarkEntity mE : thisMark.getEntityMentions()) {
            final int left = (getChildAt(mE.getStart())).getLeft();
            final int top = (getChildAt(mE.getStart())).getTop();
            final int right = (getChildAt(mE.getEnd())).getRight();
            final int bottom = (getChildAt(mE.getEnd())).getBottom();
            if (mE.getLabel() == "Location->Country") drawMarkedEntity(canvas, left, top, right, bottom, eaPaint, childHeight, childWidth);
            else if (mE.getLabel() == "Location->City") drawMarkedEntity(canvas, left, top, right, bottom, ebPaint, childHeight, childWidth);
            else drawMarkedEntity(canvas, left, top, right, bottom, ecPaint, childHeight, childWidth);
            Log.i(TAG, "now draw the marked entities");
        } // Draw the marked entities
        if (isWaiting) {
            final int left = (getChildAt(tempRelation.getStart1())).getLeft();
            final int top = (getChildAt(tempRelation.getStart1())).getTop();
            final int right = (getChildAt(tempRelation.getEnd1())).getRight();
            final int bottom = (getChildAt(tempRelation.getEnd1())).getBottom();
            drawMarkedEntity(canvas, left, top, right, bottom, temPaint, childHeight, childWidth);
        } // Draw the temporary marked entity in a relation
        drawMarkedRelation(canvas, childHeight, childWidth);
        if (mCurrentBang != null) {
            /*float left = Math.min(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
            float right = Math.max(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
            float top = Math.min(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
            float bottom = Math.max(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);*/
            float oriTop = Math.min(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
            float curBottom = Math.max(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
            float oriLeft;
            float curRight;
            if (mCurrentBang.getOrigin().y < mCurrentBang.getCurrent().y) {
                oriLeft = mCurrentBang.getOrigin().x;
                curRight = mCurrentBang.getCurrent().x;
            }
            else {
                oriLeft = mCurrentBang.getCurrent().x;
                curRight = mCurrentBang.getOrigin().x;
            }
            for (int i = 0; i < getChildCount(); i++) {
                TextView eachchild = (TextView)getChildAt(i);
                final int theLeft = eachchild.getLeft();
                final int theTop = eachchild.getTop();
                final int theRight = eachchild.getRight();
                final int theBottom = eachchild.getBottom();
                if (oriTop >= theTop && curBottom <= theBottom && oriLeft >= theLeft && curRight <= theRight) {
                    canvas.drawRect(theLeft, theTop, theRight, theBottom, mBangPaint);
                }
                else {
                    if (theBottom <= oriTop || theTop >= curBottom || (theBottom <= oriTop + childHeight && theRight <= oriLeft) || (theTop >= curBottom - childHeight && theLeft >= curRight)) {}
                    else {
                    /*if (theTop >= oriTop + childHeight && theBottom <= curBottom - childHeight) {
                        canvas.drawRect(theLeft, theTop, theRight, theBottom, mBangPaint);
                    }
                    else if (oriTop >= theTop && oriTop <= theBottom) {
                        if (oriLeft >= theLeft && oriLeft <= theRight) {
                            canvas.drawRect(oriLeft, oriTop, theRight, theBottom, mBangPaint);
                        }
                        else {
                            canvas.drawRect(theLeft, oriTop, theRight, theBottom, mBangPaint);
                        }
                    }
                    else {
                        if (curRight >= theLeft && curRight <= theRight) {
                            canvas.drawRect(theLeft, theTop, curRight, curBottom, mBangPaint);
                        }
                        else {
                            canvas.drawRect(theLeft, theTop, theRight, curBottom, mBangPaint);
                        }
                    }*/
                        canvas.drawRect(theLeft, theTop, theRight, theBottom, mBangPaint);
                    } // is enclosed
                }
            }
            /*canvas.drawRect(left, top, right, bottom, mBangPaint);*/
        } // Draw the translucent marquee when choosing
    }
    private void drawMarkedEntity(Canvas canvas, int left, int top, int right, int bottom, Paint paint, int childHeight, int childWidth) {
        for (int i = top; i < bottom; i += childHeight) {
            int tempLeft;
            int tempRight;
            if (i != top) {
                tempLeft = getLeft();
            } // Not the first row
            else {
                tempLeft = left;
            }
            if (i + childHeight != bottom) {
                tempRight = getRight();
            } // not the last row
            else {
                tempRight = right;
            }
            RectF rectF = new RectF(tempLeft + childWidth / 4, i + childHeight / 4, tempRight - childWidth / 4, i + childHeight - childHeight / 4);
            canvas.drawRoundRect(rectF, 10, 10, paint); // the 2nd and 3rd para is fillet-radius in X and Y
        }
    }
    private void drawMarkedRelation(Canvas canvas, int childHeight, int childWidth) {
        for (MarkRelation mR : thisMark.getRelationMentions()) {
            final int left1 = (getChildAt(mR.getStart1())).getLeft();
            final int top1 = (getChildAt(mR.getStart1())).getTop();
            final int right1 = (getChildAt(mR.getEnd1())).getRight();
            final int bottom1 = (getChildAt(mR.getEnd1())).getBottom();
            final int left2 = (getChildAt(mR.getStart2())).getLeft();
            final int top2 = (getChildAt(mR.getStart2())).getTop();
            final int right2 = (getChildAt(mR.getEnd2())).getRight();
            final int bottom2 = (getChildAt(mR.getEnd2())).getBottom();
            if (mR.getLabel() == "City∈Country") {
                drawMarkedEntity(canvas, left1, top1, right1, bottom1, raPaint, childHeight, childWidth);
                drawMarkedEntity(canvas, left2, top2, right2, bottom2, raPaint, childHeight, childWidth);
                if (mR.getStart1() < mR.getStart2()) linkMarkedRelation(canvas, right1, bottom1 - childHeight / 2, left2, top2 + childHeight / 2, raPaint, childWidth, childHeight);
                else linkMarkedRelation(canvas, right2, bottom2 - childHeight / 2, left1, top1 + childHeight / 2, raPaint, childWidth, childHeight);
            }
            else {
                drawMarkedEntity(canvas, left1, top1, right1, bottom1, rbPaint, childHeight, childWidth);
                drawMarkedEntity(canvas, left2, top2, right2, bottom2, rbPaint, childHeight, childWidth);
                if (mR.getStart1() < mR.getStart2()) linkMarkedRelation(canvas, right1, bottom1 - childHeight / 2, left2, top2 + childHeight / 2, rbPaint, childWidth, childHeight);
                else linkMarkedRelation(canvas, right2, bottom2 - childHeight / 2, left1, top1 + childHeight / 2, rbPaint, childWidth, childHeight);
            }
        }
    }
    private void linkMarkedRelation(Canvas canvas, int tailX, int tailY, int headX, int headY, Paint rPaint, int childWidth, int childHeight) {
        int oStrokeWidth = 5;
        int nStrokeWidth = 2;
        int retract = 50;
        rPaint.setStrokeWidth(nStrokeWidth);
        tailX -= childWidth / 4;
        headX += childWidth / 4;
        if (tailY == headY) {
            canvas.drawLine(tailX, tailY, headX, headY, rPaint);
        }
        else {
            if (tailX > headX) {
                canvas.drawLine(tailX, tailY, tailX + childWidth / 2 - retract, tailY, rPaint);
                canvas.drawLine(headX, headY, headX - childWidth / 2 + retract, headY, rPaint);
                canvas.drawLine(tailX + childWidth / 2 - retract, tailY, tailX + childWidth / 2 - retract, tailY + (headY - tailY) / 2, rPaint);
                canvas.drawLine(headX - childWidth / 2 + retract, headY, headX - childWidth / 2 + retract, headY - (headY - tailY) / 2, rPaint);
                canvas.drawLine(tailX + childWidth / 2 - retract, tailY + (headY - tailY) / 2, headX - childWidth / 2 + retract, headY - (headY - tailY) / 2, rPaint);
            } // the above one on the right of the below one
            else {
                canvas.drawLine(tailX, tailY, tailX + (headX - tailX) / 2, tailY, rPaint);
                canvas.drawLine(headX, headY, headX - (headX - tailX) / 2, headY, rPaint);
                canvas.drawLine(tailX + (headX - tailX) / 2, tailY, headX - (headX - tailX) / 2, headY, rPaint);
            } // tailX < headX, that is the above one on the left and the below one on the right
        }
        rPaint.setStrokeWidth(oStrokeWidth);
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
                    /*float left = Math.min(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
                    float right = Math.max(mCurrentBang.getOrigin().x, mCurrentBang.getCurrent().x);
                    float top = Math.min(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
                    float bottom = Math.max(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);*/
                    float oriTop = Math.min(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
                    float curBottom = Math.max(mCurrentBang.getOrigin().y, mCurrentBang.getCurrent().y);
                    float oriLeft;
                    float curRight;
                    if (mCurrentBang.getOrigin().y < mCurrentBang.getCurrent().y) {
                        oriLeft = mCurrentBang.getOrigin().x;
                        curRight = mCurrentBang.getCurrent().x;
                    }
                    else {
                        oriLeft = mCurrentBang.getCurrent().x;
                        curRight = mCurrentBang.getOrigin().x;
                    }
                    for (int i = 0; i < childCount; i++) {
                        TextView child = (TextView) getChildAt(i);
                        final int childHeight = child.getHeight();
                        final int theLeft = child.getLeft();
                        final int theTop = child.getTop();
                        final int theRight = child.getRight();
                        final int theBottom = child.getBottom();
                        /*if (theLeft >= right || theRight <= left || theTop >= bottom || theBottom <= top) {
                        } // is not enclosed*/
                        if (oriTop >= theTop && curBottom <= theBottom && oriLeft >= theLeft && curRight <= theRight) {
                            if (chosen.contains(i)) {
                                chosen.remove(i);
                            } // is not chosen
                            else {
                                chosen.add(i);
                            } // is chosen
                        }
                        else {
                            if (theBottom <= oriTop || theTop >= curBottom || (theBottom <= oriTop + childHeight && theRight <= oriLeft) || (theTop >= curBottom - childHeight && theLeft >= curRight)) {}
                            else {
                                if (chosen.contains(i)) {
                                    chosen.remove(i);
                                } // is not chosen
                                else {
                                    chosen.add(i);
                                } // is chosen
                            } // is enclosed
                        }

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
        /*Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);*/
        if (mCurrentBang != null) {
            for (int i = 0; i < childCount; i++) {
                TextView child = (TextView) getChildAt(i);
                changeColor(child, i);
            }
        }
        /*showChosens();*/
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

    /*// Show the set `chosen` in Log
    private void showChosens() {
        for (Integer i : chosen) {
            Log.i(TAG, String.valueOf(i));
        }
    }*/

}
