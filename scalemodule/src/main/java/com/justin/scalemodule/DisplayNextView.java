package com.justin.scalemodule;/**
 * Created by Administrator on 2016/9/12.
 */

import android.app.Activity;
import android.view.animation.Animation;

/**
 * 作者：justin on 2016/9/12 17:05
 */
public class DisplayNextView implements Animation.AnimationListener {
    Object obj;

    // 动画监听器的构造函数
    Activity ac;
    int order;

    public DisplayNextView(Activity ac, int order) {
        this.ac = ac;
        this.order = order;
    }

    public void onAnimationStart(Animation animation) {
    }

    public void onAnimationEnd(Animation animation) {
        doSomethingOnEnd(order);
    }

    public void onAnimationRepeat(Animation animation) {
    }

    private final class SwapViews implements Runnable {
        public void run() {
            switch (order) {
                case Constant.KEY_SECONDPAGE:
                    ((ScaleDialog) ac).removeLayout();
                    break;
            }
        }
    }

    public void doSomethingOnEnd(int _order) {
        switch (_order) {

            case Constant.KEY_SECONDPAGE:
                ((ScaleDialog) ac).layout_parent.post(new SwapViews());
                break;
        }
    }
}
