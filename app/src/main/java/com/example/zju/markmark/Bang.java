package com.example.zju.markmark;

import android.graphics.PointF;

/**
 * Created by 本 on 2017-11-27.
 */

public class Bang {

    private PointF mOrigin;
    private PointF mCurrent;

    public Bang(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

}
