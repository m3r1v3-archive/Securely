package com.merive.securely.elements;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

public class TypingTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final Handler handler = new Handler();
    private CharSequence text;

    private int index;
    private long delay = 150;

    public TypingTextView(Context context) {
        super(context);
    }

    public TypingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    private void animateText(CharSequence text) {
        this.text = text;
        index = 0;

        setText("");
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if (index <= text.length()) {
                handler.postDelayed(characterAdder, delay);
            }
        }
    };

    private void setCharacterDelay(long millis) {
        delay = millis;
    }
}
