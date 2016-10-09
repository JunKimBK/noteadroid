package com.dut.note.lib;

import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.Queue;

public final class ViewUtils {
    public static void hookEvent(ViewGroup root, IFunc forView) {
        Queue<ViewGroup> viewGroups = new LinkedList<>();
        viewGroups.add(root);
        do {
            ViewGroup current = viewGroups.poll();
            forView.forView(current);
            for (int i = 0; i < current.getChildCount(); i++) {
                View view = current.getChildAt(i);
                forView.forView(view);
                if (view instanceof ViewGroup)
                    viewGroups.add((ViewGroup) view);
            }
        } while (!viewGroups.isEmpty());
        viewGroups.clear();
    }

    public static void hookClickEvent(ViewGroup root, final View.OnClickListener listener) {
        hookEvent(root, new IFunc() {
            @Override
            public void forView(View view) {
                view.setOnClickListener(listener);
            }
        });
    }

    public static void hookTouchEvent(ViewGroup root, final View.OnTouchListener listener) {
        hookEvent(root, new IFunc() {
            @Override
            public void forView(View view) {
                view.setOnTouchListener(listener);
            }
        });
    }

    public static void registerButtonEffect(ViewGroup root) {
        hookEvent(root, new IFunc() {
            @Override
            public void forView(View view) {
                registerButtonEffect(view);
            }
        });
    }

    public static void registerButtonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    public static void unregisterButtonEffect(ViewGroup root) {
        hookEvent(root, new IFunc() {
            @Override
            public void forView(View view) {
                unregisterButtonEffect(view);
            }
        });
    }

    public static void unregisterButtonEffect(View button) {
        button.setOnTouchListener(null);
    }

    public interface IFunc {
        void forView(View view);
    }
}