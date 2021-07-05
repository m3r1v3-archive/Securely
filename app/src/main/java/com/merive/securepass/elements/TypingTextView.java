package com.merive.securepass.elements;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

public class TypingTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final Handler mHandler = new Handler();
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 150;
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };

    public TypingTextView(Context context) {
        super(context);
    }
    public TypingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void animateText(CharSequence text) {
        /* Animation for text */
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    public static void typingAnimation(TypingTextView view, String text) {
        /* Typing animation for text elements */
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }
}
