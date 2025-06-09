package com.mobile.pomodoro.utils.Timer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


// xử lí sự kiện các nút, animation
public class TimerAnimationHelper {
    // animation thu nhỏ phóng to
    public static void animateButton(View button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f, 1f);
        scaleX.setDuration(150);
        scaleY.setDuration(150);
        scaleX.start();
        scaleY.start();
    }

    // animation khi timer hoàn thành
    public static void animateCompletion(ProgressBar progressCircle, FloatingActionButton btnPlayPause) {
        AnimatorSet completionSet = new AnimatorSet();
        // tạo lại progress bar
        ObjectAnimator rotate = ObjectAnimator.ofFloat(progressCircle, "rotation", progressCircle.getRotation(), progressCircle.getRotation() + 360f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(btnPlayPause, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnPlayPause, "scaleY", 1f, 1.3f, 1f);

        completionSet.playTogether(rotate, scaleX, scaleY);
        completionSet.setDuration(800);
        completionSet.setInterpolator(new AccelerateDecelerateInterpolator());
        completionSet.start();
    }

    // animation khi reset
    public static void animateReset(ProgressBar progressCircle) {
        AnimatorSet resetSet = new AnimatorSet();
        // thiết lập lại progress bar, full 100%
        ObjectAnimator resetRotation = ObjectAnimator.ofFloat(progressCircle, "rotation", progressCircle.getRotation(), -90f);
        resetSet.play(resetRotation);
        resetSet.setDuration(400);
        resetSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resetSet.start();
    }

    public static void animateTextChange(TextView textView) {
        if (textView != null) {
            AnimatorSet textAnimSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.2f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.2f, 1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0.7f, 1f);

            textAnimSet.playTogether(scaleX, scaleY, alpha);
            textAnimSet.setDuration(300);
            textAnimSet.start();
        }
    }

    // chuyển đổi task
    public static void animateTaskTransition(TextView currentTaskText) {
        AnimatorSet transitionSet = new AnimatorSet();
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(currentTaskText, "translationX", 0f, -300f, 300f, 0f);
        ObjectAnimator fade = ObjectAnimator.ofFloat(currentTaskText, "alpha", 1f, 0.3f, 1f);

        transitionSet.playTogether(slideOut, fade);
        transitionSet.setDuration(600);
        transitionSet.setInterpolator(new DecelerateInterpolator());
        transitionSet.start();
    }

    // session hoàn thành thì phóng to cái số session
    public static void animateIndicatorCompletion(View indicator) {
        AnimatorSet indicatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(indicator, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(indicator, "scaleY", 1f, 1.5f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(indicator, "rotation", 0f, 360f);

        indicatorSet.playTogether(scaleX, scaleY, rotation);
        indicatorSet.setDuration(400);
        indicatorSet.start();
    }

    public static void animateIndicatorCurrent(View indicator) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(indicator, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(indicator, "scaleY", 1f, 1.2f, 1f);

        pulse.setDuration(1000);
        pulseY.setDuration(1000);
        pulse.setRepeatMode(ValueAnimator.REVERSE);
        pulseY.setRepeatMode(ValueAnimator.REVERSE);
        pulse.setRepeatCount(ValueAnimator.INFINITE);
        pulseY.setRepeatCount(ValueAnimator.INFINITE);

        pulse.start();
        pulseY.start();
    }

    // dừng animation, gọi destroy
    public static void clearAnimations(View... views) {
        for (View view : views) {
            if (view != null) {
                view.clearAnimation();
            }
        }
    }
}
