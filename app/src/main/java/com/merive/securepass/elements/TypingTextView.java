package com.merive.securepass.elements;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

public class TypingTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final Handler handler = new Handler();
    private CharSequence text;
    private int index;
    private long delay = 150;
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if (index <= text.length()) {
                handler.postDelayed(characterAdder, delay);
            }
        }
    };

    /**
     * TypingTextView Constructor.
     *
     * @param context Using by AppCompatTextView.
     * @see androidx.appcompat.widget.AppCompatTextView
     */
    public TypingTextView(Context context) {
        super(context);
    }

    /**
     * TypingTextView Constructor
     *
     * @param context Using by AppCompatTextView.
     * @param attrs   Using by AppCompatTextView.
     * @see Context
     * @see AttributeSet
     */
    public TypingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * This static method is making typing animation.
     *
     * @param view TypingTextView what will be animating.
     * @param text Text what will be setting to view.
     */
    public static void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    /**
     * This method is animating text.
     *
     * @param text Assigns to this.text.
     */
    public void animateText(CharSequence text) {
        this.text = text;
        index = 0;

        setText("");
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    /**
     * This method assigns value to delay variable.
     *
     * @param millis Character delay.
     */
    public void setCharacterDelay(long millis) {
        delay = millis;
    }
}
