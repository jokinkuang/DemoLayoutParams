package com.jokin.demo.demolayoutparams;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;



/**
 * Created by jokin on 2018/7/31 17:31.
 *
 * out <- a copy <- LayoutParams
 *  in -> a copy -> LayoutParams
 *
 * Do a copy instead of reference to forbid changing the LayoutParams directly by reference.
 */

public class WindowLayout extends FrameLayout {
    private static final String TAG = "CWindowLayout";

    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mDefaultLayoutParams = new WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            0,
            0,
            WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );

    public WindowLayout(@NonNull Context context) {
        super(context);
    }

    public WindowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WindowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * attach and add to windowManager with the default layoutParams
     * @param windowManager
     */
    public void attachWindowManager(WindowManager windowManager) {
        if (getLayoutParams() == null) {
            attachWindowManager(windowManager, mDefaultLayoutParams);
        } else {
            attachWindowManager(windowManager, getLayoutParams());
        }
    }

    /**
     * attach and add to windowManager with the giving layoutParams
     * @param windowManager
     * @param params
     */
    public void attachWindowManager(WindowManager windowManager, WindowManager.LayoutParams params) {
        if (windowManager == null || params == null) {
            throw new IllegalArgumentException("windowManager or params can not be null");
        }
        if (!(params instanceof WindowManager.LayoutParams)) {
            throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
        }
        // 1. detach
        detachWindowManager();
        // 2. add      (PS. addView() will call setLayoutParams())
        windowManager.addView(this, params);
        // 3. attach   (PS. attach last to avoid pass through setLayoutParams() with mWindowManager != null)
        mWindowManager = windowManager;
    }

    /**
     * detach and remove from windowManager
     */
    public void detachWindowManager() {
        // 1. remove
        if (mWindowManager != null) {
            mWindowManager.removeView(this);
        }
        // 2. detach
        mWindowManager = null;
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        ViewGroup.LayoutParams srcwparams = super.getLayoutParams();
        if (srcwparams == null) {
            return null;
        }
        if (!(srcwparams instanceof WindowManager.LayoutParams)) {
            return null;
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom((WindowManager.LayoutParams) srcwparams);
        return layoutParams;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new IllegalArgumentException("params can not be null");
        }
        // 1. normal view
        if (mWindowManager == null) {
            super.setLayoutParams(params);
            return;
        }
        // 2. window view
        if (!(params instanceof WindowManager.LayoutParams)) {
            throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
        }
        WindowManager.LayoutParams srcwparams = (WindowManager.LayoutParams) super.getLayoutParams();
        int changed = 0;
        if (srcwparams == null) {
            WindowManager.LayoutParams wparams = new WindowManager.LayoutParams();
            wparams.copyFrom((WindowManager.LayoutParams) params);
            srcwparams = wparams;
            changed = -1;
        } else {
            if (srcwparams.equals(params)) {
                WindowManager.LayoutParams wparams = new WindowManager.LayoutParams();
                wparams.copyFrom((WindowManager.LayoutParams) params);
                changed = srcwparams.copyFrom(wparams);
            } else {
                changed = srcwparams.copyFrom((WindowManager.LayoutParams) params);
            }
        }
        Log.d(TAG, "setLayoutParams: changed = " + changed);

        // 3. update if LayoutParams changed
        if (changed != 0) {
            // 4. set LayoutParams into super to avoid dead loop
            super.setLayoutParams(srcwparams);

            // 5. updateViewLayout() will call setLayoutParams() again (cause a loop) !
            mWindowManager.updateViewLayout(this, srcwparams);
        }
    }
}
