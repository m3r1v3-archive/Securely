package com.merive.securely.components;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

public class TypingTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final Handler handler = new Handler();
    private final long delay = 125;
    private CharSequence text;
    private int index;

    public TypingTextView(Context context) {
        super(context);
    }

    public TypingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Make typing animation for TypingTextView component
     *
     * @param view Component what will be animated
     * @param text Text what will be typed
     */
    public static void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.animateText(text);
    }

    /**
     * Makes text typing animation
     *
     * @param text Text what will be typed
     */
    private void animateText(CharSequence text) {
        this.text = text;
        index = 0;

        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if (index <= text.length()) handler.postDelayed(characterAdder, delay);
        }
    };
}
