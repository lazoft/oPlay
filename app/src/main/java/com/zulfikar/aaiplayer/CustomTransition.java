package com.zulfikar.aaiplayer;

import android.animation.Animator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.ViewGroup;

public class CustomTransition extends Transition {

    @Override
    public void captureStartValues(TransitionValues values) {}

    @Override
    public void captureEndValues(TransitionValues values) {}

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }
}