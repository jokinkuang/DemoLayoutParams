package com.jokin.demo.demolayoutparams;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private ViewGroup mRoot;
    private WindowManager mWindowManager;

    private View mvg_add1;
    private View mvg_add2;

    private View mwm_add1;
    private View mwm_add2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRoot = findViewById(R.id.root);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        findViewById(R.id.vg_add1).setOnClickListener(this);
        findViewById(R.id.vg_add2).setOnClickListener(this);
        findViewById(R.id.vg_update).setOnClickListener(this);
        findViewById(R.id.view_update).setOnClickListener(this);
        findViewById(R.id.wm_add1).setOnClickListener(this);
        findViewById(R.id.wm_add2).setOnClickListener(this);
        findViewById(R.id.wm_update).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vg_add1:
                mvg_add1 = createChild("vg_addView()");
                mvg_add1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mRoot.addView(mvg_add1);
                break;
            case R.id.vg_add2:
                mvg_add2 = createChild("vg_addView(LayoutParams)");
                mvg_add2.setY(100);
                mRoot.addView(mvg_add2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.vg_update:
                if (mvg_add1 != null) {
                    mRoot.updateViewLayout(mvg_add1, updateVG_add1());
                }
                if (mvg_add2 != null) {
                    mRoot.updateViewLayout(mvg_add2, updateVG_add2());
                }
                break;

            case R.id.wm_add1:
                mwm_add1 = createChild("wm_addView()");
                Toast.makeText(this, "WindowManager does not support addView() method!",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.wm_add2:
                mwm_add2 = createChild("wm_addView(LayoutParams)");
                mWindowManager.addView(mwm_add2, createSubWindowLayoutParams());
                Log.e(TAG, "window manger: "+mwm_add2.getLayoutParams());
                break;
            case R.id.wm_update:
                mWindowManager.updateViewLayout(mwm_add2, updateWM_add2());
                break;

            case R.id.view_update:
                if (mvg_add1 != null) {
                    mvg_add1.setLayoutParams(updateVG_add1());
                }
                if (mvg_add2 != null) {
                    mvg_add2.setLayoutParams(updateVG_add2());
                    mvg_add2.requestLayout();
                }
                // WindowManager View can not update by view.setLayoutParams()
                if (mwm_add2 != null) {
                    mwm_add2.setLayoutParams(updateWM_add2());
                }
                Toast.makeText(this, "You can see the view added by windowmanger" +
                        " does not changed when view.setLayoutParams() called!",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "update mvg_add1: " + mvg_add1.getLayoutParams());
                Log.e(TAG, "update mvg_add2: " + mvg_add2.getLayoutParams());
                Log.e(TAG, "update mwm_add2: " + mwm_add2.getLayoutParams());
                break;
        }
    }

    private int mIndex = 0;
    private View createChild(String text) {
        TextView child = new TextView(this);
        child.setText(text);
        int color = Color.CYAN;
        switch (mIndex) {
            case 0:
                color = Color.CYAN;
                break;
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.DKGRAY;
                break;
            case 3:
                color = Color.YELLOW;
                break;
        }
        mIndex += 1;
        child.setBackground(new ColorDrawable(color));
        return child;
    }

    private WindowManager.LayoutParams createSubWindowLayoutParams() {
        // 最少参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.format = PixelFormat.RGBA_8888;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.y = 500;
        return params;
    }

    /** ViewGroup addView() **/
    private ViewGroup.LayoutParams updateVG_add1() {
        View view = mvg_add1;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT ||
                params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            params.width = view.getWidth() + 20;
        } else {
            params.width += 20;
        }
        return params;
    }

    /** ViewGroup addView(view, layoutParams) **/
    private ViewGroup.LayoutParams updateVG_add2() {
        View view = mvg_add2;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT ||
                params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            params.width = view.getWidth() + 10;
        } else {
            params.width += 10;
        }
        return params;
    }

    /**
     * 窗口
     */
    private ViewGroup.LayoutParams updateWM_add2() {
        View view = mwm_add2;
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
        if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT ||
                params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            params.width = view.getWidth() + 10;
        } else {
            params.width += 10;
        }
        return params;
    }
}
